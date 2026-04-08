package com.redpanda.concepto.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redpanda.concepto.domain.interfaces.repository.IHotPointLogRepository
import com.redpanda.concepto.domain.model.HotPointLog
import com.redpanda.concepto.infrastructure.loging.DebugState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HotPointLogViewModel @Inject constructor(
    private val hotPointLogRepository: IHotPointLogRepository) : ViewModel()
{
    var logs = mutableStateOf<List<HotPointLog>>(emptyList())

    fun loadLogs()
    {
        viewModelScope.launch {
            DebugState.separator("LOAD LOGS")

            val data = hotPointLogRepository.findAll()

            DebugState.info("Logs cargados: ${data.size}")

            data.forEach {
                DebugState.debug("Log -> pointId=${it.pointId}, desc=${it.description}")
            }

            logs.value = data
        }
    }

    fun addLog(log: HotPointLog)
    {
        viewModelScope.launch {
            DebugState.separator("ADD LOG")

            DebugState.info("Guardando log para pointId=${log.pointId}")

            hotPointLogRepository.save(log)

            DebugState.info("Log guardado OK")

            loadLogs()
        }
    }
}