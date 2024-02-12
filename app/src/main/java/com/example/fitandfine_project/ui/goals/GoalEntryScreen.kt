package com.example.fitandfine_project.ui.goals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitandfine_project.R
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitandfine_project.mToast
import com.example.fitandfine_project.ui.AppViewModelProvider
import com.example.fitandfine_project.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object GoalEntryDestination : NavigationDestination {
    override val route = "item_entry"
    override val titleRes = R.string.item_entry_title
}

@Composable
fun GoalEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: GoalEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
    ) { innerPadding ->
        GoalEntryBody(
            goalUiState = viewModel.goalUiState,
            onGoalValueChange = viewModel::updateUiState,
            onSaveClick = {
                // Note: If the user rotates the screen very fast, the operation may get cancelled
                // and the item may not be saved in the Database. This is because when config
                // change occurs, the Activity will be recreated and the rememberCoroutineScope will
                // be cancelled - since the scope is bound to composition.
                coroutineScope.launch {
                    viewModel.saveGoal()
                    navigateBack()
                }
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun GoalEntryBody(
    goalUiState: GoalUiState,
    onGoalValueChange: (GoalDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val mContext = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        val  goalDetails = goalUiState.goalDetails
        val focusManager = LocalFocusManager.current

        Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = goalDetails.name,
                onValueChange = {
                    goalDetails.copy(name = it)
                    onGoalValueChange(goalDetails.copy(name = it))
                },
                label = { Text(stringResource(R.string.goal_name_req)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
                singleLine = true
            )
            OutlinedTextField(
                value = goalDetails.target,
                onValueChange = {
                    goalDetails.copy(target = it)
                    onGoalValueChange(goalDetails.copy(target = it))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {focusManager.clearFocus()
                    if (goalDetails.target.isDigitsOnly())
                        onSaveClick()
                    else
                        mToast(mContext,"Please enter a number")
                }),
                leadingIcon = { Icon(Icons.Filled.TrackChanges,
                                    stringResource(R.string.TargetIconDesc)
                                ) },
                label = { Text(stringResource(R.string.target_req)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
                singleLine = true,
            )
        }
        Button(
            onClick = {  if (goalDetails.target.isDigitsOnly())
                onSaveClick()
            else
                mToast(mContext,"Please enter a number") },
            enabled = goalUiState.isEntryValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.save_action))
        }
    }
}



@Preview(showBackground = true)
@Composable
private fun GoalEntryScreenPreview() {
    GoalEntryBody(
        goalUiState =GoalUiState(
           GoalDetails(
                name = "Walk to work",
                steps = "0",
                target = "2000"
            )
        ),
        onGoalValueChange = {},
        onSaveClick = {}
    )
}
