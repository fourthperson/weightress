package iak.wrc.data.source

import iak.wrc.data.entity.PastWeight

interface LocalDataSource {
    fun addWeight(weight: PastWeight)
    fun getPastWeights(): List<PastWeight>
}