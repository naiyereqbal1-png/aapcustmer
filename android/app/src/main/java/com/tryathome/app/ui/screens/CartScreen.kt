package com.tryathome.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tryathome.app.data.CartItem
import com.tryathome.app.data.Product
import com.tryathome.app.ui.theme.*

@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    onUpdateQuantity: (CartItem, Int) -> Unit,
    onRemoveItem: (CartItem) -> Unit,
    onCheckout: () -> Unit,
    onShopMore: () -> Unit
) {
    val totalQuantity = cartItems.sumOf { it.quantity }
    val subtotal = cartItems.sumOf { it.product.mrp * it.quantity }
    val finalTotal = cartItems.sumOf { it.product.sellingPrice * it.quantity }
    val discount = subtotal - finalTotal
    val deliveryCharge = if (finalTotal >= 499 || finalTotal == 0.0) 0.0 else 49.0
    val totalPayable = finalTotal + deliveryCharge

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
    ) {
        // Simple Top Bar
        Surface(
            color = Color.White,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Shopping Bag",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = Slate900,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        // Limit Banner Notice (Mandatory 5 item check!)
        Surface(
            color = Color(0xFFFEF3C7), // Amber warning bg
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = Amber600,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Order Limit: Max 5 items",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF78350F)
                    )
                }

                Text(
                    text = "Bag Status: $totalQuantity/5",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = if (totalQuantity >= 5) Rose600 else Slate900
                )
            }
        }

        // Items Stack
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Empty",
                        tint = Color.LightGray,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Your bag is empty",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Explore stylish Indian apparel curated for you.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onShopMore,
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("SHOP GARMENTS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(cartItems) { item ->
                    CartItemCard(
                        item = item,
                        onIncrease = { onUpdateQuantity(item, item.quantity + 1) },
                        onDecrease = { onUpdateQuantity(item, item.quantity - 1) },
                        onRemove = { onRemoveItem(item) },
                        increaseEnabled = totalQuantity < 5
                    )
                }

                // Pricing Summary Box
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "PRICE DETAILS (${cartItems.size} Items)",
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                color = Color.Gray,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total MRP", fontSize = 12.sp, color = Slate800)
                                Text("₹${subtotal.toInt()}", fontSize = 12.sp, color = Slate900)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Product Discount", fontSize = 12.sp, color = Emerald700, fontWeight = FontWeight.Bold)
                                Text("-₹${discount.toInt()}", fontSize = 12.sp, color = Emerald700, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Delivery Fee", fontSize = 12.sp, color = Slate800)
                                Text(
                                    text = if (deliveryCharge == 0.0) "FREE" else "₹${deliveryCharge.toInt()}",
                                    fontSize = 12.sp,
                                    fontWeight = if (deliveryCharge == 0.0) FontWeight.Bold else FontWeight.Normal,
                                    color = if (deliveryCharge == 0.0) Emerald700 else Slate900
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = Color(0xFFEDF2F7))
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total Amount", fontSize = 14.sp, fontWeight = FontWeight.Black, color = Slate900)
                                Text("₹${totalPayable.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Indigo600)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(60.dp)) // Cushion spacer
                }
            }

            // Checkout bottom button
            Surface(
                color = Color.White,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .border(1.dp, Color(0xFFEDF2F7))
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Button(
                        onClick = onCheckout,
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo600),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text("PROCEED TO CHECKOUT", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
    increaseEnabled: Boolean
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFEDF2F7), RoundedCornerShape(12.dp))
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            // Image thumbnail
            AsyncImage(
                model = item.product.images.firstOrNull()?.imageUrl ?: "",
                contentDescription = item.product.name,
                contentScale = ContentScale.Cover,
                modifier = Modifier
                    .size(72.dp, 88.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF7FAFC))
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Details Column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.brand.uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Text(
                    text = item.product.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Selected variant
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("Size: ${item.size}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate800)
                    }
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(item.color, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate800)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Price and quantity selector row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₹${(item.product.sellingPrice * item.quantity).toInt()}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Slate900
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(6.dp))
                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(6.dp))
                    ) {
                        IconButton(
                            onClick = onDecrease,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Text("-", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = item.quantity.toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(
                            onClick = onIncrease,
                            enabled = increaseEnabled,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Text(
                                text = "+",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (increaseEnabled) Slate900 else Color.LightGray
                            )
                        }
                    }

                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Delete, "Delete", tint = Color.LightGray, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
