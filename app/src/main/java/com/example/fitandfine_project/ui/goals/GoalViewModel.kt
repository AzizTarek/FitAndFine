/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.fitandfine_project.ui.goals

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fitandfine_project.InventoryApplication
import com.example.fitandfine_project.data.Goal.Goal
import com.example.fitandfine_project.data.Goal.GoalsRepository
import com.example.fitandfine_project.data.History.History
import com.example.fitandfine_project.data.History.HistoryRepository
import com.example.fitandfine_project.data.UserPreferencesRepository
import com.example.fitandfine_project.ui.theme.Green400
import com.example.fitandfine_project.ui.theme.Pink200
import com.example.fitandfine_project.ui.theme.Pink400
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * View Model to retrieve all goals in the Room database.
 */
class GoalViewModel(private val userPreferencesRepository: UserPreferencesRepository,
                    private val goalsRepository: GoalsRepository,
                    private val historyRepository: HistoryRepository
)
    : ViewModel()
{
    /**
     * Holds home ui state. The list of items are retrieved from [GoalsRepository] and mapped to
     * [HomeUiState]
     */
    val homeUiState: StateFlow<HomeUiState> =
       goalsRepository.getAllGoalsStream().map { HomeUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )
    val preferencesState: StateFlow<GoalPreferencesUiState> =
        userPreferencesRepository.goalEditingAllowed.map {goalEditingAllowed ->
           GoalPreferencesUiState(goalEditingAllowed)
        } .stateIn(
            scope = viewModelScope,
            // Flow is set to emits value for when app is on the foreground
            // 5 seconds stop delay is added to ensure it flows continuously
            // for cases such as configuration change
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GoalPreferencesUiState()
        )
    //Background color of goal
    private val _goalBgColor = mutableStateOf(Pink400)
    val goalBgColor : State<Color> = _goalBgColor

    //Current active goal
    private val _currActiveGoal = mutableStateOf(Goal(0,"",false,0,0,0f,0))
    val currActiveGoal : State<Goal> = _currActiveGoal
    //Deleted goal
    private val _deletedGoal = mutableStateOf(Goal(0,"",false,0,0,0f,0))
    val deletedGoal : State<Goal> = _deletedGoal

    //Boolean that indicates if goal editing is allowed
    //Goal configuration will be allowed only if goal is inactive [provided that the toggle in the settings in 'on']
    private val _configureGoal = mutableStateOf(false)
    val configureGoal : State<Boolean> = _configureGoal



    /**
     * Delete goal from the database
     */
    suspend fun deleteGoal(goal: Goal)
    {
        _deletedGoal.value = goal // Save it temporarily in case undo is invoked
        goalsRepository.deleteGoal(goal)
    }

    /**
     * Undo goal deletion
     */
    suspend fun  undoGoalDeletion()
    {
        viewModelScope.launch {
            goalsRepository.insertGoal(_deletedGoal.value)
        }
    }

    /**
    Change the active goal (disable current active goal and set the goal in the parameter as the new active goal)
     */
    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.O)
    fun changeActiveGoal(goal: Goal){
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val mCurrentDate = current.format(formatter)
        Log.d("Mytag",mCurrentDate)
        if (!goal.active) //Only if the goal is not already active can proceed to be activated
        {
            viewModelScope.launch {
                goalsRepository.updateGoal(_currActiveGoal.value.copy(active = false)) //Disable current active goal
                goalsRepository.updateGoal(goal.copy(
                    active = true,
                    steps = _currActiveGoal.value.steps,
                    progress = "%.2f".format((_currActiveGoal.value.steps.toFloat()/goal.target.toFloat())*100f).toFloat(),
                    selectedDate = mCurrentDate.toInt()))
                val oldActiveGoal = _currActiveGoal.value
                _currActiveGoal.value = goal //Save 'new' current active goal

                var historyItem = History(0,
                    _currActiveGoal.value.name,
                    "%.2f".format((_currActiveGoal.value.steps.toFloat()/goal.target.toFloat())*100f).toFloat()
                    ,_currActiveGoal.value.steps
                    ,_currActiveGoal.value.target,
                    mCurrentDate.toInt())

                    if (oldActiveGoal.selectedDate == mCurrentDate.toInt() && historyRepository.getAll().isNotEmpty())
                        historyRepository.deleteHistory(historyRepository.getAll().last())
                    historyRepository.insertHistory(historyItem)
//                    Log.d("","last item $oldActiveGoal")

            }
        }



    }
    /**
     * Find the current active goal by checking active value
     */
    fun findActiveGoal(goal: Goal) {
        if (goal.active)
            _currActiveGoal.value  = goal
    }

    /**
    Set background color of goals according to their active status
     */
    fun getGoalBgColor(goal: Goal): Color {
        if (goal.active) //if goal is active make its background color green
            _goalBgColor.value = Green400
        else
            _goalBgColor.value = Pink200
        return goalBgColor.value
    }

    /**
    Set the visibility of action buttons to True or False according to the goal's active status
     */
    fun actionButtonsVisibility (goal: Goal): Boolean {
        return !goal.active
    }

    /**
    Stores T or F according to the toggle state in the settings page for 'configure goals'
     */
    fun toggleGoalEditing(goalEditingEnabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveGoalEditingPreference(goalEditingEnabled)
        }
    }


    /**
     * Check whether a goal can be edited depending on its active status and the settings option 'configure goals'
     */
    fun allowEditing(goal: Goal) : Boolean
    {
        var res = false
        if ( actionButtonsVisibility(goal))
            if (preferencesState.value.goalEditingAllowed)
                res= true
        return res
    }

    fun updateGoal(goal: Goal, name: String, target: String)
    {
        viewModelScope.launch {
            goalsRepository.updateGoal(goal.copy(name = name, target = target.toInt()))
        }
    }


    /**
     * Updates the [goalUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(goalDetails: GoalDetails) {
       for (goal in homeUiState.value.goalList)
       {
//           if (goal.id == goalDetails.id)
       }

    }

    companion object {
        const val TIMEOUT_MILLIS = 5_000L
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as InventoryApplication)
                GoalViewModel(application.userPreferencesRepository,
                    goalsRepository = application.container.goalsRepository,
                    historyRepository = application.container.historyRepository)
            }
        }
    }
}

/**
 * Ui State for HomeScreen
 */
data class HomeUiState(var goalList: List<Goal> = listOf()
)

data class GoalPreferencesUiState(
    val goalEditingAllowed: Boolean = false)

