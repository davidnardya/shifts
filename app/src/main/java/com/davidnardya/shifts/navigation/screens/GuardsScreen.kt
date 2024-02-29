package com.davidnardya.shifts.navigation.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.davidnardya.shifts.viewmodels.MainViewModel

@Composable
fun GuardsScreen(navController: NavHostController, viewModel: MainViewModel) {
    val list = viewModel.guardsListLiveData.observeAsState()
    list.value?.let { guardsList ->
        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                Button(onClick = {
                    viewModel.resetCurrentGuard()
                    navController.navigate(Screen.GuardDetails.route)
                }) {
                    Text(text = "הוסף שומר חדש")
                }
            }
            itemsIndexed(guardsList) { index, guard ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .clickable {
                            viewModel.resetCurrentGuard()
                            viewModel.updateCurrentGuard(guard)
                            navController.navigate(Screen.GuardDetails.route)
                        },
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = guard.name.toString())
                }
                if (index != guardsList.lastIndex) {
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp),
                    )
                }
            }
        }
    }

}