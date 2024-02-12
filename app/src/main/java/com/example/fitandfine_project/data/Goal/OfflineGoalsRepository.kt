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

import com.example.fitandfine_project.data.Goal.Goal
import com.example.fitandfine_project.data.Goal.GoalDao
import com.example.fitandfine_project.data.Goal.GoalsRepository
import kotlinx.coroutines.flow.Flow

class   OfflineGoalsRepository(private val goalDao: GoalDao) : GoalsRepository {
    override fun getAllGoalsStream(): Flow<List<Goal>> = goalDao.getAllGoals()
    override fun getGoalStream(id: Int): Flow<Goal?> = goalDao.getGoal(id)
    override suspend fun insertGoal(goal: Goal) = goalDao.insert(goal)
    override suspend fun deleteGoal(goal: Goal)= goalDao.delete(goal)
    override suspend fun updateGoal(goal: Goal) = goalDao.update(goal)
    override fun getActiveGoal(): Flow<Goal> =goalDao.getActiveGoal()
    override fun getActive(): Goal =goalDao.getActive()
    override fun resetGoals() = goalDao.resetGoals()

}
