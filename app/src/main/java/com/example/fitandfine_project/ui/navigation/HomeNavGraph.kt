package com.example.fitandfine_project.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fitandfine_project.DayStatusScreen
import com.example.fitandfine_project.ui.history.HistoryScreen
import com.example.fitandfine_project.SettingsDestination
import com.example.fitandfine_project.SettingsScreen
import com.example.fitandfine_project.ui.goals.GoalEntryDestination
import com.example.fitandfine_project.ui.goals.GoalEntryScreen
import com.example.fitandfine_project.ui.goals.GoalScreen
import com.example.fitandfine_project.ui.history.HistoryEntryDestination
import com.example.fitandfine_project.ui.history.HistoryEntryScreen

//import com.example.fitandfine_project.Sceens.HistoryScreen

@Composable
fun HomeNavGraph(navController: NavHostController){
    NavHost(navController = navController, startDestination = BottomBarScreen.Home.route)
    {
        composable(route = BottomBarScreen.Home.route){
            DayStatusScreen()
        }

        composable(route = BottomBarScreen.Goals.route){
           GoalScreen(navigateToGoalEntry = { navController.navigate(GoalEntryDestination.route) })
        }
        composable(route = BottomBarScreen.History.route){
            HistoryScreen(navigateToHistoryEntry = { navController.navigate(HistoryEntryDestination.route) })
        }

        composable(route = GoalEntryDestination.route){
            GoalEntryScreen(navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp()})
        }
        composable(route =HistoryEntryDestination.route){
            HistoryEntryScreen(navigateBack = { navController.popBackStack() })
        }

        composable(route = SettingsDestination.route){
            SettingsScreen(navigateBack = { navController.popBackStack()}, navHostController = navController)
        }

    }
}