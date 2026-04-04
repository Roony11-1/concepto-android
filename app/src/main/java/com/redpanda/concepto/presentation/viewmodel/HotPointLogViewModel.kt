package com.redpanda.concepto.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redpanda.concepto.domain.interfaces.repository.IHotPointLogRepository
import com.redpanda.concepto.domain.model.HotPointLog
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