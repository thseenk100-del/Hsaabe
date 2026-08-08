package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OvertimeRecord
import com.example.data.OvertimeType
import com.example.data.UserContractSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOvertimeScreen(
  settings: UserContractSettings,
  onAddRecord: (OvertimeRecord) -> Unit,
  onNavigateBack: () -> Unit
) {
  var hoursState by remember { mutableStateOf("2.5") }
  var dateState by remember { mutableStateOf("2026-08-07") }
  var selectedType by remember { mutableStateOf(OvertimeType.WORKDAY) }
  var customMultiplierState by remember { mutableDoubleStateOf(selectedType.defaultMultiplier) }
  var notesState by remember { mutableStateOf("") }

  val hoursDouble = hoursState.toDoubleOrNull() ?: 0.0
  val calculatedTotal = hoursDouble * settings.standardHourlyRate * customMultiplierState

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "إضافة ساعات إضافية جديدة",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
          )
        },
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.primaryContainer,
          titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
      )
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .background(MaterialTheme.colorScheme.background)
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Date Picker Input
      OutlinedTextField(
        value = dateState,
        onValueChange = { dateState = it },
        label = { Text("التاريخ (YYYY-MM-DD)") },
        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
      )

      // Overtime Hours Input
      OutlinedTextField(
        value = hoursState,
        onValueChange = { hoursState = it },
        label = { Text("عدد الساعات الإضافية (مثلاً 1.5 أو 3)") },
        leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
      )

      // Overtime Type Selector Cards
      Text(
        text = "نوع اليوم / المناسبة:",
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        modifier = Modifier.padding(top = 4.dp)
      )

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OvertimeType.values().forEach { type ->
          val isSelected = selectedType == type
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp),
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                selectedType = type
                customMultiplierState = when (type) {
                  OvertimeType.WORKDAY -> settings.workdayMultiplier
                  OvertimeType.WEEKEND -> settings.weekendMultiplier
                  OvertimeType.HOLIDAY -> 2.0
                }
              }
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = type.label,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "المضاعف المعيارى: %.1fx".format(type.defaultMultiplier),
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.outline
                )
              }

              if (isSelected) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary
                )
              }
            }
          }
        }
      }

      // Notes Field
      OutlinedTextField(
        value = notesState,
        onValueChange = { notesState = it },
        label = { Text("ملاحظات (اختياري - مثلاً: تغطية وردية مسائية)") },
        leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
      )

      // Live Calculated Amount Box
      Card(
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "المبلغ المستحق لهذه العملية:",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSecondaryContainer
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "+%.2f %s".format(calculatedTotal, settings.currency),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "تفاصيل الحساب: %.1f ساعة × %.2f %s (سعر الساعة) × %.1fx (المضاعف)".format(
              hoursDouble,
              settings.standardHourlyRate,
              settings.currency,
              customMultiplierState
            ),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Submit Button
      Button(
        onClick = {
          if (hoursDouble > 0) {
            val record = OvertimeRecord(
              date = dateState.ifEmpty { "2026-08-07" },
              hours = hoursDouble,
              type = selectedType,
              multiplier = customMultiplierState,
              hourlyRate = settings.standardHourlyRate,
              notes = notesState
            )
            onAddRecord(record)
            onNavigateBack()
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp),
        shape = RoundedCornerShape(14.dp)
      ) {
        Icon(Icons.Default.Check, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "حفظ وإضافة الساعات",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}
