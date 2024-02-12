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
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [History] from a given data source.
 */
interface HistoryRepository {
    /**
     * Retrieve all the history items from the the given data source.
     */
    fun getAllHistoryStream(): Flow<List<History>>

    /**
     * Retrieve a history from the given data source that matches with the [id].
     */
    fun getHistoryStream(id: Int): Flow<History?>

    /**
     * Insert history in the data source
     */
    suspend fun insertHistory(history: History)

    /**
     * Delete history from the data source
     */
    suspend fun deleteHistory(history: History)

    /**
     * Update history in the data source
     */
    suspend fun updateHistory(history: History)

    /**
     * Delete all history
     */
    fun clearAllHistory()


    fun getAll(): List<History>

}
