package com.fxlibs.app.subsidy.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_access")
data class UserAccess(
    @PrimaryKey val id:Int,
    val allowed:Int
)
