package com.tryathome.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tryathome.app.data.AppState
import com.tryathome.app.data.Product
import com.tryathome.app.data.WishlistItem
import com.tryathome.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: Product,
    onBack: () -> Unit,
    onAddToCart: (Product, String, String, Int) -> Unit,
    onBuyNow: (Product, String, String, Int) -> Unit
) {
    val context = LocalContext.current
    val isWishlisted = AppState.wishlist.any { it.product.id == product.id }

    var selectedImageIdx by remember { mutableStateOf(0) }
    var selectedSize by remember { mutableStateOf(product.sizes.firstOrNull() ?: "M") }
    var selectedColor by remember { mutableStateOf(product.colors.firstOrNull() ?: "Default") }
    var quantity by remember { mutableStateOf(1) }
    var pincode by remember { mutableStateOf("560001") }
    var pincodeChecked by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Main Top Bar overlay
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .background(Color.LightGray.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Slate900)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = {
                            if (isWishlisted) {
                                AppState.wishlist.removeAll { it.product.id == product.id }
                                Toast.makeText(context, "Removed from Wishlist", Toast.LENGTH_SHORT).show()
                            } else {
                                AppState.wishlist.add(
                                    WishlistItem(
                                        id = "wish-${System.currentTimeMillis()}",
                                        product = product
                                    )
                                )
                                Toast.makeText(context, "Added to Wishlist ❤️", Toast.LENGTH_SHORT).show()
                            }
                            AppState.saveState(context)
                        },
                        modifier = Modifier
                            .background(Color.LightGray.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isWishlisted) Rose600 else Slate900
                        )
                    }
                }
            }

            // Image Gallery Stage
            val currentImageUrl = product.images.getOrNull(selectedImageIdx)?.imageUrl ?: ""
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .background(Color(0xFFF7FAFC))
            ) {
                AsyncImage(
                    model = currentImageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Cover,
                    modifier = Modifier.fillMaxSize()
                )

                // Image pagination dots
                if (product.images.size > 1) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        product.images.forEachIndexed { idx, _ ->
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (selectedImageIdx == idx) Indigo600 else Color.LightGray)
                                    .clickable { selectedImageIdx = idx }
                            )
                        }
                    }
                }
            }

            // Product Meta Information Block
            Column(modifier = Modifier.padding(16.dp)) {
                // Brand name
                Text(
                    text = product.brand.uppercase(),
                    color = Indigo600,
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Product Title
                Text(
                    text = product.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Ratings and reviews
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Emerald700, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = product.rating.toString(),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                    Text(
                        text = "${product.ratingCount} Ratings & Reviews",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Price display (Secured: No leak of shopkeeper cost price!)
                Surface(
                    color = Slate50,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFEDF2F7)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "₹${product.sellingPrice.toInt()}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Slate900
                            )
                            if (product.mrp > product.sellingPrice) {
                                Text(
                                    text = "₹${product.mrp.toInt()}",
                                    fontSize = 14.sp,
                                    textDecoration = TextDecoration.LineThrough,
                                    color = Color.Gray
                                )
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFE6F4EA), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${product.discountPercentage}% OFF",
                                        color = Emerald700,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Inclusive of all taxes • TRYatHOME Free trials included",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sizing Options Selector
                Text(
                    text = "Select Size: $selectedSize",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Slate900
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    product.sizes.forEach { size ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedSize == size) Slate900 else Color.White)
                                .border(1.dp, if (selectedSize == size) Slate900 else Color.LightGray, RoundedCornerShape(8.dp))
                                .clickable { selectedSize = size },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = size,
                                color = if (selectedSize == size) Color.White else Slate900,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Color Options Selector
                Text(
                    text = "Select Color: $selectedColor",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Slate900
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    product.colors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .border(1.dp, if (selectedColor == color) Indigo600 else Color.LightGray, RoundedCornerShape(8.dp))
                                .background(if (selectedColor == color) Color(0xFFEEF2FF) else Color.White)
                                .clickable { selectedColor = color }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = color,
                                color = if (selectedColor == color) Indigo600 else Slate900,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quantity selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Quantity:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Slate900
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text("-", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                        Text(
                            text = quantity.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        IconButton(
                            onClick = { if (quantity < product.stock) quantity++ },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Text("+", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Service check Pincode
                Column(
                    modifier = Modifier
                        .background(Slate50, RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFEDF2F7), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Check TRYatHOME Doorstep Service",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Slate900
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextField(
                            value = pincode,
                            onValueChange = { pincode = it.filter { char -> char.isDigit() } },
                            placeholder = { Text("Enter 6-digit Pincode", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                        Button(
                            onClick = { pincodeChecked = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Slate900),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Text("Check", fontSize = 12.sp, color = Color.White)
                        }
                    }

                    if (pincodeChecked) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Check, "Available", tint = Emerald700, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Try At Home trial slot available!", fontSize = 11.sp, color = Emerald700, fontWeight = FontWeight.Bold)
                            }
                            Text("Delivery & trial within 2-3 working days.", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Description specification
                Text(
                    text = "Product Specifications",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Slate900
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.description,
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    lineHeight = 18.sp
                )
                if (product.fabric.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Fabric: ${product.fabric}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate800
                    )
                }

                Spacer(modifier = Modifier.height(80.dp)) // Padding for CTA bar
            }
        }

        // Bottom Sticky Action Buttons
        Surface(
            color = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .border(1.dp, Color(0xFFEDF2F7))
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onAddToCart(product, selectedSize, selectedColor, quantity) },
                    colors = ButtonDefaults.buttonColors(containerColor = Slate900),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.ShoppingBag, "Cart", tint = Amber500)
                        Text("ADD TO BAG", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Button(
                    onClick = { onBuyNow(product, selectedSize, selectedColor, quantity) },
                    colors = ButtonDefaults.buttonColors(containerColor = Amber500),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Zap, "Buy", tint = Slate900)
                        Text("BUY NOW", color = Slate900, fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
