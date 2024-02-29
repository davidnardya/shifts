package com.davidnardya.shifts.navigation.screens

sealed class Screen(val route: String) {
    data object Main : Screen("MainScreen")
    data object Shifts : Screen("ShiftsScreen")
    data object Guards : Screen("GuardsScreen")
    data object GuardDetails : Screen("GuardDetailsScreen")
}