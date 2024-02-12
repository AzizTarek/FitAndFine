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

package com.example.fitandfine_project.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fitandfine_project.InventoryApplication
import com.example.fitandfine_project.ui.goals.GoalEntryViewModel
import com.example.fitandfine_project.ui.goals.GoalViewModel
import com.example.fitandfine_project.ui.history.HistoryEntryViewModel
import com.example.fitandfine_project.ui.history.HistoryViewModel
import com.example.fitandfine_project.ui.home.DayStatusViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Inventory app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {

        // Initializer for ItemEntryViewModel
        initializer {
            GoalEntryViewModel(inventoryApplication().container.goalsRepository)
        }
        // Initializer for HistoryEntryViewModel
        initializer {
            HistoryEntryViewModel(inventoryApplication().container.historyRepository)
        }
        // Initializer for DayStatusViewModel
        initializer {
           DayStatusViewModel(inventoryApplication().container.goalsRepository,inventoryApplication().container.historyRepository )
        }

//        // Initializer for GoalViewModel
        initializer {
            GoalViewModel(inventoryApplication().userPreferencesRepository,inventoryApplication().container.goalsRepository,inventoryApplication().container.historyRepository )
        }

//        // Initializer for GoalViewModel
        initializer {
           HistoryViewModel(inventoryApplication().userPreferencesRepository,inventoryApplication().container.historyRepository )
        }
    }



}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [InventoryApplication].
 */
fun CreationExtras.inventoryApplication(): InventoryApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as InventoryApplication)
