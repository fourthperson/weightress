package iak.wrc.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import iak.wrc.domain.entity.Weight
import iak.wrc.domain.use_case.RecordWeightUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val recordWeightUseCase: RecordWeightUseCase) :
    ViewModel() {
    private val _alertMessage = MutableLiveData<String>()
    val alertMessage: MutableLiveData<String>
        get() = _alertMessage

    private val _showAlert = MutableLiveData<Boolean>()
    val showAlert: MutableLiveData<Boolean>
        get() = _showAlert

    fun validate(kgs: String, notes: String) {
        val weight = kgs.toFloatOrNull()
        if (weight == null) {
            alertMessage.postValue("Enter a valid weight")
            println("Invalid weight")
            return
        }
        recordWeight(weight, notes.trim())
    }

    private fun recordWeight(weight: Float, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val newWeight = Weight(
                weight = weight,
                notes = notes,
                date = System.currentTimeMillis()
            )
            recordWeightUseCase.invoke(newWeight)
        }

    }
}