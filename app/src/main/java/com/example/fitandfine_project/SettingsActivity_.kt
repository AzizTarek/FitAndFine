package com.example.fitandfine_project

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.fitandfine_project.ui.AppViewModelProvider
import com.example.fitandfine_project.ui.goals.GoalViewModel
import com.example.fitandfine_project.ui.history.HistoryViewModel
import com.example.fitandfine_project.ui.navigation.NavigationDestination
import com.example.fitandfine_project.ui.theme.FitAndFine_ProjectTheme
import com.example.fitandfine_project.ui.theme.Green400

object SettingsDestination : NavigationDestination {
    override val route = "preferences"
    override val titleRes =(R.string.SettingsDesc)
}
//class SettingsActivity_ : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            FitAndFine_ProjectTheme {
//                SettingsScreen(navigateBack = {})
//            }
//        }
//    }
//}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SettingsScreen(
    viewModel: GoalViewModel = viewModel(factory = AppViewModelProvider.Factory),
    h_viewModel: HistoryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateBack: () -> Unit,
    canNavigateBack: Boolean = true,
    navHostController: NavHostController
)
{

        val preferencesUiState by viewModel.preferencesState.collectAsState()
        val h_preferencesUiState by h_viewModel.preferencesState.collectAsState()

        Column(modifier = Modifier
            .background(
                color = MaterialTheme.colors.background
            )
            .fillMaxSize()) {
            Card( modifier = Modifier
                .width(400.dp)
                .height(100.dp)
                .padding(10.dp),
                elevation = 10.dp,
                backgroundColor = Green400)
            {
                Column (horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center, modifier = Modifier.padding(10.dp)){
                    Text(text = "Configure Goals",fontSize = 17.sp,
                        style= MaterialTheme.typography.h2,
                        color = MaterialTheme.colors.onSurface)
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start=8.dp) ){
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Edit Button Icon", modifier = Modifier.size(17.dp)
                        )
                        Text(text = "Edit goal details",
                            style= MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.onSurface)
                    }
                }
                Column (horizontalAlignment = Alignment.End,verticalArrangement = Arrangement.Center, modifier = Modifier.padding(10.dp)){
                    // Declaring a boolean value for storing checked state
                    val mCheckedState = preferencesUiState.goalEditingAllowed
                    // Creating a Switch, when value changes,
                    // it updates mCheckedState value
                    Switch(checked = mCheckedState, onCheckedChange = {viewModel.toggleGoalEditing(!mCheckedState)
                        navigateBack()}
                    )

                }
            }

            Card( modifier = Modifier
                .width(500.dp)
                .height(100.dp)
                .padding(10.dp),
                elevation = 10.dp,backgroundColor = Green400)
            {
                Column (horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center, modifier = Modifier.padding(10.dp)){
                    Text(text = "Historical Mode",fontSize = 17.sp,
                        style= MaterialTheme.typography.h2,
                        color = MaterialTheme.colors.onSurface)
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start=8.dp) ){
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Edit Button Icon", modifier = Modifier.size(17.dp)
                        )
                        Text(text = "Normal or historical activity recording",
                            style= MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.onSurface)
                    }
                }
                Column (horizontalAlignment = Alignment.End,verticalArrangement = Arrangement.Center, modifier = Modifier.padding(10.dp)){
                    // Declaring a boolean value for storing checked state
                    var mCheckedState = h_preferencesUiState.historyEditingEnabled
                    // Creating a Switch, when value changes,
                    // it updates mCheckedState value
                    Switch(checked = mCheckedState, onCheckedChange ={h_viewModel.toggleHistoryEditing(!mCheckedState)
                        navigateBack()})
                }
            }
        }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FitAndFine_ProjectTheme {
//        SettingsScreen(navigateBack = {}, navHostController = {})
    }
}