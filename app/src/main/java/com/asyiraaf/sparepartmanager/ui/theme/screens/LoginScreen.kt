package com.asyiraaf.sparepartmanager.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    val sharedPref = remember { context.getSharedPreferences("app_security", Context.MODE_PRIVATE) }
    var savedPin by remember { mutableStateOf(sharedPref.getString("user_pin", null)) }
    var inputPin by remember { mutableStateOf("") }

    var showForgotDialog by remember { mutableStateOf(false) }
    var inputMasterKey by remember { mutableStateOf("") }
    val MASTER_KEY = "admin123"

    val isCreatingNew = savedPin == null

    // --- FUNGSI BIOMETRIK ---
    fun showBiometric() {
        val biometricManager = BiometricManager.from(context)
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) != BiometricManager.BIOMETRIC_SUCCESS) {
            // Toast.makeText(context, "Sensor tidak tersedia", Toast.LENGTH_SHORT).show()
            return
        }

        if (activity == null) return

        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(context, "Autentikasi Berhasil", Toast.LENGTH_SHORT).show()
                    goToHome(navController)
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Login Sparepart Manager")
            .setSubtitle("Sentuh sensor sidik jari")
            .setNegativeButtonText("Gunakan PIN")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    LaunchedEffect(Unit) {
        if (!isCreatingNew) {
            showBiometric()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isCreatingNew) "BUAT PIN BARU" else "MASUKKAN PIN",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF4081)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isCreatingNew) "Lindungi data sparepart Anda" else "Demi keamanan data",
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        // INDIKATOR PIN
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(4) { index ->
                val isFilled = index < inputPin.length
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (isFilled) Color(0xFFFF4081) else Color.Transparent)
                        .border(2.dp, Color(0xFFFF4081), CircleShape)
                )
            }
        }

        if (!isCreatingNew) {
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { showForgotDialog = true }) {
                Text("Lupa PIN?", color = Color.Gray)
            }
        } else {
            Spacer(modifier = Modifier.height(48.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // KEYPAD
        val buttons = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("", "0", "del")
        )

        buttons.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                row.forEach { key ->
                    // Memanggil fungsi PinButton di bawah
                    PinButton(key) { pressedKey ->
                        if (pressedKey == "del") {
                            if (inputPin.isNotEmpty()) inputPin = inputPin.dropLast(1)
                        } else if (pressedKey.isNotEmpty()) {
                            if (inputPin.length < 4) {
                                inputPin += pressedKey
                                if (inputPin.length == 4) {
                                    if (isCreatingNew) {
                                        sharedPref.edit().putString("user_pin", inputPin).apply()
                                        Toast.makeText(context, "PIN Dibuat!", Toast.LENGTH_SHORT).show()
                                        goToHome(navController)
                                    } else {
                                        if (inputPin == savedPin) {
                                            goToHome(navController)
                                        } else {
                                            Toast.makeText(context, "PIN Salah!", Toast.LENGTH_SHORT).show()
                                            inputPin = ""
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // TOMBOL BIOMETRIK MANUAL
        if (!isCreatingNew) {
            Spacer(modifier = Modifier.height(16.dp))
            IconButton(
                onClick = { showBiometric() },
                modifier = Modifier
                    .size(60.dp)
                    .background(Color(0xFFE8F5E9), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = "Biometrik",
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(32.dp)
                )
            }
            Text("Login dengan Sidik Jari", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top=8.dp))
        }
    }

    // DIALOG LUPA PIN
    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            title = { Text("Reset PIN") },
            text = {
                Column {
                    Text("Masukkan Kode Rahasia:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inputMasterKey,
                        onValueChange = { inputMasterKey = it },
                        label = { Text("Kode Rahasia") },
                        visualTransformation = PasswordVisualTransformation()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputMasterKey == MASTER_KEY) {
                            sharedPref.edit().remove("user_pin").apply()
                            savedPin = null
                            inputPin = ""
                            showForgotDialog = false
                            inputMasterKey = ""
                            Toast.makeText(context, "PIN Direset!", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(context, "Kode Salah!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4081))
                ) { Text("Reset") }
            },
            dismissButton = { TextButton(onClick = { showForgotDialog = false }) { Text("Batal") } }
        )
    }
}

// --- FUNGSI BANTUAN (HELPER) DI BAWAH SINI ---

fun goToHome(navController: NavController) {
    navController.navigate("home") {
        popUpTo("login") { inclusive = true }
    }
}

@Composable
fun PinButton(text: String, onClick: (String) -> Unit) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(if (text.isNotEmpty()) Color(0xFFEEEEEE) else Color.Transparent)
            .clickable(enabled = text.isNotEmpty()) { onClick(text) },
        contentAlignment = Alignment.Center
    ) {
        if (text == "del") {
            Icon(Icons.Default.Backspace, contentDescription = null, tint = Color.Black)
        } else {
            Text(text, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}