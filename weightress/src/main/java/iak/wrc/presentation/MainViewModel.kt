package iak.wrc.presentation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import iak.wrc.domain.repository.WeightRepo
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val weightRepo: WeightRepo) : ViewModel() {
}