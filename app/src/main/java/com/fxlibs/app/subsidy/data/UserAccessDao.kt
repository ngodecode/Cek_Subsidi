package com.fxlibs.app.subsidy.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserAccessDao {

    @Query("SELECT * FROM user_access LIMIT 1")
    fun getUserAccess() : UserAccess?

    @Insert
    fun insert(access: UserAccess)
}