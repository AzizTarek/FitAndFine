package com.example.fitandfine_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.example.fitandfine_project.ui.goals.GoalEntryScreen
//import com.example.fitandfine_project.data.Goal.Goal
//import com.example.fitandfine_project.data.goals
import com.example.fitandfine_project.ui.theme.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitAndFine_ProjectTheme {
              MainScreen()
            }
        }
    }
}

