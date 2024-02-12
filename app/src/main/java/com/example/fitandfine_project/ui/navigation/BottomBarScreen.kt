package com.example.fitandfine_project.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Checklist
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val route : String,
    val title: String,
    val icon: ImageVector
)
{
    object Home: BottomBarScreen(
        route = "Home",
        title= "Home",
        icon = Icons.Rounded.Home
    )
    object Goals: BottomBarScreen(
        route = "Goals",
        title= "Goals",
        icon = Icons.Rounded.Checklist
    )
    object History: BottomBarScreen(
        route = "History",
        title= "History",
        icon = Icons.Rounded.History
    )
}

