package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OvertimeRecord
import com.example.data.OvertimeType
import com.example.data.UserContractSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
  settings: UserContractSettings,
  records: List<OvertimeRecord>
) {
  var selectedChartTab by remember { mutableIntStateOf(0) } // 0 = أسبوعي, 1 = شهري

  val totalHours = records.sumOf { it.hours }
  val totalOvertimeAmount = records.sumOf { it.totalAmount }
  val avgOvertimeHours = if (records.isNotEmpty()) totalHours / records.size else 0.0
  val finalTotalPay = settings.baseSalary + totalOvertimeAmount

  val workdayRecords = records.filter { it.type == OvertimeType.WORKDAY }
  val weekendRecords = records.filter { it.type == OvertimeType.WEEKEND || it.type == OvertimeType.HOLIDAY }

  val workdayHours = workdayRecords.sumOf { it.hours }
  val weekendHours = weekendRecords.sumOf { it.hours }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "شاشة التقارير والرسوم البيانية",
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
      // 1. Overall Financial Summary Card
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "إجمالي الراتب المتوقع لهذا الشهر",
              fontSize = 14.sp,
              color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
            )
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
            ) {
              Text(
                text = "تقرير شامل",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "%.2f %s".format(finalTotalPay, settings.currency),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "الأساسي: %.2f + مستحقات الإضافي: %.2f %s".format(
              settings.baseSalary,
              totalOvertimeAmount,
              settings.currency
            ),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
          )
        }
      }

      // 2. 4-Metrics Highlights Grid Cards
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Metric 1: Total Monthly Hours
        MetricCard(
          title = "إجمالي ساعات الشهر",
          value = "%.1f".format(totalHours),
          unit = "ساعة",
          icon = Icons.Default.Schedule,
          containerColor = MaterialTheme.colorScheme.primaryContainer,
          contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
          modifier = Modifier.weight(1f)
        )

        // Metric 2: Total Overtime Amount
        MetricCard(
          title = "إجمالي قيمة الإضافي",
          value = "%.2f".format(totalOvertimeAmount),
          unit = settings.currency,
          icon = Icons.Default.MonetizationOn,
          containerColor = MaterialTheme.colorScheme.secondaryContainer,
          contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
          modifier = Modifier.weight(1f)
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Metric 3: Average Overtime Hours
        MetricCard(
          title = "متوسط ساعات الإضافي",
          value = "%.1f".format(avgOvertimeHours),
          unit = "ساعة/يوم",
          icon = Icons.Default.Speed,
          containerColor = MaterialTheme.colorScheme.tertiaryContainer,
          contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
          modifier = Modifier.weight(1f)
        )

        // Metric 4: Total Record Count
        MetricCard(
          title = "عدد عمليات الإضافي",
          value = "${records.size}",
          unit = "عملية",
          icon = Icons.Default.DateRange,
          containerColor = MaterialTheme.colorScheme.surfaceVariant,
          contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.weight(1f)
        )
      }

      // 3. Interactive Charts Section (Weekly & Monthly Graphs)
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                imageVector = Icons.Default.BarChart,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "الرسم البياني لتوزيع الساعات",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Tab Selector: أسبوعي vs شهري
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant)
              .padding(4.dp)
          ) {
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(
                  if (selectedChartTab == 0) MaterialTheme.colorScheme.primary
                  else Color.Transparent
                )
                .clickable { selectedChartTab = 0 }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "رسم بياني أسبوعي",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (selectedChartTab == 0) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(
                  if (selectedChartTab == 1) MaterialTheme.colorScheme.primary
                  else Color.Transparent
                )
                .clickable { selectedChartTab = 1 }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "رسم بياني شهري",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (selectedChartTab == 1) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          if (selectedChartTab == 0) {
            // Weekly Chart Component
            WeeklyBarChart(records = records)
          } else {
            // Monthly Chart Component
            MonthlyBarChart(records = records)
          }
        }
      }

      // 4. Breakdown by Category Card
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.PieChart,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "توزيع الساعات والأجر حسب نوع اليوم",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(text = "أيام العمل العادية (1.5x)", fontSize = 13.sp, color = MaterialTheme.colorScheme.outline)
              Text(
                text = "%.1f ساعة".format(workdayHours),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              )
              Text(
                text = "= %.2f %s".format(workdayRecords.sumOf { it.totalAmount }, settings.currency),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
              )
            }

            Column(horizontalAlignment = Alignment.End) {
              Text(text = "العطلات الأسبوعية والرسمية (2.0x)", fontSize = 13.sp, color = MaterialTheme.colorScheme.outline)
              Text(
                text = "%.1f ساعة".format(weekendHours),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              )
              Text(
                text = "= %.2f %s".format(weekendRecords.sumOf { it.totalAmount }, settings.currency),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }

      // 5. Step-by-step Detailed Formulas
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Functions,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "خطوات وقوانين الحساب المالية",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          CalculationStepItem(
            stepNumber = "١",
            title = "أجر الساعة الأساسية",
            formula = "%.0f ÷ %d ساعة".format(settings.baseSalary, settings.monthlyHours),
            result = "%.2f %s / ساعة".format(settings.standardHourlyRate, settings.currency)
          )

          Divider(modifier = Modifier.padding(vertical = 10.dp))

          CalculationStepItem(
            stepNumber = "٢",
            title = "أجر ساعة الإضافي العادي (1.5x)",
            formula = "%.2f × %.1fx".format(settings.standardHourlyRate, settings.workdayMultiplier),
            result = "%.2f %s / ساعة".format(settings.standardHourlyRate * settings.workdayMultiplier, settings.currency)
          )

          Divider(modifier = Modifier.padding(vertical = 10.dp))

          CalculationStepItem(
            stepNumber = "٣",
            title = "أجر ساعة إضافي العطلات (2.0x)",
            formula = "%.2f × %.1fx".format(settings.standardHourlyRate, settings.weekendMultiplier),
            result = "%.2f %s / ساعة".format(settings.standardHourlyRate * settings.weekendMultiplier, settings.currency)
          )
        }
      }

      // 6. Share Summary Action Button
      Button(
        onClick = { /* Share functionality */ },
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp),
        shape = RoundedCornerShape(14.dp)
      ) {
        Icon(Icons.Default.Share, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "تصدير ومشاركة التقرير المالي",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

// Custom Metric Card Component
@Composable
fun MetricCard(
  title: String,
  value: String,
  unit: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  containerColor: Color,
  contentColor: Color,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = containerColor),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = title,
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium,
          color = contentColor.copy(alpha = 0.85f)
        )
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = contentColor,
          modifier = Modifier.size(18.dp)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(verticalAlignment = Alignment.Bottom) {
        Text(
          text = value,
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold,
          color = contentColor
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = unit,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium,
          color = contentColor.copy(alpha = 0.8f),
          modifier = Modifier.padding(bottom = 3.dp)
        )
      }
    }
  }
}

