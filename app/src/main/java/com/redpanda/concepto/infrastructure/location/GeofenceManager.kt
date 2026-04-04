package com.redpanda.concepto.infrastructure.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.redpanda.concepto.domain.model.HotPoint

class GeofenceManager(private val context: Context)
{
    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun addGeofence(point: HotPoint)
    {
        val geofence = Geofence.Builder()
            .setRequestId(point.id.toString())
            .setCircularRegion(point.lat, point.lon, 100f)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
            addOnSuccessListener {
                Log.d("GeofenceManager", "Geocerca agregada exitosamente")
            }
            addOnFailureListener {
                Log.e("GeofenceManager", "Error al agregar geocerca: ${it.message}")
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun removeGeofence(pointId: Long)
    {
        geofencingClient.removeGeofences(listOf(pointId.toString())).run {
            addOnSuccessListener {
                Log.d("GeofenceManager", "Geocerca $pointId eliminada exitosamente")
            }
            addOnFailureListener {
                Log.e("GeofenceManager", "Error al eliminar geocerca: ${it.message}")
            }
        }
    }
}