package com.example.fitandfine_project.ui.history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.fitandfine_project.data.Goal.Goal
import com.example.fitandfine_project.data.Goal.GoalsRepository
import com.example.fitandfine_project.data.History.History
import com.example.fitandfine_project.data.History.HistoryRepository
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class  HistoryEntryViewModel(private val historyRepository: HistoryRepository) : ViewModel() {

    /**
     * Holds current history ui state
     */
    var historyUiState by mutableStateOf(HistoryUiState())
        private set

    /**
     * Updates the [historyUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(historyDetails: HistoryDetails) {
        historyUiState =
            HistoryUiState(historyDetails = historyDetails, isEntryValid = validateInput(historyDetails))
    }

    /**
     * Inserts an [History] in the Room database
     */
    suspend fun saveHistory() {
        if (validateInput()) {
            historyRepository.insertHistory(historyUiState.historyDetails.toHistory())
        }
    }

    fun checkHistoryDateValidity(date:Int) : Boolean
    {
        val historyList = historyRepository.getAll()
        for(historyItem in historyList)
        {
            if(historyItem.date == date)
                return false
        }
        return true
    }

    private fun validateInput(uiState: HistoryDetails = historyUiState.historyDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && target.isNotBlank() && date.isNotBlank() && steps.isNotBlank()//Check if fields are empty
        }
    }
}

/**
 * Represents Ui State for a History.
 */
data class HistoryUiState(
    val historyDetails: HistoryDetails = HistoryDetails(),
    val isEntryValid: Boolean = false
)

data class HistoryDetails(
    val id : Int=0,
    var name: String="",
    var progress: String="0",
    var steps: String="",
    var target: String="",
    var date: String="01/01/2001" //  -> yyyy/mm/dd
)

/**
 * Extension function to convert [HistoryUiState] to [History]. If the value of [HistoryUiState.steps] is
 * not a valid [Int], then the price will be set to 0. Similarly if the value of
 * [HistoryiState] is not a valid [Int], then the quantity will be set to 0
 */
fun HistoryDetails.toHistory(): History = History(
    id = id,
    name = name,
    progress =progress.toFloatOrNull() ?:0f,
    steps= steps.toIntOrNull() ?:0,
    target = target.toIntOrNull() ?:0,
    date = date.toIntOrNull() ?:0
)

/**
 * Extension function to convert [History] to [HistoryUiState]
 */
fun History.toHistoryUiState(isEntryValid: Boolean = false): HistoryUiState = HistoryUiState(
    historyDetails = this.toHistoryDetails(),
    isEntryValid = isEntryValid
)

/**
 * Extension function to convert [History] to [HistoryDetails]
 */
fun History.toHistoryDetails(): HistoryDetails = HistoryDetails(
    id = id,
    name = name,
    progress =progress.toString(),
    steps= steps.toString(),
    target = target.toString(),
    date = date.toString()
)

