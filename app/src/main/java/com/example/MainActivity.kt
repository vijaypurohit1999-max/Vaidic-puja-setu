package com.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.Architecture
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.data.model.PaymentMethodOption
import com.example.data.model.PujaBooking
import com.example.data.model.PujaServiceItem
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import com.example.data.model.VaidikCatalog
import com.example.data.repository.PujaRepository
import com.example.ui.PujaViewModel
import com.example.ui.SpecsAndGuidelinesScreen
import com.example.ui.UiState
import com.example.ui.auth.AuthScreen
import com.example.ui.theme.KesariyaSaffron
import com.example.ui.theme.KumkumDark
import com.example.ui.theme.KumkumMaroon
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PitambaraGold
import com.example.ui.theme.PitambaraLight
import com.example.ui.theme.SacredGreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AppNavigation()
            }
        }
    }
}

enum class MainDestination(
    val route: String,
    val labelHi: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("home", "होम (Home)", Icons.Filled.Home, Icons.Outlined.Home),
    BOOK_PUJA("book_puja", "पूजा बुकिंग", Icons.Filled.EventAvailable, Icons.Outlined.EventAvailable),
    PANDIT_DASHBOARD("pandit_dashboard", "पंडित डैशबोर्ड", Icons.Filled.SelfImprovement, Icons.Outlined.SelfImprovement),
    SPECS("specs", "ब्लूप्रिंट/Specs", Icons.Filled.Architecture, Icons.Outlined.Architecture)
}

private const val PREFS_NAME = "vaidik_puja_mobile_auth"
private const val KEY_LOGGED_IN_PHONE = "logged_in_phone"
private const val KEY_LOGGED_IN_ROLE = "logged_in_role"

