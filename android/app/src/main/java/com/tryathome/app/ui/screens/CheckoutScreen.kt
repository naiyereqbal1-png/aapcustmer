package com.tryathome.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tryathome.app.data.*
import com.tryathome.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onOrderPlaced: (Order) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val cartItems = AppState.cart
    val settings = AppState.settings
    val currentCust = AppState.customers.find { it.mobile == AppState.currentSession?.mobile } 
        ?: AppState.customers.firstOrNull() // Fallback to Aarav if not logged in

    val addresses = currentCust?.addresses ?: mutableListOf()
    var selectedAddressId by remember { mutableStateOf(addresses.firstOrNull()?.id ?: "") }

    var selectedPaymentMethod by remember { mutableStateOf("COD") } // COD or ONLINE

    // Calculations
    val subtotal = cartItems.sumOf { it.product.mrp * it.quantity }
    val finalPrice = cartItems.sumOf { it.product.sellingPrice * it.quantity }
    val discount = subtotal - finalPrice
    val deliveryFee = if (finalPrice >= settings.freeDeliveryThreshold || finalPrice == 0.0) 0.0 else settings.deliveryCharge
    val tryAtHomeFee = settings.tryAtHomeCharge
    val taxAmount = finalPrice * (settings.gstPercentage / 100.0)
    val totalPayable = finalPrice + deliveryFee + tryAtHomeFee

    // Add Address State
    var showAddAddressDialog by remember { mutableStateOf(false) }
    var newAddrName by remember { mutableStateOf("") }
    var newAddrMobile by remember { mutableStateOf("") }
    var newAddrPincode by remember { mutableStateOf("") }
    var newAddrBody by remember { mutableStateOf("") }
    var newAddrCity by remember { mutableStateOf("") }
    var newAddrState by remember { mutableStateOf("") }

    val handleAddAddress = {
        if (newAddrName.isBlank() || newAddrMobile.isBlank() || newAddrPincode.isBlank() || newAddrBody.isBlank() || newAddrCity.isBlank() || newAddrState.isBlank()) {
            Toast.makeText(context, "Please fill in all address fields", Toast.LENGTH_SHORT).show()
        } else {
            val newAddress = CustomerAddress(
                id = "addr-${System.currentTimeMillis()}",
                customerId = currentCust?.id ?: "cust-1",
                name = newAddrName,
                mobile = newAddrMobile,
                pincode = newAddrPincode,
                address = newAddrBody,
                city = newAddrCity,
                state = newAddrState,
                isDefault = addresses.isEmpty()
            )
            addresses.add(newAddress)
            selectedAddressId = newAddress.id
            AppState.saveState(context)
            showAddAddressDialog = false
            Toast.makeText(context, "Address added successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    val handlePlaceOrder = {
        val selectedAddress = addresses.find { it.id == selectedAddressId }
        if (selectedAddress == null) {
            Toast.makeText(context, "Please select or add a delivery address!", Toast.LENGTH_SHORT).show()
        } else if (cartItems.isEmpty()) {
            Toast.makeText(context, "Your bag is empty!", Toast.LENGTH_SHORT).show()
        } else {
            val orderIdNum = (100000..999999).random()
            val orderIdString = "STYLE1-ORD-$orderIdNum"
            
            val orderItems = cartItems.map { item ->
                OrderItem(
                    id = "oi-${System.currentTimeMillis()}-${(10..99).random()}",
                    orderId = orderIdString,
                    productId = item.product.id,
                    productName = item.product.name,
                    brand = item.product.brand,
                    quantity = item.quantity,
                    price = item.product.sellingPrice,
                    mrp = item.product.mrp,
                    size = item.size,
                    color = item.color,
                    imageUrl = item.product.images.firstOrNull()?.imageUrl ?: "",
                    itemStatus = "Pending"
                )
            }

            val newOrder = Order(
                id = "ord-$orderIdNum",
                orderId = orderIdString,
                customerId = currentCust?.id ?: "cust-1",
                customerName = currentCust?.name ?: "Valued Customer",
                mobile = currentCust?.mobile ?: "9876543210",
                email = currentCust?.email ?: "customer@tryathome.in",
                address = selectedAddress,
                items = orderItems,
                subtotal = subtotal,
                discount = discount,
                deliveryCharge = deliveryFee,
                taxAmount = taxAmount,
                total = totalPayable,
                paymentMethod = selectedPaymentMethod,
                paymentStatus = if (selectedPaymentMethod == "ONLINE") "PAID" else "PENDING",
                orderStatus = "Pending",
                createdAt = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).format(java.util.Date()),
                updatedAt = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).format(java.util.Date()),
                trialDurationMinutes = settings.tryAtHomeDurationMinutes
            )

            newOrder.statusHistory.add(
                StatusHistory(
                    id = "sh-${System.currentTimeMillis()}",
                    orderId = newOrder.id,
                    status = "Pending",
                    changedBy = "Customer (Order Placed)",
                    changedAt = newOrder.createdAt,
                    notes = "Doorstep Try-at-Home order submitted successfully!"
                )
            )

            // Update product stock and shopkeeper stats
            orderItems.forEach { orderItem ->
                val prod = AppState.products.find { it.id == orderItem.productId }
                if (prod != null) {
                    prod.stock = (prod.stock - orderItem.quantity).coerceAtLeast(0)
                }
            }

            AppState.orders.add(newOrder)
            cartItems.clear()
            AppState.saveState(context)
            
            Toast.makeText(context, "🎉 TRYatHOME Session Booked! Order ID: $orderIdString", Toast.LENGTH_LONG).show()
            onOrderPlaced(newOrder)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
    ) {
        // Simple Header
        Surface(
            color = Color.White,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
                Text(
                    text = "Confirm Try-at-Home Booking",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = Slate900,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Doorstep Try-at-Home Service Banner
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.HomeWork, "Doorstep", tint = Indigo600, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Doorstep Trial Session booking", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Indigo600)
                        Text(
                            "A delivery boy will coordinate a ${settings.tryAtHomeDurationMinutes}-minute trial slot. Pay ONLY for garments you choose to keep! Returned garments are collected on the spot.",
                            fontSize = 11.sp,
                            color = Color.DarkGray,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Addresses Section
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("SELECT TRIAL ADDRESS", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Slate900)
                        TextButton(onClick = { showAddAddressDialog = true }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, "Add", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("New Address", fontSize = 12.sp)
                            }
                        }
                    }

                    if (addresses.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                .clickable { showAddAddressDialog = true }
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+ Tap to Add Shipping / Trial Address", color = Indigo600, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        addresses.forEach { addr ->
                            val isSelected = selectedAddressId == addr.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) Indigo600 else Color(0xFFE2E8F0),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .background(if (isSelected) Color(0xFFF8FAFC) else Color.White)
                                    .clickable { selectedAddressId = addr.id }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedAddressId = addr.id },
                                    colors = RadioButtonDefaults.colors(selectedColor = Indigo600)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(addr.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate900)
                                    Text("${addr.address}, ${addr.city} - ${addr.pincode}", fontSize = 11.sp, color = Color.Gray)
                                    Text("Phone: +91 ${addr.mobile}", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }

            // Payment Options
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("SELECT PAYMENT METHOD", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Slate900, modifier = Modifier.padding(bottom = 10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (selectedPaymentMethod == "COD") 1.5.dp else 1.dp,
                                color = if (selectedPaymentMethod == "COD") Indigo600 else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedPaymentMethod = "COD" }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selectedPaymentMethod == "COD", onClick = { selectedPaymentMethod = "COD" })
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Cash/UPI on Delivery (Post-Trial)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Pay only for the kept clothes after you try them on.", fontSize = 11.sp, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (selectedPaymentMethod == "ONLINE") 1.5.dp else 1.dp,
                                color = if (selectedPaymentMethod == "ONLINE") Indigo600 else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedPaymentMethod = "ONLINE" }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selectedPaymentMethod == "ONLINE", onClick = { selectedPaymentMethod = "ONLINE" })
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Simulated Razorpay / Online checkout", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("Instant digital checkout (Kept products only refund/escrow).", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }

            // Bill Summary
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("BILLING & TRIAL DETAILS", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Slate900, modifier = Modifier.padding(bottom = 6.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Cart MRP Subtotal", fontSize = 12.sp, color = Color.Gray)
                        Text("₹${subtotal.toInt()}", fontSize = 12.sp)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Boutique Discount", fontSize = 12.sp, color = Emerald700)
                        Text("-₹${discount.toInt()}", fontSize = 12.sp, color = Emerald700)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Try-at-Home Doorstep Fee", fontSize = 12.sp, color = Color.Gray)
                        Text("₹${tryAtHomeFee.toInt()}", fontSize = 12.sp)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Delivery & Handling Fees", fontSize = 12.sp, color = Color.Gray)
                        Text(if (deliveryFee == 0.0) "FREE" else "₹${deliveryFee.toInt()}", fontSize = 12.sp)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("GST & Taxes (5% included)", fontSize = 11.sp, color = Color.LightGray)
                        Text("₹${taxAmount.toInt()}", fontSize = 11.sp, color = Color.LightGray)
                    }

                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Payable Booking", fontWeight = FontWeight.Black, fontSize = 14.sp)
                        Text("₹${totalPayable.toInt()}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = Indigo600)
                    }
                }
            }
        }

        // Action CTA Sticky Bottom
        Surface(
            color = Color.White,
            tonalElevation = 6.dp,
            modifier = Modifier
                .border(1.dp, Color(0xFFEDF2F7))
                .fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Button(
                    onClick = handlePlaceOrder,
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("BOOK DOORSTEP TRIAL SESSION", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                }
            }
        }
    }

    // Add Address Modal Dialog
    if (showAddAddressDialog) {
        AlertDialog(
            onDismissRequest = { showAddAddressDialog = false },
            title = { Text("Add Doorstep Shipping Address", fontSize = 16.sp, fontWeight = FontWeight.Black) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(
                        value = newAddrName,
                        onValueChange = { newAddrName = it },
                        label = { Text("Receiver's Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newAddrMobile,
                        onValueChange = { newAddrMobile = it.filter { it.isDigit() } },
                        label = { Text("10-Digit Phone Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newAddrPincode,
                        onValueChange = { newAddrPincode = it.filter { it.isDigit() } },
                        label = { Text("6-Digit Pincode") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newAddrBody,
                        onValueChange = { newAddrBody = it },
                        label = { Text("Flat/House No, Building, Street") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newAddrCity,
                        onValueChange = { newAddrCity = it },
                        label = { Text("City") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newAddrState,
                        onValueChange = { newAddrState = it },
                        label = { Text("State") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = handleAddAddress, colors = ButtonDefaults.buttonColors(containerColor = Indigo600)) {
                    Text("SAVE & SELECT")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAddressDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
}
