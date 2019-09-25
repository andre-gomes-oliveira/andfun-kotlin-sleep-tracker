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

package com.example.android.trackmysleepquality.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// DONE (01) Create an abstract class that extends RoomDatabase.
@Database(entities = [SleepNight::class], version = 1, exportSchema = false)
abstract class SleepDatabase : RoomDatabase() {

    // DONE (02) Declare an abstract value of type SleepDatabaseDao.
    abstract val sleepDatabaseDao: SleepDatabaseDao

    // DONE (03) Declare a companion object.
    companion object {
        // DONE (04) Declare a @Volatile INSTANCE variable.
        @Volatile
        private var INSTANCE: SleepDatabase? = null

        // DONE (05) Define a getInstance() method with a synchronized block.
        fun getInstance(context: Context): SleepDatabase {
            // DONE (06) Inside the synchronized block:
            // Check whether the database already exists,
            // and if it does not, use Room.databaseBuilder to create it.
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            SleepDatabase::class.java,
                            "sleep_history_database"
                    )
                            .fallbackToDestructiveMigration()
                            .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}


