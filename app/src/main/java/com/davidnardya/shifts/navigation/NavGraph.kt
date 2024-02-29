package com.davidnardya.shifts.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.davidnardya.shifts.navigation.screens.GuardDetailsScreen
import com.davidnardya.shifts.navigation.screens.GuardsScreen
import com.davidnardya.shifts.navigation.screens.MainScreen
import com.davidnardya.shifts.navigation.screens.Screen
import com.davidnardya.shifts.navigation.screens.ShiftsScreen
import com.davidnardya.shifts.viewmodels.MainViewModel

@Composable
fun SetupNavGraph(
    navController: NavHostController, viewModel: MainViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(
            route = Screen.Main.route
        ) {
            MainScreen(navController, viewModel)
        }
        composable(
            route = Screen.Shifts.route
        ) {
            ShiftsScreen(navController, viewModel)
        }
        composable(
            route = Screen.Guards.route
        ) {
            GuardsScreen(navController, viewModel)
        }
        composable(
            route = Screen.GuardDetails.route
        ) {
            GuardDetailsScreen(navController, viewModel)
        }
    }
}