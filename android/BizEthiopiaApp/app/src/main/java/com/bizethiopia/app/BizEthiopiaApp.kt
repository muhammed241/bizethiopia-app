package com.bizethiopia.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
    val productName: String,
    val customer: String,
    val quantity: Int,
    val total: Double,
    val payment: String
)

private data class Customer(
    val id: Int,
    val name: String,
    val phone: String,
    val debt: Double,
)

private data class Order(
    val id: Int,
    val customer: String,
    val items: String,
    val total: Double,
    val status: String,
)

private data class Expense(
    val id: Int,
    val category: String,
    val amount: Double,
    val note: String,
)

private enum class AppTab { DASHBOARD, INVENTORY, SALES, CUSTOMERS, ORDERS }

@Composable
fun BizEthiopiaApp() {
    var isLoggedIn by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("demo@bizethiopia.com") }
    var password by remember { mutableStateOf("admin123") }

    if (!isLoggedIn) {
        LoginScreen(
            username = userName,
            password = password,
            onUsernameChange = { userName = it },
            onPasswordChange = { password = it },
            onLogin = { isLoggedIn = true }
        )
        return
    }

    val products = remember {
        mutableStateListOf(
            Product(1, "Coffee", 120.0, 15, 5),
            Product(2, "Bread", 60.0, 35, 10),
            Product(3, "Milk", 90.0, 20, 8),
            Product(4, "Rice", 250.0, 12, 5)
        )
    }

    val sales = remember {
        mutableStateListOf(
            Sale(1, "Coffee", "Walk-in", 2, 240.0, "Cash"),
            Sale(2, "Bread", "Abebe", 5, 300.0, "Telebirr"),
            Sale(3, "Milk", "Hanna Store", 4, 360.0, "CBE Birr")
        )
    }

    val expenses = remember {
        mutableStateListOf(
            Expense(1, "Transport", 100.0, "Delivery"),
            Expense(2, "Utilities", 200.0, "Power bill"),
            Expense(3, "Stock purchases", 500.0, "Restock")
        )
    }

    val customers = remember {
        mutableStateListOf(
            Customer(1, "Abebe Kebede", "0911000000", 450.0),
            Customer(2, "Hanna Store", "0922000000", 0.0),
            Customer(3, "Selam Boutique", "0933000000", 250.0)
        )
    }

    val orders = remember {
        mutableStateListOf(
            Order(1, "Hanna Store", "10 × Milk", 900.0, "Pending"),
            Order(2, "Abebe Kebede", "4 × Rice", 1000.0, "Confirmed"),
            Order(3, "Selam Boutique", "2 × Coffee", 240.0, "Packing")
        )
    }

    var selectedTab by remember { mutableStateOf(AppTab.DASHBOARD) }

    val todaySales = sales.sumOf { it.total.toDouble() }
    val todayExpenses = expenses.sumOf { it.amount.toDouble() }
    val totalDebt = customers.sumOf { it.debt.toDouble() }
    val profit = todaySales - todayExpenses
    val lowStock = products.filter { it.stock <= it.minStock }

    val dashboardMetrics = listOf(
        MetricCardData("Sales today", "ETB ${formatMoney(todaySales)}", "Live", Icons.Filled.MonetizationOn),
        MetricCardData("Expenses", "ETB ${formatMoney(todayExpenses)}", "Today", Icons.Filled.AttachMoney),
        MetricCardData("Profit", "ETB ${formatMoney(profit)}", "On track", Icons.Filled.Storefront),
        MetricCardData("Debt", "ETB ${formatMoney(totalDebt)}", "Customers", Icons.Filled.Business)
    )

    val saleForm = remember { mutableStateOf(SaleFormState()) }
    val productForm = remember { mutableStateOf(ProductFormState()) }
    val customerForm = remember { mutableStateOf(CustomerFormState()) }

    Scaffold(
        topBar = {
            TopBar(title = "BizEthiopia", subtitle = "Business manager")
        },
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (selectedTab) {
                AppTab.DASHBOARD -> DashboardScreen(
                    metrics = dashboardMetrics,
                    sales = sales,
                    lowStock = lowStock,
                    products = products,
                    customers = customers,
                    orders = orders,
                    onLogout = { isLoggedIn = false }
                )
                AppTab.INVENTORY -> InventoryScreen(
                    products = products,
                    form = productForm.value,
                    onFormChange = { productForm.value = it },
                    onAddProduct = {
                        val p = productForm.value
                        if (p.name.isNotBlank()) {
                            products.add(
                                Product(
                                    id = (products.maxOfOrNull { it.id } ?: 0) + 1,
                                    name = p.name.trim(),
                                    price = p.price.toDoubleOrNull() ?: 0.0,
                                    stock = p.stock.toIntOrNull() ?: 0,
                                    minStock = p.minStock.toIntOrNull() ?: 0
                                )
                            )
                            productForm.value = ProductFormState()
                        }
                    }
                )
                AppTab.SALES -> SalesScreen(
                    products = products,
                    sales = sales,
                    form = saleForm.value,
                    onFormChange = { saleForm.value = it },
                    onAddSale = {
                        val form = saleForm.value
                        val selectedProduct = products.firstOrNull { it.id.toString() == form.productId }
                        if (selectedProduct != null) {
                            val qty = form.quantity.toIntOrNull() ?: 0
                            val total = selectedProduct.price * qty - (form.discount.toDoubleOrNull() ?: 0.0)
                            sales.add(
                                Sale(
                                    id = (sales.maxOfOrNull { it.id } ?: 0) + 1,
                                    productName = selectedProduct.name,
                                    customer = form.customer.ifBlank { "Walk-in" },
                                    quantity = qty,
                                    total = maxOf(total, 0.0),
                                    payment = form.payment
                                )
                            )
                            val updated = selectedProduct.copy(stock = selectedProduct.stock - qty)
                            val index = products.indexOfFirst { it.id == selectedProduct.id }
                            if (index >= 0) products[index] = updated
                            saleForm.value = SaleFormState()
                        }
                    }
                )
                AppTab.CUSTOMERS -> CustomersScreen(
                    customers = customers,
                    form = customerForm.value,
                    onFormChange = { customerForm.value = it },
                    onAddCustomer = {
                        val c = customerForm.value
                        if (c.name.isNotBlank()) {
                            customers.add(
                                Customer(
                                    id = (customers.maxOfOrNull { it.id } ?: 0) + 1,
                                    name = c.name.trim(),
                                    phone = c.phone.trim(),
                                    debt = c.debt.toDoubleOrNull() ?: 0.0
                                )
                            )
                            customerForm.value = CustomerFormState()
                        }
                    }
                )
                AppTab.ORDERS -> OrdersScreen(orders = orders)
            }
        }
    }
}

