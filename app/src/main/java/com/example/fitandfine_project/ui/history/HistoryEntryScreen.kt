package com.example.fitandfine_project.ui.history

import android.app.DatePickerDialog
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitandfine_project.R
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.core.view.isVisible
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitandfine_project.mToast
import com.example.fitandfine_project.ui.AppViewModelProvider
import com.example.fitandfine_project.ui.goals.GoalDetails
import com.example.fitandfine_project.ui.goals.GoalEntryViewModel
import com.example.fitandfine_project.ui.goals.GoalUiState
import com.example.fitandfine_project.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch
import java.util.*

object HistoryEntryDestination : NavigationDestination {
    override val route = "history_entry"
    override val titleRes = (R.string.HistoryEntryTitle)
}

@Composable
fun HistoryEntryScreen(
    navigateBack: () -> Unit,
    canNavigateBack: Boolean = true,
    viewModel: HistoryEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
    ) { innerPadding ->
        HistoryEntryBody(viewModel,
            historyUiState = viewModel.historyUiState,
            onGoalValueChange = viewModel::updateUiState,
            onSaveClick = {
                // Note: If the user rotates the screen very fast, the operation may get cancelled
                // and the item may not be saved in the Database. This is because when config
                // change occurs, the Activity will be recreated and the rememberCoroutineScope will
                // be cancelled - since the scope is bound to composition.
                coroutineScope.launch {
                    viewModel.saveHistory()
                    navigateBack()
                }
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun HistoryEntryBody(
    viewModel: HistoryEntryViewModel,
    historyUiState: HistoryUiState,
    onGoalValueChange: (HistoryDetails) -> Unit,
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
        val  historyDetails = historyUiState.historyDetails
        val focusManager = LocalFocusManager.current
        var validDate = false //If date is valid

        Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = historyDetails.name,
                onValueChange = {
                    historyDetails.copy(name = it)
                    onGoalValueChange(historyDetails.copy(name = it))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                }),
                label = { Text(stringResource(R.string.goal_name_req)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
                singleLine = true
            )


            OutlinedTextField(
                value = historyDetails.steps,
                onValueChange = {
                    historyDetails.copy(steps = it)
                    onGoalValueChange(historyDetails.copy(steps = it))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    if(historyDetails.steps.isDigitsOnly())
                        focusManager.moveFocus(FocusDirection.Down)
                    else
                        mToast( mContext,"Please enter a number")

                }),
                leadingIcon = { Icon(Icons.Filled.DirectionsWalk,
                    stringResource(R.string.TargetIconDesc)
                ) },
                label = { Text("Steps") },
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
                singleLine = true,
            )
            OutlinedTextField(
                value = historyDetails.target,
                onValueChange = {
                    historyDetails.copy(target = it)
                    onGoalValueChange(historyDetails.copy(target = it))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    if(historyDetails.target.isDigitsOnly())
                        focusManager.moveFocus(FocusDirection.Down)
                    else
                        mToast( mContext,"Please enter a number")

                }),
                leadingIcon = { Icon(Icons.Filled.TrackChanges,
                    stringResource(R.string.TargetIconDesc)
                ) },
                label = { Text(stringResource(R.string.target_req)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
                singleLine = true,
            )

            MyDatePicker(viewModel,historyUiState = historyUiState)

        }
        Button(
            onClick = {
                if (viewModel.checkHistoryDateValidity(historyUiState.historyDetails.date.toInt()) )
                {
                    onSaveClick()
                    Log.d("","Saving")
                }
                    else
                {
                    mToast(mContext,"Please select a different date, this date is present in the history")
                    Log.d("","Date not valid")
                }
                },
            enabled = historyUiState.isEntryValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.save_action))
        }
    }
}

@Composable
fun MyDatePicker(viewModel: HistoryEntryViewModel,historyUiState: HistoryUiState){

    // Fetching the Local Context
    val mContext = LocalContext.current

    // Declaring integer values
    // for year, month and day
    val mYear: Int
    val mMonth: Int
    val mDay: Int

    // Initializing a Calendar
    val mCalendar = Calendar.getInstance()

    // Fetching current year, month and day
    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()

    // Declaring a string value to
    // store date in string format
    val mDate = remember { mutableStateOf("") }
    // Declaring DatePickerDialog and setting
    // initial values as current values (present year, month and day)
    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
//            mDate.value = "$mDayOfMonth/${mMonth+1}/$mYear"
            mDate.value = mYear.toString()
            if(mMonth<10)
                mDate.value += "0${mMonth+1}"
            else
                mDate.value += "${mMonth+1}"

            if(mDayOfMonth<10)
                mDate.value += "0$mDayOfMonth"
            else
                mDate.value += "$mDayOfMonth"


        }, mYear, mMonth, mDay
    )
    mDatePickerDialog.datePicker.maxDate = mCalendar.timeInMillis


    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        // Creating a button that on
        // click displays/shows the DatePickerDialog
        // Displaying the mDate value in the Text
        Text(text =
        if (mDate.value.isNotEmpty())
        {
            "Selected Date ${mDate.value[0]}" +
            "${mDate.value[1]}${mDate.value[2]}${mDate.value[3]}/" +
            "${mDate.value[4]}${mDate.value[5]}/${mDate.value[6]}${mDate.value[7]}"
        }
         else "Selected Date ${mDate.value}",
            fontSize = 20.sp, textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h3, modifier = Modifier.padding(20.dp))
        OutlinedButton(
            onClick = {
            mDatePickerDialog.show()
            viewModel.updateUiState(historyUiState.historyDetails.copy(date = mDate.value))
        },
        shape = CircleShape,
        elevation=  ButtonDefaults.elevation(1.dp))
        {
            Text(text = "Select Date",
                color = Color.White,
                style = MaterialTheme.typography.h2
            )
        }

        // Adding a space of 100dp height
        Spacer(modifier = Modifier.size(20.dp))

       viewModel.updateUiState(historyUiState.historyDetails.copy(date = mDate.value))


    }
}

@Preview(showBackground = true)
@Composable
private fun GoalEntryScreenPreview() {
//    HistoryEntryBody(
//
//        historyUiState =HistoryUiState(
//          HistoryDetails(
//                name = "Walk to work",
//                steps = "0",
//                target = "2000",
//                date = "02022001"
//            )
//        ),
//        onGoalValueChange = {},
//        onSaveClick = {}
//    )
}
