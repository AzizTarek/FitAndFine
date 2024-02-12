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

package com.example.fitandfine_project.data.History

import com.example.fitandfine_project.data.Goal.Goal
import com.example.fitandfine_project.data.Goal.GoalDao
import com.example.fitandfine_project.data.Goal.GoalsRepository
import kotlinx.coroutines.flow.Flow

class   OfflineHistoryRepository(private val historyDao: HistoryDao) : HistoryRepository {
    override fun getAllHistoryStream(): Flow<List<History>> =historyDao.getAllHistory()

    override fun getHistoryStream(id: Int): Flow<History?> =historyDao.getHistory(id)

    override suspend fun insertHistory(history: History) =historyDao.insert(history = history)

    override suspend fun deleteHistory(history: History) =historyDao.delete(history)

    override suspend fun updateHistory(history: History) =historyDao.update(history)

    override fun clearAllHistory()=historyDao.clearAllHistory()
    override fun getAll(): List<History> =historyDao.getAll()


}
