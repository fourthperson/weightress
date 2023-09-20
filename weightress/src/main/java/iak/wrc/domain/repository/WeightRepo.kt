package iak.wrc.domain.repository

import iak.wrc.domain.entity.Weight

interface WeightRepo {
    fun getAll(): List<Weight>

    fun record(weight: Weight)
}