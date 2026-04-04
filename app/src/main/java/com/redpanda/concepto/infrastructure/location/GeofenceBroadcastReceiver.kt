package com.redpanda.concepto.infrastructure.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GeofenceBroadcastReceiver : BroadcastReceiver()
{
    override fun onReceive(context: Context, intent: Intent)
    {
        val event = GeofencingEvent.fromIntent(intent) ?: return
        if (event.hasError()) return

        val transition = event.geofenceTransition
        if (transition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            transition == Geofence.GEOFENCE_TRANSITION_DWELL)
        {
            event.triggeringGeofences?.forEach { geofence ->
                val pointId = geofence.requestId.toLongOrNull() ?: return@forEach

                val serviceIntent = Intent(context, GeofenceTrackingService::class.java).apply {
                    putExtra("POINT_ID", pointId)
                }
                context.startForegroundService(serviceIntent)
            }
        }
    }
}