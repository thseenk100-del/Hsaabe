package com.example.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WorkHistory
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.AddOvertimeScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SetupScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.viewmodel.OvertimeViewModel

import androidx.compose.foundation.isSystemInDarkTheme
import com.example.ui.theme.KayfHasabtTheme

sealed class BottomNavItem(
  val route: String,
  val title: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector
) {
  object Home : BottomNavItem(
    route = "home",
    title = "الرئيسية",
    selectedIcon = Icons.Filled.Home,
    unselectedIcon = Icons.Outlined.Home
  )

  object Add : BottomNavItem(
    route = "add_overtime",
    title = "إضافة",
    selectedIcon = Icons.Filled.AddCircle,
    unselectedIcon = Icons.Outlined.AddCircleOutline
  )

  object History : BottomNavItem(
    route = "history",
    title = "السجل",
    selectedIcon = Icons.Filled.History,
    unselectedIcon = Icons.Outlined.History
  )

  object Reports : BottomNavItem(
    route = "reports",
    title = "التقارير",
    selectedIcon = Icons.Filled.WorkHistory,
    unselectedIcon = Icons.Outlined.WorkHistory
  )

  object Settings : BottomNavItem(
    route = "settings",
    title = "الإعدادات",
    selectedIcon = Icons.Filled.Settings,
    unselectedIcon = Icons.Outlined.Settings
  )
}

@Composable
fun MainAppNavigation(
  viewModel: OvertimeViewModel = viewModel()
) {
  val navController = rememberNavController()

  val settingsState by viewModel.settingsState.collectAsStateWithLifecycle()
  val recordsState by viewModel.recordsState.collectAsStateWithLifecycle()

  val isDarkTheme = when (settingsState.themeMode) {
    "DARK" -> true
    "LIGHT" -> false
    else -> isSystemInDarkTheme()
  }

  KayfHasabtTheme(darkTheme = isDarkTheme) {
    val navItems = listOf(
      BottomNavItem.Home,
      BottomNavItem.Add,
      BottomNavItem.History,
      BottomNavItem.Reports,
      BottomNavItem.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in navItems.map { it.route }

    Scaffold(
      bottomBar = {
        if (showBottomBar) {
          NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
          ) {
            navItems.forEach { item ->
              val isSelected = currentRoute == item.route
              NavigationBarItem(
                selected = isSelected,
                onClick = {
                  navController.navigate(item.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                      saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                  }
                },
                icon = {
                  Icon(
                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                    contentDescription = item.title
                  )
                },
                label = {
                  Text(
                    text = item.title,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  )
                }
              )
            }
          }
        }
      }
    ) { innerPadding ->
      NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = Modifier.padding(innerPadding)
      ) {
        composable("splash") {
          SplashScreen(
            onNavigateNext = {
              navController.navigate("home") {
                popUpTo("splash") { inclusive = true }
              }
            }
          )
        }

        composable("setup") {
          SetupScreen(
            currentSettings = settingsState,
            onSaveSettings = { newSettings ->
              viewModel.saveSettings(newSettings)
              navController.navigate("home") {
                popUpTo("setup") { inclusive = true }
              }
            }
          )
        }

        composable("home") {
          HomeScreen(
            settings = settingsState,
            records = recordsState,
            onNavigateToAddOvertime = { navController.navigate("add_overtime") },
            onNavigateToHistory = { navController.navigate("history") },
            onNavigateToReports = { navController.navigate("reports") },
            onNavigateToSettings = { navController.navigate("settings") }
          )
        }

        composable("add_overtime") {
          AddOvertimeScreen(
            settings = settingsState,
            onAddRecord = { newRecord ->
              viewModel.addRecord(newRecord)
            },
            onNavigateBack = {
              navController.popBackStack()
            }
          )
        }

        composable("history") {
          HistoryScreen(
            settings = settingsState,
            records = recordsState,
            onDeleteRecord = { idToDelete ->
              viewModel.deleteRecord(idToDelete)
            }
          )
        }

        composable("reports") {
          ReportsScreen(
            settings = settingsState,
            records = recordsState
          )
        }

        composable("settings") {
          SettingsScreen(
            settings = settingsState,
            onSaveSettings = { newSettings ->
              viewModel.saveSettings(newSettings)
            },
            onNavigateToSetup = { navController.navigate("setup") },
            onResetData = {
              viewModel.resetData()
            },
            onExportBackup = { callback ->
              viewModel.exportBackup(callback)
            },
            onImportBackup = { jsonStr, callback ->
              viewModel.importBackup(jsonStr, callback)
            }
          )
        }
      }
    }
  }
}
