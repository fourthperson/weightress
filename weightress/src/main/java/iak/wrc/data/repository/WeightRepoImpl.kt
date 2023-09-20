package iak.wrc.data.repository

import iak.wrc.data.entity.PastWeight
import iak.wrc.data.source.LocalDataSource
import iak.wrc.domain.entity.Weight
import iak.wrc.domain.repository.WeightRepo
import javax.inject.Inject

class WeightRepoImpl @Inject constructor(private val localDataSource: LocalDataSource) :
    WeightRepo {
    override fun getAll(): List<Weight> {
        val list = ArrayList<Weight>()
        localDataSource.getPastWeights().forEach { pastWeight ->
            list.add(
                Weight(
                    weight = pastWeight.weight,
                    notes = pastWeight.notes,
                    date = pastWeight.date
                )
            )
        }
        return list
    }

    override fun record(weight: Weight) {
        localDataSource.addWeight(
            PastWeight(
                id = 0,
                weight = weight.weight,
                notes = weight.notes,
                date = weight.date
            )
        )
    }
}