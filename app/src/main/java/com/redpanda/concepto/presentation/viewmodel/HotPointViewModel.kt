package com.redpanda.concepto.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redpanda.concepto.domain.interfaces.repository.IHotPointRepository
import com.redpanda.concepto.domain.model.HotPoint
import kotlinx.coroutines.launch

class HotPointViewModel(
    private val hotPointRepository: IHotPointRepository) : ViewModel()
{
    var points = mutableStateOf<List<HotPoint>>(emptyList())

    fun loadPoints()
    {
        viewModelScope.launch {
            points.value = hotPointRepository.getAll()
        }
    }

    fun addPoint(point: HotPoint)
    {
        viewModelScope.launch {
            hotPointRepository.save(point)
            loadPoints()
        }
    }

    fun deletePointById(id: Int)
    {
        viewModelScope.launch {
            hotPointRepository.deleteById(id)
            loadPoints()
        }
    }
}