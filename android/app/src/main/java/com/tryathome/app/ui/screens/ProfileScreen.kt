package com.tryathome.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tryathome.app.data.AppState
import com.tryathome.app.data.Order
import com.tryathome.app.data.OrderItem
import com.tryathome.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ProfileScreen(
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val currentCust = AppState.customers.find { it.mobile == AppState.currentSession?.mobile }
        ?: AppState.customers.firstOrNull() // Fallback to Aarav if not logged in

    val customerOrders = AppState.orders.filter { it.customerId == currentCust?.id }.sortedByDescending { it.createdAt }

    var selectedOrder by remember { mutableStateOf<Order?>(null) }
    var showOrderDetailModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
    ) {
        // Profile Summary Header
        Surface(
            color = Slate900,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Initials / Avatar
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Amber500),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentCust?.name?.split(" ")?.mapNotNull { it.firstOrNull() }?.joinToString("")?.uppercase() ?: "US",
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        color = Slate900
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = currentCust?.name ?: "Valued Customer",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = Color.White
                )

                Text(
                    text = "+91 ${currentCust?.mobile ?: "9876543210"} • ${currentCust?.email ?: "customer@tryathome.in"}",
                    fontSize = 11.sp,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        AppState.logout(context)
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.LogOut, "Logout", tint = Color.White, modifier = Modifier.size(14.dp))
                        Text("SIGN OUT", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Orders Segment
        Text(
            text = "My Try-at-Home Orders (${customerOrders.size})",
            fontWeight = FontWeight.Black,
            fontSize = 13.sp,
            color = Slate900,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
        )

        if (customerOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ListAlt, "Orders", tint = Color.LightGray, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No past or active trial sessions found", fontSize = 12.sp, color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(customerOrders) { order ->
                    CustomerOrderRow(
                        order = order,
                        onClick = {
                            selectedOrder = order
                            showOrderDetailModal = true
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Order Detail Dialog Modal
    if (showOrderDetailModal && selectedOrder != null) {
        val order = selectedOrder!!
        AlertDialog(
            onDismissRequest = { showOrderDetailModal = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Order Details", fontSize = 16.sp, fontWeight = FontWeight.Black)
                    IconButton(onClick = { showOrderDetailModal = false }) {
                        Icon(Icons.Default.Close, "Close")
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // ID & Date
                    Column {
                        Text("ORDER NUMBER", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Text(order.orderId, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                        Text("Placed at: ${order.createdAt}", fontSize = 11.sp, color = Color.Gray)
                    }

                    // Countdown Banner
                    if (order.orderStatus == "Trial Started") {
                        TryAtHomeCountdownTimer(order = order)
                    }

                    // Order Status Tag
                    Surface(
                        color = when(order.orderStatus) {
                            "Closed" -> Color(0xFFE6F4EA)
                            "Trial Started" -> Color(0xFFEFF6FF)
                            "Pending" -> Color(0xFFFEF3C7)
                            else -> Color(0xFFF1F5F9)
                        },
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when(order.orderStatus) {
                                    "Closed" -> Icons.Default.CheckCircle
                                    "Trial Started" -> Icons.Default.Timer
                                    "Pending" -> Icons.Default.Pending
                                    else -> Icons.Default.LocalShipping
                                },
                                contentDescription = "Status",
                                tint = when(order.orderStatus) {
                                    "Closed" -> Emerald700
                                    "Trial Started" -> Indigo600
                                    "Pending" -> Amber600
                                    else -> Slate900
                                },
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Trial Session Status: ${order.orderStatus}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = when(order.orderStatus) {
                                    "Closed" -> Emerald700
                                    "Trial Started" -> Indigo600
                                    "Pending" -> Amber600
                                    else -> Slate900
                                }
                            )
                        }
                    }

                    // Item summary List
                    Text("GARMENTS LIST", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Black)
                    order.items.forEach { itm ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = itm.imageUrl,
                                contentDescription = itm.productName,
                                contentScale = ContentScale.Cover,
                                modifier = Modifier
                                    .size(50.dp, 60.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(itm.productName, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Size: ${itm.size} | Color: ${itm.color}", fontSize = 10.sp, color = Color.Gray)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("₹${itm.price.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    if (itm.mrp > itm.price) {
                                        Text("₹${itm.mrp.toInt()}", fontSize = 10.sp, color = Color.LightGray, textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
                                    }
                                }
                            }
                            
                            // Item Keep/Returned Tag
                            if (order.orderStatus == "Closed") {
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = if (itm.itemStatus == "Kept") Color(0xFFD1FAE5) else Color(0xFFFEE2E2),
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = itm.itemStatus.uppercase(),
                                        color = if (itm.itemStatus == "Kept") Emerald700 else Rose600,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Bill Breakdown
                    Divider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Cart Price", fontSize = 12.sp, color = Color.Gray)
                        Text("₹${order.total.toInt() - 99}", fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Try-at-Home Charge", fontSize = 12.sp, color = Color.Gray)
                        Text("₹99", fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Payment Type", fontSize = 12.sp, color = Color.Gray)
                        Text(order.paymentMethod, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showOrderDetailModal = false }, colors = ButtonDefaults.buttonColors(containerColor = Slate900)) {
                    Text("DONE")
                }
            }
        )
    }
}

@Composable
fun CustomerOrderRow(
    order: Order,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(1.dp, Color(0xFFEDF2F7), RoundedCornerShape(10.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("ID: ${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                    Text("Placed: ${order.createdAt.substringBefore("T")}", fontSize = 11.sp, color = Color.Gray)
                }

                // Status Badge
                val badgeColor = when(order.orderStatus) {
                    "Closed" -> Emerald700
                    "Trial Started" -> Indigo600
                    "Pending" -> Amber600
                    else -> Color.DarkGray
                }
                Box(
                    modifier = Modifier
                        .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = order.orderStatus.uppercase(),
                        color = badgeColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${order.items.size} Garments • Total: ₹${order.total.toInt()}",
                    fontSize = 11.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text("View Details >", fontSize = 11.sp, color = Indigo600, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TryAtHomeCountdownTimer(order: Order) {
    val durationMs = order.trialDurationMinutes * 60 * 1000L
    val startTimestamp = order.trialStartedAt ?: System.currentTimeMillis()
    var remainingSeconds by remember { mutableStateOf(0L) }

    LaunchedEffect(key1 = startTimestamp) {
        while (true) {
            val elapsed = System.currentTimeMillis() - startTimestamp
            val remaining = ((durationMs - elapsed) / 1000L).coerceAtLeast(0L)
            remainingSeconds = remaining
            if (remaining <= 0) {
                break
            }
            delay(1000)
        }
    }

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)), // light rose
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFFECDD3), RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.HourglassEmpty, "Timer", tint = Rose600, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ACTIVE TRIAL COUNTDOWN",
                    color = Rose600,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = String.format("%02d:%02d", minutes, seconds),
                color = Rose600,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Please try on your clothing. The delivery boy is waiting outside to record your kept garments.",
                fontSize = 10.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}
