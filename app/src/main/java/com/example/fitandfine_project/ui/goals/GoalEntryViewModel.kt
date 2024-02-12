package com.example.fitandfine_project.ui.goals

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.fitandfine_project.data.Goal.Goal
import com.example.fitandfine_project.data.Goal.GoalsRepository
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


class  GoalEntryViewModel(private val goalsRepository: GoalsRepository) : ViewModel() {

    /**
     * Holds current goal ui state
     */
    var goalUiState by mutableStateOf(GoalUiState())
        private set

    /**
     * Updates the [goalUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(goalDetails: GoalDetails) {
        goalUiState =
            GoalUiState(goalDetails = goalDetails, isEntryValid = validateInput(goalDetails))
    }

    /**
     * Inserts an [Goal] in the Room database
     */
    suspend fun saveGoal() {
        if (validateInput()) {
           goalsRepository.insertGoal(goalUiState.goalDetails.toGoal())
        }
    }

    private fun validateInput(uiState: GoalDetails = goalUiState.goalDetails): Boolean {
        return with(uiState) {

            val p: Pattern = Pattern.compile("[^A-Za-z0-9 ]", Pattern.CASE_INSENSITIVE)
            val m: Matcher = p.matcher(name)
            val b: Boolean = m.find()
           if (b) //Remove any special characters from the input
           {
               val re = Regex("[^A-Za-z0-9 ]")
               name = re.replace(name, "")
           }

            val p2: Pattern = Pattern.compile("[^A-Za-z0-9 ]", Pattern.CASE_INSENSITIVE)
            val m2: Matcher = p2.matcher(target)
            val b2: Boolean = m2.find()
            if (b2) //Replace any special characters
            {
                val re = Regex("[0-9]")
                target = re.replace(target, "")
            }
            name.isNotBlank() && target.isNotBlank()//Check if fields are empty
        }
    }
}

/**
 * Represents Ui State for a Goal.
 */
data class GoalUiState(
    val goalDetails: GoalDetails = GoalDetails(),
    val isEntryValid: Boolean = false
)

data class GoalDetails(
    val id : Int=0,
    var name: String="",
    var active: Boolean = false,
    var progress: String="0",
    var steps: String="0",
    var target: String="",
    var selectedDate: String="0"
)

/**
 * Extension function to convert [GoalUiState] to [Goal]. If the value of [GoalUiState.steps] is
 * not a valid [Int], then the price will be set to 0. Similarly if the value of
 * [GoalUiState] is not a valid [Int], then the quantity will be set to 0
 */
fun GoalDetails.toGoal(): Goal = Goal(
    id = id,
    name = name,
    active= active,
    progress =progress.toFloatOrNull() ?:0f,
    steps= steps.toIntOrNull() ?:0,
    target = target.toIntOrNull() ?:0,
    selectedDate =  selectedDate.toIntOrNull() ?:0
)




/**
 * Extension function to convert [Goal] to [GoalUiState]
 */
fun Goal.toGoalUiState(isEntryValid: Boolean = false): GoalUiState = GoalUiState(
    goalDetails = this.toGoalDetails(),
    isEntryValid = isEntryValid
)

/**
 * Extension function to convert [Goal] to [GoalDetails]
 */
fun Goal.toGoalDetails(): GoalDetails = GoalDetails(
    id = id,
    name = name,
    active= active,
    progress =progress.toString(),
    steps= steps.toString(),
    target = target.toString(),
    selectedDate = selectedDate.toString()
)

