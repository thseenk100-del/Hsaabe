package com.example.data

import java.util.UUID

enum class OvertimeType(val label: String, val defaultMultiplier: Double, val badgeColorHex: Long) {
  WORKDAY("يوم عمل عادي (1.5x)", 1.5, 0xFF0F766E),
  WEEKEND("عطلة أسبوعية (2.0x)", 2.0, 0xFFD97706),
  HOLIDAY("عطلة رسمية / أعياد (2.0x)", 2.0, 0xFFE11D48)
}

data class OvertimeRecord(
  val id: String = UUID.randomUUID().toString(),
  val date: String,
  val hours: Double,
  val type: OvertimeType,
  val multiplier: Double,
  val hourlyRate: Double,
  val notes: String = ""
) {
  val totalAmount: Double
    get() = hours * hourlyRate * multiplier
}

data class CurrencyOption(
  val code: String,
  val nameAr: String,
  val symbol: String
)

val SupportedCurrencies = listOf(
  CurrencyOption("SAR", "ريال سعودي (SAR)", "ر.س"),
  CurrencyOption("YER", "ريال يمني (YER)", "ر.ي"),
  CurrencyOption("USD", "دولار أمريكي (USD)", "$"),
  CurrencyOption("AED", "درهم إماراتي (AED)", "د.إ"),
  CurrencyOption("OMR", "ريال عماني (OMR)", "ر.ع"),
  CurrencyOption("KWD", "دينار كويتي (KWD)", "د.ك"),
  CurrencyOption("BHD", "دينار بحريني (BHD)", "د.ب"),
  CurrencyOption("QAR", "ريال قطري (QAR)", "ر.ق")
)

data class UserContractSettings(
  val baseSalary: Double = 6000.0,
  val dailyHours: Int = 8,
  val workDaysPerMonth: Int = 20,
  val monthlyHours: Int = 160,
  val currency: String = "ر.س",
  val workdayMultiplier: Double = 1.5,
  val weekendMultiplier: Double = 2.0,
  val isSetupCompleted: Boolean = true,
  val themeMode: String = "SYSTEM" // "LIGHT", "DARK", "SYSTEM"
) {
  val calculatedMonthlyHours: Int
    get() = if (monthlyHours > 0) monthlyHours else (dailyHours * workDaysPerMonth)

  val standardHourlyRate: Double
    get() {
      val hours = calculatedMonthlyHours
      return if (hours > 0) baseSalary / hours else 0.0
    }
}
