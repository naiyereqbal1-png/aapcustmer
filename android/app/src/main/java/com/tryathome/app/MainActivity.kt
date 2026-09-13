package com.tryathome.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tryathome.app.data.*
import com.tryathome.app.ui.screens.*
import com.tryathome.app.ui.theme.Slate900
import com.tryathome.app.ui.theme.TRYatHOMETheme

enum class Screen {
    HOME,
    DETAIL,
    CART,
    CHECKOUT,
    WISHLIST,
    PROFILE
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Centralized Offline DB setup & initialization
        AppState.initialize(this)
        
        setContent {
            TRYatHOMETheme {
                TRYatHOMEApp()
            }
        }
    }
}

@Composable
fun TRYatHOMEApp() {
    val context = LocalContext.current
    val currentSession = AppState.currentSession

    // Check if authenticated
    if (currentSession == null) {
        TryAtHomeLoginScreen(
            onLoginSuccess = {
                // Compose state automatically recomposes on session change
            }
        )
    } else {
        // Authenticated viewport dispatcher based on user Role
        when (currentSession.role) {
            "ADMIN" -> {
                AdminScreen(onLogout = { /* recomposes automatically */ })
            }
            "SHOPKEEPER" -> {
                ShopkeeperScreen(onLogout = { /* recomposes automatically */ })
            }
            "DELIVERY_BOY" -> {
                DeliveryBoyScreen(onLogout = { /* recomposes automatically */ })
            }
            "CUSTOMER" -> {
                CustomerPortalFlow()
            }
            else -> {
                TryAtHomeLoginScreen(onLoginSuccess = {})
            }
        }
    }
}

@Composable
fun CustomerPortalFlow() {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var activeTab by remember { mutableStateOf("HOME") }

    val cartItems = AppState.cart

    // Navigation and Add-To-Bag routines
    val navigateToDetail: (Product) -> Unit = { product ->
        selectedProduct = product
        currentScreen = Screen.DETAIL
    }

    val handleAddToCart: (Product, String, String, Int) -> Unit = { product, size, color, qty ->
        val totalQty = cartItems.sumOf { it.quantity }
        if (totalQty + qty > 5) {
            Toast.makeText(context, "Order Limit Exceeded! Max 5 garments allowed in bag.", Toast.LENGTH_LONG).show()
        } else {
            val existingItem = cartItems.find { it.product.id == product.id && it.size == size && it.color == color }
            if (existingItem != null) {
                existingItem.quantity += qty
            } else {
                cartItems.add(CartItem(
                    id = "cart-item-${System.currentTimeMillis()}",
                    product = product,
                    size = size,
                    color = color,
                    quantity = qty
                ))
            }
            AppState.saveState(context)
            Toast.makeText(context, "Added to bag successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    val handleBuyNow: (Product, String, String, Int) -> Unit = { product, size, color, qty ->
        val totalQty = cartItems.sumOf { it.quantity }
        if (totalQty + qty > 5) {
            Toast.makeText(context, "Order Limit Exceeded! Max 5 garments allowed in bag.", Toast.LENGTH_LONG).show()
        } else {
            val existingItem = cartItems.find { it.product.id == product.id && it.size == size && it.color == color }
            if (existingItem == null) {
                cartItems.add(CartItem(
                    id = "cart-item-${System.currentTimeMillis()}",
                    product = product,
                    size = size,
                    color = color,
                    quantity = qty
                ))
            }
            AppState.saveState(context)
            currentScreen = Screen.CART
            activeTab = "CART"
        }
    }

    Scaffold(
        bottomBar = {
            if (currentScreen != Screen.CHECKOUT) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = activeTab == "HOME" && currentScreen == Screen.HOME,
                        onClick = {
                            activeTab = "HOME"
                            currentScreen = Screen.HOME
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )

                    NavigationBarItem(
                        selected = activeTab == "WISHLIST" && currentScreen == Screen.WISHLIST,
                        onClick = {
                            activeTab = "WISHLIST"
                            currentScreen = Screen.WISHLIST
                        },
                        icon = { Icon(Icons.Default.Favorite, contentDescription = "Wishlist") },
                        label = { Text("Wishlist") }
                    )

                    NavigationBarItem(
                        selected = activeTab == "CART" && currentScreen == Screen.CART,
                        onClick = {
                            activeTab = "CART"
                            currentScreen = Screen.CART
                        },
                        icon = {
                            BadgedBox(badge = {
                                if (cartItems.isNotEmpty()) {
                                    Badge(containerColor = Color(0xFFE11D48)) {
                                        Text(cartItems.sumOf { it.quantity }.toString(), color = Color.White)
                                    }
                                }
                            }) {
                                Icon(Icons.Default.ShoppingBag, contentDescription = "Bag")
                            }
                        },
                        label = { Text("Bag") }
                    )

                    NavigationBarItem(
                        selected = activeTab == "PROFILE" && currentScreen == Screen.PROFILE,
                        onClick = {
                            activeTab = "PROFILE"
                            currentScreen = Screen.PROFILE
                        },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile") }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.HOME -> {
                    HomeScreen(
                        onProductSelected = navigateToDetail,
                        onAddToCart = { product, sz, col -> handleAddToCart(product, sz, col, 1) },
                        onBuyNow = { product, sz, col -> handleBuyNow(product, sz, col, 1) }
                    )
                }
                Screen.DETAIL -> {
                    selectedProduct?.let { product ->
                        ProductDetailScreen(
                            product = product,
                            onBack = { currentScreen = Screen.HOME },
                            onAddToCart = handleAddToCart,
                            onBuyNow = handleBuyNow
                        )
                    } ?: run {
                        currentScreen = Screen.HOME
                    }
                }
                Screen.CART -> {
                    CartScreen(
                        cartItems = cartItems,
                        onUpdateQuantity = { item, newQty ->
                            if (newQty <= 0) {
                                cartItems.remove(item)
                            } else {
                                val itemIndex = cartItems.indexOf(item)
                                if (itemIndex != -1) {
                                    cartItems[itemIndex] = item.copy(quantity = newQty)
                                }
                            }
                            AppState.saveState(context)
                        },
                        onRemoveItem = {
                            cartItems.remove(it)
                            AppState.saveState(context)
                        },
                        onCheckout = {
                            currentScreen = Screen.CHECKOUT
                        },
                        onShopMore = {
                            currentScreen = Screen.HOME
                            activeTab = "HOME"
                        }
                    )
                }
                Screen.WISHLIST -> {
                    WishlistScreen(
                        onShopMore = {
                            currentScreen = Screen.HOME
                            activeTab = "HOME"
                        }
                    )
                }
                Screen.PROFILE -> {
                    ProfileScreen(
                        onLogout = {
                            // Compose state automatically routes to login screen
                        }
                    )
                }
                Screen.CHECKOUT -> {
                    CheckoutScreen(
                        onOrderPlaced = { _ ->
                            currentScreen = Screen.PROFILE
                            activeTab = "PROFILE"
                        },
                        onBack = {
                            currentScreen = Screen.CART
                            activeTab = "CART"
                        }
                    )
                }
            }
        }
    }
}
