package com.example.data.repository

import com.example.data.OvertimeRecord
import com.example.data.OvertimeType
import com.example.data.UserContractSettings
import com.example.data.db.AppDatabase
import com.example.data.db.ContractSettingsEntity
import com.example.data.db.OvertimeRecordEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class OvertimeRepository(private val db: AppDatabase) {
  private val settingsDao = db.settingsDao()
  private val overtimeDao = db.overtimeDao()

  val settingsFlow: Flow<UserContractSettings> = settingsDao.getSettings()
    .map { entity ->
      entity?.toDomain() ?: UserContractSettings()
    }

  val recordsFlow: Flow<List<OvertimeRecord>> = overtimeDao.getAllRecords()
    .map { list ->
      list.map { it.toDomain() }
    }

  suspend fun saveSettings(settings: UserContractSettings) {
    val entity = ContractSettingsEntity.fromDomain(settings)
    settingsDao.insertOrUpdateSettings(entity)
  }

  suspend fun addRecord(record: OvertimeRecord) {
    // Ensure settings entry exists so FK constraint is satisfied
    val defaultSettings = ContractSettingsEntity.fromDomain(UserContractSettings())
    settingsDao.insertOrUpdateSettings(defaultSettings)

    val entity = OvertimeRecordEntity.fromDomain(record)
    overtimeDao.insertRecord(entity)
  }

  suspend fun deleteRecord(id: String) {
    overtimeDao.deleteRecordById(id)
  }

  suspend fun clearAllRecords() {
    overtimeDao.deleteAllRecords()
  }

  suspend fun exportBackupJson(): String {
    val settings = settingsFlow.firstOrNull() ?: UserContractSettings()
    val records = recordsFlow.firstOrNull() ?: emptyList()

    val root = JSONObject()
    root.put("version", 1)

    val sObj = JSONObject()
    sObj.put("baseSalary", settings.baseSalary)
    sObj.put("dailyHours", settings.dailyHours)
    sObj.put("workDaysPerMonth", settings.workDaysPerMonth)
    sObj.put("monthlyHours", settings.monthlyHours)
    sObj.put("currency", settings.currency)
    sObj.put("workdayMultiplier", settings.workdayMultiplier)
    sObj.put("weekendMultiplier", settings.weekendMultiplier)
    sObj.put("themeMode", settings.themeMode)
    root.put("settings", sObj)

    val arr = JSONArray()
    records.forEach { r ->
      val rObj = JSONObject()
      rObj.put("id", r.id)
      rObj.put("date", r.date)
      rObj.put("hours", r.hours)
      rObj.put("type", r.type.name)
      rObj.put("multiplier", r.multiplier)
      rObj.put("hourlyRate", r.hourlyRate)
      rObj.put("notes", r.notes)
      arr.put(rObj)
    }
    root.put("records", arr)

    return root.toString(2)
  }

  suspend fun importBackupJson(jsonStr: String): Boolean {
    return try {
      val root = JSONObject(jsonStr)
      if (root.has("settings")) {
        val sObj = root.getJSONObject("settings")
        val newSettings = UserContractSettings(
          baseSalary = sObj.optDouble("baseSalary", 6000.0),
          dailyHours = sObj.optInt("dailyHours", 8),
          workDaysPerMonth = sObj.optInt("workDaysPerMonth", 20),
          monthlyHours = sObj.optInt("monthlyHours", 160),
          currency = sObj.optString("currency", "ر.س"),
          workdayMultiplier = sObj.optDouble("workdayMultiplier", 1.5),
          weekendMultiplier = sObj.optDouble("weekendMultiplier", 2.0),
          themeMode = sObj.optString("themeMode", "SYSTEM")
        )
        saveSettings(newSettings)
      }

      if (root.has("records")) {
        val recordsArr = root.getJSONArray("records")
        for (i in 0 until recordsArr.length()) {
          val rObj = recordsArr.getJSONObject(i)
          val typeStr = rObj.optString("type", "WORKDAY")
          val type = try { OvertimeType.valueOf(typeStr) } catch(e: Exception) { OvertimeType.WORKDAY }
          val record = OvertimeRecord(
            id = rObj.optString("id", UUID.randomUUID().toString()),
            date = rObj.optString("date", "2026-08-07"),
            hours = rObj.optDouble("hours", 0.0),
            type = type,
            multiplier = rObj.optDouble("multiplier", 1.5),
            hourlyRate = rObj.optDouble("hourlyRate", 37.5),
            notes = rObj.optString("notes", "")
          )
          val entity = OvertimeRecordEntity.fromDomain(record)
          overtimeDao.insertRecord(entity)
        }
      }
      true
    } catch (e: Exception) {
      false
    }
  }

  suspend fun seedInitialDataIfEmpty(firstTime: Boolean) {
    val defaultSettings = ContractSettingsEntity(
      id = 1L,
      baseSalary = 6000.0,
      dailyHours = 8,
      workDaysPerMonth = 20,
      monthlyHours = 160,
      currency = "ر.س",
      workdayMultiplier = 1.5,
      weekendMultiplier = 2.0,
      isSetupCompleted = true,
      themeMode = "SYSTEM"
    )
    settingsDao.insertOrUpdateSettings(defaultSettings)

    if (firstTime) {
      val sampleRecords = listOf(
        OvertimeRecord(
          date = "2026-08-06",
          hours = 3.0,
          type = OvertimeType.WORKDAY,
          multiplier = 1.5,
          hourlyRate = 37.5,
          notes = "تغطية ساعات بعد الدوام"
        ),
        OvertimeRecord(
          date = "2026-08-01",
          hours = 5.0,
          type = OvertimeType.WEEKEND,
          multiplier = 2.0,
          hourlyRate = 37.5,
          notes = "عمل يوم الجمعة عطلة"
        ),
        OvertimeRecord(
          date = "2026-07-28",
          hours = 2.0,
          type = OvertimeType.WORKDAY,
          multiplier = 1.5,
          hourlyRate = 37.5,
          notes = "إنهاء تسليم مشروع"
        )
      )

      sampleRecords.forEach { record ->
        overtimeDao.insertRecord(OvertimeRecordEntity.fromDomain(record))
      }
    }
  }
}

