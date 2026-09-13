package com.tryathome.app.data

data class Category(
    val id: String,
    val name: String,
    val slug: String,
    val imageUrl: String,
    val status: String = "ACTIVE"
)

data class ProductImage(
    val id: String,
    val imageUrl: String,
    val isPrimary: Boolean = false,
    val caption: String = ""
)

data class Product(
    val id: String,
    val name: String,
    val brand: String,
    val description: String,
    var sellingPrice: Double, // Matches admin_selling_price/selling_price strictly. Cost prices are fully hidden!
    var mrp: Double,
    val discountPercentage: Int,
    val rating: Double,
    val ratingCount: Int,
    val images: List<ProductImage>,
    val sizes: List<String>,
    val colors: List<String>,
    var stock: Int,
    val categoryName: String,
    val categorySlug: String,
    val fabric: String = "",
    var status: String = "ACTIVE" // ACTIVE or INACTIVE
)

data class CartItem(
    val id: String,
    val product: Product,
    val size: String,
    val color: String,
    var quantity: Int
)

data class WishlistItem(
    val id: String,
    val product: Product,
    var size: String = "M"
)

data class CustomerAddress(
    val id: String,
    val customerId: String,
    val name: String,
    val mobile: String,
    val pincode: String,
    val address: String,
    val locality: String = "",
    val city: String,
    val state: String,
    val landmark: String = "",
    val addressType: String = "HOME", // HOME or WORK
    var isDefault: Boolean = false
)

data class Customer(
    val id: String,
    val customerId: String,
    val name: String,
    val mobile: String,
    val email: String,
    val createdAt: String,
    val status: String = "ACTIVE",
    var totalOrders: Int = 0,
    var totalSpent: Double = 0.0,
    var lastOrderAt: String = "",
    val addresses: MutableList<CustomerAddress> = mutableListOf()
)

data class StatusHistory(
    val id: String,
    val orderId: String,
    val status: String,
    val changedBy: String,
    val changedAt: String,
    val notes: String = ""
)

data class OrderItem(
    val id: String,
    val orderId: String,
    val productId: String,
    val productName: String,
    val brand: String,
    val quantity: Int,
    val price: Double,
    val mrp: Double,
    val size: String,
    val color: String,
    val imageUrl: String,
    var itemStatus: String = "Pending" // Pending, Kept, Returned
)

data class Order(
    val id: String,
    val orderId: String,
    val customerId: String,
    val customerName: String,
    val mobile: String,
    val email: String,
    val address: CustomerAddress,
    val items: List<OrderItem>,
    val subtotal: Double,
    val discount: Double,
    val deliveryCharge: Double,
    val taxAmount: Double,
    val total: Double,
    val paymentMethod: String, // COD or ONLINE
    var paymentStatus: String, // PENDING or PAID
    var orderStatus: String, // Pending, Packed, Shipped, Out for Delivery, Trial Started, Closed, Cancelled
    val createdAt: String,
    var updatedAt: String,
    var trackingNumber: String = "",
    var courierPartner: String = "",
    val statusHistory: MutableList<StatusHistory> = mutableListOf(),
    var assignedDeliveryBoyId: String? = null,
    var assignedShopkeeperId: String? = null,
    var trialStartedAt: Long? = null, // timestamp when trial started
    var trialDurationMinutes: Int = 30
)

data class DeliveryBoy(
    val id: String,
    val deliveryBoyId: String,
    val name: String,
    val mobile: String,
    val email: String,
    val vehicleType: String,
    val vehicleNumber: String,
    var status: String = "ACTIVE",
    val assignedArea: String,
    val createdAt: String,
    var totalDelivered: Int = 0,
    var rating: Double = 4.8
)

data class Shopkeeper(
    val id: String,
    val shopkeeperId: String,
    val name: String,
    val storeName: String,
    val mobile: String,
    val email: String,
    val city: String,
    var status: String = "ACTIVE",
    val createdAt: String,
    var totalProducts: Int = 0,
    var liveProducts: Int = 0,
    var pendingProducts: Int = 0,
    var currentStock: Int = 0,
    var totalOrders: Int = 0
)

data class StoreSettings(
    var storeName: String = "TRYatHOME",
    var storeTagline: String = "India’s Modern Garment & Fashion Destination • Try at Home",
    var contactEmail: String = "care@tryathome.in",
    var contactPhone: String = "+91 98765 43210",
    var deliveryCharge: Double = 49.0,
    var freeDeliveryThreshold: Double = 499.0,
    var codEnabled: Boolean = true,
    var onlinePaymentEnabled: Boolean = true,
    var minOrderValue: Double = 199.0,
    var gstPercentage: Double = 5.0,
    var currency: String = "INR",
    var currencySymbol: String = "₹",
    var tryAtHomeDurationMinutes: Int = 30,
    var tryAtHomeAutoCloseOnExpiry: Boolean = true,
    var tryAtHomeCharge: Double = 99.0
)

data class AuthSession(
    val id: String,
    val role: String, // CUSTOMER, ADMIN, SHOPKEEPER, DELIVERY_BOY
    val name: String,
    val mobile: String,
    val email: String,
    val token: String
)

