package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SupportedCurrencies
import com.example.data.UserContractSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  settings: UserContractSettings,
  onSaveSettings: (UserContractSettings) -> Unit,
  onNavigateToSetup: () -> Unit,
  onResetData: () -> Unit,
  onExportBackup: ((String) -> Unit) -> Unit,
  onImportBackup: (String, (Boolean) -> Unit) -> Unit
) {
  val context = LocalContext.current

  // In-place editable state for Work Data
  var baseSalaryText by remember(settings.baseSalary) { mutableStateOf(settings.baseSalary.toInt().toString()) }
  var dailyHoursText by remember(settings.dailyHours) { mutableStateOf(settings.dailyHours.toString()) }
  var workDaysText by remember(settings.workDaysPerMonth) { mutableStateOf(settings.workDaysPerMonth.toString()) }
  var workdayMultText by remember(settings.workdayMultiplier) { mutableStateOf(settings.workdayMultiplier.toString()) }
  var weekendMultText by remember(settings.weekendMultiplier) { mutableStateOf(settings.weekendMultiplier.toString()) }

  // Currency Dropdown Menu state
  var isCurrencyMenuExpanded by remember { mutableStateOf(false) }

  // Dialog Visibility states
  var showExportDialog by remember { mutableStateOf(false) }
  var exportJsonData by remember { mutableStateOf("") }

  var showImportDialog by remember { mutableStateOf(false) }
  var importJsonInput by remember { mutableStateOf("") }

  var showDeleteBackupDialog by remember { mutableStateOf(false) }
  var showResetConfirmDialog by remember { mutableStateOf(false) }
  var showAboutDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "إعدادات التطبيق والتفضيلات",
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
      verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

      // 1. WORK DATA & SALARY SETTINGS SECTION
      SectionTitle(title = "بيانات العمل والراتب", icon = Icons.Default.Badge)

      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Salary Field
          OutlinedTextField(
            value = baseSalaryText,
            onValueChange = {
              baseSalaryText = it
              val sal = it.toDoubleOrNull() ?: settings.baseSalary
              onSaveSettings(settings.copy(baseSalary = sal))
            },
            label = { Text("الراتب الشهرية الأساسي") },
            leadingIcon = { Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            suffix = { Text(settings.currency, fontWeight = FontWeight.Bold) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          )

          // Daily Working Hours & Days Per Month
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedTextField(
              value = dailyHoursText,
              onValueChange = {
                dailyHoursText = it
                val dHours = it.toIntOrNull() ?: settings.dailyHours
                val computedMonthly = dHours * settings.workDaysPerMonth
                onSaveSettings(settings.copy(dailyHours = dHours, monthlyHours = computedMonthly))
              },
              label = { Text("ساعات الدوام اليومية") },
              suffix = { Text("ساعة") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
              value = workDaysText,
              onValueChange = {
                workDaysText = it
                val wDays = it.toIntOrNull() ?: settings.workDaysPerMonth
                val computedMonthly = settings.dailyHours * wDays
                onSaveSettings(settings.copy(workDaysPerMonth = wDays, monthlyHours = computedMonthly))
              },
              label = { Text("أيام العمل في الشهر") },
              suffix = { Text("يوم") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(12.dp)
            )
          }

          // Overtime Multipliers
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedTextField(
              value = workdayMultText,
              onValueChange = {
                workdayMultText = it
                val mult = it.toDoubleOrNull() ?: settings.workdayMultiplier
                onSaveSettings(settings.copy(workdayMultiplier = mult))
              },
              label = { Text("نسبة الإضافي العادي") },
              suffix = { Text("x") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
              value = weekendMultText,
              onValueChange = {
                weekendMultText = it
                val mult = it.toDoubleOrNull() ?: settings.weekendMultiplier
                onSaveSettings(settings.copy(weekendMultiplier = mult))
              },
              label = { Text("نسبة العطلات الرسمية") },
              suffix = { Text("x") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(12.dp)
            )
          }

          // Computed Hourly Rate Preview
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "قيمة الساعة العادية (الحالية)",
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                  text = "%.2f %s / ساعة".format(settings.standardHourlyRate, settings.currency),
                  fontWeight = FontWeight.Bold,
                  fontSize = 16.sp,
                  color = MaterialTheme.colorScheme.primary
                )
              }

              Button(
                onClick = onNavigateToSetup,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
              ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("تعديل شامل", fontSize = 12.sp)
              }
            }
          }
        }
      }

      // 2. CURRENCY SELECTION SECTION (DROPDOWN MENU)
      SectionTitle(title = "عملة الحسابات والتطبيق", icon = Icons.Default.MonetizationOn)

      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "اختر العملة الافتراضية لاستخدامها في جميع الشاشات والتقارير:",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(10.dp))

          ExposedDropdownMenuBox(
            expanded = isCurrencyMenuExpanded,
            onExpandedChange = { isCurrencyMenuExpanded = !isCurrencyMenuExpanded }
          ) {
            val selectedOption = SupportedCurrencies.find { it.symbol == settings.currency || it.code == settings.currency }
              ?: SupportedCurrencies.first()

            OutlinedTextField(
              value = "${selectedOption.nameAr} (${selectedOption.symbol})",
              onValueChange = {},
              readOnly = true,
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCurrencyMenuExpanded) },
              leadingIcon = { Icon(Icons.Default.Tune, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
              modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
              shape = RoundedCornerShape(12.dp)
            )

            ExposedDropdownMenu(
              expanded = isCurrencyMenuExpanded,
              onDismissRequest = { isCurrencyMenuExpanded = false }
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
                    isCurrencyMenuExpanded = false
                    onSaveSettings(settings.copy(currency = option.symbol))
                    Toast.makeText(context, "تم تغيير العملة إلى: ${option.nameAr}", Toast.LENGTH_SHORT).show()
                  }
                )
              }
            }
          }
        }
      }

      // 3. THEME & APPEARANCE SECTION
      SectionTitle(title = "مظهر التطبيق", icon = Icons.Default.Palette)

      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          ThemeOptionItem(
            title = "الوضع الفاتح (Light Mode)",
            subtitle = "ألوان واضحة ومناسبة للإضاءة النهارية",
            icon = Icons.Default.LightMode,
            isSelected = settings.themeMode == "LIGHT",
            onClick = { onSaveSettings(settings.copy(themeMode = "LIGHT")) }
          )

          Divider(modifier = Modifier.padding(vertical = 8.dp))

          ThemeOptionItem(
            title = "الوضع الداكن (Dark Mode)",
            subtitle = "مريح للعينين أثناء الاستخدام الليلى",
            icon = Icons.Default.DarkMode,
            isSelected = settings.themeMode == "DARK",
            onClick = { onSaveSettings(settings.copy(themeMode = "DARK")) }
          )

          Divider(modifier = Modifier.padding(vertical = 8.dp))

          ThemeOptionItem(
            title = "اتباع إعدادات النظام",
            subtitle = "التكيف التلقائي مع ثيم إعدادات الهاتف",
            icon = Icons.Default.SettingsSuggest,
            isSelected = settings.themeMode == "SYSTEM",
            onClick = { onSaveSettings(settings.copy(themeMode = "SYSTEM")) }
          )
        }
      }

      // 4. BACKUP & RESTORE SECTION
      SectionTitle(title = "النسخ الاحتياطي والاستعادة", icon = Icons.Default.Storage)

      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Export Backup
          OutlinedButton(
            onClick = {
              onExportBackup { json ->
                exportJsonData = json
                showExportDialog = true
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("إنشاء نسخة احتياطية من البيانات", fontWeight = FontWeight.Bold)
          }

          // Import Backup
          OutlinedButton(
            onClick = { showImportDialog = true },
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.CloudDownload, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("استعادة نسخة احتياطية", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
          }

          // Delete Backup
          OutlinedButton(
            onClick = { showDeleteBackupDialog = true },
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.width(8.dp))
            Text("حذف النسخة الاحتياطية", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
          }
        }
      }

      // 5. ADDITIONAL SETTINGS & ABOUT SECTION
      SectionTitle(title = "إعدادات إضافية والمعلومات", icon = Icons.Default.Info)

      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          // About App Dialog Trigger
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .clickable { showAboutDialog = true }
              .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(text = "عن التطبيق (حقوقك محفوظة)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(text = "الإصدار 1.0.0 • خاص بالعمال والموظفين", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
            }
            Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
          }

          Divider(modifier = Modifier.padding(vertical = 8.dp))

          // Labor Law Guide Dialog Trigger
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .clickable { showAboutDialog = true }
              .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Gavel, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(text = "قوانين نظام العمل والنسب المالية", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(text = "حساب الأجر بإنقاص ساعات العمل الفعلية", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
            }
            Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
          }
        }
      }

      // Reset All Data Button
      Button(
        onClick = { showResetConfirmDialog = true },
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp),
        shape = RoundedCornerShape(14.dp)
      ) {
        Icon(Icons.Default.RestartAlt, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("إعادة ضبط جميع الإعدادات والسجلات", fontWeight = FontWeight.Bold)
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // --- DIALOGS IMPLEMENTATION ---

  // 1. Export Backup Dialog
  if (showExportDialog) {
    AlertDialog(
      onDismissRequest = { showExportDialog = false },
      title = { Text("تم إنشاء النسخة الاحتياطية بنجاح", fontWeight = FontWeight.Bold) },
      text = {
        Column {
          Text("يمكنك نسخ كود النسخة الاحتياطية التالي وحفظه في مكان آمن لاستعادته لاحقاً:")
          Spacer(modifier = Modifier.height(10.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
              .fillMaxWidth()
              .height(120.dp)
              .verticalScroll(rememberScrollState())
              .padding(8.dp)
          ) {
            Text(text = exportJsonData, fontSize = 11.sp, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("KayfHasabtBackup", exportJsonData)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "تم نسخ كود النسخة الاحتياطية للحافظة", Toast.LENGTH_SHORT).show()
            showExportDialog = false
          }
        ) {
          Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("نسخ للحافظة")
        }
      },
      dismissButton = {
        TextButton(onClick = { showExportDialog = false }) {
          Text("إغلاق")
        }
      }
    )
  }

  // 2. Import Backup Dialog
  if (showImportDialog) {
    AlertDialog(
      onDismissRequest = { showImportDialog = false },
      title = { Text("استعادة نسخة احتياطية", fontWeight = FontWeight.Bold) },
      text = {
        Column {
          Text("الصق كود النسخة الاحتياطية (JSON) أدناه لاستعادة إعداداتك وسجلات الإضافي:")
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = importJsonInput,
            onValueChange = { importJsonInput = it },
            placeholder = { Text("الصق الكود هنا...") },
            modifier = Modifier
              .fillMaxWidth()
              .height(130.dp),
            shape = RoundedCornerShape(10.dp)
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (importJsonInput.isBlank()) {
              Toast.makeText(context, "الرجاء إدخال كود النسخة الاحتياطية أولاً", Toast.LENGTH_SHORT).show()
            } else {
              onImportBackup(importJsonInput) { success ->
                if (success) {
                  Toast.makeText(context, "تمت استعادة البيانات بنجاح!", Toast.LENGTH_LONG).show()
                  showImportDialog = false
                  importJsonInput = ""
                } else {
                  Toast.makeText(context, "فشلت الاستعادة. كود النسخة غير صالحة.", Toast.LENGTH_LONG).show()
                }
              }
            }
          }
        ) {
          Text("استعادة الآن")
        }
      },
      dismissButton = {
        TextButton(onClick = { showImportDialog = false }) {
          Text("إلغاء")
        }
      }
    )
  }

  // 3. Delete Backup Confirm Dialog
  if (showDeleteBackupDialog) {
    AlertDialog(
      onDismissRequest = { showDeleteBackupDialog = false },
      title = { Text("حذف النسخة الاحتياطية", fontWeight = FontWeight.Bold) },
      text = { Text("هل أنت تأكد من رغبتك في مسح ملفات ومخزون النسخ الاحتياطية؟") },
      confirmButton = {
        Button(
          onClick = {
            exportJsonData = ""
            importJsonInput = ""
            Toast.makeText(context, "تم تمسح النسخ الاحتياطية المحلية", Toast.LENGTH_SHORT).show()
            showDeleteBackupDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("تأكيد الحذف")
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteBackupDialog = false }) {
          Text("إلغاء")
        }
      }
    )
  }

  // 4. Reset Confirm Dialog
  if (showResetConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showResetConfirmDialog = false },
      title = { Text("تأكيد إعادة الضبط الشامل", fontWeight = FontWeight.Bold) },
      text = { Text("سيتم مسح جميع سجلات الإضافي المخزنة وإعادة الراتب والعملة للإعدادات الافتراضية. هل تريد الاستمرار؟") },
      confirmButton = {
        Button(
          onClick = {
            onResetData()
            Toast.makeText(context, "تمت إعادة ضبط البيانات بنجاح", Toast.LENGTH_SHORT).show()
            showResetConfirmDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("إعادة الضبط")
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetConfirmDialog = false }) {
          Text("إلغاء")
        }
      }
    )
  }

  // 5. About App Dialog
  if (showAboutDialog) {
    AlertDialog(
      onDismissRequest = { showAboutDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Badge, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          Spacer(modifier = Modifier.width(8.dp))
          Text("حول تطبيق (كيف حسبت)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "تطبيق حاسبة الساعات الإضافية الذكي والخاص بالعمال والموظفين، حيث يتيح لك متابعة ساعات العمل والراتب ومستحقاتك المالية بدقة متناهية ودون الحاجة لتدخل المحاسب.",
            fontSize = 13.sp,
            lineHeight = 20.sp
          )
          Divider(modifier = Modifier.padding(vertical = 4.dp))
          Text(
            text = "• حساب أجر الساعة بدقة بناءً على الراتب الأساسي ورصيد الساعات.\n" +
                "• دعم كامل لجميع العملات العربية والعالمية.\n" +
                "• مضاعفات القانون النظامية (1.5x للأيام العادية و 2.0x للعطلات والرسميات).\n" +
                "• تقارير إحصائية ورسوم بيانية أسبوعية وشهرية.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
          )
        }
      },
      confirmButton = {
        Button(onClick = { showAboutDialog = false }) {
          Text("حسناً، فهمت")
        }
      }
    )
  }
}

@Composable
private fun SectionTitle(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = MaterialTheme.colorScheme.primary,
      modifier = Modifier.size(20.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = title,
      fontWeight = FontWeight.Bold,
      fontSize = 15.sp,
      color = MaterialTheme.colorScheme.primary
    )
  }
}

@Composable
private fun ThemeOptionItem(
  title: String,
  subtitle: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .padding(vertical = 8.dp, horizontal = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
      )
      Spacer(modifier = Modifier.width(12.dp))
      Column {
        Text(
          text = title,
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
          fontSize = 14.sp,
          color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }

    RadioButton(
      selected = isSelected,
      onClick = onClick
    )
  }
}
