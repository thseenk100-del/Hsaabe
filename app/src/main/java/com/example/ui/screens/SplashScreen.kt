package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
  onNavigateNext: () -> Unit
) {
  val scale = remember { Animatable(0.5f) }

  LaunchedEffect(key1 = true) {
    scale.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 800)
    )
    delay(1200)
    onNavigateNext()
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primaryContainer
          )
        )
      ),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier
        .scale(scale.value)
        .padding(24.dp)
    ) {
      Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.onPrimary,
        tonalElevation = 8.dp,
        modifier = Modifier.size(110.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.Calculate,
            contentDescription = "كيف حسبت",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(60.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(28.dp))

      Text(
        text = "كيف حسبت",
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onPrimary,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(10.dp))

      Surface(
        color = Color.White.copy(alpha = 0.2f),
        shape = RoundedCornerShape(16.dp)
      ) {
        Text(
          text = "حاسبة العمل الإضافي والحقوق المالية الدقيقة",
          fontSize = 15.sp,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onPrimary,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
      }

      Spacer(modifier = Modifier.height(48.dp))

      CircularProgressIndicator(
        color = MaterialTheme.colorScheme.onPrimary,
        strokeWidth = 3.dp,
        modifier = Modifier.size(32.dp)
      )
    }
  }
}
