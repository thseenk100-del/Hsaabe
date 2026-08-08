package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.OvertimeRecord
import com.example.data.UserContractSettings
import com.example.data.db.AppDatabase
import com.example.data.repository.OvertimeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OvertimeViewModel(application: Application) : AndroidViewModel(application) {
  private val repository: OvertimeRepository

  val settingsState: StateFlow<UserContractSettings>
  val recordsState: StateFlow<List<OvertimeRecord>>

  init {
    val db = AppDatabase.getDatabase(application)
    repository = OvertimeRepository(db)

    settingsState = repository.settingsFlow
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserContractSettings()
      )

    recordsState = repository.recordsFlow
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
      )

    viewModelScope.launch {
      val existing = repository.recordsFlow.first()
      if (existing.isEmpty()) {
        repository.seedInitialDataIfEmpty(firstTime = true)
      } else {
        repository.seedInitialDataIfEmpty(firstTime = false)
      }
    }
  }

  fun saveSettings(newSettings: UserContractSettings) {
    viewModelScope.launch {
      repository.saveSettings(newSettings)
    }
  }

  fun addRecord(record: OvertimeRecord) {
    viewModelScope.launch {
      repository.addRecord(record)
    }
  }

  fun deleteRecord(id: String) {
    viewModelScope.launch {
      repository.deleteRecord(id)
    }
  }

  fun resetData() {
    viewModelScope.launch {
      repository.clearAllRecords()
    }
  }

  fun exportBackup(onResult: (String) -> Unit) {
    viewModelScope.launch {
      val json = repository.exportBackupJson()
      onResult(json)
    }
  }

  fun importBackup(jsonStr: String, onResult: (Boolean) -> Unit) {
    viewModelScope.launch {
      val success = repository.importBackupJson(jsonStr)
      onResult(success)
    }
  }
}