@Composable
private fun LoginScreen(
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit
) {
    val isValid = username.isNotBlank() && password.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Text(
                text = "B",
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 16.dp),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        Text("BizEthiopia", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Business manager", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 20.dp))

        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("Email or username") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = { Icon(Icons.Filled.AccountCircle, contentDescription = null) }
        )

        Spacer(modifier = Modifier.height(18.dp))

        ElevatedButton(
            onClick = onLogin,
            enabled = isValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Login, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log in")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onLogin) { Text("Use demo account") }
    }
}

@Composable
private fun DashboardScreen(
    metrics: List<MetricCardData>,
    sales: List<Sale>,
    lowStock: List<Product>,
    products: List<Product>,
    customers: List<Customer>,
    orders: List<Order>,
    onLogout: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Column(Modifier.padding(22.dp)) {
                    val today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy"))
                    Text("Good evening 👋", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Overview for $today", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f))
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                metrics.forEach { metric ->
                    MetricCard(metric)
                }
            }
        }

        if (lowStock.isNotEmpty()) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Warning, contentDescription = null, tint = Color(0xFFB7791F))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Low stock alerts", fontWeight = FontWeight.Bold)
                        }
                        lowStock.forEach { item ->
                            Text("• ${item.name}: ${item.stock} left (min ${item.minStock})")
                        }
                    }
                }
            }
        }

        item { SectionHeader("Recent sales") }
        items(sales.take(4)) { sale ->
            ListRow(
                title = "${sale.productName} × ${sale.quantity}",
                subtitle = "${sale.customer} • ${sale.payment}",
                amount = "ETB ${formatMoney(sale.total)}"
            )
        }

        item { SectionHeader("Inventory summary") }
        items(products.take(3)) { product ->
            ListRow(
                title = product.name,
                subtitle = "${formatMoney(product.price)} ETB",
                amount = "${product.stock} left",
                tag = if (product.stock <= product.minStock) "Low" else "OK"
            )
        }

        item { SectionHeader("Customers") }
        items(customers.take(3)) { customer ->
            ListRow(
                title = customer.name,
                subtitle = customer.phone,
                amount = "ETB ${formatMoney(customer.debt)}",
                tag = if (customer.debt > 0.0) "Debt" else "Clear"
            )
        }

        item { SectionHeader("WhatsApp orders") }
        items(orders.take(3)) { order ->
            ListRow(
                title = order.customer,
                subtitle = order.items,
                amount = "ETB ${formatMoney(order.total)}",
                tag = order.status
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                Text("Log out")
            }
        }
    }
}

@Composable
private fun InventoryScreen(
    products: List<Product>,
    form: ProductFormState,
    onFormChange: (ProductFormState) -> Unit,
    onAddProduct: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Add product", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = form.name, onValueChange = { onFormChange(form.copy(name = it)) }, label = { Text("Product name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = form.price, onValueChange = { onFormChange(form.copy(price = it)) }, label = { Text("Price") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = form.stock, onValueChange = { onFormChange(form.copy(stock = it)) }, label = { Text("Opening stock") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = form.minStock, onValueChange = { onFormChange(form.copy(minStock = it)) }, label = { Text("Min stock alert") }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = onAddProduct, modifier = Modifier.fillMaxWidth()) { Text("Save product") }
                }
            }
        }

        item { SectionHeader("Inventory") }
        items(products) { product ->
            ListRow(
                title = product.name,
                subtitle = "${formatMoney(product.price)} ETB • min ${product.minStock}",
                amount = "${product.stock} left",
                tag = if (product.stock <= product.minStock) "Low" else "OK"
            )
        }
    }
}

