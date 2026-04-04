package com.redpanda.concepto.infrastructure.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PrecisionTracker(
    private val context: Context,
    private val pointLat: Double,
    private val pointLon: Double,
    private val pointId: Long,
    private val logAction: (Long) -> Unit)
{
    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private var locationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun startTracking()
    {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L)
            .setMinUpdateDistanceMeters(1f)
            .build()

        locationCallback = object : LocationCallback()
        {
            override fun onLocationResult(locationResult: LocationResult)
            {
                for (location in locationResult.locations)
                {
                    val distance = calculateDistance(pointLat, pointLon, location.latitude, location.longitude)
                    Log.d("PrecisionTracker", "Acercándose... Distancia actual: $distance m")

                    if (distance <= 5.0)
                    {
                        Log.d("PrecisionTracker", "¡Punto alcanzado! Guardando log y apagando GPS.")

                        CoroutineScope(Dispatchers.IO).launch {
                            logAction(pointId)
                        }

                        stopTracking()
                        return
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            context.mainLooper)
    }

    fun stopTracking()
    {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
            Log.d("PrecisionTracker", "Tracker apagado.")
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float
    {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }
}