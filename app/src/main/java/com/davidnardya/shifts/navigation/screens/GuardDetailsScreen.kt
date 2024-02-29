package com.davidnardya.shifts.navigation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.davidnardya.shifts.models.ShiftDay
import com.davidnardya.shifts.viewmodels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun GuardDetailsScreen(navController: NavHostController, viewModel: MainViewModel) {
    val guardState = viewModel.currentGuardLiveData.observeAsState()
    var name by remember { mutableStateOf(guardState.value?.name) }
    var offTime by remember { mutableStateOf(guardState.value?.offTime ?: mutableListOf()) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        TextField(
            value = name ?: "",
            onValueChange = {
                name = it
            },
            placeholder = {
                if (guardState.value == null) {
                    Text(text = "שם השומר")
                }
            }
        )
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Text(text = "אילוצים:")
            ShiftDay.entries.forEach { shiftDay ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var isChecked by remember { mutableStateOf(offTime.contains(shiftDay)) }
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { isCheckedChanged ->
                            isChecked = isCheckedChanged
                            val newList = offTime.toMutableList()
                            if (isCheckedChanged && !offTime.contains(shiftDay)) {
                                newList.add(shiftDay)
                                newList.let {
                                    offTime = it
                                }

                            }
                            if (!isCheckedChanged && offTime.contains(shiftDay)) {
                                newList.remove(shiftDay)
                                newList.let {
                                    offTime = it
                                }
                            }
                        }
                    )
                    Text(
                        text = shiftDay.text,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
        Button(onClick = {
            if (guardState.value != null) {
                scope.launch(Dispatchers.IO) {
                    viewModel.updateGuardDetails(guardState.value, name, offTime)
                    viewModel.getGuardList()
                }
            } else {
                scope.launch(Dispatchers.IO) {
                    viewModel.createNewGuard(name, offTime)
                    viewModel.getGuardList()
                }
            }
            navController.navigate(Screen.Guards.route) {
                popUpTo(Screen.GuardDetails.route) {
                    inclusive = true
                }
                popUpTo(Screen.Guards.route) {
                    inclusive = true
                }
            }
        }) {
            Text(
                text = if (guardState.value == null) {
                    "שמור חדש"
                } else {
                    "שמור שינויים"
                }
            )
        }

        if(guardState.value != null) {
            Button(
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        viewModel.deleteGuard(guardState.value)
                        viewModel.getGuardList()
                    }
                    navController.navigate(Screen.Guards.route) {
                        popUpTo(Screen.GuardDetails.route) {
                            inclusive = true
                        }
                        popUpTo(Screen.Guards.route) {
                            inclusive = true
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
            ) {
                Text(text = "מחק שומר")
            }
        }
    }
}