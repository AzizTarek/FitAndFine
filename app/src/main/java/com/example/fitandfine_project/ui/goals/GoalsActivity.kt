package com.example.fitandfine_project.ui.goals

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitandfine_project.R
import com.example.fitandfine_project.data.Goal.Goal
import com.example.fitandfine_project.mToast
import com.example.fitandfine_project.ui.AppViewModelProvider
import com.example.fitandfine_project.ui.history.HistoryPreferencesUiState
import com.example.fitandfine_project.ui.home.DayStatusViewModel
import com.example.fitandfine_project.ui.theme.*
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun GoalScreen(   navigateToGoalEntry: ()-> Unit,
  viewModel: GoalViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val homeUiState by viewModel.homeUiState.collectAsState()
    val preferencesUiState by viewModel.preferencesState.collectAsState()
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToGoalEntry,
                modifier = Modifier.padding(vertical = 45.dp),
                backgroundColor = Green700
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.item_entry_title),
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        }
    )
    {
        LazyColumn(modifier = Modifier.padding(bottom = 50.dp))
        {
            itemsIndexed(homeUiState.goalList, key = {_,listItem->
                listItem.hashCode()
            }){
                    index,item->
                if (viewModel.actionButtonsVisibility(item)) //If action buttons are enabled then sliding gesture will be enabled too
                {
                    val state = rememberDismissState( //Sliding gesture will delete a goal
                        confirmStateChange = {
                            if(it==DismissValue.DismissedToStart)
                            {
                                coroutineScope.launch {
                                    viewModel.deleteGoal(item)
                                 val snackBarResult = scaffoldState.snackbarHostState.showSnackbar(
                                        message = "Goal has been deleted",
                                        actionLabel = "Undo"
                                    )
                                 when (snackBarResult){
                                     SnackbarResult.Dismissed -> Log.i("MyTAG","Dismissed")
                                     SnackbarResult.ActionPerformed -> viewModel.undoGoalDeletion()
                                 }
                                }
                            }
                            true
                        }
                    )
                    SwipeToDismiss(state = state, background = {
                        val color = when(state.dismissDirection){
                            DismissDirection.StartToEnd ->MaterialTheme.colors.background
                            DismissDirection.EndToStart -> MaterialTheme.colors.primaryVariant
                            null ->MaterialTheme.colors.background
                        }
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .background(color = color)){
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete Button Icon",
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 10.dp),
                                tint = MaterialTheme.colors.surface
                            )
                        }
                    },
                        dismissContent = {
                            GoalItem(preferencesUiState,viewModel,goal = item)
                        },
                        directions = setOf(DismissDirection.EndToStart))
                    Divider()
                }
                else
                {
                    GoalItem(preferencesUiState,viewModel,goal = item)
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GoalItem(preferencesUiState: GoalPreferencesUiState, viewModel: GoalViewModel, goal: Goal, modifier: Modifier = Modifier,
             d_viewModel: DayStatusViewModel = viewModel(factory = AppViewModelProvider.Factory)){
    val coroutineScope = rememberCoroutineScope()
    viewModel.findActiveGoal(goal) //check if the goal is active
    Card(
    modifier = modifier
        .padding(8.dp)
        .clickable {
            viewModel.changeActiveGoal(goal)
            d_viewModel.updateActiveGoalInfo()
        },
    elevation = 4.dp,
    backgroundColor = viewModel.getGoalBgColor(goal)
    ) {
    Row(
        modifier= Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ){
        GoalInformation(preferencesUiState,viewModel,goal)
    }
}
}

@Composable
fun GoalInformation(preferencesUiState: GoalPreferencesUiState,
                    viewModel: GoalViewModel,
                    goal: Goal
){
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val homeUiState by viewModel.homeUiState.collectAsState()
    val mContext = LocalContext.current

    Row{
        Column {
            var txt2 = goal.name
            var text2 by remember {mutableStateOf(txt2)}
            OutlinedTextField( //Text field for Goal name
                value = text2,
                leadingIcon = {Icon(Icons.Filled.EmojiEvents, contentDescription = null)},
                onValueChange = { newText-> text2 = newText  },
                label = {
                    Text(text = "Goal")
                },
                textStyle =MaterialTheme.typography.h2,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ), keyboardActions = KeyboardActions(onDone = {focusManager.clearFocus()
                    coroutineScope.launch {
                        viewModel.updateGoal(goal,text2,goal.target.toString())
                    }  }),
                readOnly = !viewModel.allowEditing(goal),
                singleLine = true
            )

            val txt = stringResource(R.string.steps,goal.target)
            var text by remember {
                mutableStateOf(txt)
            }
            Row {
                OutlinedTextField( //Text field for target steps
                    value = text,
                    leadingIcon = {Icon(Icons.Filled.TrackChanges, contentDescription = null)},
                    onValueChange = { newText ->text = newText },
                    label = {
                        Text(text = "Target")
                    },
                    modifier = Modifier
                        .width(150.dp)
                        .height(60.dp),
                    maxLines = 1,
                    textStyle =MaterialTheme.typography.body1,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ), keyboardActions = KeyboardActions(onDone = {
                        if(text.isDigitsOnly())
                        {
                            coroutineScope.launch {
                                viewModel.updateGoal(goal,goal.name,text)
                            }
                            focusManager.clearFocus()

                        }
                        else
                        {
                            mToast(mContext,"Please enter a number")
                        }
                         }),
                    readOnly = !viewModel.allowEditing(goal),

                )
            }
        }
    }

}




@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = { /* Do nothing */ },
        title = { Text(stringResource(R.string.attention)) },
        text = { Text(stringResource(R.string.delete_question)) },
        modifier = modifier.padding(16.dp),
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(text = stringResource(R.string.no))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(text = stringResource(R.string.yes))
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun GoalsPreview() {
    FitAndFine_ProjectTheme {
        GoalScreen(navigateToGoalEntry = {})
    }
}
