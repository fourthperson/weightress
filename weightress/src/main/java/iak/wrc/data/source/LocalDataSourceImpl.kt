package iak.wrc.data.source

import iak.wrc.data.entity.PastWeight
import iak.wrc.data.source.db.PastWeightDao
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(private val pastWeightDao: PastWeightDao) :
    LocalDataSource {
    override fun addWeight(weight: PastWeight) {
        pastWeightDao.insert(weight)
    }

    override fun getPastWeights(): List<PastWeight> {
        return pastWeightDao.getAll()
    }
}