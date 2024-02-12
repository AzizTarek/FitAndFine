package com.example.fitandfine_project

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.rounded.DirectionsWalk
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitandfine_project.ui.AppViewModelProvider
import com.example.fitandfine_project.ui.goals.toGoal
import com.example.fitandfine_project.ui.home.DayStatusViewModel
import com.example.fitandfine_project.ui.navigation.NavigationDestination
import com.example.fitandfine_project.ui.theme.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object GoalDetailsDestination : NavigationDestination {
    override val route = "active_goal_details"
    override val titleRes = (R.string.activeGoalDetails)
    const val goalIdArg = "itemId"
    val routeWithArgs = "$route/{$goalIdArg}"
}
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun DayStatusScreen(
    viewModel: DayStatusViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    val formatted = current.format(formatter)

    if(uiState.goalDetails.active &&
        uiState.goalDetails.selectedDate.toInt()<viewModel.getCurrentDate()) //If date has passed send goal to history
        viewModel.sendGoalToHistory()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Center) {
            //Circular progress bar
            CircularProgressIndicator(
                progress = 1f,
                modifier = Modifier
                    .padding(10.dp)
                    .size(300.dp), strokeWidth = 40.dp, color = MaterialTheme.colors.primary
            )
            CircularProgressIndicator(
                progress = uiState.goalDetails.progress.toFloat()/100f,
                modifier = Modifier
                    .padding(10.dp)
                    .clip(CircleShape)
                    .size(300.dp), strokeWidth = 40.dp, color = Green400
            )
            //Text within progress bar
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Rounded.DirectionsWalk, contentDescription = "Walking Icon",
                    modifier = Modifier.size(50.dp), tint = MaterialTheme.colors.primary)

                Text(
                   text= uiState.goalDetails.progress + "%" ,
                    color = MaterialTheme.colors.primary,
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h2.copy(
                        shadow = Shadow(
                            color = Color(0x4c000000),
                            offset = Offset(2f, 2f),
                            blurRadius = 7f
                        )
                    )
                )
                Text(
                    text = "Completed",
                    color = MaterialTheme.colors.primary,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.h2.copy(
                        shadow = Shadow(
                            color = Color(0x4c000000),
                            offset = Offset(2f, 2f),
                            blurRadius = 7f
                        )
                    )
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.Center
        )
        {
            val focusManager = LocalFocusManager.current
            var progressText by remember { mutableStateOf("") } //temporary text ,
            // when user submits the input (by pressing done button on keyboard) it will be passed to the viewmodel
            val mContext = LocalContext.current
            OutlinedTextField(
                value = progressText,
                onValueChange = { newText: String ->
                   progressText = newText
                },
                label = {
                    Text(text = "Add Steps")
                },
                modifier = Modifier
                    .width(300.dp)
                    .height(60.dp),
                maxLines = 1,
                textStyle = MaterialTheme.typography.body1,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    if (progressText.isDigitsOnly())
                    {
                        viewModel.updateProgress(progressText)
                    }
                    else
                        mToast(mContext,"Please enter a number")
                    if (!uiState.goalDetails.active )
                        mToast(mContext,"Please select a goal")
                    focusManager.clearFocus() //When done button on keyboard is pressed keyboard will close
                    progressText = ""
                })
            )

        }
        Text(
            text = uiState.goalDetails.name,
            modifier = Modifier.padding(19.dp),
            color = MaterialTheme.colors.primaryVariant,
            fontSize = 26.sp,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h2.copy(
                shadow = Shadow(
                    color = Color(0x4c000000),
                    offset = Offset(2f, 2f),
                    blurRadius = 7f
                )
            )
        )
        Text(
            text =  uiState.goalDetails.steps + " Steps", //total current steps count
            modifier = Modifier.padding(5.dp),
            color = MaterialTheme.colors.primary,
            fontSize = 23.sp,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h2.copy(
                shadow = Shadow(
                    color = Color(0x4c000000),
                    offset = Offset(2f, 2f),
                    blurRadius = 7f
                )
            )
        )
        Text(
            text ="Target: " + uiState.goalDetails.target,
            modifier = Modifier.padding(10.dp),
            color = Green400,
            fontSize = 23.sp,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h2.copy(
                shadow = Shadow(
                    color = Color(0x4c000000),
                    offset = Offset(2f, 2f),
                    blurRadius = 7f
                )
            )
        )
    }
    }
// Function to generate a Toast
fun mToast(context: Context,message: String){
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun DayStatusPreview() {
    FitAndFine_ProjectTheme {
        DayStatusScreen()
    }
}


