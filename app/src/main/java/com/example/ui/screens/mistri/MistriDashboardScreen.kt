package com.example.ui.screens.mistri

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.*
import com.example.ui.components.AddSparePartsDialog
import com.example.ui.components.CategoryIconHelper
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MarketplaceViewModel

@Composable
fun MistriDashboardScreen(
    viewModel: MarketplaceViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val mistriProfile by viewModel.currentMistriProfile.collectAsState()
    val mistriJobs by viewModel.mistriJobs.collectAsState()
    val isBangla = uiState.language == AppLanguage.BN

    val mistri = mistriProfile ?: return
    var targetJobForParts by remember { mutableStateOf<String?>(null) }

    // Incoming requests (REQUESTED status)
    val incomingRequests = mistriJobs.filter { it.status == JobStatus.REQUESTED }
    // Active jobs in progress (ACCEPTED, ON_THE_WAY, IN_PROGRESS, COMPLETED)
    val activeJobs = mistriJobs.filter {
        it.status == JobStatus.ACCEPTED || it.status == JobStatus.ON_THE_WAY || it.status == JobStatus.IN_PROGRESS || it.status == JobStatus.COMPLETED
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Availability Status Card (Online / Offline Toggle)
        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = if (mistri.isOnline) MistriPrimary else Color(0xFF334155),
                contentColor = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(if (mistri.isOnline) MistriEmerald else Color(0xFF94A3B8))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (mistri.isOnline) {
                                    if (isBangla) "অনলাইন • কাজের জন্য প্রস্তুত" else "ONLINE • Ready for Jobs"
                                } else {
                                    if (isBangla) "অফলাইন • নতুন কাজ আসবে না" else "OFFLINE • Not Available"
                                },
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (isBangla) "নতুন কাজের অনুরোধ পেতে অন রাখুন" else "Keep online to receive requests",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Switch(
                        checked = mistri.isOnline,
                        onCheckedChange = { viewModel.setMistriOnline(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MistriEmerald,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFF64748B)
                        ),
                        modifier = Modifier.testTag("switch_mistri_online")
                    )
                }
            }
        }

        // 2. Mistri Quick Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Today's Earnings
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, BorderLight),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { viewModel.navigateTo(AppScreen.MISTRI_EARNINGS) }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(if (isBangla) "আজকের আয়" else "Today's Net", fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("৳ ১,৮৫০", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MistriEmerald)
                        Text(if (isBangla) "৩টি কাজ" else "3 jobs", fontSize = 10.sp, color = TextSecondary)
                    }
                }

                // Completed Jobs Count
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(if (isBangla) "মোট কাজ" else "Total Jobs", fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${mistri.completedJobs}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MistriPrimary)
                        Text(if (isBangla) "সাফল্য ৯৮%" else "98% Rate", fontSize = 10.sp, color = TextSecondary)
                    }
                }

                // Rating
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(if (isBangla) "রেটিং" else "Rating", fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${mistri.rating}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MistriAmberDark)
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(Icons.Default.Star, contentDescription = null, tint = MistriAmber, modifier = Modifier.size(16.dp))
                        }
                        Text("${mistri.reviewCount} ${if (isBangla) "রিভিউ" else "reviews"}", fontSize = 10.sp, color = TextSecondary)
                    }
                }
            }
        }

        // 3. Quick Action Chips (Earnings, Profile & KYC)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.navigateTo(AppScreen.MISTRI_EARNINGS) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = MistriPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isBangla) "আয় ও কমিশন" else "Earnings")
                }

                OutlinedButton(
                    onClick = { viewModel.navigateTo(AppScreen.MISTRI_PROFILE) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = MistriEmerald, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isBangla) "প্রোফাইল ও KYC" else "Profile & KYC")
                }
            }
        }

        // 4. Incoming Job Requests (Alert Section)
        if (incomingRequests.isNotEmpty()) {
            item {
                Text(
                    text = if (isBangla) "নতুন কাজের অনুরোধ (${incomingRequests.size})" else "Incoming Job Requests (${incomingRequests.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                )
            }

            items(incomingRequests) { reqJob ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFEF2F2),
                    border = BorderStroke(1.5.dp, Color(0xFFEF4444)),
                    shadowElevation = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Job ID: #${reqJob.id}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEF4444)
                            )
                            if (reqJob.urgency == JobUrgency.EMERGENCY) {
                                Surface(
                                    color = Color(0xFFEF4444),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (isBangla) "জরুরি সার্ভিস!" else "EMERGENCY!",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isBangla) reqJob.serviceTitleBn else reqJob.serviceTitleEn,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        Text(
                            text = "${reqJob.customerName} • ${reqJob.customerAddress}",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${if (isBangla) "প্রত্যাশিত আয়: " else "Est. Earnings: "}৳ ${reqJob.totalAmount.toInt()}",
                                fontWeight = FontWeight.Bold,
                                color = MistriPrimary
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { viewModel.updateJobStatus(reqJob.id, JobStatus.CANCELLED) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
                                ) {
                                    Text(if (isBangla) "প্রত্যাখ্যান" else "Decline", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = { viewModel.updateJobStatus(reqJob.id, JobStatus.ACCEPTED) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MistriEmerald),
                                    modifier = Modifier.testTag("btn_mistri_accept_${reqJob.id}")
                                ) {
                                    Text(if (isBangla) "গ্রহণ করুন" else "Accept Job", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Active Jobs List
        item {
            Text(
                text = if (isBangla) "চলমান কাজের তালিকা (${activeJobs.size})" else "Active Assigned Jobs (${activeJobs.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        if (activeJobs.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = MistriEmerald, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isBangla) "কোনো কাজ পেন্ডিং নেই!" else "No pending active jobs!",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isBangla) "অনলাইন থাকুন, নতুন কাজের অনুরোধ আসবে।" else "Stay online to receive new requests.",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        } else {
            items(activeJobs) { job ->
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
                            Text("Job ID: #${job.id}", fontWeight = FontWeight.Bold, color = MistriPrimary)
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (job.status) {
                                    JobStatus.ACCEPTED -> StatusAccepted.copy(alpha = 0.15f)
                                    JobStatus.ON_THE_WAY -> StatusOnTheWay.copy(alpha = 0.15f)
                                    JobStatus.IN_PROGRESS -> StatusInProgress.copy(alpha = 0.15f)
                                    JobStatus.COMPLETED -> StatusCompleted.copy(alpha = 0.15f)
                                    else -> SurfaceLight
                                }
                            ) {
                                Text(
                                    text = when (job.status) {
                                        JobStatus.ACCEPTED -> if (isBangla) "গৃহীত" else "Accepted"
                                        JobStatus.ON_THE_WAY -> if (isBangla) "রওনা হয়েছেন" else "On the Way"
                                        JobStatus.IN_PROGRESS -> if (isBangla) "কাজ চলছে" else "In Progress"
                                        JobStatus.COMPLETED -> if (isBangla) "কাজ সম্পন্ন" else "Completed"
                                        else -> ""
                                    },
                                    color = MistriPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isBangla) job.serviceTitleBn else job.serviceTitleEn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )

                        Text(
                            text = "${job.customerName} • ${job.customerAddress}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Customer contact buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.triggerCall(job.customerName, job.customerPhone) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = MistriPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isBangla) "গ্রাহককে কল" else "Call Customer", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.openChat(job.id) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = null, tint = MistriPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isBangla) "মেসেজ" else "Chat", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = BorderLight)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Workflow Action Buttons according to current status
                        when (job.status) {
                            JobStatus.ACCEPTED -> {
                                Button(
                                    onClick = { viewModel.updateJobStatus(job.id, JobStatus.ON_THE_WAY) },
                                    modifier = Modifier.fillMaxWidth().testTag("btn_mistri_start_journey"),
                                    colors = ButtonDefaults.buttonColors(containerColor = MistriPrimary),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.DirectionsRun, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(if (isBangla) "রওনা দিন (Start Journey)" else "Start Journey")
                                }
                            }

                            JobStatus.ON_THE_WAY -> {
                                Button(
                                    onClick = { viewModel.updateJobStatus(job.id, JobStatus.IN_PROGRESS) },
                                    modifier = Modifier.fillMaxWidth().testTag("btn_mistri_arrived"),
                                    colors = ButtonDefaults.buttonColors(containerColor = MistriAmber),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Home, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(if (isBangla) "পৌঁছে গেছি ও কাজ শুরু করুন" else "I Have Arrived • Start Job")
                                }
                            }

                            JobStatus.IN_PROGRESS -> {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { targetJobForParts = job.id },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, tint = MistriAmberDark)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            if (isBangla) "+ মালামাল / পার্টসের বিল যোগ করুন" else "+ Add Spare Parts Cost",
                                            color = MistriAmberDark,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Button(
                                        onClick = { viewModel.updateJobStatus(job.id, JobStatus.COMPLETED) },
                                        modifier = Modifier.fillMaxWidth().testTag("btn_mistri_mark_completed"),
                                        colors = ButtonDefaults.buttonColors(containerColor = MistriEmerald),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(if (isBangla) "কাজ শেষ ঘোষণা করুন (Mark Completed)" else "Mark Work Completed")
                                    }
                                }
                            }

                            JobStatus.COMPLETED -> {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MistriEmeraldLight,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = if (isBangla) "কাজ শেষ হয়েছে। গ্রাহকের পেমেন্ট নিশ্চিতকরণের অপেক্ষায়..."
                                        else "Job finished. Waiting for customer confirmation & payment...",
                                        color = MistriEmerald,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }

    // Add Spare Parts Dialog
    if (targetJobForParts != null) {
        AddSparePartsDialog(
            jobId = targetJobForParts!!,
            language = uiState.language,
            onDismiss = { targetJobForParts = null },
            onConfirm = { cost ->
                val jId = targetJobForParts!!
                targetJobForParts = null
                viewModel.addSparePartsCost(jId, cost)
            }
        )
    }
}
