package com.redpanda.concepto.infrastructure.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.room.Room
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.redpanda.concepto.infrastructure.local.AppDbContext
import com.redpanda.concepto.infrastructure.local.entity.HotPointLogEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GeofenceBroadcastReceiver : BroadcastReceiver()
{
    override fun onReceive(context: Context, intent: Intent)
    {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent == null || geofencingEvent.hasError())
        {
            Log.e("GeofenceReceiver", "Error en el evento de geocerca")
            return
        }

        if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
        {
            val triggeringGeofences = geofencingEvent.triggeringGeofences ?: return

            val db = Room.databaseBuilder(
                context.applicationContext,
                AppDbContext::class.java,
                "app_db"
            )
                .fallbackToDestructiveMigration(true)
                .build()

            val pointDao = db.hotPointDao()
            val logDao = db.HotPointLogDao()

            CoroutineScope(Dispatchers.IO).launch {
                triggeringGeofences.forEach { geofence ->
                    val pointId = geofence.requestId.toIntOrNull() ?: return@forEach

                    val point = pointDao.getById(pointId)

                    if (point != null)
                    {
                        val newLog = HotPointLogEntity(
                            pointId = point.id,
                            description = point.description,
                            lat = point.lat,
                            lon = point.lon,
                            timestamp = System.currentTimeMillis()
                        )
                        logDao.insert(newLog)
                        Log.d("GeofenceReceiver", "Log registrado: ${point.description}")
                    }
                }
            }
        }
    }
}