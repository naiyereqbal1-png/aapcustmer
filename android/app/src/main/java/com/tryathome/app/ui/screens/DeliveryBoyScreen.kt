package com.tryathome.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.tryathome.app.data.*
import com.tryathome.app.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryBoyScreen(
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val currentRider = AppState.deliveryBoys.find { it.mobile == AppState.currentSession?.mobile }
        ?: AppState.deliveryBoys.firstOrNull() // Fallback if missing

    val riderOrders = AppState.orders.filter { it.assignedDeliveryBoyId == currentRider?.id }

    var selectedOrderForVerification by remember { mutableStateOf<Order?>(null) }
    var showTrialSelectorModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
    ) {
        // Delivery Boy Title bar
        Surface(color = Slate900) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Amber500),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Moped, "Delivery Boy", tint = Slate900, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Associate Rider: ${currentRider?.name ?: "Logistics Associate"}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp)
                    }

                    IconButton(onClick = {
                        AppState.logout(context)
                        onLogout()
                    }) {
                        Icon(Icons.Default.PowerSettingsNew, "Logout", tint = Color.White)
                    }
                }
            }
        }

        // Active Tasks Header
        Text(
            text = "ASSIGNED DOORSTEP TRIALS (${riderOrders.size})",
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = Slate900,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
        )

        if (riderOrders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Task, "No tasks", tint = Color.LightGray, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No doorstep sessions assigned to you.", color = Color.Gray, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(riderOrders) { order ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFEDF2F7), RoundedCornerShape(10.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("ORDER ID: ${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Address: ${order.address.address}, ${order.address.city}", fontSize = 11.sp, color = Color.Gray)
                                    Text("Customer: ${order.customerName} (+91 ${order.mobile})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate900)
                                }
                                Box(
                                    modifier = Modifier
                                        .background(
                                            when(order.orderStatus) {
                                                "Closed" -> Color(0xFFE6F4EA)
                                                "Trial Started" -> Color(0xFFEFF6FF)
                                                else -> Color(0xFFFEF3C7)
                                            },
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = order.orderStatus.uppercase(),
                                        color = when(order.orderStatus) {
                                            "Closed" -> Emerald700
                                            "Trial Started" -> Indigo600
                                            else -> Color(0xFFB45309)
                                        },
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }

                            Divider(modifier = Modifier.padding(vertical = 10.dp))

                            if (order.orderStatus == "Closed") {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, "Completed", tint = Emerald700, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Trial session successfully completed and closed.", fontSize = 11.sp, color = Emerald700, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (order.orderStatus == "Shipped") {
                                        Button(
                                            onClick = {
                                                val index = AppState.orders.indexOf(order)
                                                if (index != -1) {
                                                    AppState.orders[index].orderStatus = "Out for Delivery"
                                                    AppState.orders[index].statusHistory.add(
                                                        StatusHistory(
                                                            id = "sh-${System.currentTimeMillis()}",
                                                            orderId = order.id,
                                                            status = "Out for Delivery",
                                                            changedBy = "Rider Associate",
                                                            changedAt = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).format(java.util.Date()),
                                                            notes = "Rider out for delivery. Co-ordinating doorstep trial session."
                                                        )
                                                    )
                                                    AppState.saveState(context)
                                                    Toast.makeText(context, "Marked as Out for Delivery!", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Slate900),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f).height(38.dp)
                                        ) {
                                            Text("Mark Out for Delivery", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Button(
                                            onClick = {
                                                selectedOrderForVerification = order
                                                showTrialSelectorModal = true
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (order.orderStatus == "Trial Started") Rose600 else Indigo600
                                            ),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth().height(38.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Icon(
                                                    imageVector = if (order.orderStatus == "Trial Started") Icons.Default.Timer else Icons.Default.PlayArrow,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Text(
                                                    text = if (order.orderStatus == "Trial Started") "RESUME & VERIFY CLOTHING" else "START TRIAL SESSION",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Black
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }
    }

    // Doorstep Trial Verification Screen / Dialog
    if (showTrialSelectorModal && selectedOrderForVerification != null) {
        val order = selectedOrderForVerification!!
        
        // Local state list to capture Kept/Returned status for each garment
        val itemStatuses = remember { mutableStateMapOf<String, String>().apply {
            order.items.forEach { itm ->
                this[itm.id] = itm.itemStatus // Initialise with existing or "Pending"
            }
        }}

        // Handle Start Trial Session
        if (order.orderStatus != "Trial Started") {
            LaunchedEffect(order.id) {
                val index = AppState.orders.indexOf(order)
                if (index != -1) {
                    AppState.orders[index].orderStatus = "Trial Started"
                    AppState.orders[index].trialStartedAt = System.currentTimeMillis()
                    AppState.orders[index].statusHistory.add(
                        StatusHistory(
                            id = "sh-${System.currentTimeMillis()}",
                            orderId = order.id,
                            status = "Trial Started",
                            changedBy = "Rider Associate",
                            changedAt = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).format(java.util.Date()),
                            notes = "Rider arrived at customer doorstep. Launched the 30-minute trials timer session."
                        )
                    )
                    AppState.saveState(context)
                }
            }
        }

        AlertDialog(
            onDismissRequest = { showTrialSelectorModal = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Doorstep Trial Verification", fontSize = 16.sp, fontWeight = FontWeight.Black)
                    IconButton(onClick = { showTrialSelectorModal = false }) {
                        Icon(Icons.Default.Close, "Close")
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Countdown Display
                    TryAtHomeCountdownTimer(order = order)

                    Text(
                        text = "GARMENTS TRIAL STATUS CHECKLIST",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    // Individual Item Selector
                    order.items.forEach { itm ->
                        val currentStatus = itemStatuses[itm.id] ?: "Pending"
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(
                                        model = itm.imageUrl,
                                        contentDescription = itm.productName,
                                        contentScale = ContentScale.Cover,
                                        modifier = Modifier.size(40.dp, 50.dp).clip(RoundedCornerShape(4.dp))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(itm.productName, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text("Size: ${itm.size} | Color: ${itm.color} | ₹${itm.price.toInt()}", fontSize = 10.sp, color = Color.Gray)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Keep vs Return buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { itemStatuses[itm.id] = "Kept" },
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (currentStatus == "Kept") Emerald700 else Color(0xFFE2E8F0)
                                        ),
                                        modifier = Modifier.weight(1f).height(32.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Icon(Icons.Default.Check, null, tint = if (currentStatus == "Kept") Color.White else Color.Gray, modifier = Modifier.size(12.dp))
                                            Text("KEPT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (currentStatus == "Kept") Color.White else Color.Gray)
                                        }
                                    }

                                    Button(
                                        onClick = { itemStatuses[itm.id] = "Returned" },
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (currentStatus == "Returned") Rose600 else Color(0xFFE2E8F0)
                                        ),
                                        modifier = Modifier.weight(1f).height(32.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Icon(Icons.Default.Close, null, tint = if (currentStatus == "Returned") Color.White else Color.Gray, modifier = Modifier.size(12.dp))
                                            Text("RETURNED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (currentStatus == "Returned") Color.White else Color.Gray)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Calculation breakdown based ONLY on KEPT garments
                    Divider(modifier = Modifier.padding(vertical = 6.dp))
                    
                    val keptCount = itemStatuses.values.count { it == "Kept" }
                    val keptTotal = order.items.sumOf { if (itemStatuses[it.id] == "Kept") it.price * it.quantity else 0.0 }
                    val finalPayableBill = keptTotal + 99.0 // Kept clothes + standard TryAtHome fee

                    Text("POST-TRIAL BILL CALCULATION", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Black)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Kept Clothes Total ($keptCount items)", fontSize = 11.sp)
                        Text("₹${keptTotal.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Doorstep Trial Booking Fee", fontSize = 11.sp)
                        Text("₹99", fontSize = 11.sp)
                    }
                    Divider()
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("TOTAL CASH/UPI COLLECTABLE", fontSize = 12.sp, fontWeight = FontWeight.Black)
                        Text("₹${finalPayableBill.toInt()}", fontSize = 15.sp, fontWeight = FontWeight.Black, color = Indigo600)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // All items must be verified
                        val allVerified = order.items.all { itemStatuses[it.id] == "Kept" || itemStatuses[it.id] == "Returned" }
                        if (!allVerified) {
                            Toast.makeText(context, "Please select trial status (Kept / Returned) for all garments!", Toast.LENGTH_LONG).show()
                        } else {
                            // Update order statuses
                            val index = AppState.orders.indexOf(order)
                            if (index != -1) {
                                val targetOrder = AppState.orders[index]
                                
                                // Save verification statuses back to order items
                                targetOrder.items.forEach { itm ->
                                    itm.itemStatus = itemStatuses[itm.id] ?: "Returned"
                                    
                                    // If returned, add item stock back to product boutique database!
                                    if (itm.itemStatus == "Returned") {
                                        val prod = AppState.products.find { it.id == itm.productId }
                                        if (prod != null) {
                                            prod.stock = prod.stock + itm.quantity
                                        }
                                    }
                                }

                                targetOrder.orderStatus = "Closed"
                                targetOrder.paymentStatus = "PAID"
                                targetOrder.updatedAt = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).format(java.util.Date())
                                
                                val keptTotalVal = targetOrder.items.sumOf { if (itemStatuses[it.id] == "Kept") it.price * it.quantity else 0.0 }
                                val collectedSum = keptTotalVal + 99.0
                                
                                targetOrder.statusHistory.add(
                                    StatusHistory(
                                        id = "sh-${System.currentTimeMillis()}",
                                        orderId = order.id,
                                        status = "Closed",
                                        changedBy = "Rider Associate",
                                        changedAt = targetOrder.updatedAt,
                                        notes = "Verified clothes doorstep trial. Kept: ${itemStatuses.values.count { it == "Kept" }} garments. Returned: ${itemStatuses.values.count { it == "Returned" }}. Collected total: ₹${collectedSum.toInt()} via ${targetOrder.paymentMethod}."
                                    )
                                )

                                AppState.saveState(context)
                                showTrialSelectorModal = false
                                Toast.makeText(context, "🎉 Doorstep trial session successfully closed! Collected ₹${collectedSum.toInt()}.", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Text("COLLECT PAY & CLOSE TRIAL")
                }
            }
        )
    }
}
