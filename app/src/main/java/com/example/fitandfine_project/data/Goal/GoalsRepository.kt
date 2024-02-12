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

package com.example.fitandfine_project.data.Goal

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Goal] from a given data source.
 */
interface GoalsRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllGoalsStream(): Flow<List<Goal>>

    /**
     * Retrieve a goal from the given data source that matches with the [id].
     */
    fun getGoalStream(id: Int): Flow<Goal?>

    /**
     * Insert goal in the data source
     */
    suspend fun insertGoal(goal: Goal)

    /**
     * Delete goal from the data source
     */
    suspend fun deleteGoal(goal: Goal)

    /**
     * Update goal in the data source
     */
    suspend fun updateGoal(goal: Goal)

    /**
     * Returns the current active goal
     */
    fun getActiveGoal(): Flow<Goal>

    /**
     *  Returns the current active goal
     */
    fun getActive(): Goal

    fun resetGoals()

}
