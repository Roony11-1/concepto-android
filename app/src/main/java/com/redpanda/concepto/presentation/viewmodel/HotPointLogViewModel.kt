package com.redpanda.concepto.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redpanda.concepto.domain.interfaces.repository.IHotPointLogRepository
import com.redpanda.concepto.domain.model.HotPointLog
import kotlinx.coroutines.launch

class HotPointLogViewModel(
    private val hotPointLogRepository: IHotPointLogRepository) : ViewModel()
{
    var logs = mutableStateOf<List<HotPointLog>>(emptyList())

    fun loadLogs()
    {
        viewModelScope.launch {
            logs.value = hotPointLogRepository.findAll()
        }
    }

    fun addLog(log: HotPointLog)
    {
        viewModelScope.launch {
            hotPointLogRepository.save(log)
            loadLogs()
        }
    }
}