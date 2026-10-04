package com.example.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.JobStatus
import com.example.ui.components.CategoryIconHelper
import com.example.ui.theme.*
import com.example.ui.viewmodel.MarketplaceViewModel

@Composable
fun AdminPortalScreen(
    viewModel: MarketplaceViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val allJobs by viewModel.allJobs.collectAsState()
    val pendingKycMistris by viewModel.pendingKycMistris.collectAsState()
    val isBangla = uiState.language == AppLanguage.BN

    var selectedAdminTab by remember { mutableStateOf(0) }
    val adminTabs = if (isBangla) listOf("সার্বিক ওভারভিউ", "KYC অনুমোদন (${pendingKycMistris.size})", "কাজের মনিটরিং")
    else listOf("Overview", "KYC Approvals (${pendingKycMistris.size})", "All Jobs Monitor")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab Row
        TabRow(
            selectedTabIndex = selectedAdminTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MistriPrimary
        ) {
            adminTabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedAdminTab == index,
                    onClick = { selectedAdminTab = index },
                    text = { Text(title, fontWeight = if (selectedAdminTab == index) FontWeight.Bold else FontWeight.Normal, fontSize = 12.sp) }
                )
            }
        }

        when (selectedAdminTab) {
            0 -> {
                // Executive Overview
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MistriPrimary,
                            contentColor = Color.White,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                    text = if (isBangla) "প্ল্যাটফর্ম রেভিনিউ ও GMV" else "Platform GMV & Net Revenue",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "৳ ১২,৪৮,৫০০",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 28.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = MistriEmerald,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = if (isBangla) "৮% প্ল্যাটফর্ম কমিশন" else "8% Commission Cut",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isBangla) "মোট আয়: ৳ ৯৯,৮৮০" else "Total Margin: ৳ 99,880",
                                        color = MistriAmberLight,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Total Mistris
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, BorderLight),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(if (isBangla) "নিবন্ধিত মিস্ত্রি" else "Registered Pros", fontSize = 11.sp, color = TextMuted)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("১৮৫ জন", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MistriPrimary)
                                    Text(if (isBangla) "৯৮% সক্রিয়" else "98% Active", fontSize = 10.sp, color = MistriEmerald)
                                }
                            }

                            // Total Bookings
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, BorderLight),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(if (isBangla) "মোট সার্ভিস জব" else "Total Bookings", fontSize = 11.sp, color = TextMuted)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("১,৪২০ টি", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MistriAmberDark)
                                    Text(if (isBangla) "ডিসপুট হার < 0.৫%" else "Dispute < 0.5%", fontSize = 10.sp, color = TextMuted)
                                }
                            }
                        }
                    }

                    // Security & Operational Health
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, BorderLight),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = if (isBangla) "প্ল্যাটফর্ম অপারেশনাল সেটিংস" else "Platform Operations & Commission",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(if (isBangla) "প্ল্যাটফর্ম সার্ভিস কমিশন হার:" else "Platform Commission Rate:")
                                    Text("8.0%", fontWeight = FontWeight.Bold, color = MistriEmerald)
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(if (isBangla) "জরুরি এক্সপ্রেস সারচার্জ:" else "Emergency Surcharge:")
                                    Text("৳ 150", fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(if (isBangla) "NID ও ব্যাকগ্রাউন্ড চেকিং:" else "Mandatory NID Verification:")
                                    Text(if (isBangla) "সক্রিয় (Active)" else "Active", color = MistriEmerald, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // KYC Approvals Queue
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (pendingKycMistris.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.TaskAlt, contentDescription = null, tint = MistriEmerald, modifier = Modifier.size(48.dp))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = if (isBangla) "কোনো আবেদন পেন্ডিং নেই!" else "All KYC applications reviewed!",
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (isBangla) "সকল মিস্ত্রি ভেরিফাইড।" else "No pending submissions.",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    } else {
                        items(pendingKycMistris) { pendingUser ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, BorderLight),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(pendingUser.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Surface(
                                            color = MistriAmberLight,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = if (isBangla) "যাচাই বাকি" else "Pending Review",
                                                color = MistriAmberDark,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = "${if (isBangla) pendingUser.categoryTitleBn else pendingUser.categoryTitleEn} • ${pendingUser.area}",
                                        fontSize = 12.sp,
                                        color = MistriPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text("NID: ${pendingUser.nidNumber}", fontSize = 12.sp, color = TextSecondary)
                                    Text("Trade: ${pendingUser.tradeLicense}", fontSize = 12.sp, color = TextSecondary)

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { viewModel.approveOrRejectKyc(pendingUser.id, false) },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
                                        ) {
                                            Text(if (isBangla) "প্রত্যাখ্যান" else "Reject")
                                        }

                                        Button(
                                            onClick = { viewModel.approveOrRejectKyc(pendingUser.id, true) },
                                            modifier = Modifier.weight(1f).testTag("btn_admin_approve_${pendingUser.id}"),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MistriEmerald)
                                        ) {
                                            Text(if (isBangla) "অনুমোদন করুন" else "Approve KYC", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // All Jobs Monitoring
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(allJobs) { job ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, BorderLight),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { viewModel.openJobTracking(job.id) }
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("ID: #${job.id}", fontWeight = FontWeight.Bold, color = MistriPrimary)
                                    Text(job.status.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MistriAmberDark)
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(if (isBangla) job.serviceTitleBn else job.serviceTitleEn, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Customer: ${job.customerName} | Mistri: ${job.mistriName}", fontSize = 11.sp, color = TextSecondary)

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Total: ৳ ${job.totalAmount.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = if (isBangla) "বিস্তারিত দেখুন →" else "View Audit →",
                                        color = MistriPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
