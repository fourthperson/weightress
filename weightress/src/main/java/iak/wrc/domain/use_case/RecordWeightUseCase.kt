package iak.wrc.domain.use_case

import iak.wrc.domain.entity.Weight
import iak.wrc.domain.repository.WeightRepo

class RecordWeightUseCase(private val weightRepo: WeightRepo) {
    operator fun invoke(weight: Weight) {
        weightRepo.record(weight)
    }
}