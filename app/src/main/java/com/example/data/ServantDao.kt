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

    @Query("SELECT * FROM traits ORDER BY name ASC")
    fun getAllTraits(): Flow<List<TraitEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrait(trait: TraitEntity)

    @Query("DELETE FROM traits WHERE id = :id")
    suspend fun deleteTraitById(id: Int)

    @Query("SELECT * FROM alignments ORDER BY name ASC")
    fun getAllAlignments(): Flow<List<AlignmentEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAlignment(alignment: AlignmentEntity)

    @Query("DELETE FROM alignments WHERE id = :id")
    suspend fun deleteAlignmentById(id: Int)
}