object MockData {
    val categories = listOf(
        Category("cat-1", "Jeans", "jeans", "https://images.unsplash.com/photo-1542272604-787c3835535d?w=600&q=80"),
        Category("cat-2", "Shirts", "shirts", "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=600&q=80"),
        Category("cat-3", "Kurtis", "kurtis", "https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=600&q=80"),
        Category("cat-4", "T-Shirts", "t-shirts", "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=600&q=80")
    )

    val products = mutableListOf(
        Product(
            id = "p-1",
            name = "Premium Indigo Slim Fit Denim Jeans",
            brand = "Roadster",
            description = "Handcrafted pure cotton indigo jeans, perfect for everyday casual wear. Features durable stitching, premium washed finish, and classic five-pocket styling.",
            sellingPrice = 1299.0,
            mrp = 2499.0,
            discountPercentage = 48,
            rating = 4.4,
            ratingCount = 128,
            images = listOf(
                ProductImage("img-1a", "https://images.unsplash.com/photo-1542272604-787c3835535d?w=600&q=80", true, "Front View"),
                ProductImage("img-1b", "https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=600&q=80", false, "Back View")
            ),
            sizes = listOf("S", "M", "L", "XL"),
            colors = listOf("Indigo Blue", "Sky Blue"),
            stock = 12,
            categoryName = "Jeans",
            categorySlug = "jeans",
            fabric = "100% Cotton Denim"
        ),
        Product(
            id = "p-2",
            name = "Pure Linen Mandarin Collar Shirt",
            brand = "Wrogn",
            description = "Lightweight and highly breathable pure linen shirt. Features a modern mandarin band collar, premium button-down front, and curved hemline.",
            sellingPrice = 1499.0,
            mrp = 2999.0,
            discountPercentage = 50,
            rating = 4.2,
            ratingCount = 94,
            images = listOf(
                ProductImage("img-2a", "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=600&q=80", true, "Front View")
            ),
            sizes = listOf("M", "L", "XL"),
            colors = listOf("White", "Linen Grey"),
            stock = 4,
            categoryName = "Shirts",
            categorySlug = "shirts",
            fabric = "100% Linen"
        ),
        Product(
            id = "p-3",
            name = "Lucknowi Chikankari Hand Embroidered Kurti",
            brand = "Anouk",
            description = "Traditional Lucknowi Chikankari hand embroidered kurti in pure georgette fabric. Adorned with beautiful floral motifs and intricate thread work.",
            sellingPrice = 1899.0,
            mrp = 3999.0,
            discountPercentage = 52,
            rating = 4.6,
            ratingCount = 215,
            images = listOf(
                ProductImage("img-3a", "https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=600&q=80", true, "Front View")
            ),
            sizes = listOf("S", "M", "L", "XL", "XXL"),
            colors = listOf("Peach", "Mint Green"),
            stock = 15,
            categoryName = "Kurtis",
            categorySlug = "kurtis",
            fabric = "Georgette with Inner"
        ),
        Product(
            id = "p-4",
            name = "Oversized Streetwear Graphic Tee",
            brand = "HRX",
            description = "Heavyweight 240 GSM pre-shrunk cotton oversized t-shirt. Features a vibrant, high-density chest print and comfortable drop-shoulder silhouette.",
            sellingPrice = 799.0,
            mrp = 1499.0,
            discountPercentage = 46,
            rating = 4.1,
            ratingCount = 82,
            images = listOf(
                ProductImage("img-4a", "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=600&q=80", true, "Front View")
            ),
            sizes = listOf("S", "M", "L", "XL"),
            colors = listOf("Charcoal Black", "Vintage White"),
            stock = 8,
            categoryName = "T-Shirts",
            categorySlug = "t-shirts",
            fabric = "100% Premium Cotton"
        )
    )

    fun getInitialCustomers(): List<Customer> {
        val c1 = Customer(
            id = "cust-1",
            customerId = "STYLE1-CUST-000001",
            name = "Aarav Sharma",
            mobile = "9876543210",
            email = "aarav.sharma@example.com",
            createdAt = "2026-08-12T10:00:00Z",
            totalOrders = 2,
            totalSpent = 3198.0,
            lastOrderAt = "2026-09-10T12:00:00Z"
        )
        c1.addresses.addAll(listOf(
            CustomerAddress(
                id = "addr-1",
                customerId = "STYLE1-CUST-000001",
                name = "Aarav Sharma",
                mobile = "9876543210",
                pincode = "560001",
                address = "Flat 402, Sunshine Heights, MG Road",
                locality = "Near Trinity Metro Station",
                city = "Bengaluru",
                state = "Karnataka",
                landmark = "Opposite Taj Vivanta",
                addressType = "HOME",
                isDefault = true
            ),
            CustomerAddress(
                id = "addr-2",
                customerId = "STYLE1-CUST-000001",
                name = "Aarav Sharma (Office)",
                mobile = "9876543210",
                pincode = "560103",
                address = "Tech Park 5B, Outer Ring Road, Bellandur",
                city = "Bengaluru",
                state = "Karnataka",
                addressType = "WORK",
                isDefault = false
            )
        ))

        val c2 = Customer(
            id = "cust-2",
            customerId = "STYLE1-CUST-000002",
            name = "Priya Patel",
            mobile = "9898989898",
            email = "priya.patel@example.com",
            createdAt = "2026-08-28T14:30:00Z",
            totalOrders = 1,
            totalSpent = 1499.0,
            lastOrderAt = "2026-09-07T15:00:00Z"
        )
        c2.addresses.add(
            CustomerAddress(
                id = "addr-3",
                customerId = "STYLE1-CUST-000002",
                name = "Priya Patel",
                mobile = "9898989898",
                pincode = "380009",
                address = "A-12 Nilgiri Apartments, Navrangpura",
                city = "Ahmedabad",
                state = "Gujarat",
                addressType = "HOME",
                isDefault = true
            )
        )
        return listOf(c1, c2)
    }

