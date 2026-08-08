package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OvertimeRecord
import com.example.data.OvertimeType
import com.example.data.UserContractSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
  settings: UserContractSettings,
  records: List<OvertimeRecord>,
  onDeleteRecord: (String) -> Unit
) {
  var selectedFilter by remember { mutableStateOf<OvertimeType?>(null) }
  var searchQuery by remember { mutableStateOf("") }

  val filteredRecords = remember(records, selectedFilter, searchQuery) {
    records.filter { record ->
      val matchesFilter = selectedFilter == null || record.type == selectedFilter
      val matchesQuery = searchQuery.isEmpty() || record.notes.contains(searchQuery, ignoreCase = true) || record.date.contains(searchQuery)
      matchesFilter && matchesQuery
    }
  }

  val totalFilteredHours = filteredRecords.sumOf { it.hours }
  val totalFilteredEarned = filteredRecords.sumOf { it.totalAmount }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "سجل الساعات الإضافية",
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
        .padding(horizontal = 16.dp)
    ) {
      Spacer(modifier = Modifier.height(12.dp))

      // Search Field
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("بحث بالتاريخ أو الملاحظات...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Filter Chips
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FilterChip(
          selected = selectedFilter == null,
          onClick = { selectedFilter = null },
          label = { Text("الكل (${records.size})") }
        )
        FilterChip(
          selected = selectedFilter == OvertimeType.WORKDAY,
          onClick = { selectedFilter = if (selectedFilter == OvertimeType.WORKDAY) null else OvertimeType.WORKDAY },
          label = { Text("أيام عادية") }
        )
        FilterChip(
          selected = selectedFilter == OvertimeType.WEEKEND,
          onClick = { selectedFilter = if (selectedFilter == OvertimeType.WEEKEND) null else OvertimeType.WEEKEND },
          label = { Text("عطلات") }
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Filter Summary Card
      Card(
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "النتائج: ${filteredRecords.size} عملية | %.1f ساعة".format(totalFilteredHours),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = "الإجمالي: %.2f %s".format(totalFilteredEarned, settings.currency),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // History Log Items
      if (filteredRecords.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Default.History,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.outline,
              modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "لا توجد سجلات مطابقة للبحث",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      } else {
        LazyColumn(
          verticalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.weight(1f)
        ) {
          items(filteredRecords, key = { it.id }) { record ->
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                      shape = CircleShape,
                      color = Color(record.type.badgeColorHex).copy(alpha = 0.15f),
                      modifier = Modifier.size(40.dp)
                    ) {
                      Box(contentAlignment = Alignment.Center) {
                        Icon(
                          imageVector = Icons.Default.Schedule,
                          contentDescription = null,
                          tint = Color(record.type.badgeColorHex),
                          modifier = Modifier.size(20.dp)
                        )
                      }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                      Text(
                        text = record.type.label,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                      )
                      Text(
                        text = record.date,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                    }
                  }

                  IconButton(onClick = { onDeleteRecord(record.id) }) {
                    Icon(
                      imageVector = Icons.Default.Delete,
                      contentDescription = "حذف",
                      tint = MaterialTheme.colorScheme.error
                    )
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "%.1f ساعة × %.2f %s × %.1fx".format(
                      record.hours,
                      record.hourlyRate,
                      settings.currency,
                      record.multiplier
                    ),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                  )

                  Text(
                    text = "+%.2f %s".format(record.totalAmount, settings.currency),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                  )
                }

                if (record.notes.isNotEmpty()) {
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "ملاحظة: ${record.notes}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                  )
                }
              }
            }
          }

          item { Spacer(modifier = Modifier.height(16.dp)) }
        }
      }
    }
  }
}
