package com.tryathome.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.tryathome.app.data.AppState
import com.tryathome.app.data.CartItem
import com.tryathome.app.data.WishlistItem
import com.tryathome.app.ui.theme.*

@Composable
fun WishlistScreen(
    onShopMore: () -> Unit
) {
    val context = LocalContext.current
    val wishlistItems = AppState.wishlist

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
    ) {
        // Top Bar
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
                    text = "My Wishlist",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = Slate900,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        if (wishlistItems.isEmpty()) {
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
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Empty Wishlist",
                        tint = Color.LightGray,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Your wishlist is empty",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap the heart icon on any garment to save it here.",
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
                        Text("EXPLORE CATALOUGE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(wishlistItems) { item ->
                    WishlistItemCard(
                        item = item,
                        onRemove = {
                            AppState.wishlist.remove(item)
                            AppState.saveState(context)
                            Toast.makeText(context, "Removed from Wishlist", Toast.LENGTH_SHORT).show()
                        },
                        onMoveToBag = { size ->
                            val totalQty = AppState.cart.sumOf { it.quantity }
                            if (totalQty >= 5) {
                                Toast.makeText(context, "Order Limit Exceeded! Max 5 garments allowed in bag.", Toast.LENGTH_LONG).show()
                            } else {
                                val product = item.product
                                val existing = AppState.cart.find { it.product.id == product.id && it.size == size }
                                if (existing != null) {
                                    existing.quantity += 1
                                } else {
                                    AppState.cart.add(
                                        CartItem(
                                            id = "cart-item-${System.currentTimeMillis()}",
                                            product = product,
                                            size = size,
                                            color = product.colors.firstOrNull() ?: "Default",
                                            quantity = 1
                                        )
                                    )
                                }
                                AppState.wishlist.remove(item)
                                AppState.saveState(context)
                                Toast.makeText(context, "Moved to Bag!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WishlistItemCard(
    item: WishlistItem,
    onRemove: () -> Unit,
    onMoveToBag: (String) -> Unit
) {
    val product = item.product
    var selectedSize by remember { mutableStateOf(product.sizes.firstOrNull() ?: "M") }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFEDF2F7), RoundedCornerShape(12.dp))
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            // Image
            AsyncImage(
                model = product.images.firstOrNull()?.imageUrl ?: "",
                contentDescription = product.name,
                contentScale = ContentScale.Cover,
                modifier = Modifier
                    .size(80.dp, 100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF7FAFC))
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Info and size/add to bag section
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = product.brand.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Text(
                            text = product.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Slate900,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, "Delete", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                    }
                }

                // Price display
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                ) {
                    Text(
                        text = "₹${product.sellingPrice.toInt()}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Slate900
                    )
                    if (product.mrp > product.sellingPrice) {
                        Text(
                            text = "₹${product.mrp.toInt()}",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                        )
                    }
                }

                // Selector size & move button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Size Selector dropdown (or row)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Size:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        product.sizes.take(3).forEach { sz ->
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (selectedSize == sz) Slate900 else Color(0xFFF1F5F9))
                                    .border(1.dp, if (selectedSize == sz) Slate900 else Color.LightGray, RoundedCornerShape(4.dp))
                                    .clickable { selectedSize = sz },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = sz,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (selectedSize == sz) Color.White else Slate900
                                )
                            }
                        }
                    }

                    // Move to Bag button
                    Button(
                        onClick = { onMoveToBag(selectedSize) },
                        colors = ButtonDefaults.buttonColors(containerColor = Amber500),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("MOVE TO BAG", color = Slate900, fontSize = 9.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}
