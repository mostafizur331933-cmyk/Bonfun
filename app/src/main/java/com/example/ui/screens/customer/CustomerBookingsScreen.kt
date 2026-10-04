package com.example.ui.screens.customer

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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.Job
import com.example.data.model.JobStatus
import com.example.ui.components.CategoryIconHelper
import com.example.ui.theme.*
import com.example.ui.viewmodel.MarketplaceViewModel

@Composable
fun CustomerBookingsScreen(
    viewModel: MarketplaceViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val jobs by viewModel.customerJobs.collectAsState()
    val isBangla = uiState.language == AppLanguage.BN

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = if (isBangla) listOf("চলমান কাজ", "সম্পন্ন কাজ", "সকল রেকর্ড") else listOf("Active", "Completed", "All")

    val filteredJobs = remember(jobs, selectedTab) {
        when (selectedTab) {
            0 -> jobs.filter { it.status != JobStatus.COMPLETED && it.status != JobStatus.CONFIRMED && it.status != JobStatus.REVIEWED && it.status != JobStatus.CANCELLED }
            1 -> jobs.filter { it.status == JobStatus.COMPLETED || it.status == JobStatus.CONFIRMED || it.status == JobStatus.REVIEWED }
            else -> jobs
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MistriPrimary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        if (filteredJobs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AssignmentLate,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (isBangla) "কোনো কাজ পাওয়া যায়নি" else "No bookings found",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = if (isBangla) "হোম স্ক্রিন থেকে সেবা বুক করুন" else "Book a service from home screen",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredJobs) { job ->
                    JobBookingCard(
                        job = job,
                        language = uiState.language,
                        onClick = { viewModel.openJobTracking(job.id) },
                        onCall = { viewModel.triggerCall(job.mistriName, job.mistriPhone) }
                    )
                }
            }
        }
    }
}

@Composable
fun JobBookingCard(
    job: Job,
    language: AppLanguage,
    onClick: () -> Unit,
    onCall: () -> Unit
) {
    val isBangla = language == AppLanguage.BN

    val statusColor = when (job.status) {
        JobStatus.REQUESTED -> StatusRequested
        JobStatus.ACCEPTED -> StatusAccepted
        JobStatus.ON_THE_WAY -> StatusOnTheWay
        JobStatus.IN_PROGRESS -> StatusInProgress
        JobStatus.COMPLETED -> StatusCompleted
        JobStatus.CONFIRMED, JobStatus.REVIEWED -> MistriEmerald
        JobStatus.CANCELLED -> StatusCancelled
    }

    val statusText = when (job.status) {
        JobStatus.REQUESTED -> if (isBangla) "অনুরোধ পাঠানো হয়েছে" else "Requested"
        JobStatus.ACCEPTED -> if (isBangla) "মিস্ত্রি গ্রহণ করেছেন" else "Accepted"
        JobStatus.ON_THE_WAY -> if (isBangla) "মিস্ত্রি রওনা হয়েছেন" else "On the Way"
        JobStatus.IN_PROGRESS -> if (isBangla) "কাজ চলছে" else "In Progress"
        JobStatus.COMPLETED -> if (isBangla) "কাজ শেষ • পেমেন্ট বাকি" else "Done • Pay Pending"
        JobStatus.CONFIRMED -> if (isBangla) "পরিশোধিত ও সম্পন্ন" else "Paid & Completed"
        JobStatus.REVIEWED -> if (isBangla) "রিভিউ সম্পন্ন" else "Reviewed"
        JobStatus.CANCELLED -> if (isBangla) "বাতিল" else "Cancelled"
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, BorderLight),
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("job_card_${job.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Job ID & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "ID: ${job.id}",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MistriPrimary
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Service title and category
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MistriPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = CategoryIconHelper.getIconForCategory(job.categoryId),
                        contentDescription = null,
                        tint = MistriPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isBangla) job.serviceTitleBn else job.serviceTitleEn,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${job.mistriName} • ${job.customerArea}",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = BorderLight.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            // Bottom info: Date, Total, Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${job.scheduledDate}, ${job.scheduledTimeSlot}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "৳ ${job.totalAmount.toInt()}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MistriPrimary
                        )
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = onCall,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SurfaceLight)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = "Call", tint = MistriPrimary, modifier = Modifier.size(18.dp))
                    }

                    Button(
                        onClick = onClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MistriPrimary),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(if (isBangla) "ট্র্যাক করুন" else "Track", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
