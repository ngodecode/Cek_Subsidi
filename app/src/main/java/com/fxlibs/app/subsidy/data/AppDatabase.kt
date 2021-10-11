package com.fxlibs.app.subsidy.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(UserAccess::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userAccessDao() : UserAccessDao
}