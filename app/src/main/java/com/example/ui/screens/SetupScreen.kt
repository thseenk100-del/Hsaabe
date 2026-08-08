package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SupportedCurrencies
import com.example.data.UserContractSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupScreen(
  currentSettings: UserContractSettings,
  onSaveSettings: (UserContractSettings) -> Unit
) {
  var baseSalaryState by remember { mutableStateOf(currentSettings.baseSalary.toInt().toString()) }
  var dailyHoursState by remember { mutableStateOf(currentSettings.dailyHours.toString()) }
  var workDaysState by remember { mutableStateOf(currentSettings.workDaysPerMonth.toString()) }
  var currencyState by remember { mutableStateOf(currentSettings.currency) }
  var workdayMultiplierState by remember { mutableStateOf(currentSettings.workdayMultiplier.toString()) }
  var weekendMultiplierState by remember { mutableStateOf(currentSettings.weekendMultiplier.toString()) }

  var isCurrencyDropdownExpanded by remember { mutableStateOf(false) }

  val computedMonthlyHours = remember(dailyHoursState, workDaysState) {
    val dHours = dailyHoursState.toIntOrNull() ?: 8
    val wDays = workDaysState.toIntOrNull() ?: 20
    dHours * wDays
  }

  val calculatedHourlyRate = remember(baseSalaryState, computedMonthlyHours) {
    val sal = baseSalaryState.toDoubleOrNull() ?: 0.0
    if (computedMonthlyHours > 0) sal / computedMonthlyHours else 0.0
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "إعداد عقد العمل وساعات الدوام",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
          )
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
      // Header Banner
      Card(
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Badge,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(end = 12.dp)
          )
          Column {
            Text(
              text = "ضبط أجر الساعة وساعات الدوام",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
              text = "تُحسب جميع قيم ساعات العمل الإضافية بدقة وفق بيانات العقد المحددة هنا.",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
            )
          }
        }
      }

      // Base Salary Input
      OutlinedTextField(
        value = baseSalaryState,
        onValueChange = { baseSalaryState = it },
        label = { Text("الراتب الأساسي الشهري") },
        leadingIcon = { Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        suffix = { Text(currencyState, fontWeight = FontWeight.Bold) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
      )

      // Daily Hours & Work Days Inputs
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedTextField(
          value = dailyHoursState,
          onValueChange = { dailyHoursState = it },
          label = { Text("ساعات الدوام اليومية") },
          leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
          suffix = { Text("ساعة") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = workDaysState,
          onValueChange = { workDaysState = it },
          label = { Text("أيام العمل في الشهر") },
          suffix = { Text("يوم") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(12.dp)
        )
      }

      // Currency Dropdown Selection
      ExposedDropdownMenuBox(
        expanded = isCurrencyDropdownExpanded,
        onExpandedChange = { isCurrencyDropdownExpanded = !isCurrencyDropdownExpanded }
      ) {
        val currentCurrencyObj = SupportedCurrencies.find { it.symbol == currencyState || it.code == currencyState }
          ?: SupportedCurrencies.first()

        OutlinedTextField(
          value = "${currentCurrencyObj.nameAr} (${currentCurrencyObj.symbol})",
          onValueChange = {},
          readOnly = true,
          label = { Text("اختر عملة الحساب والتقارير") },
          leadingIcon = { Icon(Icons.Default.Tune, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
          trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCurrencyDropdownExpanded) },
          modifier = Modifier
            .menuAnchor()
            .fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        )

        ExposedDropdownMenu(
          expanded = isCurrencyDropdownExpanded,
          onDismissRequest = { isCurrencyDropdownExpanded = false }
        ) {
          SupportedCurrencies.forEach { option ->
            DropdownMenuItem(
              text = {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(text = option.nameAr, fontWeight = FontWeight.Medium)
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                  ) {
                    Text(
                      text = option.symbol,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onPrimaryContainer,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                  }
                }
              },
              onClick = {
                currencyState = option.symbol
                isCurrencyDropdownExpanded = false
              }
            )
          }
        }
      }

      // Overtime Multipliers
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedTextField(
          value = workdayMultiplierState,
          onValueChange = { workdayMultiplierState = it },
          label = { Text("مضاعف الأيام العادية") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = weekendMultiplierState,
          onValueChange = { weekendMultiplierState = it },
          label = { Text("مضاعف العطلات") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(12.dp)
        )
      }

      // Calculated Standard Hourly Rate Preview
      Card(
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(16.dp)
        ) {
          Text(
            text = "إجمالي الساعات الشهرية النظامية: $computedMonthlyHours ساعة",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "أجر الساعة النظامية: %.2f %s / ساعة".format(calculatedHourlyRate, currencyState),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
          )
          Text(
            text = "أجر الساعة الإضافية العادية (1.5x): %.2f %s".format(
              calculatedHourlyRate * (workdayMultiplierState.toDoubleOrNull() ?: 1.5),
              currencyState
            ),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Save Button
      Button(
        onClick = {
          val sal = baseSalaryState.toDoubleOrNull() ?: 6000.0
          val dHours = dailyHoursState.toIntOrNull() ?: 8
          val wDays = workDaysState.toIntOrNull() ?: 20
          val workdayMult = workdayMultiplierState.toDoubleOrNull() ?: 1.5
          val weekendMult = weekendMultiplierState.toDoubleOrNull() ?: 2.0
          onSaveSettings(
            UserContractSettings(
              baseSalary = sal,
              dailyHours = dHours,
              workDaysPerMonth = wDays,
              monthlyHours = dHours * wDays,
              currency = currencyState.ifEmpty { "ر.س" },
              workdayMultiplier = workdayMult,
              weekendMultiplier = weekendMult,
              isSetupCompleted = true,
              themeMode = currentSettings.themeMode
            )
          )
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp),
        shape = RoundedCornerShape(14.dp)
      ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "حفظ وإعادة الحساب الشامل",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}
