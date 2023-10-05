package iak.wrc.presentation.ui.page

import android.app.Application
import android.text.format.DateFormat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.yml.charts.common.model.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import iak.wrc.domain.entity.Weight
import iak.wrc.domain.use_case.GetAllWeightsUseCase
import iak.wrc.domain.use_case.RecordWeightUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.Format
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
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
    val history: MutableLiveData<List<Weight>>
        get() = _history

    private var deviceDatePattern: String = ""
    private val chartDateFormat = "dd MMM yy"

    init {
        val using24Hr = DateFormat.is24HourFormat(application)
        val dateFormat: Format = DateFormat.getDateFormat(application)
        val timeFormat = if (using24Hr) "HH:mm:ss" else "h:mm:ss a"
        deviceDatePattern = (dateFormat as SimpleDateFormat).toLocalizedPattern()
        deviceDatePattern = "$deviceDatePattern $timeFormat"
    }

    fun loadHistory() {
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

    fun chartPoints(weights: List<Weight>): List<Point> {
        val list = arrayListOf<Point>()
        for (i in weights.indices) {
            list.add(
                Point(
                    x = i.toFloat(),
                    y = weights[i].weight
                )
            )
        }
        return list
    }

    fun chartDate(timestamp: Long): String {
        return formatDate(timestamp = timestamp, chartDateFormat)
    }

    fun listDate(timestamp: Long): String {
        return formatDate(timestamp, deviceDatePattern)
    }

    private fun formatDate(timestamp: Long, format: String): String {
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.timeInMillis = timestamp
        return DateFormat.format(format, calendar).toString()
    }
}