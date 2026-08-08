package com.example.data.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.data.OvertimeRecord
import com.example.data.OvertimeType
import com.example.data.UserContractSettings

@Entity(tableName = "contract_settings")
data class ContractSettingsEntity(
  @PrimaryKey val id: Long = 1L,
  val baseSalary: Double = 6000.0,
  val dailyHours: Int = 8,
  val workDaysPerMonth: Int = 20,
  val monthlyHours: Int = 160,
  val currency: String = "ر.س",
  val workdayMultiplier: Double = 1.5,
  val weekendMultiplier: Double = 2.0,
  val isSetupCompleted: Boolean = true,
  val themeMode: String = "SYSTEM"
) {
  fun toDomain(): UserContractSettings {
    return UserContractSettings(
      baseSalary = baseSalary,
      dailyHours = dailyHours,
      workDaysPerMonth = workDaysPerMonth,
      monthlyHours = monthlyHours,
      currency = currency,
      workdayMultiplier = workdayMultiplier,
      weekendMultiplier = weekendMultiplier,
      isSetupCompleted = isSetupCompleted,
      themeMode = themeMode
    )
  }

  companion object {
    fun fromDomain(domain: UserContractSettings, id: Long = 1L): ContractSettingsEntity {
      return ContractSettingsEntity(
        id = id,
        baseSalary = domain.baseSalary,
        dailyHours = domain.dailyHours,
        workDaysPerMonth = domain.workDaysPerMonth,
        monthlyHours = domain.monthlyHours,
        currency = domain.currency,
        workdayMultiplier = domain.workdayMultiplier,
        weekendMultiplier = domain.weekendMultiplier,
        isSetupCompleted = domain.isSetupCompleted,
        themeMode = domain.themeMode
      )
    }
  }
}

@Entity(tableName = "categories")
data class CategoryEntity(
  @PrimaryKey val categoryId: String,
  val name: String,
  val badgeColorHex: Long
)

@Entity(
  tableName = "overtime_records",
  foreignKeys = [
    ForeignKey(
      entity = ContractSettingsEntity::class,
      parentColumns = ["id"],
      childColumns = ["contractSettingsId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index("contractSettingsId")]
)
data class OvertimeRecordEntity(
  @PrimaryKey val id: String,
  val contractSettingsId: Long = 1L,
  val date: String,
  val hours: Double,
  val type: OvertimeType,
  val multiplier: Double,
  val hourlyRate: Double,
  val notes: String = "",
  val createdAt: Long = System.currentTimeMillis()
) {
  fun toDomain(): OvertimeRecord {
    return OvertimeRecord(
      id = id,
      date = date,
      hours = hours,
      type = type,
      multiplier = multiplier,
      hourlyRate = hourlyRate,
      notes = notes
    )
  }

  companion object {
    fun fromDomain(domain: OvertimeRecord, contractSettingsId: Long = 1L): OvertimeRecordEntity {
      return OvertimeRecordEntity(
        id = domain.id,
        contractSettingsId = contractSettingsId,
        date = domain.date,
        hours = domain.hours,
        type = domain.type,
        multiplier = domain.multiplier,
        hourlyRate = domain.hourlyRate,
        notes = domain.notes
      )
    }
  }
}

data class ContractWithRecords(
  @Embedded val contract: ContractSettingsEntity,
  @Relation(
    parentColumn = "id",
    entityColumn = "contractSettingsId"
  )
  val records: List<OvertimeRecordEntity>
)
