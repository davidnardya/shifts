package com.davidnardya.shifts.navigation.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.davidnardya.shifts.viewmodels.MainViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import com.davidnardya.shifts.models.Guard
import com.davidnardya.shifts.models.Shift
import com.davidnardya.shifts.utils.ListToFileHelper
import com.davidnardya.shifts.utils.showToast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShiftsScreen(navController: NavHostController, viewModel: MainViewModel) {
    val list = viewModel.shiftListLiveData.observeAsState()
    val guards = viewModel.guardsListLiveData.observeAsState()
    val guardsOnVacation = viewModel.guardsOnVacationLiveData.observeAsState()
    val listToFileHelper = ListToFileHelper()

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        list.value?.let { listNotNull ->
            item {
                Button(onClick = {

                    val currentDateTime =
                        SimpleDateFormat(
                            "dd MM yyyy HH mm",
                            Locale.getDefault()
                        ).format(
                            Date()
                        )
                    val fileName = "Shifts $currentDateTime.csv"

                    val data = guardsOnVacation.value?.let { printList(listNotNull, it) }
                    val saved = listToFileHelper.saveToFile(
                        data ?: "",
                        fileName
                    )
                    val message = if (saved) {
                        "הקובץ נשמר בהצלחה"
                    } else {
                        "אירעה תקלה בשמירה"
                    }
                    showToast(message)
                }) {
                    Text(text = "ייצא רשימה לקובץ TXT")
                }
            }
            // Group shifts by day
            val groupedShifts = listNotNull.groupBy { it.shiftDay }

            // Iterate over each day and its shifts
            groupedShifts.forEach { (day, dayShifts) ->
                // Add a header for the day
                item {
                    Text(
                        text = day?.text.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }

                // Display shifts for the day
                itemsIndexed(dayShifts) { index, shift ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "${shift.shiftTime?.text}")
                        shift.guards?.forEach { guard ->
                            var isEditing by remember { mutableStateOf(false) }
                            var editedName by remember { mutableStateOf(guard.name) }
                            val keyboardController = LocalSoftwareKeyboardController.current
                            if (isEditing) {
                                TextField(
                                    value = editedName.toString(),
                                    onValueChange = { editedName = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            val newList =
                                                shift.guards
                                                    ?.filter { it.name != guard.name }
                                                    ?.toMutableList()
                                            guards.value?.firstOrNull { it.name == editedName }
                                                ?.let {
                                                    newList?.add(it)
                                                    shift.guards = newList
                                                    isEditing = false
                                                    keyboardController?.hide()
                                                } ?: run {
                                                showToast("לא ניתן למצוא את $editedName")
                                            }

                                        }),
                                    singleLine = true
                                )
                            } else {
                                Text(
                                    text = editedName.toString(),
                                    modifier = Modifier
                                        .combinedClickable(
                                            onClick = {},
                                            onLongClick = {
                                                isEditing = true
                                            }
                                        )
                                )
                            }
                        }
                    }
                    if (index != dayShifts.lastIndex) {
                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .padding(horizontal = 16.dp),
                        )
                    }
                }
                item {
                    Text(
                        text = "ברענון",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }

                guardsOnVacation.value?.let { guardsOnVaca ->
                    itemsIndexed(guardsOnVaca) { index, guard ->
                        if (guard.offTime?.contains(day) == true) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                            ) {
                                Text(text = guard.name.toString())
                            }
                            if (index != guardsOnVaca.lastIndex) {
                                Divider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .padding(horizontal = 16.dp),
                                )
                            }
                        }
                    }
                }

            }
        }

    }
}

fun printList(shiftList: List<Shift>, guardsOnVacation: List<Guard>): String {
    var result = ""
    val grouped = shiftList.groupBy { it.shiftDay }
    grouped.forEach { (day, shifts) ->
        result += "${day?.text}\n"
        shifts.forEach { shift ->
            result += "${shift.shiftDay?.text} "
            result += "${shift.shiftTime?.text} "
            shift.guards?.forEach { guard ->
                result += "${guard.name} "
            }
            result += "\n"
        }
        result += "\n"
        result += "ברענון"
        result += "\n"
        guardsOnVacation.forEach { guard ->
            if (guard.offTime?.contains(day) == true) {
                result += "${guard.name}\n"
            }
        }
        result += "\n"
    }
    return result
}