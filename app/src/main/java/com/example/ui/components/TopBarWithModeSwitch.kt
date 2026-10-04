package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithModeSwitch(
    viewModel: MarketplaceViewModel,
    title: String,
    canNavigateBack: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val isBangla = uiState.language == AppLanguage.BN
    var showRoleMenu by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.primary,
        contentColor = Color.White,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            // Main Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Back button or Brand Logo & Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (canNavigateBack) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .testTag("btn_top_bar_back")
                                .size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    } else {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MistriAmber),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Handyman,
                                contentDescription = "App Logo",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 18.sp
                            ),
                            maxLines = 1
                        )
                        // Role subtitle
                        Text(
                            text = when (uiState.activeRole) {
                                UserRole.CUSTOMER -> if (isBangla) "গ্রাহক মোড (Customer)" else "Customer Mode"
                                UserRole.MISTRI -> if (isBangla) "মিস্ত্রি মোড (Technician)" else "Mistri / Pro Mode"
                                UserRole.ADMIN -> if (isBangla) "অ্যাডমিন পোর্টাল" else "Admin Portal"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Right: Language toggle & Role switch dropdown & Notifications
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Language Toggle Chip
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { viewModel.toggleLanguage() }
                            .testTag("btn_language_toggle")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                tint = MistriAmberLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isBangla) "বাং" else "EN",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }

                    // Mode Switcher Button
                    Box {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MistriAmber,
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { showRoleMenu = true }
                                .testTag("btn_role_switch")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = when (uiState.activeRole) {
                                        UserRole.CUSTOMER -> Icons.Default.Person
                                        UserRole.MISTRI -> Icons.Default.Build
                                        UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                                    },
                                    contentDescription = "Role",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = when (uiState.activeRole) {
                                        UserRole.CUSTOMER -> if (isBangla) "গ্রাহক" else "Cust"
                                        UserRole.MISTRI -> if (isBangla) "মিস্ত্রি" else "Mistri"
                                        UserRole.ADMIN -> if (isBangla) "অ্যাডমিন" else "Admin"
                                    },
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "More",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showRoleMenu,
                            onDismissRequest = { showRoleMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = if (isBangla) "গ্রাহক মোড (Customer)" else "Customer Mode",
                                            fontWeight = if (uiState.activeRole == UserRole.CUSTOMER) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Text(
                                            text = if (isBangla) "সার্ভিস খুঁজুন ও বুক করুন" else "Find & book technicians",
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = MistriPrimary)
                                },
                                onClick = {
                                    showRoleMenu = false
                                    viewModel.switchRole(UserRole.CUSTOMER)
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = if (isBangla) "মিস্ত্রি মোড (Mistri / Pro)" else "Mistri / Pro Mode",
                                            fontWeight = if (uiState.activeRole == UserRole.MISTRI) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Text(
                                            text = if (isBangla) "কাজের অনুরোধ গ্রহণ ও ইনকাম" else "Accept jobs & manage earnings",
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Build, contentDescription = null, tint = MistriAmber)
                                },
                                onClick = {
                                    showRoleMenu = false
                                    viewModel.switchRole(UserRole.MISTRI)
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = if (isBangla) "অ্যাডমিন মোড (Admin Panel)" else "Admin Management",
                                            fontWeight = if (uiState.activeRole == UserRole.ADMIN) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Text(
                                            text = if (isBangla) "কেওয়াইসি অনুমোদন ও সার্বিক পর্যবেক্ষণ" else "KYC approval & dispute oversight",
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MistriEmerald)
                                },
                                onClick = {
                                    showRoleMenu = false
                                    viewModel.switchRole(UserRole.ADMIN)
                                }
                            )
                        }
                    }

                    // Notification Button
                    IconButton(
                        onClick = { viewModel.navigateTo(AppScreen.NOTIFICATIONS) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        BadgedBox(
                            badge = {
                                val notifs by viewModel.notifications.collectAsState()
                                val unread = notifs.count { !it.isRead }
                                if (unread > 0) {
                                    Badge(containerColor = MistriAmber) {
                                        Text(unread.toString(), fontSize = 10.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
