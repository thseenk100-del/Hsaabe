package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OvertimeRecord
import com.example.data.OvertimeType
import com.example.data.UserContractSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  settings: UserContractSettings,
  records: List<OvertimeRecord>,
  onNavigateToAddOvertime: () -> Unit,
  onNavigateToHistory: () -> Unit,
  onNavigateToReports: () -> Unit,
  onNavigateToSettings: () -> Unit
) {
  val totalOvertimeHours = records.sumOf { it.hours }
  val totalOvertimeEarned = records.sumOf { it.totalAmount }
  val totalSalaryWithOvertime = settings.baseSalary + totalOvertimeEarned

  // Quick Calculator Slider State
  var quickHoursState by remember { mutableFloatStateOf(2.0f) }
  val quickCalculatedAmount = quickHoursState * settings.standardHourlyRate * settings.workdayMultiplier

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Calculate,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(end = 8.dp)
            )
            Column {
              Text(
                text = "كيف حسبت",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
              )
              Text(
                text = "أغسطس 2026",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        },
        actions = {
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.padding(end = 12.dp)
          ) {
            Row(
              modifier = Modifier
                .clickable { onNavigateToSettings() }
                .padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "${settings.baseSalary.toInt()} ${settings.currency}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = onNavigateToAddOvertime,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = CircleShape,
        modifier = Modifier.size(60.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = "إضافة ساعات إضافية",
          modifier = Modifier.size(28.dp)
        )
      }
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .background(MaterialTheme.colorScheme.background)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item { Spacer(modifier = Modifier.height(4.dp)) }

      // Total Earnings Hero Banner
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                brush = Brush.horizontalGradient(
                  colors = listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.secondary
                  )
                )
              )
              .padding(20.dp)
          ) {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "إجمالي المستحقات هذا الشهر",
                  fontSize = 14.sp,
                  color = Color.White.copy(alpha = 0.9f)
                )
                Surface(
                  color = Color.White.copy(alpha = 0.2f),
                  shape = RoundedCornerShape(20.dp)
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = Icons.Default.TrendingUp,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "محدث",
                      fontSize = 11.sp,
                      color = Color.White,
                      fontWeight = FontWeight.Medium
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = "%.2f %s".format(totalSalaryWithOvertime, settings.currency),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )

              Spacer(modifier = Modifier.height(16.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column {
                  Text(text = "مبلغ الإضافي", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                  Text(
                    text = "+%.2f %s".format(totalOvertimeEarned, settings.currency),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                  )
                }

                Column {
                  Text(text = "إجمالي الساعات", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                  Text(
                    text = "%.1f ساعة".format(totalOvertimeHours),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                  )
                }

                Column {
                  Text(text = "سعر الساعة", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                  Text(
                    text = "%.1f %s".format(settings.standardHourlyRate, settings.currency),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                  )
                }
              }
            }
          }
        }
      }

      // Quick Action Bar Cards
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Card(
            onClick = onNavigateToAddOvertime,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.primaryContainer
            )
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "تسجيل إضافي",
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                  text = "إضافة ساعات جديدة",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
              }
            }
          }

          Card(
            onClick = onNavigateToReports,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.WorkHistory,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "كيف حُسبت؟",
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                  text = "التقرير والتفاصيل",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                )
              }
            }
          }
        }
      }

      // Quick Interactive Overtime Calculator Widget
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Payments,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "حاسبة سريعة (تقدير فوري)",
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp
                )
              }
              Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
              ) {
                Text(
                  text = "%.1fx (عادي)".format(settings.workdayMultiplier),
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "عدد الساعات: %.1f ساعة".format(quickHoursState),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
              )
              Text(
                text = "= %.2f %s".format(quickCalculatedAmount, settings.currency),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            }

            Slider(
              value = quickHoursState,
              onValueChange = { quickHoursState = it },
              valueRange = 0.5f..12.0f,
              steps = 22,
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      }

      // Recent Overtime Entries Title
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "آخر الساعات الإضافية المسجلة",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
          Text(
            text = "عرض الكل",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onNavigateToHistory() }
          )
        }
      }

      if (records.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(48.dp)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "لا توجد ساعات إضافية مسجلة بعد",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "اضغط على زر (+) في الأسفل لإضافة ساعات عملك الإضافية",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.outline
              )
            }
          }
        }
      } else {
        items(records.take(4)) { record ->
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = CircleShape,
                  color = Color(record.type.badgeColorHex).copy(alpha = 0.15f),
                  modifier = Modifier.size(44.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      imageVector = Icons.Default.Schedule,
                      contentDescription = null,
                      tint = Color(record.type.badgeColorHex),
                      modifier = Modifier.size(22.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                  Text(
                    text = record.type.label,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                  )
                  Text(
                    text = "${record.date} • ${record.hours} ساعة",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = "+%.2f %s".format(record.totalAmount, settings.currency),
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp,
                  color = MaterialTheme.colorScheme.primary
                )
                Text(
                  text = "مضاعف %.1fx".format(record.multiplier),
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.outline
                )
              }
            }
          }
        }
      }

      item { Spacer(modifier = Modifier.height(24.dp)) }
    }
  }
}
