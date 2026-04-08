package com.redpanda.concepto.presentation.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redpanda.concepto.domain.interfaces.repository.IHotPointRepository
import com.redpanda.concepto.domain.model.HotPoint
import com.redpanda.concepto.infrastructure.location.SimpleLocationTracker
import com.redpanda.concepto.infrastructure.loging.DebugState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HotPointViewModel @Inject constructor(
    private val hotPointRepository: IHotPointRepository) : ViewModel()
{
    var points = mutableStateOf<List<HotPoint>>(emptyList())
    private var cachedPoints: List<HotPoint> = emptyList()
    private var tracker: SimpleLocationTracker? = null
    private var targetPoint: HotPoint? = null

    fun loadPoints()
    {
        viewModelScope.launch {
            cachedPoints = hotPointRepository.getAll()
            points.value = cachedPoints
        }
    }

    fun addPoint(point: HotPoint)
    {
        viewModelScope.launch {
            DebugState.separator("ADD POINT")

            DebugState.info("Guardando punto: ${point.description}")

            val savedPoint = hotPointRepository.save(point)

            DebugState.info("Guardado OK id=${savedPoint.id}")

            loadPoints()
        }
    }

    fun deletePointById(id: Long)
    {
        viewModelScope.launch {
            DebugState.separator("DELETE POINT")

            DebugState.info("Eliminando punto id=$id")

            hotPointRepository.deleteById(id)

            DebugState.info("Eliminado OK id=$id")

            loadPoints()
        }
    }

    fun startTracking(context: Context)
    {
        if (tracker != null)
            return

        if (cachedPoints.isEmpty())
        {
            DebugState.error("No hay puntos cargados")
            return
        }

        targetPoint = cachedPoints.random()
        var hasReachedTarget = false

        tracker = SimpleLocationTracker(context) { lat, lon, accuracy ->

            DebugState.debug("Lat: $lat, Lon: $lon")
            DebugState.debug("Accuracy: $accuracy m")

            val point = targetPoint ?: return@SimpleLocationTracker

            DebugState.info("Target: ${point.description}")

                val results = FloatArray(1)

                android.location.Location.distanceBetween(
                    point.lat,
                    point.lon,
                    lat,
                    lon,
                    results
                )

                val distance = results[0]

                DebugState.debug("Distancia a ${point.description}: $distance m")

                if (distance <= 5f && !hasReachedTarget)
                {
                    hasReachedTarget = true

                    DebugState.info("Punto alcanzado: ${point.description}")
                }
        }

        tracker?.start()
    }

    fun stopTracking()
    {
        tracker?.stop()
        tracker = null
    }

    override fun onCleared()
    {
        super.onCleared()
        tracker?.stop()
    }
}