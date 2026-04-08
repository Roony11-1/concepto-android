package com.redpanda.concepto.infrastructure.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.redpanda.concepto.infrastructure.loging.DebugState

class SimpleLocationTracker(
    private val context: Context,
    private val onLocation: (lat: Double, lon: Double, accuracy: Float) -> Unit)
{
    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var locationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun start()
    {
        DebugState.separator("START TRACKING")

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 2000L)
                .setMinUpdateDistanceMeters(1f)
                .build()

        locationCallback = object : LocationCallback()
        {
            override fun onLocationResult(result: LocationResult)
            {
                val loc = result.lastLocation ?: return

                onLocation(loc.latitude, loc.longitude, loc.accuracy)
            }
        }

        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback!!,
            context.mainLooper)
    }

    fun stop()
    {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
            DebugState.separator("STOP TRACKING")
        }
    }
}