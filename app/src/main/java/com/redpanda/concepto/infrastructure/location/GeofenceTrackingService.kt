package com.redpanda.concepto.infrastructure.location

import android.annotation.SuppressLint
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.redpanda.concepto.R
import com.redpanda.concepto.domain.interfaces.repository.IHotPointLogRepository
import com.redpanda.concepto.domain.interfaces.repository.IHotPointRepository
import com.redpanda.concepto.domain.model.HotPoint
import com.redpanda.concepto.domain.model.HotPointLog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GeofenceTrackingService : LifecycleService()
{
    @Inject lateinit var pointRepo: IHotPointRepository
    @Inject lateinit var logRepo: IHotPointLogRepository

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate()
    {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        super.onStartCommand(intent, flags, startId)

        val pointId = intent?.getLongExtra("POINT_ID", -1L) ?: -1L
        if (pointId != -1L)
        {
            startForegroundServiceNotification()
            startHighAccuracyTracking(pointId)
        }

        return START_NOT_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun startHighAccuracyTracking(pointId: Long)
    {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L) // Cada 3 seg
            .setMinUpdateDistanceMeters(1f)
            .build()

        val callback = object : LocationCallback()
        {
            override fun onLocationResult(result: LocationResult)
            {
                scope.launch {
                    val point = pointRepo.getById(pointId) ?: return@launch
                    val currentLoc = result.lastLocation ?: return@launch

                    val distance = calculateDistance(point.lat, point.lon, currentLoc.latitude, currentLoc.longitude)

                    if (distance <= 5.0)
                    {
                        saveLog(point)
                        stopSelf()
                    }
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(request, callback, mainLooper)
    }

    private suspend fun saveLog(point: HotPoint)
    {
        logRepo.save(HotPointLog(
            id = 0L,
            pointId = point.id,
            description = point.description,
            lat = point.lat,
            lon = point.lon,
            timestamp = System.currentTimeMillis()))
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float
    {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    private fun startForegroundServiceNotification()
    {
        val notification = NotificationCompat.Builder(this, "geofence_channel")
            .setContentTitle("Validando ubicación")
            .setContentText("Calculando distancia de precisión...")
            .build()

        startForeground(1, notification)
    }
}