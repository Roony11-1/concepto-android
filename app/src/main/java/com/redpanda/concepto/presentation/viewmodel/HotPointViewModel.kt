package com.redpanda.concepto.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redpanda.concepto.domain.interfaces.repository.IHotPointRepository
import com.redpanda.concepto.domain.model.HotPoint
import com.redpanda.concepto.infrastructure.location.GeofenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HotPointViewModel @Inject constructor(
    private val hotPointRepository: IHotPointRepository,
    private val geofenceManager: GeofenceManager) : ViewModel()
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
            val savedPoint = hotPointRepository.save(point)

            try
            {
                geofenceManager.addGeofence(savedPoint)
            }
            catch (e: SecurityException)
            {
                e.printStackTrace()
            }

            loadPoints()
        }
    }

    fun deletePointById(id: Long)
    {
        viewModelScope.launch {
            hotPointRepository.deleteById(id)

            geofenceManager.removeGeofence(id)

            loadPoints()
        }
    }
}