package com.example.data

import kotlinx.coroutines.flow.Flow

class ServantRepository(private val servantDao: ServantDao) {
    val allServants: Flow<List<Servant>> = servantDao.getAllServants()

    suspend fun insert(servant: Servant) {
        servantDao.insertServant(servant)
    }

    suspend fun delete(id: Int) {
        servantDao.deleteServantById(id)
    }
}
