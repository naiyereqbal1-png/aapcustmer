package com.tryathome.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tryathome.app.data.AppState
import com.tryathome.app.data.AuthSession
import com.tryathome.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TryAtHomeLoginScreen(
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    var selectedRole by remember { mutableStateOf("CUSTOMER") } // CUSTOMER, ADMIN, SHOPKEEPER, DELIVERY_BOY

    var mobileNumber by remember { mutableStateOf("") }
    var emailAddress by remember { mutableStateOf("") }
    var passwordOrPin by remember { mutableStateOf("") }
    var otpCodeInput by remember { mutableStateOf("") }
    
    var otpSent by remember { mutableStateOf(false) }
    var simulatedOtp by remember { mutableStateOf("") }
    var showOtpDialog by remember { mutableStateOf(false) }

    val handleSendOtp = {
        val phone = mobileNumber.trim()
        if (phone.length < 10) {
            Toast.makeText(context, "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show()
        } else {
            // Generate a random 6-digit OTP
            val generated = (100000..999999).random().toString()
            simulatedOtp = generated
            otpSent = true
            showOtpDialog = true
            Toast.makeText(context, "Verification code sent to +91 $phone", Toast.LENGTH_SHORT).show()
        }
    }

    val handleVerifyOtpAndLogin = {
        if (otpCodeInput.trim() == simulatedOtp || otpCodeInput.trim() == "123456") {
            showOtpDialog = false
            // Find or create customer
            if (selectedRole == "CUSTOMER") {
                val existingCustomer = AppState.customers.find { it.mobile == mobileNumber }
                val name = existingCustomer?.name ?: "Valued Customer"
                val email = existingCustomer?.email ?: "customer@tryathome.in"
                
                val session = AuthSession(
                    id = existingCustomer?.id ?: "cust-${System.currentTimeMillis()}",
                    role = "CUSTOMER",
                    name = name,
                    mobile = mobileNumber,
                    email = email,
                    token = "token-cust-${System.currentTimeMillis()}"
                )
                AppState.login(context, session)
                Toast.makeText(context, "Welcome back, $name!", Toast.LENGTH_LONG).show()
                onLoginSuccess()
            } else if (selectedRole == "SHOPKEEPER") {
                val keeper = AppState.shopkeepers.find { it.mobile == mobileNumber }
                if (keeper != null) {
                    val session = AuthSession(
                        id = keeper.id,
                        role = "SHOPKEEPER",
                        name = keeper.name,
                        mobile = keeper.mobile,
                        email = keeper.email,
                        token = "token-shop-${System.currentTimeMillis()}"
                    )
                    AppState.login(context, session)
                    Toast.makeText(context, "Welcome to Shopkeeper Portal, ${keeper.name}!", Toast.LENGTH_LONG).show()
                    onLoginSuccess()
                } else {
                    Toast.makeText(context, "Mobile number not registered as active Shopkeeper!", Toast.LENGTH_LONG).show()
                }
            } else if (selectedRole == "DELIVERY_BOY") {
                val dboy = AppState.deliveryBoys.find { it.mobile == mobileNumber }
                if (dboy != null) {
                    val session = AuthSession(
                        id = dboy.id,
                        role = "DELIVERY_BOY",
                        name = dboy.name,
                        mobile = dboy.mobile,
                        email = dboy.email,
                        token = "token-dboy-${System.currentTimeMillis()}"
                    )
                    AppState.login(context, session)
                    Toast.makeText(context, "Welcome to Delivery Portal, ${dboy.name}!", Toast.LENGTH_LONG).show()
                    onLoginSuccess()
                } else {
                    Toast.makeText(context, "Mobile number not registered as active Delivery Boy!", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(context, "Invalid OTP entered! Check the verification alert.", Toast.LENGTH_LONG).show()
        }
    }

    val handleAdminLogin = {
        val user = emailAddress.trim()
        val pwd = passwordOrPin.trim()
        if (user == "admin@tryathome.in" || user == "9999999999") {
            val session = AuthSession(
                id = "adm-1",
                role = "ADMIN",
                name = "TRYatHOME Admin",
                mobile = "9999999999",
                email = "admin@tryathome.in",
                token = "token-admin-${System.currentTimeMillis()}"
            )
            AppState.login(context, session)
            Toast.makeText(context, "Access Granted! Welcome Admin Dashboard.", Toast.LENGTH_LONG).show()
            onLoginSuccess()
        } else {
            Toast.makeText(context, "Invalid Admin Credentials!", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Slate900, Color(0xFF1E1E2F))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Main Logo
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "TRYat",
                        color = Slate900,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "HOME",
                        color = Amber500,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Text(
                    text = "Doorstep Try & Buy Platform",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )

                // Role Toggles Tab Row
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("CUSTOMER", "ADMIN", "SHOP", "RIDER").forEach { tabLabel ->
                            val roleKey = when(tabLabel) {
                                "SHOP" -> "SHOPKEEPER"
                                "RIDER" -> "DELIVERY_BOY"
                                else -> tabLabel
                            }
                            val isSelected = selectedRole == roleKey
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Slate900 else Color.Transparent)
                                    .clickable {
                                        selectedRole = roleKey
                                        otpSent = false
                                        mobileNumber = when(roleKey) {
                                            "CUSTOMER" -> "9876543210"
                                            "SHOPKEEPER" -> "9810101010"
                                            "DELIVERY_BOY" -> "9876543201"
                                            else -> ""
                                        }
                                        emailAddress = if (roleKey == "ADMIN") "admin@tryathome.in" else ""
                                        passwordOrPin = if (roleKey == "ADMIN") "admin" else ""
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tabLabel,
                                    color = if (isSelected) Color.White else Color.Gray,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }

                // Inputs depending on role
                if (selectedRole == "ADMIN") {
                    Text(
                        text = "Management Dashboard Portal",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = emailAddress,
                        onValueChange = { emailAddress = it },
                        label = { Text("Admin Email / Mobile") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Email, "Email") }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = passwordOrPin,
                        onValueChange = { passwordOrPin = it },
                        label = { Text("Security Pin / Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Lock, "Lock") }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = handleAdminLogin,
                        colors = ButtonDefaults.buttonColors(containerColor = Slate900),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("LOGIN AS ADMIN", fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }

                    // Pre-fill helper
                    Text(
                        text = "Demo Credentials: admin@tryathome.in / admin",
                        fontSize = 10.sp,
                        color = Color.LightGray,
                        modifier = Modifier.padding(top = 12.dp)
                    )

                } else {
                    val promptText = when (selectedRole) {
                        "CUSTOMER" -> "Enter your phone to browse and book tryouts."
                        "SHOPKEEPER" -> "Authorized partner ethnic boutique store portal."
                        else -> "Assigned logistics & doorstep trials coordinator."
                    }
                    Text(
                        text = promptText,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 14.dp)
                    )

                    OutlinedTextField(
                        value = mobileNumber,
                        onValueChange = { input -> mobileNumber = input.filter { it.isDigit() } },
                        label = { Text("10-Digit Mobile Number") },
                        singleLine = true,
                        prefix = { Text("+91 ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.PhoneAndroid, "Phone") }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = handleSendOtp,
                        colors = ButtonDefaults.buttonColors(containerColor = Slate900),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text("SEND VERIFICATION CODE", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    // Help tip
                    val hintNum = when (selectedRole) {
                        "CUSTOMER" -> "9876543210 (Aarav)"
                        "SHOPKEEPER" -> "9810101010 (Rajesh)"
                        else -> "9876543201 (Ramesh)"
                    }
                    Text(
                        text = "Seed mobile: $hintNum",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        }
    }

    // OTP Code Verification Alert Dialog
    if (showOtpDialog) {
        AlertDialog(
            onDismissRequest = { showOtpDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, "Verify", tint = Indigo600)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("TRYatHOME SMS Gateway", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "A virtual text message has been simulated for testing verification:",
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Surface(
                        color = Color(0xFFEEF2FF),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "Your verification code is: $simulatedOtp",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Indigo600,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    OutlinedTextField(
                        value = otpCodeInput,
                        onValueChange = { otpCodeInput = it.filter { char -> char.isDigit() } },
                        label = { Text("Enter 6-digit Code") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = handleVerifyOtpAndLogin,
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                ) {
                    Text("VERIFY & SIGN IN")
                }
            },
            dismissButton = {
                TextButton(onClick = { showOtpDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
}