    fun getInitialDeliveryBoys(): List<DeliveryBoy> = listOf(
        DeliveryBoy("dboy-1", "STYLE1-DBOY-000001", "Ramesh Kumar", "9876543201", "ramesh.delivery@style1.in", "Motorcycle", "KA-01-AB-1234", "ACTIVE", "Indiranagar & Central Bengaluru", "2026-08-12T10:00:00Z", 42, 4.9),
        DeliveryBoy("dboy-2", "STYLE1-DBOY-000002", "Sunil Verma", "9876543202", "sunil.delivery@style1.in", "Scooter", "KA-05-XY-5678", "ACTIVE", "Koramangala & HSR Layout", "2026-08-15T11:00:00Z", 29, 4.8),
        DeliveryBoy("dboy-3", "STYLE1-DBOY-000003", "Vicky Patil", "9876543203", "vicky.delivery@style1.in", "Scooter", "KA-03-MN-9012", "ACTIVE", "Whitefield & Bellandur", "2026-08-20T09:30:00Z", 18, 4.7)
    )

    fun getInitialShopkeepers(): List<Shopkeeper> = listOf(
        Shopkeeper("shop-1", "STYLE1-SHOP-000001", "Rajesh Mehra", "Rajesh Ethnic Trends", "9810101010", "rajesh.mehra@tryathome.in", "Jaipur", "ACTIVE", "2026-08-15T12:00:00Z", 3, 2, 1, 85, 8)
    )

    fun getInitialOrders(): List<Order> {
        val sampleAddress = CustomerAddress(
            id = "addr-1",
            customerId = "STYLE1-CUST-000001",
            name = "Aarav Sharma",
            mobile = "9876543210",
            pincode = "560001",
            address = "Flat 402, Sunshine Heights, MG Road",
            city = "Bengaluru",
            state = "Karnataka",
            addressType = "HOME",
            isDefault = true
        )
        val item1 = OrderItem("oi-1", "ord-1", "p-1", "Premium Indigo Slim Fit Denim Jeans", "Roadster", 1, 1299.0, 2499.0, "M", "Indigo Blue", "https://images.unsplash.com/photo-1542272604-787c3835535d?w=600&q=80", "Kept")
        val item2 = OrderItem("oi-2", "ord-1", "p-4", "Oversized Streetwear Graphic Tee", "HRX", 1, 799.0, 1499.0, "L", "Charcoal Black", "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=600&q=80", "Returned")

        val o1 = Order(
            id = "ord-1",
            orderId = "STYLE1-ORD-000001",
            customerId = "STYLE1-CUST-000001",
            customerName = "Aarav Sharma",
            mobile = "9876543210",
            email = "aarav.sharma@example.com",
            address = sampleAddress,
            items = listOf(item1, item2),
            subtotal = 2098.0,
            discount = 1900.0,
            deliveryCharge = 0.0,
            taxAmount = 100.0,
            total = 2198.0,
            paymentMethod = "COD",
            paymentStatus = "PAID",
            orderStatus = "Closed",
            createdAt = "2026-09-08T15:00:00Z",
            updatedAt = "2026-09-08T15:45:00Z",
            trackingNumber = "ECOM-BLR-98214",
            courierPartner = "BlueDart Express",
            assignedDeliveryBoyId = "dboy-1",
            assignedShopkeeperId = "shop-1"
        )
        o1.statusHistory.addAll(listOf(
            StatusHistory("sh-1", "ord-1", "Pending", "Customer (Order Placed)", "2026-09-08T15:00:00Z", "Order placed with doorstep Try-at-Home request."),
            StatusHistory("sh-2", "ord-1", "Packed", "Shopkeeper", "2026-09-08T15:15:00Z", "Garments packaged securely."),
            StatusHistory("sh-3", "ord-1", "Trial Started", "Delivery Boy", "2026-09-08T15:30:00Z", "Delivery Associate started the 30-min trial session."),
            StatusHistory("sh-4", "ord-1", "Closed", "Delivery Boy", "2026-09-08T15:45:00Z", "Session completed. Kept: 1 Denim. Returned: 1 Graphic Tee. Collected ₹1299.")
        ))
        return listOf(o1)
    }
}
