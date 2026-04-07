package com.redpanda.concepto.infrastructure.loging

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DebugState {
    val logs = mutableStateOf<List<String>>(emptyList())

    fun log(msg: String)
    {
        CoroutineScope(Dispatchers.Main).launch {
            logs.value = logs.value + msg
        }
    }
}