@Composable
private fun SalesScreen(
    products: List<Product>,
    sales: List<Sale>,
    form: SaleFormState,
    onFormChange: (SaleFormState) -> Unit,
    onAddSale: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("New sale", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = form.productId,
                        onValueChange = { onFormChange(form.copy(productId = it)) },
                        label = { Text("Product ID") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(value = form.quantity, onValueChange = { onFormChange(form.copy(quantity = it)) }, label = { Text("Quantity") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = form.discount, onValueChange = { onFormChange(form.copy(discount = it)) }, label = { Text("Discount") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = form.customer, onValueChange = { onFormChange(form.copy(customer = it)) }, label = { Text("Customer") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = form.payment, onValueChange = { onFormChange(form.copy(payment = it)) }, label = { Text("Payment method") }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = onAddSale, modifier = Modifier.fillMaxWidth()) { Text("Save sale") }
                }
            }
        }

        item { SectionHeader("Sales history") }
        items(sales) { sale ->
            ListRow(
                title = "${sale.productName} × ${sale.quantity}",
                subtitle = "${sale.customer} • ${sale.payment}",
                amount = "ETB ${formatMoney(sale.total)}"
            )
        }
    }
}

@Composable
private fun CustomersScreen(
    customers: List<Customer>,
    form: CustomerFormState,
    onFormChange: (CustomerFormState) -> Unit,
    onAddCustomer: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Add customer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = form.name, onValueChange = { onFormChange(form.copy(name = it)) }, label = { Text("Customer name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = form.phone, onValueChange = { onFormChange(form.copy(phone = it)) }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = form.debt, onValueChange = { onFormChange(form.copy(debt = it)) }, label = { Text("Debt amount") }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = onAddCustomer, modifier = Modifier.fillMaxWidth()) { Text("Save customer") }
                }
            }
        }

        item { SectionHeader("Customers") }
        items(customers) { customer ->
            ListRow(
                title = customer.name,
                subtitle = customer.phone,
                amount = "ETB ${formatMoney(customer.debt)}",
                tag = if (customer.debt > 0.0) "Debt" else "Clear"
            )
        }
    }
}

@Composable
private fun OrdersScreen(orders: List<Order>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("WhatsApp orders", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
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

@Composable
private fun TopBar(title: String, subtitle: String) {
    TopAppBar(
        title = {
            Column {
                Text(title)
                Text(subtitle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

@Composable
private fun BottomNavigationBar(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    Surface(shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NavItem(icon = Icons.Filled.Storefront, label = "Home", selected = selectedTab == AppTab.DASHBOARD, onClick = { onTabSelected(AppTab.DASHBOARD) })
            NavItem(icon = Icons.Filled.ShoppingCart, label = "Sales", selected = selectedTab == AppTab.SALES, onClick = { onTabSelected(AppTab.SALES) })
            NavItem(icon = Icons.Filled.Inventory2, label = "Stock", selected = selectedTab == AppTab.INVENTORY, onClick = { onTabSelected(AppTab.INVENTORY) })
            NavItem(icon = Icons.Filled.Business, label = "Customers", selected = selectedTab == AppTab.CUSTOMERS, onClick = { onTabSelected(AppTab.CUSTOMERS) })
            NavItem(icon = Icons.Filled.Add, label = "Orders", selected = selectedTab == AppTab.ORDERS, onClick = { onTabSelected(AppTab.ORDERS) })
        }
    }
}

@Composable
private fun NavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    Column(
        modifier = Modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = label, tint = tint)
        Text(label, style = MaterialTheme.typography.labelSmall, color = tint)
    }
}

@Composable
private fun MetricCard(data: MetricCardData) {
    Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(data.title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(data.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(6.dp))
                Text(data.value, fontWeight = FontWeight.Bold)
            }
            Text(data.label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}

private data class MetricCardData(
    val title: String,
    val value: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private data class ProductFormState(
    val name: String = "",
    val price: String = "",
    val stock: String = "",
    val minStock: String = "5"
)

private data class SaleFormState(
    val productId: String = "",
    val quantity: String = "1",
    val discount: String = "0",
    val customer: String = "Walk-in",
    val payment: String = "Cash"
)

private data class CustomerFormState(
    val name: String = "",
    val phone: String = "",
    val debt: String = "0"
)

@Composable
private fun SectionHeader(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
}

@Composable
private fun ListRow(title: String, subtitle: String, amount: String, tag: String? = null) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(amount, fontWeight = FontWeight.Bold)
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

