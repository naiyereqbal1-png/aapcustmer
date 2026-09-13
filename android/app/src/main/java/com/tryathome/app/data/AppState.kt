package com.tryathome.app.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object AppState {
    private const val PREFS_NAME = "tryathome_state_prefs"
    private val gson = Gson()

    // Observable Compose States
    val products = mutableStateListOf<Product>()
    val categories = mutableStateListOf<Category>()
    val orders = mutableStateListOf<Order>()
    val customers = mutableStateListOf<Customer>()
    val deliveryBoys = mutableStateListOf<DeliveryBoy>()
    val shopkeepers = mutableStateListOf<Shopkeeper>()
    val cart = mutableStateListOf<CartItem>()
    val wishlist = mutableStateListOf<WishlistItem>()
    
    var settings by mutableStateOf(StoreSettings())
    var currentSession by mutableStateOf<AuthSession?>(null)

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun initialize(context: Context) {
        val prefs = getPrefs(context)
        if (!prefs.contains("initialized")) {
            // First time seeding
            products.clear()
            products.addAll(MockData.products)

            categories.clear()
            categories.addAll(MockData.categories)

            orders.clear()
            orders.addAll(MockData.getInitialOrders())

            customers.clear()
            customers.addAll(MockData.getInitialCustomers())

            deliveryBoys.clear()
            deliveryBoys.addAll(MockData.getInitialDeliveryBoys())

            shopkeepers.clear()
            shopkeepers.addAll(MockData.getInitialShopkeepers())

            settings = StoreSettings()
            currentSession = null
            
            cart.clear()
            wishlist.clear()

            saveState(context)
            prefs.edit().putBoolean("initialized", true).apply()
        } else {
            loadState(context)
        }
    }

    fun saveState(context: Context) {
        val editor = getPrefs(context).edit()
        editor.putString("products", gson.toJson(products))
        editor.putString("categories", gson.toJson(categories))
        editor.putString("orders", gson.toJson(orders))
        editor.putString("customers", gson.toJson(customers))
        editor.putString("deliveryBoys", gson.toJson(deliveryBoys))
        editor.putString("shopkeepers", gson.toJson(shopkeepers))
        editor.putString("cart", gson.toJson(cart))
        editor.putString("wishlist", gson.toJson(wishlist))
        editor.putString("settings", gson.toJson(settings))
        editor.putString("currentSession", gson.toJson(currentSession))
        editor.apply()
    }

    private fun loadState(context: Context) {
        val prefs = getPrefs(context)
        try {
            val prodStr = prefs.getString("products", null)
            if (!prodStr.isNullOrEmpty()) {
                products.clear()
                val list: List<Product> = gson.fromJson(prodStr, object : TypeToken<List<Product>>() {}.type)
                products.addAll(list)
            }

            val catStr = prefs.getString("categories", null)
            if (!catStr.isNullOrEmpty()) {
                categories.clear()
                val list: List<Category> = gson.fromJson(catStr, object : TypeToken<List<Category>>() {}.type)
                categories.addAll(list)
            }

            val ordStr = prefs.getString("orders", null)
            if (!ordStr.isNullOrEmpty()) {
                orders.clear()
                val list: List<Order> = gson.fromJson(ordStr, object : TypeToken<List<Order>>() {}.type)
                orders.addAll(list)
            }

            val custStr = prefs.getString("customers", null)
            if (!custStr.isNullOrEmpty()) {
                customers.clear()
                val list: List<Customer> = gson.fromJson(custStr, object : TypeToken<List<Customer>>() {}.type)
                customers.addAll(list)
            }

            val dboyStr = prefs.getString("deliveryBoys", null)
            if (!dboyStr.isNullOrEmpty()) {
                deliveryBoys.clear()
                val list: List<DeliveryBoy> = gson.fromJson(dboyStr, object : TypeToken<List<DeliveryBoy>>() {}.type)
                deliveryBoys.addAll(list)
            }

            val shopStr = prefs.getString("shopkeepers", null)
            if (!shopStr.isNullOrEmpty()) {
                shopkeepers.clear()
                val list: List<Shopkeeper> = gson.fromJson(shopStr, object : TypeToken<List<Shopkeeper>>() {}.type)
                shopkeepers.addAll(list)
            }

            val cartStr = prefs.getString("cart", null)
            if (!cartStr.isNullOrEmpty()) {
                cart.clear()
                val list: List<CartItem> = gson.fromJson(cartStr, object : TypeToken<List<CartItem>>() {}.type)
                cart.addAll(list)
            }

            val wishStr = prefs.getString("wishlist", null)
            if (!wishStr.isNullOrEmpty()) {
                wishlist.clear()
                val list: List<WishlistItem> = gson.fromJson(wishStr, object : TypeToken<List<WishlistItem>>() {}.type)
                wishlist.addAll(list)
            }

            val setStr = prefs.getString("settings", null)
            if (!setStr.isNullOrEmpty()) {
                settings = gson.fromJson(setStr, StoreSettings::class.java)
            }

            val sessStr = prefs.getString("currentSession", null)
            if (!sessStr.isNullOrEmpty()) {
                currentSession = gson.fromJson(sessStr, AuthSession::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun logout(context: Context) {
        currentSession = null
        cart.clear()
        saveState(context)
    }

    fun login(context: Context, session: AuthSession) {
        currentSession = session
        // If customer, load or seed customer's address/profile if needed
        saveState(context)
    }
}
