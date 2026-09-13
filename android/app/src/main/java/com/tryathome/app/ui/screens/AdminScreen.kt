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
fun AdminScreen(
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var activeSubTab by remember { mutableStateOf("DASHBOARD") } // DASHBOARD, ORDERS, PRODUCTS, PARTNERS, SETTINGS

    // States from AppState
    val orders = AppState.orders
    val products = AppState.products
    val settings = AppState.settings
    val deliveryBoys = AppState.deliveryBoys
    val shopkeepers = AppState.shopkeepers

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
    ) {
        // Admin Navigation Title Bar
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
                            Icon(Icons.Default.AdminPanelSettings, "Admin", tint = Slate900, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("TRYatHOME Admin Console", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }

                    IconButton(onClick = {
                        AppState.logout(context)
                        onLogout()
                    }) {
                        Icon(Icons.Default.PowerSettingsNew, "Logout", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable sub tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("DASHBOARD", "ORDERS", "PRODUCTS", "PARTNERS", "SETTINGS").forEach { tab ->
                        val isSelected = activeSubTab == tab
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) Color.White.copy(alpha = 0.2f) else Color.Transparent)
                                .clickable { activeSubTab = tab }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(tab, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Render sections depending on sub tab
        Box(modifier = Modifier.weight(1f)) {
            when (activeSubTab) {
                "DASHBOARD" -> AdminDashboardTab()
                "ORDERS" -> AdminOrdersTab()
                "PRODUCTS" -> AdminProductsTab()
                "PARTNERS" -> AdminPartnersTab()
                "SETTINGS" -> AdminSettingsTab()
            }
        }
    }
}

@Composable
fun AdminDashboardTab() {
    val orders = AppState.orders
    val products = AppState.products
    val revenue = orders.filter { it.orderStatus == "Closed" }.sumOf { it.total }
    val trials = orders.count { it.orderStatus == "Trial Started" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("BUSINESS OVERVIEW", fontWeight = FontWeight.Black, fontSize = 13.sp, color = Slate900)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                DashboardCard(title = "Total Sales", value = "₹${revenue.toInt()}", icon = Icons.Default.CurrencyRupee, color = Emerald700)
            }
            Box(modifier = Modifier.weight(1f)) {
                DashboardCard(title = "Total Orders", value = "${orders.size}", icon = Icons.Default.ShoppingBag, color = Indigo600)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                DashboardCard(title = "Active Trials", value = "$trials", icon = Icons.Default.HourglassEmpty, color = Rose600)
            }
            Box(modifier = Modifier.weight(1f)) {
                DashboardCard(title = "Unique Products", value = "${products.size}", icon = Icons.Default.Inventory, color = Amber600)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("QUICK ACTION LOGS", fontWeight = FontWeight.Black, fontSize = 13.sp, color = Slate900)
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, "Log", tint = Indigo600, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Audit trail: Simulated secure admin logging active.", fontSize = 12.sp, color = Color.Gray)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudQueue, "Log", tint = Emerald700, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("API integrations: Razorpay, SMS Gateways healthy.", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun DashboardCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Black, color = Slate900)
        }
    }
}

@Composable
fun AdminOrdersTab() {
    val context = LocalContext.current
    val orders = AppState.orders
    val deliveryBoys = AppState.deliveryBoys
    val shopkeepers = AppState.shopkeepers

    var showAssignDialog by remember { mutableStateOf(false) }
    var selectedOrderToAssign by remember { mutableStateOf<Order?>(null) }
    var selectedDboyId by remember { mutableStateOf("") }
    var selectedShopId by remember { mutableStateOf("") }

    if (orders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No orders placed yet.", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(orders) { order ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFEDF2F7), RoundedCornerShape(10.dp))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("ID: ${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Cust: ${order.customerName} (${order.mobile})", fontSize = 11.sp, color = Color.Gray)
                                Text("Amt: ₹${order.total.toInt()} (${order.paymentMethod})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFFEF3C7), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(order.orderStatus.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFFB45309))
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp))

                        // Assigned details
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                val dboy = deliveryBoys.find { it.id == order.assignedDeliveryBoyId }?.name ?: "Unassigned"
                                val shop = shopkeepers.find { it.id == order.assignedShopkeeperId }?.storeName ?: "Unassigned"
                                Text("Assigned Shop: $shop", fontSize = 10.sp, color = Color.Gray)
                                Text("Assigned Rider: $dboy", fontSize = 10.sp, color = Color.Gray)
                            }

                            if (order.orderStatus == "Pending") {
                                Button(
                                    onClick = {
                                        selectedOrderToAssign = order
                                        selectedDboyId = deliveryBoys.firstOrNull()?.id ?: ""
                                        selectedShopId = shopkeepers.firstOrNull()?.id ?: ""
                                        showAssignDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Assign Partner", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAssignDialog && selectedOrderToAssign != null) {
        AlertDialog(
            onDismissRequest = { showAssignDialog = false },
            title = { Text("Assign Order Fulfillment", fontSize = 15.sp, fontWeight = FontWeight.Black) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Select boutique shop and delivery associate to dispatch this tryout order.", fontSize = 11.sp, color = Color.Gray)

                    // Shopkeeper Selector
                    Text("BOUTIQUE SHOP OWNER", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Column {
                        shopkeepers.forEach { sk ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedShopId = sk.id }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = selectedShopId == sk.id, onClick = { selectedShopId = sk.id })
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(sk.storeName, fontSize = 12.sp)
                            }
                        }
                    }

                    // Delivery Boy Selector
                    Text("DELIVERY ASSOCIATE RIDER", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Column {
                        deliveryBoys.forEach { db ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedDboyId = db.id }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = selectedDboyId == db.id, onClick = { selectedDboyId = db.id })
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("${db.name} (${db.assignedArea})", fontSize = 12.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val order = selectedOrderToAssign!!
                        order.assignedDeliveryBoyId = selectedDboyId
                        order.assignedShopkeeperId = selectedShopId
                        order.orderStatus = "Packed" // Mark as packed once assigned
                        
                        order.statusHistory.add(
                            StatusHistory(
                                id = "sh-${System.currentTimeMillis()}",
                                orderId = order.id,
                                status = "Packed",
                                changedBy = "Warehouse Admin",
                                changedAt = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).format(java.util.Date()),
                                notes = "Assigned shopkeeper & rider successfully."
                            )
                        )
                        
                        AppState.saveState(context)
                        showAssignDialog = false
                        Toast.makeText(context, "Order assigned and dispatch updated!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                ) {
                    Text("CONFIRM ASSIGNMENT")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAssignDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
}

@Composable
fun AdminProductsTab() {
    val context = LocalContext.current
    val products = AppState.products

    var showEditDialog by remember { mutableStateOf(false) }
    var selectedProductToEdit by remember { mutableStateOf<Product?>(null) }
    var editPriceInput by remember { mutableStateOf("") }
    var editStockInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Quick Excel Importer Bar
        Surface(
            color = Color(0xFFEFF6FF),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudUpload, "Excel", tint = Indigo600)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Excel Bulk Importer Catalog", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        // Simulate Excel Import
                        val excelProducts = listOf(
                            Product(
                                id = "p-excel-${System.currentTimeMillis()}",
                                name = "Imported Lucknowi Anarkali Kurta",
                                brand = "Ethnic Weaver",
                                description = "Elegant designer traditional Lucknowi silk Anarkali dress.",
                                sellingPrice = 2499.0,
                                mrp = 4999.0,
                                discountPercentage = 50,
                                rating = 4.7,
                                ratingCount = 45,
                                images = listOf(ProductImage("img-e1", "https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=600&q=80", true)),
                                sizes = listOf("M", "L", "XL"),
                                colors = listOf("Pink Silk"),
                                stock = 20,
                                categoryName = "Kurtis",
                                categorySlug = "kurtis"
                            )
                        )
                        AppState.products.addAll(excelProducts)
                        AppState.saveState(context)
                        Toast.makeText(context, "Successfully bulk imported 1 product from Excel catalog sheet!", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Slate900),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(30.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                ) {
                    Text("Import Excel", fontSize = 10.sp, color = Color.White)
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
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
                                Text("Price: ₹${prod.sellingPrice.toInt()}", fontSize = 11.sp, fontWeight = FontWeight.Black)
                                Text("Stock: ${prod.stock}", fontSize = 11.sp, color = Indigo600, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Edit stock/price button
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
    }

    if (showEditDialog && selectedProductToEdit != null) {
        val prod = selectedProductToEdit!!
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Garment Stats", fontSize = 14.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(prod.name, fontSize = 11.sp, color = Color.Gray)

                    OutlinedTextField(
                        value = editPriceInput,
                        onValueChange = { editPriceInput = it.filter { char -> char.isDigit() } },
                        label = { Text("Selling Price (₹)") },
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
                        Toast.makeText(context, "Product statistics updated!", Toast.LENGTH_SHORT).show()
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
fun AdminPartnersTab() {
    val deliveryBoys = AppState.deliveryBoys
    val shopkeepers = AppState.shopkeepers

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("AUTHORIZED SHOPKEEPERS (${shopkeepers.size})", fontWeight = FontWeight.Black, fontSize = 13.sp, color = Slate900)
        shopkeepers.forEach { sk ->
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFEDF2F7), RoundedCornerShape(8.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(sk.storeName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Owner: ${sk.name} | City: ${sk.city}", fontSize = 11.sp, color = Color.Gray)
                    Text("Live Products: ${sk.liveProducts} | Status: ACTIVE", fontSize = 11.sp, color = Emerald700, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text("DELIVERY RIDERS (${deliveryBoys.size})", fontWeight = FontWeight.Black, fontSize = 13.sp, color = Slate900)
        deliveryBoys.forEach { db ->
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFEDF2F7), RoundedCornerShape(8.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(db.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("★ ${db.rating}", fontSize = 12.sp, color = Amber500, fontWeight = FontWeight.Bold)
                    }
                    Text("Vehicle: ${db.vehicleType} (${db.vehicleNumber})", fontSize = 11.sp, color = Color.Gray)
                    Text("Assigned Zone: ${db.assignedArea}", fontSize = 11.sp, color = Color.DarkGray)
                }
            }
        }
    }
}

@Composable
fun AdminSettingsTab() {
    val context = LocalContext.current
    val settings = AppState.settings

    var storeNameInput by remember { mutableStateOf(settings.storeName) }
    var freeDelInput by remember { mutableStateOf(settings.freeDeliveryThreshold.toInt().toString()) }
    var delChargeInput by remember { mutableStateOf(settings.deliveryCharge.toInt().toString()) }
    var durationInput by remember { mutableStateOf(settings.tryAtHomeDurationMinutes.toString()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("SYSTEM SETTINGS", fontWeight = FontWeight.Black, fontSize = 13.sp, color = Slate900)

        OutlinedTextField(
            value = storeNameInput,
            onValueChange = { storeNameInput = it },
            label = { Text("Boutique Store Title Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = freeDelInput,
            onValueChange = { freeDelInput = it.filter { char -> char.isDigit() } },
            label = { Text("Free Delivery Threshold Limit (₹)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = delChargeInput,
            onValueChange = { delChargeInput = it.filter { char -> char.isDigit() } },
            label = { Text("Standard Delivery Charge (₹)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = durationInput,
            onValueChange = { durationInput = it.filter { char -> char.isDigit() } },
            label = { Text("Try-At-Home Slots Duration (Minutes)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                settings.storeName = storeNameInput
                settings.freeDeliveryThreshold = freeDelInput.toDoubleOrNull() ?: settings.freeDeliveryThreshold
                settings.deliveryCharge = delChargeInput.toDoubleOrNull() ?: settings.deliveryCharge
                settings.tryAtHomeDurationMinutes = durationInput.toIntOrNull() ?: settings.tryAtHomeDurationMinutes
                
                AppState.saveState(context)
                Toast.makeText(context, "Global configuration values persisted!", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Slate900),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("SAVE CONFIGURATIONS", fontWeight = FontWeight.Bold)
        }
    }
}
