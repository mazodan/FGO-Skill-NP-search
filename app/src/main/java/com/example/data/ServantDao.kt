package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ServantDao {
    @Query("SELECT * FROM servants ORDER BY name ASC")
    fun getAllServants(): Flow<List<Servant>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServant(servant: Servant)

    @Query("DELETE FROM servants WHERE id = :id")
    suspend fun deleteServantById(id: Int)
}
