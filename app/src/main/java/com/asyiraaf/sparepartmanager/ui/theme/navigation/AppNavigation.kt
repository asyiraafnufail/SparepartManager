package com.asyiraaf.sparepartmanager.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.asyiraaf.sparepartmanager.ui.screens.*
import com.asyiraaf.sparepartmanager.viewmodel.SparepartViewModel

@Composable
fun AppNavigation(viewModel: SparepartViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute == "home" || currentRoute == "transaksi_menu" || currentRoute == "history"

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = Color(0xFFFF4081)
                ) {
                    // TAB 1: HOME
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = currentRoute == "home",
                        onClick = {
                            navController.navigate("home") {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFFFF4081), indicatorColor = Color(0xFFFFEBEE))
                    )

                    // TAB 2: TRANSAKSI
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Transaksi") },
                        label = { Text("Transaksi") },
                        selected = currentRoute == "transaksi_menu",
                        onClick = {
                            navController.navigate("transaksi_menu") {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFFFF4081), indicatorColor = Color(0xFFFFEBEE))
                    )

                    // TAB 3: HISTORY
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.History, contentDescription = "History") },
                        label = { Text("Riwayat") },
                        selected = currentRoute == "history",
                        onClick = {
                            navController.navigate("history") {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFFFF4081), indicatorColor = Color(0xFFFFEBEE))
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("splash",
                exitTransition = { fadeOut(animationSpec = tween(500)) }
            ) { SplashScreen(navController) }

            composable("login",
                exitTransition = { fadeOut(animationSpec = tween(500)) }
            ) { LoginScreen(navController) }

            composable("home") { HomeScreen(navController, viewModel) }

            composable("transaksi_menu") { TransactionMenuScreen(navController, viewModel) }

            composable("history") { HistoryScreen(navController, viewModel) }

            // 1. HALAMAN ADD
            composable("add") { AddScreen(navController, viewModel) }

            // 2. HALAMAN EDIT
            composable(
                "edit/{id}/{kode}/{nama}/{harga}/{stok}",
                arguments = listOf(
                    navArgument("id") { type = NavType.IntType },
                    navArgument("kode") { type = NavType.StringType },
                    navArgument("nama") { type = NavType.StringType },
                    navArgument("harga") { type = NavType.LongType },
                    navArgument("stok") { type = NavType.IntType }
                )
            ) { backStack ->
                val id = backStack.arguments?.getInt("id") ?: 0
                val kode = backStack.arguments?.getString("kode") ?: ""
                val nama = backStack.arguments?.getString("nama") ?: ""
                val harga = backStack.arguments?.getLong("harga") ?: 0L
                val stok = backStack.arguments?.getInt("stok") ?: 0
                EditScreen(navController, viewModel, id, kode, nama, harga, stok)
            }

            // 3. HALAMAN TRANSAKSI MASUK/KELUAR
            composable(
                "transaction/{type}",
                arguments = listOf(navArgument("type") { type = NavType.StringType })
            ) { backStack ->
                val tipe = backStack.arguments?.getString("type") ?: "masuk"
                TransactionScreen(navController, viewModel, tipe)
            }
        }
    }
}