package com.example.data

import kotlinx.coroutines.flow.Flow

class ServantRepository(private val servantDao: ServantDao) {
    val allServants: Flow<List<Servant>> = servantDao.getAllServants()
    val allTraits: Flow<List<TraitEntity>> = servantDao.getAllTraits()
    val allAlignments: Flow<List<AlignmentEntity>> = servantDao.getAllAlignments()

    suspend fun insert(servant: Servant) {
        servantDao.insertServant(servant)
    }

    suspend fun update(servant: Servant) {
        servantDao.updateServant(servant)
    }

    suspend fun delete(id: Int) {
        servantDao.deleteServantById(id)
    }

    suspend fun insertTrait(trait: TraitEntity) {
        servantDao.insertTrait(trait)
    }

    suspend fun updateTrait(trait: TraitEntity) {
        servantDao.updateTrait(trait)
    }

    suspend fun deleteTrait(id: Int) {
        servantDao.deleteTraitById(id)
    }

    suspend fun insertAlignment(alignment: AlignmentEntity) {
        servantDao.insertAlignment(alignment)
    }

    suspend fun updateAlignment(alignment: AlignmentEntity) {
        servantDao.updateAlignment(alignment)
    }

    suspend fun deleteAlignment(id: Int) {
        servantDao.deleteAlignmentById(id)
    }
}
