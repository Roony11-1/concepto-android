package com.redpanda.concepto.infrastructure.loging

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DebugState
{
    private const val MAX_LOGS = 100
    private var isEnabled = true
    private var showTimestamp = true
    enum class LogLevel(val priority: Int)
    {
        DEBUG(0),
        INFO(1),
        ERROR(2)
    }

    private var minLevel = LogLevel.DEBUG

    val logs = mutableStateListOf<String>()

    private val scope = CoroutineScope(Dispatchers.Main)

    fun configure(
        enabled: Boolean = true,
        showTime: Boolean = true,
        level: LogLevel = LogLevel.DEBUG
    )
    {
        isEnabled = enabled
        showTimestamp = showTime
        minLevel = level
    }

    private fun log(level: LogLevel, msg: String)
    {
        if (!isEnabled)
            return

        if (level.priority < minLevel.priority)
            return

        val time = if (showTimestamp)
        {
            SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                .format(Date())
        } else ""

        val formatted =
            if (showTimestamp)
            {
                "[$time][${level.name}] $msg"
            }
            else
            {
                "[${level.name}] $msg"
            }

        scope.launch {
            try
            {
                logs.add(formatted)
                if (logs.size > MAX_LOGS)
                    logs.removeAt(0)
            }
            catch (e: Exception)
            {
                android.util.Log.e("DebugState", "Error logging", e)
            }
        }
    }

    fun info(msg: String) = log(LogLevel.INFO, msg)
    fun error(msg: String) = log(LogLevel.ERROR, msg)
    fun debug(msg: String) = log(LogLevel.DEBUG, msg)

    fun separator(title: String)
    {
        log(LogLevel.INFO, "----- $title -----")
    }

    fun clear()
    {
        logs.clear()
    }
}