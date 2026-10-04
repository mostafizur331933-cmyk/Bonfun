package com.example.ui.screens.mistri

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.MarketplaceViewModel

@Composable
fun MistriEarningsScreen(
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val uiState by viewModel.uiState.collectAsState()
    val mistriJobs by viewModel.mistriJobs.collectAsState()
    val isBangla = uiState.language == AppLanguage.BN

    val completedJobs = mistriJobs.filter { it.status == JobStatus.CONFIRMED || it.status == JobStatus.REVIEWED }

    var withdrawSuccess by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Wallet Balance Hero Card
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MistriPrimary,
                contentColor = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (isBangla) "উত্তোলনযোগ্য ব্যালেন্স" else "Withdrawable Balance",
                        style = MaterialTheme.typography.labelMedium.copy(color = Color.White.copy(alpha = 0.8f))
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "৳ ৪,৫৫০",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 32.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE11D48)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("bK", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "bKash: 01812-987654",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }

                        Button(
                            onClick = { withdrawSuccess = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MistriAmber),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("btn_withdraw_earnings")
                        ) {
                            Text(if (isBangla) "উত্তোলন করুন" else "Withdraw", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (withdrawSuccess) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MistriEmerald.copy(alpha = 0.9f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isBangla) "✓ বিকাশে টাকা পাঠানোর অনুরোধ গৃহীত হয়েছে!" else "✓ bKash transfer request submitted!",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. Summary Grid (Total Earnings, 8% Platform Fee, Settled)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(if (isBangla) "মোট গ্রস আয়" else "Gross Revenue", fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("৳ ৪২,৫০০", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = MistriPrimary)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(if (isBangla) "প্ল্যাটফর্ম ফি (৮%)" else "Platform Fee (8%)", fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("-৳ ৩,৪০০", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFFEF4444))
                    }
                }
            }
        }

        // 3. Transactions List Header
        item {
            Text(
                text = if (isBangla) "সাম্প্রতিক কাজের আয় বিবরণী" else "Recent Earnings History",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        // Sample transactions
        item {
            EarningsTransactionItem(
                jobId = "MS-84912",
                serviceTitle = if (isBangla) "মাস্টার এসি সার্ভিসিং ও জেট ওয়াশ" else "Master AC Servicing",
                customer = "Sidratul Muntaha (ধানমন্ডি)",
                gross = 700.0,
                platformFee = 56.0,
                net = 644.0,
                date = if (isBangla) "আজ, ১২:৩০ PM" else "Today, 12:30 PM",
                isBangla = isBangla
            )
        }

        item {
            EarningsTransactionItem(
                jobId = "MS-81001",
                serviceTitle = if (isBangla) "এসি গ্যাস রিফিল ও লিকেজ মেরামত" else "AC Gas Refill & Leak Fix",
                customer = "Tanvir Ahmed (মিরপুর)",
                gross = 1800.0,
                platformFee = 144.0,
                net = 1656.0,
                date = if (isBangla) "গতকাল" else "Yesterday",
                isBangla = isBangla
            )
        }

        item {
            EarningsTransactionItem(
                jobId = "MS-81002",
                serviceTitle = if (isBangla) "কম্প্রেসার পরিবর্তন" else "Compressor Replacement",
                customer = "Nusrat Jahan (গুলশান)",
                gross = 3200.0,
                platformFee = 256.0,
                net = 2944.0,
                date = if (isBangla) "৩ দিন আগে" else "3 days ago",
                isBangla = isBangla
            )
        }
    }
}

@Composable
fun EarningsTransactionItem(
    jobId: String,
    serviceTitle: String,
    customer: String,
    gross: Double,
    platformFee: Double,
    net: Double,
    date: String,
    isBangla: Boolean
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, BorderLight),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Job ID: #$jobId",
                    style = MaterialTheme.typography.labelMedium.copy(color = MistriPrimary, fontWeight = FontWeight.Bold)
                )
                Text(date, fontSize = 11.sp, color = TextMuted)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(serviceTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(customer, fontSize = 11.sp, color = TextSecondary)

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = BorderLight.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${if (isBangla) "গ্রস: " else "Gross: "}৳ ${gross.toInt()} | ${if (isBangla) "ফি: " else "Fee: "}-৳ ${platformFee.toInt()}",
                    fontSize = 11.sp,
                    color = TextMuted
                )
                Text(
                    text = "+৳ ${net.toInt()}",
                    fontWeight = FontWeight.ExtraBold,
                    color = MistriEmerald,
                    fontSize = 15.sp
                )
            }
        }
    }
}
