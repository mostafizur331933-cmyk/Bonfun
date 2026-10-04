package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.AppLanguage
import com.example.data.model.UserRole
import com.example.ui.components.CallSimulationDialog
import com.example.ui.components.TopBarWithModeSwitch
import com.example.ui.screens.admin.AdminPortalScreen
import com.example.ui.screens.common.ChatScreen
import com.example.ui.screens.common.NotificationScreen
import com.example.ui.screens.customer.*
import com.example.ui.screens.mistri.*
import com.example.ui.theme.MistriAmber
import com.example.ui.theme.MistriPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MarketplaceViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent()
            }
        }
    }
}

@Composable
fun MainAppContent(
    viewModel: MarketplaceViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isBangla = uiState.language == AppLanguage.BN

    val canNavigateBack = uiState.navigationStack.size > 1

    BackHandler(enabled = canNavigateBack) {
        viewModel.navigateBack()
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    val topBarTitle = when (uiState.currentScreen) {
        AppScreen.CUSTOMER_HOME -> if (isBangla) "বনফান মিস্ত্রি সেবা (Bonfun)" else "Bonfun"
        AppScreen.MISTRI_DETAIL -> if (isBangla) "মিস্ত্রির বিস্তারিত প্রোফাইল" else "Mistri Profile"
        AppScreen.CUSTOMER_BOOKINGS -> if (isBangla) "আমার কাজের তালিকা" else "My Bookings"
        AppScreen.JOB_TRACKING -> if (isBangla) "কাজের ট্র্যাকিং" else "Job Tracking"
        AppScreen.MISTRI_DASHBOARD -> if (isBangla) "মিস্ত্রি ড্যাশবোর্ড" else "Mistri Dashboard"
        AppScreen.MISTRI_EARNINGS -> if (isBangla) "আয় ও কমিশন বিবরণী" else "Earnings & Payout"
        AppScreen.MISTRI_PROFILE -> if (isBangla) "প্রোফাইল ও KYC" else "Profile & KYC"
        AppScreen.ADMIN_PORTAL -> if (isBangla) "অ্যাডমিন পোর্টাল" else "Admin Portal"
        AppScreen.CHAT_SCREEN -> if (isBangla) "ইন-অ্যাপ চ্যাট" else "Direct Chat"
        AppScreen.NOTIFICATIONS -> if (isBangla) "নোটিফিকেশন" else "Notifications"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (uiState.currentScreen != AppScreen.CHAT_SCREEN) {
                TopBarWithModeSwitch(
                    viewModel = viewModel,
                    title = topBarTitle,
                    canNavigateBack = canNavigateBack,
                    onBackClick = { viewModel.navigateBack() }
                )
            }
        },
        bottomBar = {
            // Show bottom navigation bar when on primary screens
            if (uiState.currentScreen == AppScreen.CUSTOMER_HOME ||
                uiState.currentScreen == AppScreen.CUSTOMER_BOOKINGS ||
                uiState.currentScreen == AppScreen.MISTRI_DASHBOARD ||
                uiState.currentScreen == AppScreen.MISTRI_EARNINGS ||
                uiState.currentScreen == AppScreen.MISTRI_PROFILE
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    if (uiState.activeRole == UserRole.CUSTOMER) {
                        NavigationBarItem(
                            selected = uiState.currentScreen == AppScreen.CUSTOMER_HOME,
                            onClick = { viewModel.navigateTo(AppScreen.CUSTOMER_HOME) },
                            icon = {
                                Icon(
                                    if (uiState.currentScreen == AppScreen.CUSTOMER_HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                    contentDescription = "Home"
                                )
                            },
                            label = { Text(if (isBangla) "হোম" else "Home", fontSize = 12.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MistriPrimary,
                                selectedTextColor = MistriPrimary,
                                indicatorColor = MistriPrimary.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_customer_home")
                        )

                        NavigationBarItem(
                            selected = uiState.currentScreen == AppScreen.CUSTOMER_BOOKINGS,
                            onClick = { viewModel.navigateTo(AppScreen.CUSTOMER_BOOKINGS) },
                            icon = {
                                Icon(
                                    if (uiState.currentScreen == AppScreen.CUSTOMER_BOOKINGS) Icons.Filled.Assignment else Icons.Outlined.Assignment,
                                    contentDescription = "Bookings"
                                )
                            },
                            label = { Text(if (isBangla) "আমার কাজ" else "My Jobs", fontSize = 12.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MistriPrimary,
                                selectedTextColor = MistriPrimary,
                                indicatorColor = MistriPrimary.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_customer_bookings")
                        )
                    } else if (uiState.activeRole == UserRole.MISTRI) {
                        NavigationBarItem(
                            selected = uiState.currentScreen == AppScreen.MISTRI_DASHBOARD,
                            onClick = { viewModel.navigateTo(AppScreen.MISTRI_DASHBOARD) },
                            icon = {
                                Icon(
                                    if (uiState.currentScreen == AppScreen.MISTRI_DASHBOARD) Icons.Filled.Dashboard else Icons.Outlined.Dashboard,
                                    contentDescription = "Dashboard"
                                )
                            },
                            label = { Text(if (isBangla) "ড্যাশবোর্ড" else "Dashboard", fontSize = 12.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MistriPrimary,
                                selectedTextColor = MistriPrimary,
                                indicatorColor = MistriPrimary.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_mistri_dashboard")
                        )

                        NavigationBarItem(
                            selected = uiState.currentScreen == AppScreen.MISTRI_EARNINGS,
                            onClick = { viewModel.navigateTo(AppScreen.MISTRI_EARNINGS) },
                            icon = {
                                Icon(
                                    if (uiState.currentScreen == AppScreen.MISTRI_EARNINGS) Icons.Filled.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet,
                                    contentDescription = "Earnings"
                                )
                            },
                            label = { Text(if (isBangla) "আয় ও কমিশন" else "Earnings", fontSize = 12.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MistriPrimary,
                                selectedTextColor = MistriPrimary,
                                indicatorColor = MistriPrimary.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_mistri_earnings")
                        )

                        NavigationBarItem(
                            selected = uiState.currentScreen == AppScreen.MISTRI_PROFILE,
                            onClick = { viewModel.navigateTo(AppScreen.MISTRI_PROFILE) },
                            icon = {
                                Icon(
                                    if (uiState.currentScreen == AppScreen.MISTRI_PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                                    contentDescription = "Profile"
                                )
                            },
                            label = { Text(if (isBangla) "প্রোফাইল" else "Profile", fontSize = 12.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MistriPrimary,
                                selectedTextColor = MistriPrimary,
                                indicatorColor = MistriPrimary.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_mistri_profile")
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.currentScreen) {
                AppScreen.CUSTOMER_HOME -> CustomerHomeScreen(viewModel = viewModel)
                AppScreen.MISTRI_DETAIL -> MistriDetailScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() })
                AppScreen.CUSTOMER_BOOKINGS -> CustomerBookingsScreen(viewModel = viewModel)
                AppScreen.JOB_TRACKING -> JobTrackingScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() })
                AppScreen.MISTRI_DASHBOARD -> MistriDashboardScreen(viewModel = viewModel)
                AppScreen.MISTRI_EARNINGS -> MistriEarningsScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() })
                AppScreen.MISTRI_PROFILE -> MistriProfileKycScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() })
                AppScreen.ADMIN_PORTAL -> AdminPortalScreen(viewModel = viewModel)
                AppScreen.CHAT_SCREEN -> ChatScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() })
                AppScreen.NOTIFICATIONS -> NotificationScreen(viewModel = viewModel, onBack = { viewModel.navigateBack() })
            }
        }
    }

    // Booking Dialog Modal
    if (uiState.isBookingDialogOpen && uiState.selectedMistri != null) {
        BookServiceDialog(
            mistri = uiState.selectedMistri!!,
            viewModel = viewModel,
            onDismiss = { viewModel.showBookingDialog(false) }
        )
    }

    // Call Simulation Dialog
    if (uiState.isCallingDialogOpen) {
        CallSimulationDialog(
            targetName = uiState.callingTargetName,
            targetPhone = uiState.callingTargetPhone,
            language = uiState.language,
            onDismiss = { viewModel.dismissCall() }
        )
    }
}
