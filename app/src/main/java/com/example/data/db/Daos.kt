package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
  @Query("SELECT * FROM contract_settings WHERE id = :id LIMIT 1")
  fun getSettings(id: Long = 1L): Flow<ContractSettingsEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateSettings(settings: ContractSettingsEntity)
}

@Dao
interface OvertimeDao {
  @Query("SELECT * FROM overtime_records ORDER BY createdAt DESC")
  fun getAllRecords(): Flow<List<OvertimeRecordEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRecord(record: OvertimeRecordEntity)

  @Query("DELETE FROM overtime_records WHERE id = :id")
  suspend fun deleteRecordById(id: String)

  @Query("DELETE FROM overtime_records")
  suspend fun deleteAllRecords()

  @Transaction
  @Query("SELECT * FROM contract_settings WHERE id = :id")
  fun getContractWithRecords(id: Long = 1L): Flow<ContractWithRecords?>
}

@Dao
interface CategoryDao {
  @Query("SELECT * FROM categories")
  fun getAllCategories(): Flow<List<CategoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategory(category: CategoryEntity)
}
