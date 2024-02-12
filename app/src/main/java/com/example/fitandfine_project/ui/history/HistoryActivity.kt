package com.example.fitandfine_project.ui.history

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitandfine_project.R
import com.example.fitandfine_project.data.History.History
import com.example.fitandfine_project.mToast
import com.example.fitandfine_project.ui.AppViewModelProvider
import com.example.fitandfine_project.ui.home.DayStatusViewModel
import com.example.fitandfine_project.ui.theme.*
import kotlinx.coroutines.launch
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HistoryScreen(
    navigateToHistoryEntry: ()-> Unit,
    viewModel: HistoryViewModel = viewModel(factory = AppViewModelProvider.Factory),
    d_viewModel: DayStatusViewModel = viewModel(factory = AppViewModelProvider.Factory)
)
    {
        val historyScreenUiState by viewModel.historyScreenUiState.collectAsState()
        val preferencesUiState by viewModel.preferencesState.collectAsState()
        val scaffoldState: ScaffoldState = rememberScaffoldState()
        val coroutineScope = rememberCoroutineScope()
        val mContext = LocalContext.current

        Scaffold(scaffoldState = scaffoldState,
            floatingActionButton = {
                FloatingActionButton( //Button will clear all history
                    onClick = {
                     viewModel.showConfirmationDialog.value = true

                    },
                    modifier = Modifier.padding(vertical = 110.dp),
                    backgroundColor = Color.Red
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = stringResource(R.string.item_entry_title),
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
                if (preferencesUiState.historyEditingEnabled)
                {
                    FloatingActionButton( //Button navigates to a new screen to create a new history item
                        onClick = navigateToHistoryEntry,
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



            }
        )
        {
            History(viewModel,historyScreenUiState = historyScreenUiState)
        }

        if(viewModel.showConfirmationDialog.value)
        {
            DeleteConfirmationDialog(onDeleteConfirm = {
                viewModel.clearAllHistory()
                coroutineScope.launch {
                    val snackBarResult = scaffoldState.snackbarHostState.showSnackbar(
                        message = "History has been cleared"
                    )
                    when (snackBarResult){
                        SnackbarResult.Dismissed -> Log.i("MyTAG","Dismissed")
                        SnackbarResult.ActionPerformed -> Log.i("MyTAG","Performed")
                    }
                    d_viewModel.sendGoalToHistory()
                    viewModel.clearAllHistory()
                }
                viewModel.showConfirmationDialog.value = false

            },
                onDeleteCancel = { viewModel.showConfirmationDialog.value= false })
        }

}



@Composable
fun History(viewModel: HistoryViewModel,historyScreenUiState: HistoryScreenUiState){
   LazyColumn(modifier= Modifier
       .background(MaterialTheme.colors.background)
       .fillMaxSize()
       .padding(bottom = 50.dp)){
        items(historyScreenUiState.historyList) {
            HistoryItem(viewModel,history = it)
        }}
}
@Composable
fun HistoryItem(viewModel:HistoryViewModel ,history: History, modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val mContext = LocalContext.current

    Card(
        modifier = modifier.padding(8.dp),
        elevation = 10.dp
    ) {
        var year = ""
        var month = ""
        var day = ""
        if (history.date.toString().length>1)
        {
            year =history.date.toString()[0].toString() + history.date.toString()[1].toString()+history.date.toString()[2].toString() + history.date.toString()[3].toString()
            month =history.date.toString()[4].toString() + history.date.toString()[5].toString()
            day =history.date.toString()[6].toString() + history.date.toString()[7].toString()
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Column()
            {

                //Date
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text(
                        text = "$day/$month/$year",
                        color = Color.Black,
                        modifier = Modifier,
                        style = MaterialTheme.typography.h2
                    )

                }
                Row(modifier = Modifier.padding(top = 4.dp, bottom = 6.dp)) {
                    //Name
                    val txt = history.name
                    var text by remember {
                        mutableStateOf(txt)
                    }
                    OutlinedTextField(
                        //Text field for goal name
                        value = text,
                        onValueChange = { newText -> text = newText },
                        label = {
                            Text(text = "Goal")
                        },
                        leadingIcon = {Icon(Icons.Outlined.EmojiEvents, contentDescription = null)},

                        modifier = Modifier
                            .width(250.dp)
                            .height(60.dp),
                        maxLines = 1,
                        textStyle = MaterialTheme.typography.body1,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            coroutineScope.launch {
                                viewModel.updateHistory(history,text,history.steps)
                            }
                        }),
                        readOnly = !viewModel.allowEditing(),
                        )
                }
                Row() {
                    Row() {
                        val txt2 = history.steps.toString()
                        var text2 by remember {
                            mutableStateOf(txt2)
                        }
                        OutlinedTextField(
                            //Text field for steps
                            value = text2,
                            onValueChange = { newText -> text2 = newText },
                            label = {
                                Text(text = "Steps")
                            },
                            leadingIcon = {Icon(Icons.Outlined.DirectionsWalk, contentDescription = null)},
                            modifier = Modifier
                                .width(150.dp)
                                .height(60.dp),
                            maxLines = 1,
                            textStyle = MaterialTheme.typography.body1,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = {
                                 if(text2.isDigitsOnly())
                                {
                                    coroutineScope.launch {
                                        viewModel.updateHistory(history,history.name,text2.toInt())
                                    }
                                    focusManager.clearFocus()

                                }
                                else
                                {
                                    mToast(mContext,"Please enter a number")
                                }
                            }),
                        readOnly = !viewModel.allowEditing(),

                            )
                    }

                    Column() {
                        Row( modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                            horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                            Row() {
                                Icon(Icons.Outlined.TrackChanges, contentDescription = "Target Icon")
                                Text(
                                    text = history.target.toString(),
                                    color = MaterialTheme.colors.onSurface,
                                    style = MaterialTheme.typography.h3
                                )
                            }

                        }
                    }

                }

                Row( modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth() ) {
                    Box(contentAlignment = Alignment.Center)
                    {

                        Column() {
                            LinearProgressIndicator(progress = (history.steps.toFloat()/history.target.toFloat()),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(CircleShape)
                                    .height(14.dp))
                        }
                        Text(text = (history.progress.toString())+"%", style = MaterialTheme.typography.h3,modifier = Modifier.padding(start = 5.dp))
                    }

                }

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
        title = { Text("Confirmation") },
        text = { Text("Are you sure want to clear all history?") },
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


@Preview
@Composable
fun HistoryPreview() {
    FitAndFine_ProjectTheme {
        HistoryScreen(navigateToHistoryEntry = {})
    }
}

