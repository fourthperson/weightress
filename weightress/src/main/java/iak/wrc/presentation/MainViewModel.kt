package iak.wrc.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import iak.wrc.domain.entity.Weight
import iak.wrc.domain.use_case.GetAllWeightsUseCase
import iak.wrc.domain.use_case.RecordWeightUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val recordWeightUseCase: RecordWeightUseCase,
    private val getAllWeightsUseCase: GetAllWeightsUseCase
) :
    ViewModel() {
    private val _alertMessage = MutableLiveData<String>()
    val alertMessage: MutableLiveData<String>
        get() = _alertMessage

    private val _showAlert = MutableLiveData<Boolean>()
    val showAlert: MutableLiveData<Boolean>
        get() = _showAlert

    private val _history = MutableLiveData<List<Weight>>()
    val history: LiveData<List<Weight>>
        get() = _history

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllWeightsUseCase.invoke().let { results ->
                _history.postValue(results)
            }
        }
    }

    fun validate(kgs: String): Boolean {
        val weight = kgs.toFloatOrNull()
        if (weight == null) {
            alertMessage.postValue("Enter a valid weight")
            showAlert.postValue(true)
            Timber.i("Validation failed!")
            return false
        }
        Timber.i("Validation passed!")
        return true
    }

    fun recordWeight(kgs: String, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val newWeight = Weight(
                weight = kgs.toFloatOrNull() ?: 0f,
                notes = notes,
                date = System.currentTimeMillis()
            )
            recordWeightUseCase.invoke(newWeight)
        }
        loadHistory()
        Timber.i("Weight recorded!")
    }
}