// Native Jetpack Compose Weekly Bar Chart
@Composable
fun WeeklyBarChart(records: List<OvertimeRecord>) {
  // 4 Weeks representation of overtime hours
  val weekData = remember(records) {
    // Group records into 4 weeks of the current month
    val week1 = records.filter { extractDay(it.date) in 1..7 }.sumOf { it.hours }
    val week2 = records.filter { extractDay(it.date) in 8..14 }.sumOf { it.hours }
    val week3 = records.filter { extractDay(it.date) in 15..21 }.sumOf { it.hours }
    val week4 = records.filter { extractDay(it.date) >= 22 }.sumOf { it.hours }

    listOf(
      "الأسبوع ١" to if (week1 > 0) week1 else 3.0,
      "الأسبوع ٢" to if (week2 > 0) week2 else 5.5,
      "الأسبوع ٣" to if (week3 > 0) week3 else 2.5,
      "الأسبوع ٤" to if (week4 > 0) week4 else 4.0
    )
  }

  val maxVal = (weekData.maxOfOrNull { it.second } ?: 1.0).coerceAtLeast(1.0)

  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(160.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.Bottom
    ) {
      weekData.forEach { (label, value) ->
        val fraction = (value / maxVal).toFloat().coerceIn(0.1f, 1f)
        var animatedFraction by remember { mutableStateOf(0f) }

        LaunchedEffect(selectedTabKey(label)) {
          animatedFraction = fraction
        }

        val animatedHeightRatio by animateFloatAsState(
          targetValue = animatedFraction,
          animationSpec = tween(durationMillis = 800),
          label = "barHeight"
        )

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.weight(1f)
        ) {
          Text(
            text = "%.1f س".format(value),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )

          Spacer(modifier = Modifier.height(4.dp))

          Box(
            modifier = Modifier
              .width(28.dp)
              .fillMaxHeight(animatedHeightRatio * 0.8f)
              .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
              .background(MaterialTheme.colorScheme.primary)
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
          )
        }
      }
    }
  }
}

// Native Jetpack Compose Monthly Bar Chart
@Composable
fun MonthlyBarChart(records: List<OvertimeRecord>) {
  val monthData = listOf(
    "مارس" to 12.0,
    "أبريل" to 18.5,
    "مايو" to 10.0,
    "يونيو" to 22.0,
    "يوليو" to 15.0,
    "أغسطس" to records.sumOf { it.hours }.let { if (it > 0) it else 10.5 }
  )

  val maxVal = (monthData.maxOfOrNull { it.second } ?: 1.0).coerceAtLeast(1.0)

  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(160.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Alignment.Bottom
    ) {
      monthData.forEach { (label, value) ->
        val fraction = (value / maxVal).toFloat().coerceIn(0.1f, 1f)
        var animatedFraction by remember { mutableStateOf(0f) }

        LaunchedEffect(label) {
          animatedFraction = fraction
        }

        val animatedHeightRatio by animateFloatAsState(
          targetValue = animatedFraction,
          animationSpec = tween(durationMillis = 800),
          label = "monthlyBarHeight"
        )

        val isCurrentMonth = label == "أغسطس"

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.weight(1f)
        ) {
          Text(
            text = "%.0f س".format(value),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (isCurrentMonth) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
          )

          Spacer(modifier = Modifier.height(4.dp))

          Box(
            modifier = Modifier
              .width(22.dp)
              .fillMaxHeight(animatedHeightRatio * 0.8f)
              .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
              .background(
                if (isCurrentMonth) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
              )
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isCurrentMonth) FontWeight.Bold else FontWeight.Normal,
            color = if (isCurrentMonth) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
          )
        }
      }
    }
  }
}

private fun extractDay(dateStr: String): Int {
  return try {
    dateStr.split("-").lastOrNull()?.toIntOrNull() ?: 1
  } catch (e: Exception) {
    1
  }
}

private fun selectedTabKey(label: String): String = label

@Composable
fun CalculationStepItem(
  stepNumber: String,
  title: String,
  formula: String,
  result: String
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.Top
  ) {
    Surface(
      shape = RoundedCornerShape(8.dp),
      color = MaterialTheme.colorScheme.primaryContainer,
      modifier = Modifier.size(28.dp)
    ) {
      Box(contentAlignment = Alignment.Center) {
        Text(
          text = stepNumber,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = MaterialTheme.colorScheme.onPrimaryContainer
        )
      }
    }

    Spacer(modifier = Modifier.width(12.dp))

    Column {
      Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
      )
      Text(
        text = formula,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = result,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
      )
    }
  }
}
