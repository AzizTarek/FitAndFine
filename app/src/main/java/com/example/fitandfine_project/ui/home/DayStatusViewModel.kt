package com.example.fitandfine_project.ui.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitandfine_project.data.Goal.Goal
import com.example.fitandfine_project.data.Goal.GoalsRepository
import com.example.fitandfine_project.data.History.History
import com.example.fitandfine_project.data.History.HistoryRepository
import com.example.fitandfine_project.ui.goals.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class DayStatusViewModel(
    private val goalsRepository: GoalsRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {
    val uiState: StateFlow<GoalDetailsUiState> =
       goalsRepository.getActiveGoal()
            .filterNotNull()
            .map {
                GoalDetailsUiState( goalDetails = it.toGoalDetails())
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = GoalDetailsUiState()
            )

    private val _activeGoal = mutableStateOf( getActiveGoal())
    val activeGoal : State<Goal> = _activeGoal
    private val _progress = mutableStateOf(getActiveGoal().progress)
    val progress :  State<Float> = _progress
    private val _steps = mutableStateOf(getActiveGoal().steps)
    val steps : State<Int> = _steps



    private val _currentDate = mutableStateOf(0)
    val currentDate : State<Int> = _currentDate


    /**
     * The goal will be have its steps and progress updated when new input is entered
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateActiveGoalInfo() {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val mCurrentDate = current.format(formatter)
        _activeGoal.value = uiState.value.goalDetails.toGoal()
        viewModelScope.launch {
           goalsRepository.updateGoal(_activeGoal.value.copy(steps = _steps.value, progress = _progress.value))
            if (historyRepository.getAll().isNotEmpty())
            {
                val latestHistItem = historyRepository.getAll().last()
                if(latestHistItem.date == mCurrentDate.toInt() ) //Update only if the item is in current time
                {
                    historyRepository.updateHistory(latestHistItem.copy(progress = _progress.value,
                        steps = _steps.value))
                }

            }
        }
    }

    /**
     * Handles the user input of new steps
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateProgress(inputSteps: String){
        if (inputSteps !="")
        {
            _steps.value += inputSteps.toInt()
        }
        _progress.value = ("%.2f".format((_steps.value /
                uiState.value.goalDetails.toGoal().target.toFloat())*100f)).toFloat()
        updateActiveGoalInfo()
    }

    /**
     * Method is called from the ui page when the date of the active goal has passed
     * It inserts this now old active goal to the history and resets all the values that track progress
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun sendGoalToHistory()
    {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val formatted = current.format(formatter)
        if(_activeGoal.value.selectedDate<formatted.toInt()) //If date of the active goal has passed it should be sent to the history
        {

            val oldGoal = getActiveGoal()
            viewModelScope.launch {
                Log.d("", "inserting to history ${oldGoal.name} ${oldGoal.steps}")
                //Create new history entry
                val historyItem = History(0, "", 0f, 0, 0, 0,)
                historyRepository.updateHistory(
                    historyItem.copy(
                        id = 0,
                        name = oldGoal.name,
                        progress = oldGoal.progress,
                        steps = oldGoal.steps,
                        target = oldGoal.target,
                        date = oldGoal.selectedDate
                    )
                )
            }
            uiState.value.goalDetails = GoalDetails().copy(
                0,
                "No Goal Selected",
                false,
                "0",
                "0",
                "0",
                "0"
            ) //Reset goal details
            goalsRepository.resetGoals()
            _activeGoal.value = Goal(0, "Walk", true, 0, 0, 0f, 0) //Default active goal is empty
            _steps.value = 0
            _progress.value = 0f
            _currentDate.value = 0
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDate(): Int
    {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val formatted = current.format(formatter)
        _currentDate.value = formatted.toInt()
        return _currentDate.value
    }
    fun getActiveGoal() : Goal
    {
        return if ( goalsRepository.getActive() !=null) {
            goalsRepository.getActive()
        } else
            Goal(0,"No Goal Selected",false,0,0,0f,0)
    }
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

/**
 * UI state for GoalDetailsScreen
 */
data class GoalDetailsUiState(
    var goalDetails: GoalDetails = GoalDetails()
)