package com.bizethiopia.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val stock: Int,
    val minStock: Int
)

private data class Sale(
    val id: Int,
    val customer: String,
    val product: String,
    val quantity: Int,
    val total: Double,
    val payment: String
)

private data class Expense(
    val id: Int,
    val category: String,
    val amount: Double,
    val note: String
)

private data class Customer(
    val id: Int,
    val name: String,
    val phone: String,
    val debt: Double
)

private data class Order(
    val id: Int,
    val customer: String,
    val items: String,
    val total: Double,
    val status: String
)

@Composable
fun BizEthiopiaApp() {
    val today = remember { LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy")) }

    var products by remember {
        mutableStateOf(
            listOf(
                Product(1, "Coffee", 120.0, 15, 5),
                Product(2, "Bread", 60.0, 32, 10),
                Product(3, "Milk", 90.0, 20, 8),
                Product(4, "Rice", 250.0, 12, 5)
            )
        )
    }

    var sales by remember {
        mutableStateOf(
            listOf(
                Sale(1, "Walk-in", "Coffee", 2, 240.0, "Cash"),
                Sale(2, "Abebe", "Bread", 5, 300.0, "Telebirr"),
                Sale(3, "Hanna Store", "Milk", 4, 360.0, "CBE Birr")
            )
        )
    }

    var expenses by remember {
        mutableStateOf(
            listOf(
                Expense(1, "Transport", 100.0, "Delivery"),
                Expense(2, "Utilities", 200.0, "Power bill"),
                Expense(3, "Stock purchases", 500.0, "Restock")
            )
        )
    }

    var customers by remember {
        mutableStateOf(
            listOf(
                Customer(1, "Abebe Kebede", "0911000000", 450.0),
                Customer(2, "Hanna Store", "0922000000", 0.0),
                Customer(3, "Selam Boutique", "0933000000", 250.0)
            )
        )
    }

    var orders by remember {
        mutableStateOf(
            listOf(
                Order(1, "Hanna Store", "10 × Milk", 900.0, "Pending"),
                Order(2, "Abebe Kebede", "4 × Rice", 1000.0, "Confirmed"),
                Order(3, "Selam Boutique", "2 × Coffee", 240.0, "Packing")
            )
        )
    }

    val todaySales = sales.filter { it.total > 0 }.sumOf { it.total.toDouble() }
    val todayExpenses = expenses.filter { it.amount > 0 }.sumOf { it.amount.toDouble() }
    val profit = todaySales - todayExpenses
    val totalDebt = customers.sumOf { it.debt.toDouble() }
    val lowStockProducts = products.filter { it.stock <= it.minStock }

    val metrics = listOf(
        MetricCardData("Sales today", "ETB ${formatMoney(todaySales)}", "Live", Icons.Filled.MonetizationOn),
        MetricCardData("Expenses", "ETB ${formatMoney(todayExpenses)}", "Today", Icons.Filled.AttachMoney),
        MetricCardData("Profit", "ETB ${formatMoney(profit)}", "On track", Icons.Filled.Storefront),
        MetricCardData("Debt", "ETB ${formatMoney(totalDebt)}", "Customers", Icons.Filled.Business)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(12.dp),
                            tonalElevation = 0.dp
                        ) {
                            Text(
                                text = "B",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                        Spacer(modifier = Modifier.padding(start = 10.dp))
                        Column {
                            Text(text = "BizEthiopia", style = MaterialTheme.typography.titleLarge)
                            Text(text = "Small business manager", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.primary,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "Good evening 👋",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Business overview for $today",
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    metrics.forEach { data ->
                        MetricCard(data)
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "Quick actions", style = MaterialTheme.typography.titleMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            ActionButton("New Sale", Icons.Filled.ShoppingCart)
                            ActionButton("Add Expense", Icons.Filled.AttachMoney)
                            ActionButton("Inventory", Icons.Filled.Inventory2)
                            ActionButton("Orders", Icons.Filled.Add)
                        }
                    }
                }
            }

            if (lowStockProducts.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFB7791F))
                                Spacer(modifier = Modifier.padding(start = 8.dp))
                                Text("Low stock alerts", fontWeight = FontWeight.Bold)
                            }
                            lowStockProducts.forEach { item ->
                                Text("• ${item.name}: ${item.stock} left (min ${item.minStock})")
                            }
                        }
                    }
                }
            }

            item {
                SectionHeader(title = "Recent sales")
            }

            items(sales.take(4)) { sale ->
                ListRow(
                    title = "${sale.product} × ${sale.quantity}",
                    subtitle = "${sale.customer} • ${sale.payment}",
                    amount = "ETB ${formatMoney(sale.total)}"
                )
            }

            item {
                SectionHeader(title = "Inventory")
            }

            items(products) { product ->
                ListRow(
                    title = product.name,
                    subtitle = "${formatMoney(product.price)} ETB • min ${product.minStock}",
                    amount = "${product.stock} left",
                    tag = if (product.stock <= product.minStock) "Low" else "OK"
                )
            }

            item {
                SectionHeader(title = "Customers")
            }

            items(customers) { customer ->
                ListRow(
                    title = customer.name,
                    subtitle = customer.phone,
                    amount = "ETB ${formatMoney(customer.debt)}",
                    tag = if (customer.debt > 0.0) "Debt" else "Clear"
                )
            }

            item {
                SectionHeader(title = "WhatsApp orders")
            }

            items(orders) { order ->
                ListRow(
                    title = order.customer,
                    subtitle = order.items,
                    amount = "ETB ${formatMoney(order.total)}",
                    tag = order.status
                )
            }
        }
    }
}

private data class MetricCardData(
    val title: String,
    val value: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
private fun MetricCard(data: MetricCardData) {
    Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = data.title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(data.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.padding(start = 6.dp))
                Text(text = data.value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = data.label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun ActionButton(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Button(
        onClick = {},
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null)
            Text(label, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun ListRow(title: String, subtitle: String, amount: String, tag: String? = null) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = amount, fontWeight = FontWeight.Bold)
                if (tag != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(99.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

private fun formatMoney(value: Double): String = String.format("%,.0f", value)
