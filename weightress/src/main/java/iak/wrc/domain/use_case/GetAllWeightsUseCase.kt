package iak.wrc.domain.use_case

import iak.wrc.domain.entity.Weight
import iak.wrc.domain.repository.WeightRepo

class GetAllWeightsUseCase(private val weightRepo: WeightRepo) {
    operator fun invoke(): List<Weight> {
        return weightRepo.getAll()
    }
}