@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    // Default directly to Home screen with a pre-configured mobile session (never redirects to Google Sign-In)
    var loggedInPhone by rememberSaveable {
        mutableStateOf(prefs.getString(KEY_LOGGED_IN_PHONE, "9876543210") ?: "9876543210")
    }
    var selectedRole by rememberSaveable {
        mutableStateOf(
            UserRole.fromCode(prefs.getString(KEY_LOGGED_IN_ROLE, UserRole.YAJMAN.code) ?: UserRole.YAJMAN.code)
        )
    }
    var showDummyOtpModal by rememberSaveable { mutableStateOf(false) }

    if (showDummyOtpModal) {
        BackHandler {
            showDummyOtpModal = false
        }
        AuthScreen(
            selectedRole = selectedRole,
            onRoleSelected = { role ->
                selectedRole = role
            },
            onMobileOtpLoginSuccess = { verifiedPhone, role ->
                prefs.edit()
                    .putString(KEY_LOGGED_IN_PHONE, verifiedPhone)
                    .putString(KEY_LOGGED_IN_ROLE, role.code)
                    .apply()
                selectedRole = role
                loggedInPhone = verifiedPhone
                showDummyOtpModal = false
            },
            onBypassToHome = {
                showDummyOtpModal = false
            }
        )
    } else {
        val userId = "mobile_$loggedInPhone"
        val viewModel: PujaViewModel = viewModel(
            key = userId,
            factory = viewModelFactory {
                initializer {
                    val app = checkNotNull(this[APPLICATION_KEY]) {
                        "APPLICATION_KEY missing from CreationExtras"
                    }
                    PujaViewModel(
                        repository = PujaRepository(app),
                        currentUserId = userId,
                        initialPhone = loggedInPhone,
                        initialRole = selectedRole
                    )
                }
            }
        )
        VaidikPujaMainScreen(
            viewModel = viewModel,
            verifiedPhone = loggedInPhone,
            onOpenDummyOtpScreen = {
                showDummyOtpModal = true
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaidikPujaMainScreen(
    viewModel: PujaViewModel,
    verifiedPhone: String,
    onOpenDummyOtpScreen: () -> Unit
) {
    val activeRole by viewModel.activeRole.collectAsStateWithLifecycle()
    val statusBanner by viewModel.statusBannerMessage.collectAsStateWithLifecycle()

    // Home screen always loads as the default launch screen
    var currentTab by rememberSaveable {
        mutableStateOf(MainDestination.HOME)
    }

    if (currentTab != MainDestination.HOME) {
        BackHandler {
            currentTab = MainDestination.HOME
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Vaidik Puja • वैदिक पूजा",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "मोबाइल: +91-$verifiedPhone (${if (activeRole == UserRole.YAJMAN) "यजमान" else "पंडित जी"})",
                            style = MaterialTheme.typography.labelSmall,
                            color = PitambaraLight
                        )
                    }
                },
                actions = {
                    // Role switcher pill (Yajman <-> Pandit Ji)
                    Surface(
                        color = PitambaraGold,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .testTag("switch_role_button")
                            .clickable {
                                val nextRole = if (activeRole == UserRole.YAJMAN) UserRole.PANDIT else UserRole.YAJMAN
                                viewModel.setActiveRole(nextRole)
                                currentTab = if (nextRole == UserRole.PANDIT) {
                                    MainDestination.PANDIT_DASHBOARD
                                } else {
                                    MainDestination.HOME
                                }
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (activeRole == UserRole.YAJMAN) Icons.Default.Person else Icons.Default.SelfImprovement,
                                contentDescription = "Switch Role",
                                tint = KumkumDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (activeRole == UserRole.YAJMAN) "यजमान Mode" else "पंडित जी Mode",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = KumkumDark
                            )
                        }
                    }

                    IconButton(
                        onClick = onOpenDummyOtpScreen,
                        modifier = Modifier.testTag("open_dummy_otp_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = "Dummy Mobile OTP Login",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = KumkumMaroon
                )
            )
        },
        floatingActionButton = {
            if (currentTab == MainDestination.HOME) {
                ExtendedFloatingActionButton(
                    onClick = { currentTab = MainDestination.BOOK_PUJA },
                    containerColor = KesariyaSaffron,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("book_puja_fab"),
                    icon = {
                        Icon(
                            imageVector = Icons.Default.EventAvailable,
                            contentDescription = "Book Puja"
                        )
                    },
                    text = {
                        Text(
                            text = "पूजा बुक करें (Book Puja)",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                MainDestination.entries.forEach { dest ->
                    val selected = currentTab == dest
                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentTab = dest },
                        modifier = Modifier.testTag("nav_tab_${dest.route}"),
                        icon = {
                            Icon(
                                imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                contentDescription = dest.labelHi
                            )
                        },
                        label = {
                            Text(
                                text = dest.labelHi,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedVisibility(visible = statusBanner != null) {
                statusBanner?.let { msg ->
                    Surface(
                        color = PitambaraLight,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(14.dp),
                        tonalElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = KumkumMaroon
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = KumkumDark,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { viewModel.clearBannerMessage() },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = KumkumDark,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            when (currentTab) {
                MainDestination.HOME -> HomeScreenContent(
                    activeRole = activeRole,
                    verifiedPhone = verifiedPhone,
                    onRoleSelected = { role ->
                        viewModel.setActiveRole(role)
                        if (role == UserRole.PANDIT) {
                            currentTab = MainDestination.PANDIT_DASHBOARD
                        }
                    },
                    onOpenDummyOtpScreen = onOpenDummyOtpScreen,
                    onBookPujaSelected = { service ->
                        viewModel.selectPujaService(service)
                        currentTab = MainDestination.BOOK_PUJA
                    },
                    onOpenPanditDashboard = {
                        viewModel.setActiveRole(UserRole.PANDIT)
                        currentTab = MainDestination.PANDIT_DASHBOARD
                    },
                    onOpenSpecs = {
                        currentTab = MainDestination.SPECS
                    }
                )
                MainDestination.BOOK_PUJA -> BookingAndPaymentScreen(
                    viewModel = viewModel,
                    onBookingCompleted = {
                        currentTab = MainDestination.PANDIT_DASHBOARD
                    }
                )
                MainDestination.PANDIT_DASHBOARD -> PanditDashboardScreen(
                    viewModel = viewModel,
                    onCreateNewBooking = {
                        currentTab = MainDestination.BOOK_PUJA
                    }
                )
                MainDestination.SPECS -> SpecsAndGuidelinesScreen()
            }
        }
    }
}

@Composable
fun HomeScreenContent(
    activeRole: UserRole,
    verifiedPhone: String,
    onRoleSelected: (UserRole) -> Unit,
    onOpenDummyOtpScreen: () -> Unit,
    onBookPujaSelected: (PujaServiceItem) -> Unit,
    onOpenPanditDashboard: () -> Unit,
    onOpenSpecs: () -> Unit
) {
    val context = LocalContext.current
    val featuredBagalamukhi = VaidikCatalog.pujaServices.first()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 0. Prominent Role Selection Card on Default Launch Home Screen (Yajman / Pandit Ji)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_role_selection_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "भूमिका चुनें (Select Role: Yajman / Pandit Ji)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "सक्रिय मोबाइल: +91-$verifiedPhone (Dummy OTP Verified)",
                                style = MaterialTheme.typography.labelSmall,
                                color = SacredGreen
                            )
                        }
                        OutlinedButton(
                            onClick = onOpenDummyOtpScreen,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("home_dummy_otp_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("OTP लॉगिन", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        UserRole.entries.forEach { role ->
                            val isSelected = activeRole == role
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("home_role_${role.code.lowercase()}")
                                    .clickable { onRoleSelected(role) }
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.surface
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (role == UserRole.YAJMAN) Icons.Default.Person else Icons.Default.SelfImprovement,
                                            contentDescription = role.titleHi,
                                            tint = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (role == UserRole.YAJMAN) "यजमान (Yajman)" else "पंडित जी (Pandit Ji)",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (role == UserRole.YAJMAN) "पूजा बुक करें" else "डैशबोर्ड खोलें",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 1. Featured Showcase: Bagalamukhi Havan Poojan
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("featured_bagalamukhi_card"),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = featuredBagalamukhi.imageRes),
                        contentDescription = featuredBagalamukhi.titleHi,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.20f),
                                        KumkumDark.copy(alpha = 0.94f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(18.dp)
                    ) {
                        Surface(
                            color = PitambaraGold,
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = "★ प्रमुख विशेष अनुष्ठान • FEATURED HAVAN",
                                style = MaterialTheme.typography.labelSmall,
                                color = KumkumDark,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = featuredBagalamukhi.titleHi,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White
                        )
                        Text(
                            text = "${featuredBagalamukhi.titleEn} • ${featuredBagalamukhi.duration}",
                            style = MaterialTheme.typography.labelMedium,
                            color = PitambaraLight
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = featuredBagalamukhi.shortDescHi,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.92f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "दक्षिणा: ₹${featuredBagalamukhi.baseDakshina}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = PitambaraGold,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "+ ₹${featuredBagalamukhi.samagriCost} हवन सामग्री (वैकल्पिक)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PitambaraLight
                                )
                            }
                            Button(
                                onClick = { onBookPujaSelected(featuredBagalamukhi) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PitambaraGold,
                                    contentColor = KumkumDark
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("hero_book_puja_button")
                            ) {
                                Text(
                                    text = "अभी बुक करें (Book Now)",
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Quick Role & Architecture Shortcuts
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenPanditDashboard,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SelfImprovement,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("पंडित जी डैशबोर्ड", style = MaterialTheme.typography.labelMedium)
                }
                OutlinedButton(
                    onClick = onOpenSpecs,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Architecture,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ऐप लॉजिक व स्कीमा", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        // 3. Service Showcase Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "वैदिक पूजा एवं हवन सेवाएं (Puja Showcase)",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "प्रमाणित आचार्यों द्वारा शुद्ध वैदिक विधि से अनुष्ठान",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 4. All Puja Service Cards
        items(VaidikCatalog.pujaServices, key = { it.id }) { service ->
            PujaServiceShowcaseCard(
                service = service,
                onBookClick = { onBookPujaSelected(service) }
            )
        }

        // 5. About Us Section (हमारे बारे में)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("about_us_section"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About Us",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "हमारे बारे में (About Us • Vaidik Puja)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Vaidik Puja भारत का विश्वसनीय आध्यात्मिक मंच है जो यजमान (Devotees) को काशी, उज्जैन, नलखेड़ा एवं प्रयागराज के गुरुकुल प्रशिक्षित वैदिक पंडित जी से सीधे जोड़ता है।",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "✓ 100% प्रमाणित एवं अनुभवी वैदिक आचार्य\n" +
                            "✓ शुद्ध गाय का घी, पीली सरसों, हल्दी माला एवं प्रामाणिक हवन सामग्री\n" +
                            "✓ पारदर्शी दक्षिणा एवं मुहूर्त अनुसार समयबद्ध अनुष्ठान",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 6. Contact Us Section (संपर्क करें)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("contact_us_section"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = "Contact Us",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "संपर्क करें (Contact Us • 24x7 Yajman Sahayata)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• मुख्य कार्यालय: श्री पीताम्बरा वैदिक सेवा केंद्र, महाकाल मार्ग, उज्जैन (म.प्र.) - 456001\n" +
                            "• यजमान हेल्पलाइन: +91 98260-10808 (प्रातः 06:00 से रात्रि 10:00 तक)\n" +
                            "• ईमेल: seva@vaidikpuja.org",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+919826010808"))
                                context.startActivity(dialIntent)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SacredGreen,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("contact_helpline_call_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = "Call Helpline",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("हेल्पलाइन कॉल करें")
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}

@Composable
fun PujaServiceShowcaseCard(
    service: PujaServiceItem,
    onBookClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("service_card_${service.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = service.imageRes),
                    contentDescription = service.titleHi,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(155.dp),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    color = KumkumDark.copy(alpha = 0.88f),
                    shape = RoundedCornerShape(bottomEnd = 14.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = service.categoryTag,
                        style = MaterialTheme.typography.labelSmall,
                        color = PitambaraLight,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
                Surface(
                    color = PitambaraGold,
                    shape = RoundedCornerShape(topStart = 12.dp),
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Text(
                        text = service.rating,
                        style = MaterialTheme.typography.labelSmall,
                        color = KumkumDark,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = service.titleHi,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${service.titleEn} • अवधि: ${service.duration}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = service.shortDescHi,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                service.vidhiHighlights.forEach { step ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 1.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = KesariyaSaffron,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "आचार्य: ${service.assignedPandit} (${service.panditExperience})",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "₹${service.baseDakshina} दक्षिणा",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "सामग्री किट: +₹${service.samagriCost}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = onBookClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = KumkumMaroon,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.testTag("book_service_${service.id}")
                    ) {
                        Text("पूजा बुक करें (Book Puja)")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookingAndPaymentScreen(
    viewModel: PujaViewModel,
    onBookingCompleted: () -> Unit
) {
    val yajmanName by viewModel.yajmanName.collectAsStateWithLifecycle()
    val yajmanPhone by viewModel.yajmanPhone.collectAsStateWithLifecycle()
    val yajmanEmail by viewModel.yajmanEmail.collectAsStateWithLifecycle()
    val yajmanAddress by viewModel.yajmanAddress.collectAsStateWithLifecycle()
    val selectedGotra by viewModel.selectedGotra.collectAsStateWithLifecycle()
    val selectedPuja by viewModel.selectedPuja.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val selectedTimeSlot by viewModel.selectedTimeSlot.collectAsStateWithLifecycle()
    val includeSamagri by viewModel.includeSamagri.collectAsStateWithLifecycle()
    val specialInstructions by viewModel.specialInstructions.collectAsStateWithLifecycle()
    val selectedPaymentMethod by viewModel.selectedPaymentMethod.collectAsStateWithLifecycle()
    val isSubmitting by viewModel.isSubmitting.collectAsStateWithLifecycle()

    val upcomingDates = remember { viewModel.getUpcomingDates() }
    val draft = viewModel.buildCurrentBookingDraft()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 1: Registration & Profile Details Form (with Email ID field!)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "1. पंजीकरण एवं प्रोफाइल विवरण फॉर्म (Registration & Profile Form)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Name, Verified Mobile, Email ID, Address, Gotra, Puja Type & Instructions",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = yajmanName,
                        onValueChange = viewModel::updateYajmanName,
                        label = { Text("यजमान / पंडित जी का पूरा नाम (Full Name)") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Person, contentDescription = null)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_yajman_name"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = yajmanPhone,
                        onValueChange = viewModel::updateYajmanPhone,
                        label = { Text("मोबाइल नंबर (OTP Verified Mobile Number)") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.PhoneAndroid, contentDescription = null)
                        },
                        trailingIcon = {
                            Surface(
                                color = SacredGreen.copy(alpha = 0.14f),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "OTP Verified",
                                        tint = SacredGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "OTP सत्यापित",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SacredGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_yajman_phone"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Email ID Field inside Registration / Profile Details Form
                    OutlinedTextField(
                        value = yajmanEmail,
                        onValueChange = viewModel::updateYajmanEmail,
                        label = { Text("ईमेल आईडी (Email ID - प्रोफाइल व रसीद हेतु)") },
                        placeholder = { Text("name@example.com") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Email, contentDescription = "Email ID")
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_yajman_email"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = yajmanAddress,
                        onValueChange = viewModel::updateYajmanAddress,
                        label = { Text("पूजा स्थान का पूरा पता (Full Address / City)") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_yajman_address"),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "वैदिक गोत्र चुनें (Select Gotra for Sankalp):",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VaidikCatalog.gotraOptions.forEach { gotra ->
                            val selected = selectedGotra == gotra
                            FilterChip(
                                selected = selected,
                                onClick = { viewModel.updateSelectedGotra(gotra) },
                                label = { Text(gotra) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = KumkumMaroon,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = specialInstructions,
                        onValueChange = viewModel::updateSpecialInstructions,
                        label = { Text("विशेष निर्देश / संकल्प विवरण (Special Instructions)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_special_instructions"),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { viewModel.saveRegistrationProfile() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_registration_profile_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "पंजीकरण व प्रोफाइल सुरक्षित करें (Save Registration & Email)",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Section 2: Puja Selection, Muhurat Calendar & Time Slots
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "2. पूजा चयन, मुहूर्त कैलेंडर व समय स्लॉट (Booking & Slots)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Select Puja Type, Auspicious Date & Muhurat Time Slot",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "पूजा का प्रकार चुनें (Select Puja Type):",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        VaidikCatalog.pujaServices.forEach { service ->
                            val isSelected = selectedPuja.id == service.id
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.selectPujaService(service) }
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = service.titleHi,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${service.titleEn} • ${service.duration}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = "₹${service.baseDakshina}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Calendar Date Selection
                    Text(
                        text = "शुभ तिथि चुनें (Select Puja Date from Calendar):",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        upcomingDates.forEach { (isoDate, displayLabel) ->
                            val isSelected = selectedDate == isoDate
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) KumkumMaroon else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .clickable { viewModel.updateSelectedDate(isoDate) }
                                    .testTag("date_chip_$isoDate")
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = isoDate,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) PitambaraLight else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = displayLabel,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Time Slots (06:00 AM, 08:00 AM, etc.)
                    Text(
                        text = "मुहूर्त समय स्लॉट चुनें (Select Time Slot):",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VaidikCatalog.timeSlots.forEach { slot ->
                            val isSelected = selectedTimeSlot == slot.time
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) KesariyaSaffron else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .clickable { viewModel.updateSelectedTimeSlot(slot.time) }
                                    .testTag("slot_chip_${slot.time}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = slot.time,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = slot.muhuratNameHi,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSelected) PitambaraLight else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Samagri Toggle
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.updateIncludeSamagri(!includeSamagri) }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = includeSamagri,
                                onCheckedChange = { viewModel.updateIncludeSamagri(it) }
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "शुद्ध वैदिक हवन एवं पूजन सामग्री किट जोड़ें (+₹${selectedPuja.samagriCost})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "पंडित जी स्वयं शुद्ध गाय का घी, समिधा, पीली सरसों, हल्दी माला व कलश सामग्री साथ लाएंगे।",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Price Calculation, Commission Model & Multi-Method Payment Integration
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "3. मूल्य गणना, कमीशन मॉडल एवं भुगतान (Payment Integration)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "UPI (GPay / PhonePe / Paytm), Cards, Net Banking & 85:15 Split",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Price & Platform Commission Breakdown Box
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            PriceRow("आचार्य पूजा दक्षिणा (Base Dakshina)", "₹${draft.basePrice}")
                            PriceRow(
                                "शुद्ध हवन सामग्री किट (Havan Samagri)",
                                if (draft.includeSamagri) "+ ₹${draft.samagriPrice}" else "₹0 (स्वयं की सामग्री)"
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            PriceRow(
                                label = "कुल देय राशि (Total Payable by Yajman)",
                                value = "₹${draft.totalAmount}",
                                isBold = true
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            Text(
                                text = "पारदर्शी प्लेटफॉर्म कमीशन विभाजन (Platform Commission Split):",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            PriceRow("• पंडित जी शुद्ध भुगतान (85% Acharya Payout)", "₹${draft.panditPayout}")
                            PriceRow("• प्लेटफॉर्म सेवा एवं सत्यापन शुल्क (15% Commission)", "₹${draft.platformCommission}")
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "भुगतान माध्यम चुनें (Select Payment Method):",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PaymentMethodOption.entries.forEach { method ->
                            val isSelected = selectedPaymentMethod == method
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.updatePaymentMethod(method) }
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) KesariyaSaffron else Color.Transparent,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .testTag("payment_method_${method.code.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = when (method) {
                                            PaymentMethodOption.CARD -> Icons.Default.CreditCard
                                            PaymentMethodOption.NET_BANKING -> Icons.Default.AccountBalance
                                            else -> Icons.Default.AccountBalanceWallet
                                        },
                                        contentDescription = method.title,
                                        tint = if (isSelected) KesariyaSaffron else MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = method.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = method.subtitle,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Surface(
                                        color = if (isSelected) KesariyaSaffron else MaterialTheme.colorScheme.surface,
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        Text(
                                            text = method.badge,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            viewModel.confirmPaymentAndCreateBooking {
                                onBookingCompleted()
                            }
                        },
                        enabled = !isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("pay_and_book_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = KumkumMaroon,
                            contentColor = Color.White
                        )
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CurrencyRupee,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "₹${draft.totalAmount} भुगतान करें व पूजा बुक करें (${selectedPaymentMethod.title.substringBefore(" ")})",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PriceRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isBold) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodySmall,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            style = if (isBold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isBold) KumkumMaroon else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun PanditDashboardScreen(
    viewModel: PujaViewModel,
    onCreateNewBooking: () -> Unit
) {
    val context = LocalContext.current
    val profileUiState by viewModel.userProfileState.collectAsStateWithLifecycle()
    val bookingsUiState by viewModel.bookingsState.collectAsStateWithLifecycle()

    val isPanditOnline = (profileUiState as? UiState.Success<UserProfile?>)?.data?.isPanditOnline ?: true
    val bookings = (bookingsUiState as? UiState.Success<List<PujaBooking>>)?.data ?: emptyList()

    val todayIso = remember { viewModel.getTodayIsoDate() }
    val todaysBookingsCount = bookings.count { it.pujaDate == todayIso }
    val totalBookingsCount = bookings.size
    val completedBookings = bookings.filter { it.bookingStatus == "COMPLETED" }
    val upcomingBookings = bookings.filter { it.bookingStatus == "UPCOMING" }

    val totalGrossAmount = bookings.sumOf { it.totalAmount }
    val totalPlatformCommission = bookings.sumOf { it.platformCommission }
    val totalPanditPayout = bookings.sumOf { it.panditPayout }
    val settledPayout = completedBookings.sumOf { it.panditPayout }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Pandit Ji Online / Offline Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isPanditOnline) KumkumMaroon else Color(0xFF3E2723)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            color = if (isPanditOnline) SacredGreen else Color.Gray,
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = if (isPanditOnline) "● ONLINE • बुकिंग हेतु उपलब्ध" else "○ OFFLINE • अनुष्ठान में व्यस्त",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "आचार्य विश्वनाथ शास्त्री (Pandit Ji Dashboard)",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                        Text(
                            text = "पीताम्बरा पीठ एवं वैदिक अनुष्ठान विशेषज्ञ • उज्जैन",
                            style = MaterialTheme.typography.bodySmall,
                            color = PitambaraLight
                        )
                    }
                    Switch(
                        checked = isPanditOnline,
                        onCheckedChange = { viewModel.togglePanditOnline(it) },
                        modifier = Modifier.testTag("pandit_online_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PitambaraGold,
                            checkedTrackColor = SacredGreen
                        )
                    )
                }
            }
        }

        // 2. 4 Key Booking Metric Cards (Today's, Total, Completed, Upcoming)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardStatCard(
                        titleHi = "आज की पूजा (Today's)",
                        value = todaysBookingsCount.toString(),
                        subtitle = "तिथि: $todayIso",
                        accentColor = KesariyaSaffron,
                        modifier = Modifier.weight(1f)
                    )
                    DashboardStatCard(
                        titleHi = "कुल बुकिंग (Total)",
                        value = totalBookingsCount.toString(),
                        subtitle = "संपूर्ण रिकॉर्ड",
                        accentColor = KumkumMaroon,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardStatCard(
                        titleHi = "संपन्न पूजा (Completed)",
                        value = completedBookings.size.toString(),
                        subtitle = "दक्षिणा प्राप्त: ₹$settledPayout",
                        accentColor = SacredGreen,
                        modifier = Modifier.weight(1f)
                    )
                    DashboardStatCard(
                        titleHi = "आगामी पूजा (Upcoming)",
                        value = upcomingBookings.size.toString(),
                        subtitle = "संकल्प हेतु लंबित",
                        accentColor = PitambaraGold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 3. Earnings & Payouts Breakdown Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pandit_earnings_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = "Earnings & Payouts",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "आय एवं बैंक भुगतान विवरण (Earnings & Payouts)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    PriceRow("कुल यजमान बुकिंग राशि (Gross Bookings Value)", "₹$totalGrossAmount")
                    PriceRow("प्लेटफॉर्म सेवा कमीशन (15% Platform Fee)", "- ₹$totalPlatformCommission")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                    PriceRow(
                        label = "आचार्य शुद्ध आय (85% Net Pandit Ji Payout)",
                        value = "₹$totalPanditPayout",
                        isBold = true
                    )
                }
            }
        }

        // 4. Upcoming Bookings List with Direct Call to Yajman & Mark Completed
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "आगामी पूजा सूची (Upcoming Bookings • ${upcomingBookings.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                OutlinedButton(
                    onClick = onCreateNewBooking,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("+ नई बुकिंग", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        if (upcomingBookings.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "अभी कोई आगामी पूजा लंबित नहीं है।",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "नई बुकिंग जोड़ने के लिए 'पूजा बुकिंग' टैब पर जाएं।",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(upcomingBookings, key = { it.id }) { booking ->
                PanditBookingItemCard(
                    booking = booking,
                    onCallYajman = {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${booking.yajmanPhone}"))
                        context.startActivity(dialIntent)
                    },
                    onMarkCompleted = {
                        viewModel.markBookingCompleted(booking.id)
                    }
                )
            }
        }

        // 5. Completed Bookings History
        if (completedBookings.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "संपन्न पूजा इतिहास (Completed Bookings • ${completedBookings.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(completedBookings, key = { it.id }) { booking ->
                PanditBookingItemCard(
                    booking = booking,
                    onCallYajman = {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${booking.yajmanPhone}"))
                        context.startActivity(dialIntent)
                    },
                    onMarkCompleted = null
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun DashboardStatCard(
    titleHi: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = titleHi,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = accentColor,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PanditBookingItemCard(
    booking: PujaBooking,
    onCallYajman: () -> Unit,
    onMarkCompleted: (() -> Unit)?
) {
    val isCompleted = booking.bookingStatus == "COMPLETED"
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("pandit_booking_card_${booking.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (isCompleted) SacredGreen.copy(alpha = 0.15f) else KesariyaSaffron.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = if (isCompleted) "✓ संपन्न (COMPLETED)" else "● आगामी (UPCOMING)",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isCompleted) SacredGreen else KesariyaSaffron,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = "${booking.pujaDate} • ${booking.timeSlot}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = booking.pujaTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "यजमान: ${booking.yajmanName}  |  गोत्र: ${booking.gotra}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            if (booking.yajmanEmail.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ईमेल: ${booking.yajmanEmail}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = booking.yajmanAddress,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (booking.specialInstructions.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "संकल्प निर्देश: ${booking.specialInstructions}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "आचार्य शुद्ध देय (85%): ₹${booking.panditPayout}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = SacredGreen
                    )
                    Text(
                        text = "कुल: ₹${booking.totalAmount} (कमीशन: ₹${booking.platformCommission} • ${booking.paymentMethod})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onCallYajman,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SacredGreen,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("call_yajman_button_${booking.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call Yajman",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("यजमान को कॉल करें (${booking.yajmanPhone})")
                }

                if (onMarkCompleted != null) {
                    Button(
                        onClick = onMarkCompleted,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = KumkumMaroon,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("complete_booking_button_${booking.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Complete Puja",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("पूजा संपन्न करें")
                    }
                }
            }
        }
    }
}
