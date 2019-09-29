/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {

    //DONE (01) Declare Job() and cancel jobs in onCleared().
    private var viewModelJob = Job()

    //DONE (02) Define uiScope for coroutines.
    private val uiCoroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //DONE (03) Create a MutableLiveData variable tonight for one SleepNight.
    private var tonight = MutableLiveData<SleepNight?>()

    //DONE (04) Define a variable, nights. Then getAllNights() from the database
    //and assign to the nights variable.
    private val nights = database.getAllNights()

    //DONE (05) In an init block, initializeTonight(), and implement it to launch a coroutine
    //to getTonightFromDatabase().
    init {
        initializeTonight()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private fun initializeTonight() {
        uiCoroutineScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }

    //DONE (06) Implement getTonightFromDatabase()as a suspend function.
    private suspend fun getTonightFromDatabase(): SleepNight? {
        return withContext(Dispatchers.IO) {
            var night = database.getTonight()

            if (night?.endTimeMilli != night?.startTimeMilli) {
                night = null
            }

            night
        }
    }

    //DONE (07) Implement the click handler for the Start button, onStartTracking(), using
    //coroutines. Define the suspend function insert(), to insert a new night into the database.
    private fun onStartTracking() {
        uiCoroutineScope.launch {
            val newNight = SleepNight()
            insert(newNight)
            tonight.value = getTonightFromDatabase()
        }
    }

    private suspend fun insert(night: SleepNight) {
        return withContext(Dispatchers.IO) {
            database.insert(night)
        }
    }

    //DONE (08) Create onStopTracking() for the Stop button with an update() suspend function.
    fun onStopTracking() {
        uiCoroutineScope.launch {
            val oldNight = tonight.value ?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)
        }
    }

    private suspend fun update(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.update(night)
        }
    }

    //DONE (09) For the Clear button, created onClear() with a clear() suspend function.
    fun onClear() {
        uiCoroutineScope.launch {
            clear()
            tonight.value = null
        }
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }

    //DONE (12) Transform nights into a nightsString using formatNights().
    val nightsString = Transformations.map(nights) { nights ->
        formatNights(nights, application.resources)
    }

}

