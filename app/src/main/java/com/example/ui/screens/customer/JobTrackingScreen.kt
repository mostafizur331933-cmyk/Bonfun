package com.example.ui.screens.customer

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.CategoryIconHelper
import com.example.ui.components.PaymentOptionsDialog
import com.example.ui.components.ReviewSubmissionDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MarketplaceViewModel

@Composable
fun JobTrackingScreen(
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val uiState by viewModel.uiState.collectAsState()
    val job by viewModel.currentJob.collectAsState()
    val isBangla = uiState.language == AppLanguage.BN

    var showPaymentDialog by remember { mutableStateOf(false) }

    if (job == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MistriPrimary)
        }
        return
    }

    val currentJob = job!!

    val steps = listOf(
        JobStatus.REQUESTED to (if (isBangla) "অনুরোধ পাঠানো হয়েছে" else "Service Requested"),
        JobStatus.ACCEPTED to (if (isBangla) "অনুরোধ গৃহীত" else "Request Accepted"),
        JobStatus.ON_THE_WAY to (if (isBangla) "মিস্ত্রি রওনা হয়েছেন" else "Mistri On the Way"),
        JobStatus.IN_PROGRESS to (if (isBangla) "কাজ চলছে" else "Work in Progress"),
        JobStatus.COMPLETED to (if (isBangla) "কাজ সম্পন্ন হয়েছে" else "Work Completed"),
        JobStatus.CONFIRMED to (if (isBangla) "পেমেন্ট নিশ্চিত" else "Confirmed & Paid")
    )

    val currentStepIndex = when (currentJob.status) {
        JobStatus.REQUESTED -> 0
        JobStatus.ACCEPTED -> 1
        JobStatus.ON_THE_WAY -> 2
        JobStatus.IN_PROGRESS -> 3
        JobStatus.COMPLETED -> 4
        JobStatus.CONFIRMED, JobStatus.REVIEWED -> 5
        JobStatus.CANCELLED -> -1
    }

    Scaffold(
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    when (currentJob.status) {
                        JobStatus.COMPLETED -> {
                            Button(
                                onClick = { showPaymentDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("btn_pay_confirm"),
                                colors = ButtonDefaults.buttonColors(containerColor = MistriEmerald),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Payment, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isBangla) "বিল পরিশোধ ও নিশ্চিত করুন (৳ ${currentJob.totalAmount.toInt()})"
                                    else "Confirm & Pay (৳ ${currentJob.totalAmount.toInt()})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        JobStatus.CONFIRMED -> {
                            Button(
                                onClick = { viewModel.showReviewDialog(true) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("btn_leave_review"),
                                colors = ButtonDefaults.buttonColors(containerColor = MistriAmber),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isBangla) "মিস্ত্রিকে রেটিং ও রিভিউ দিন" else "Leave Mistri Review",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        JobStatus.REVIEWED -> {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MistriEmeraldLight,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MistriEmerald)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isBangla) "সার্ভিস ও রিভিউ সফলভাবে সম্পন্ন হয়েছে!" else "Job & Review Completed!",
                                        color = MistriEmerald,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        JobStatus.REQUESTED -> {
                            OutlinedButton(
                                onClick = { viewModel.updateJobStatus(currentJob.id, JobStatus.CANCELLED) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(if (isBangla) "বুকিং বাতিল করুন" else "Cancel Request")
                            }
                        }

                        else -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.triggerCall(currentJob.mistriName, currentJob.mistriPhone) },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = MistriPrimary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isBangla) "কল" else "Call")
                                }

                                Button(
                                    onClick = { viewModel.openChat(currentJob.id) },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MistriPrimary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isBangla) "মেসেজ" else "Chat")
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Header Card with Unique Job ID
            item {
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
                            Column {
                                Text(
                                    text = if (isBangla) "কাজের ট্র্যাকিং" else "Job Tracking",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Job ID: #${currentJob.id}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MistriPrimary
                                    )
                                )
                            }

                            if (currentJob.urgency == JobUrgency.EMERGENCY) {
                                Surface(
                                    color = Color(0xFFFEF2F2),
                                    border = BorderStroke(1.dp, Color(0xFFEF4444)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (isBangla) "জরুরি এক্সপ্রেস" else "Emergency Express",
                                        color = Color(0xFFEF4444),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isBangla) currentJob.serviceTitleBn else currentJob.serviceTitleEn,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                        )

                        if (currentJob.description.isNotBlank()) {
                            Text(
                                text = currentJob.description,
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(currentJob.customerAddress, fontSize = 12.sp, color = TextMuted)
                        }
                    }
                }
            }

            // 2. Start OTP Card (Security Verification)
            if (currentJob.status == JobStatus.ACCEPTED || currentJob.status == JobStatus.ON_THE_WAY) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MistriPrimaryContainer,
                        border = BorderStroke(1.5.dp, MistriPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = MistriPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isBangla) "নিরাপত্তা স্টার্ট কোড (Security OTP)" else "Work Security OTP",
                                    fontWeight = FontWeight.Bold,
                                    color = MistriPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (isBangla) "মিস্ত্রি বাসায় পৌঁছানোর পর এই কোডটি তাকে দিন:"
                                else "Share this code with technician upon arrival to start work:",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                border = BorderStroke(2.dp, MistriAmber)
                            ) {
                                Text(
                                    text = currentJob.startOtp,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 6.sp,
                                    color = MistriPrimary,
                                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 3. Live Pipeline Stepper
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = if (isBangla) "কাজের অগ্রগতি ধাপসমূহ" else "Live Progress Pipeline",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        steps.forEachIndexed { index, (status, label) ->
                            val isCompleted = currentStepIndex >= index
                            val isCurrent = currentStepIndex == index

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    isCompleted -> MistriEmerald
                                                    isCurrent -> MistriPrimary
                                                    else -> BorderLight
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isCompleted) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        } else {
                                            Text(
                                                text = (index + 1).toString(),
                                                color = if (isCurrent) Color.White else TextMuted,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    if (index < steps.size - 1) {
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .height(26.dp)
                                                .background(if (currentStepIndex > index) MistriEmerald else BorderLight)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.padding(bottom = if (index < steps.size - 1) 14.dp else 0.dp)) {
                                    Text(
                                        text = label,
                                        fontWeight = if (isCurrent) FontWeight.Bold else if (isCompleted) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isCurrent) MistriPrimary else if (isCompleted) TextPrimary else TextMuted,
                                        fontSize = 14.sp
                                    )
                                    if (isCurrent) {
                                        Text(
                                            text = if (isBangla) "বর্তমান অবস্থা" else "Current Stage",
                                            fontSize = 11.sp,
                                            color = MistriAmberDark,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Mistri Contact Card
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(MistriPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MistriPrimary, modifier = Modifier.size(28.dp))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(currentJob.mistriName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(currentJob.mistriPhone, fontSize = 12.sp, color = TextMuted)
                        }

                        IconButton(
                            onClick = { viewModel.triggerCall(currentJob.mistriName, currentJob.mistriPhone) },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MistriPrimaryContainer)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = "Call", tint = MistriPrimary)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { viewModel.openChat(currentJob.id) },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MistriAmberLight)
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = "Chat", tint = MistriAmberDark)
                        }
                    }
                }
            }

            // 5. Cost Breakdown
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isBangla) "বিল ও খরচের বিস্তারিত" else "Detailed Bill Breakdown",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(if (isBangla) "হোম ভিজিট ফি:" else "Home Visit Fee:", fontSize = 13.sp)
                            Text("৳ ${currentJob.visitFee.toInt()}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(if (isBangla) "কাজের মজুরি চার্জ:" else "Labor Charge:", fontSize = 13.sp)
                            Text("৳ ${currentJob.laborCost.toInt()}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        if (currentJob.partsCost > 0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(if (isBangla) "মালামাল / যন্ত্রাংশ বিল:" else "Spare Parts Bill:", fontSize = 13.sp, color = MistriAmberDark)
                                Text("+৳ ${currentJob.partsCost.toInt()}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MistriAmberDark)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(if (isBangla) "প্ল্যাটফর্ম সুরক্ষা ফি (৮%):" else "Platform Fee (8%):", fontSize = 13.sp)
                            Text("৳ ${currentJob.platformFeeAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBangla) "সর্বমোট প্রদেয় বিল:" else "Total Amount Payable:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "৳ ${currentJob.totalAmount.toInt()}",
                                fontWeight = FontWeight.ExtraBold,
                                color = MistriPrimary,
                                fontSize = 20.sp
                            )
                        }

                        if (currentJob.isPaid) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MistriEmeraldLight,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (isBangla) "✓ পরিশোধিত মাধ্যম: ${currentJob.paymentMethod.name}" else "✓ Paid via: ${currentJob.paymentMethod.name}",
                                    color = MistriEmerald,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Payment Dialog
    if (showPaymentDialog) {
        PaymentOptionsDialog(
            totalAmount = currentJob.totalAmount,
            language = uiState.language,
            onDismiss = { showPaymentDialog = false },
            onSelectPayment = { method ->
                showPaymentDialog = false
                viewModel.confirmAndPay(currentJob.id, method)
            }
        )
    }

    // Review Dialog
    if (uiState.isReviewDialogOpen) {
        ReviewSubmissionDialog(
            jobId = currentJob.id,
            mistriId = currentJob.mistriId,
            mistriName = currentJob.mistriName,
            language = uiState.language,
            onDismiss = { viewModel.showReviewDialog(false) },
            onSubmit = { rating, comment, tags ->
                viewModel.submitReview(currentJob.id, currentJob.mistriId, rating, comment, tags)
            }
        )
    }
}
