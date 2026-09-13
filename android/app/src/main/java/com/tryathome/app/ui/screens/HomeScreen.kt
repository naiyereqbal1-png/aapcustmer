package com.tryathome.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tryathome.app.data.AppState
import com.tryathome.app.data.Category
import com.tryathome.app.data.Product
import com.tryathome.app.data.WishlistItem
import com.tryathome.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onProductSelected: (Product) -> Unit,
    onAddToCart: (Product, String, String) -> Unit,
    onBuyNow: (Product, String, String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategorySlug by remember { mutableStateOf("") }

    // Filter Products from the live AppState.products
    val filteredProducts = remember(searchQuery, selectedCategorySlug, AppState.products.size) {
        AppState.products.filter { product ->
            val matchesCategory = selectedCategorySlug.isEmpty() || product.categorySlug == selectedCategorySlug
            val matchesSearch = searchQuery.isEmpty() || 
                    product.name.contains(searchQuery, ignoreCase = true) ||
                    product.brand.contains(searchQuery, ignoreCase = true) ||
                    product.categoryName.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
    ) {
        // App Header & Search Bar
        Surface(
            color = Slate900,
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Brand Title & Tagline
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "TRYat",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "HOME",
                            color = Amber500,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Search Bar
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search elegant kurtis, denim, shirts...", fontSize = 13.sp, color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }
        }

        // Home Scrollable Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Promo Banner Section (Hidden when searching/filtering)
            if (searchQuery.isEmpty() && selectedCategorySlug.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(130.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Slate900, Slate800, Color(0xFF311042))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.65f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "FESTIVE SALE",
                            color = Amber500,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Direct From Indian Looms",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Up to 50% Off • Try at Home COD",
                            color = Color.LightGray,
                            fontSize = 11.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                    )
                }
            }

            // Categories Row (Story-style circle carousel)
            Text(
                text = "Shop By Category",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp,
                color = Slate900,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    CategoryItemCircle(
                        categoryName = "All Items",
                        imageUrl = "",
                        isActive = selectedCategorySlug.isEmpty(),
                        onClick = { selectedCategorySlug = "" }
                    )
                }

                items(AppState.categories) { category ->
                    CategoryItemCircle(
                        categoryName = category.name,
                        imageUrl = category.imageUrl,
                        isActive = selectedCategorySlug == category.slug,
                        onClick = { selectedCategorySlug = category.slug }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Products Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedCategorySlug.isNotEmpty()) {
                        AppState.categories.firstOrNull { it.slug == selectedCategorySlug }?.name ?: "Products"
                    } else "All Fashion Garments",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = Slate900
                )
                Text(
                    text = "${filteredProducts.size} Items",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }

            // Grid of products (Simulated responsive layout via non-nested column)
            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = "Empty",
                            tint = Color.LightGray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No matching garments found.", fontSize = 13.sp, color = Color.Gray)
                    }
                }
            } else {
                filteredProducts.chunked(2).forEach { rowProducts ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (product in rowProducts) {
                            Box(modifier = Modifier.weight(1f)) {
                                ProductGridCard(
                                    product = product,
                                    onClick = { onProductSelected(product) },
                                    onAddToCart = { onAddToCart(product, "M", "Default") },
                                    onBuyNow = { onBuyNow(product, "M", "Default") }
                                )
                            }
                        }
                        if (rowProducts.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun CategoryItemCircle(
    categoryName: String,
    imageUrl: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .width(68.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(
                    width = if (isActive) 2.5.dp else 1.dp,
                    color = if (isActive) Indigo600 else Color(0xFFE2E8F0),
                    shape = CircleShape
                )
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            if (imageUrl.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Slate900),
                    contentAlignment = Alignment.Center
                ) {
                    Text("ALL", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            } else {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = categoryName,
                    contentScale = ContentScale.Cover,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = categoryName,
            fontSize = 11.sp,
            fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium,
            color = if (isActive) Indigo600 else Slate900,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ProductGridCard(
    product: Product,
    onClick: () -> Unit,
    onAddToCart: () -> Unit,
    onBuyNow: () -> Unit
) {
    val context = LocalContext.current
    val isWishlisted = AppState.wishlist.any { it.product.id == product.id }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(1.dp, Color(0xFFEDF2F7), RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.85f)
                    .background(Color(0xFFF7FAFC))
            ) {
                AsyncImage(
                    model = product.images.firstOrNull()?.imageUrl ?: "",
                    contentDescription = product.name,
                    contentScale = ContentScale.Cover,
                    modifier = Modifier.fillMaxSize()
                )

                // Discount tag
                if (product.discountPercentage > 0) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = Rose600,
                                shape = RoundedCornerShape(topStart = 0.dp, bottomEnd = 6.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${product.discountPercentage}% OFF",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                // Wishlist Toggle (Interactive Heart Button)
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
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(28.dp)
                        .background(Color.White.copy(alpha = 0.85f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isWishlisted) Rose600 else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Info Content
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = product.brand.uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 0.5.sp
                )

                Text(
                    text = product.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.height(34.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Ratings Block
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(color = Emerald700, shape = RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = product.rating.toString(),
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                              )
                            Spacer(modifier = Modifier.width(1.dp))
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(8.dp)
                            )
                        }
                    }
                    Text(
                        text = "(${product.ratingCount})",
                        fontSize = 9.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Price Layout
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
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
                            textDecoration = TextDecoration.LineThrough,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        onClick = onAddToCart,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp)
                    ) {
                        Text("Add", color = Slate900, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onBuyNow,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Amber500),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp)
                    ) {
                        Text("Buy", color = Slate900, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}
