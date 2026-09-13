package com.tryathome.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tryathome.app.data.*
import com.tryathome.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopkeeperScreen(
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val currentShopkeeper = AppState.shopkeepers.find { it.mobile == AppState.currentSession?.mobile }
        ?: AppState.shopkeepers.firstOrNull() // Fallback if missing

    var activeSubTab by remember { mutableStateOf("DASHBOARD") } // DASHBOARD, INVENTORY, ORDERS

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
    ) {
        // Shopkeeper Header Title Bar
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
                            Icon(Icons.Default.Storefront, "Shopkeeper", tint = Slate900, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(currentShopkeeper?.storeName ?: "Ethnic Trends Portal", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }

                    IconButton(onClick = {
                        AppState.logout(context)
                        onLogout()
                    }) {
                        Icon(Icons.Default.PowerSettingsNew, "Logout", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Segment Tabs Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("DASHBOARD", "INVENTORY", "ORDERS").forEach { tab ->
                        val isSelected = activeSubTab == tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) Color.White.copy(alpha = 0.2f) else Color.Transparent)
                                .clickable { activeSubTab = tab }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(tab, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section body
        Box(modifier = Modifier.weight(1f)) {
            when (activeSubTab) {
                "DASHBOARD" -> ShopkeeperDashboardTab(currentShopkeeper)
                "INVENTORY" -> ShopkeeperInventoryTab()
                "ORDERS" -> ShopkeeperOrdersTab(currentShopkeeper?.id)
            }
        }
    }
}

@Composable
fun ShopkeeperDashboardTab(shopkeeper: Shopkeeper?) {
    val ordersCount = AppState.orders.count { it.assignedShopkeeperId == shopkeeper?.id }
    val pendingStock = AppState.products.sumOf { it.stock }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("STORE METRICS", fontWeight = FontWeight.Black, fontSize = 13.sp, color = Slate900)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                DashboardCard(title = "Boutique Products", value = "${AppState.products.size}", icon = Icons.Default.Inventory2, color = Indigo600)
            }
            Box(modifier = Modifier.weight(1f)) {
                DashboardCard(title = "Total Stocks Qty", value = "$pendingStock", icon = Icons.Default.Widgets, color = Amber600)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                DashboardCard(title = "Assigned Orders", value = "$ordersCount", icon = Icons.Default.ListAlt, color = Emerald700)
            }
            Box(modifier = Modifier.weight(1f)) {
                DashboardCard(title = "Store Rating", value = "★ 4.9", icon = Icons.Default.Star, color = Rose600)
            }
        }

        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(10.dp))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Authorized Boutique Partner Status: ACTIVE", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Emerald700)
                Spacer(modifier = Modifier.height(4.dp))
                Text("You are curating authentic apparel for TRYatHOME central wardrobes. Manage your stocks & package incoming orders instantly to maintain premium seller scores.", fontSize = 11.sp, color = Color.DarkGray, lineHeight = 16.sp)
            }
        }
    }
}

@Composable
fun ShopkeeperInventoryTab() {
    val context = LocalContext.current
    val products = AppState.products

    var showEditDialog by remember { mutableStateOf(false) }
    var selectedProductToEdit by remember { mutableStateOf<Product?>(null) }
    var editPriceInput by remember { mutableStateOf("") }
    var editStockInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(products) { prod ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFEDF2F7), RoundedCornerShape(10.dp))
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = prod.images.firstOrNull()?.imageUrl ?: "",
                        contentDescription = prod.name,
                        contentScale = ContentScale.Cover,
                        modifier = Modifier.size(48.dp, 58.dp).clip(RoundedCornerShape(6.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(prod.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("Category: ${prod.categoryName} | Brand: ${prod.brand}", fontSize = 10.sp, color = Color.Gray)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Boutique Price: ₹${prod.sellingPrice.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Black)
                            Text("Stock: ${prod.stock}", fontSize = 11.sp, color = Indigo600, fontWeight = FontWeight.Bold)
                        }
                    }

                    IconButton(onClick = {
                        selectedProductToEdit = prod
                        editPriceInput = prod.sellingPrice.toInt().toString()
                        editStockInput = prod.stock.toString()
                        showEditDialog = true
                    }) {
                        Icon(Icons.Default.Edit, "Edit", tint = Color.Gray, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }

    if (showEditDialog && selectedProductToEdit != null) {
        val prod = selectedProductToEdit!!
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Update Boutique Stocks", fontSize = 14.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(prod.name, fontSize = 11.sp, color = Color.Gray)

                    OutlinedTextField(
                        value = editPriceInput,
                        onValueChange = { editPriceInput = it.filter { char -> char.isDigit() } },
                        label = { Text("Boutique Price (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editStockInput,
                        onValueChange = { editStockInput = it.filter { char -> char.isDigit() } },
                        label = { Text("Available Stock Qty") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newPrice = editPriceInput.toDoubleOrNull() ?: prod.sellingPrice
                        val newStock = editStockInput.toIntOrNull() ?: prod.stock
                        
                        val index = AppState.products.indexOf(prod)
                        if (index != -1) {
                            AppState.products[index] = prod.copy(
                                sellingPrice = newPrice,
                                stock = newStock
                            )
                        }
                        AppState.saveState(context)
                        showEditDialog = false
                        Toast.makeText(context, "Boutique stocks adjusted!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                ) {
                    Text("SAVE CHANGES")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
}

@Composable
fun ShopkeeperOrdersTab(shopkeeperId: String?) {
    val context = LocalContext.current
    val keeperOrders = AppState.orders.filter { it.assignedShopkeeperId == shopkeeperId }

    if (keeperOrders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No orders assigned to your shop.", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(keeperOrders) { order ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFEDF2F7), RoundedCornerShape(10.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("ID: ${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Customer: ${order.customerName}", fontSize = 11.sp, color = Color.Gray)
                            }
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFFEF3C7), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(order.orderStatus.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp))

                        Text("ITEMS TO PACKAGE:", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        order.items.forEach { item ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, "Item", tint = Indigo600, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("${item.productName} (Size: ${item.size} | Color: ${item.color}) x ${item.quantity}", fontSize = 11.sp)
                            }
                        }

                        if (order.orderStatus == "Packed") {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    val index = AppState.orders.indexOf(order)
                                    if (index != -1) {
                                        AppState.orders[index].orderStatus = "Shipped" // Ready for rider
                                        AppState.orders[index].statusHistory.add(
                                            StatusHistory(
                                                id = "sh-${System.currentTimeMillis()}",
                                                orderId = order.id,
                                                status = "Shipped",
                                                changedBy = "Shopkeeper Partner",
                                                changedAt = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).format(java.util.Date()),
                                                notes = "Garments successfully packed & sealed by Boutique curate."
                                            )
                                        )
                                        AppState.saveState(context)
                                        Toast.makeText(context, "Order marked as Ready for Delivery Associate!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().height(40.dp)
                            ) {
                                Text("MARK AS PACKED & SEALED", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
