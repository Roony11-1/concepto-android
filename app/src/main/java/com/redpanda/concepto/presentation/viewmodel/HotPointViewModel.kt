package com.redpanda.concepto.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redpanda.concepto.domain.interfaces.repository.IHotPointRepository
import com.redpanda.concepto.domain.model.HotPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HotPointViewModel @Inject constructor(
    private val hotPointRepository: IHotPointRepository) : ViewModel()
{
    var points = mutableStateOf<List<HotPoint>>(emptyList())

    fun loadPoints()
    {
        viewModelScope.launch {
            val data = hotPointRepository.getAll()
            points.value = data
        }
    }

    fun addPoint(point: HotPoint)
    {
        viewModelScope.launch {
            val savedPoint = hotPointRepository.save(point)

            loadPoints()
        }
    }

    fun deletePointById(id: Long)
    {
        viewModelScope.launch {
            hotPointRepository.deleteById(id)

            loadPoints()
        }
    }
}