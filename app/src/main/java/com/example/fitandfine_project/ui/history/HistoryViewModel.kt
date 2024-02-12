package com.example.fitandfine_project.ui.history

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fitandfine_project.InventoryApplication
import com.example.fitandfine_project.data.Goal.Goal
import com.example.fitandfine_project.data.History.History
import com.example.fitandfine_project.data.History.HistoryRepository
import com.example.fitandfine_project.data.UserPreferencesRepository
import com.example.fitandfine_project.ui.goals.GoalViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(private val userPreferencesRepository: UserPreferencesRepository,
                       private val historyRepository: HistoryRepository
)
    : ViewModel()
{


    val showConfirmationDialog = mutableStateOf(false)

    /**
     * Holds history ui state. The list of items are retrieved from [HistoryRepository] and mapped to
     * [HistoryUiState]
     */
    val historyScreenUiState: StateFlow<HistoryScreenUiState> =
        historyRepository.getAllHistoryStream().map { HistoryScreenUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(GoalViewModel.TIMEOUT_MILLIS),
                initialValue = HistoryScreenUiState()
            )
    val preferencesState: StateFlow<HistoryPreferencesUiState> =
        userPreferencesRepository.historyEditingAllowed.map {historyEditingAllowed ->
            HistoryPreferencesUiState(historyEditingAllowed)
        }.stateIn(
            scope = viewModelScope,
            // Flow is set to emits value for when app is on the foreground
            // 5 seconds stop delay is added to ensure it flows continuously
            // for cases such as configuration change
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HistoryPreferencesUiState()
        )
    /**
    Stores T or F according to the toggle state in the settings page for 'configure history'
     */
    fun toggleHistoryEditing(historyEditingEnabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveHistoryEditingPreference(historyEditingEnabled)
        }
    }
    /**
     * Check whether a goal can be edited depending on its active status and the settings option 'configure goals'
     */
    fun allowEditing() : Boolean
    {
        var res = false
        if (preferencesState.value.historyEditingEnabled)
            res= true
        return res
    }


    fun clearAllHistory()
    {
        historyRepository.clearAllHistory()
    }

    fun updateHistory(history: History,name: String, steps:Int)
    {
        viewModelScope.launch {
           historyRepository.updateHistory(history.copy(name= name, steps = steps))
        }
    }
    companion object {
        const val TIMEOUT_MILLIS = 5_000L
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as InventoryApplication)
                HistoryViewModel(application.userPreferencesRepository,
                    historyRepository = application.container.historyRepository)
            }
        }
    }

}
/**
 * Ui State for History
 */
data class HistoryScreenUiState(var historyList: List<History> = listOf()
)
data class HistoryPreferencesUiState(
    val historyEditingEnabled: Boolean = false)
