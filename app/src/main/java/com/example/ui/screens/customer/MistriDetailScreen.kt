package com.example.ui.screens.customer

import androidx.activity.compose.BackHandler
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
import com.example.data.model.AppLanguage
import com.example.data.model.User
import com.example.ui.components.CategoryIconHelper
import com.example.ui.theme.*
import com.example.ui.viewmodel.MarketplaceViewModel

@Composable
fun MistriDetailScreen(
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val uiState by viewModel.uiState.collectAsState()
    val mistri = uiState.selectedMistri ?: return
    val reviews by viewModel.mistriReviews.collectAsState()
    val isBangla = uiState.language == AppLanguage.BN

    Scaffold(
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { viewModel.triggerCall(mistri.name, mistri.phone) },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("btn_detail_call"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = MistriPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isBangla) "সরাসরি কল" else "Direct Call", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.showBookingDialog(true) },
                        modifier = Modifier
                            .weight(1.4f)
                            .height(50.dp)
                            .testTag("btn_detail_request_service"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MistriPrimary)
                    ) {
                        Icon(Icons.Default.Handyman, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isBangla) "সেবা বুক করুন" else "Request Service", fontWeight = FontWeight.Bold)
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
            // 1. Profile Header Card
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, BorderLight),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(MistriPrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = CategoryIconHelper.getIconForCategory(mistri.categoryId),
                                contentDescription = null,
                                tint = MistriPrimary,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = mistri.name,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            if (mistri.isVerified) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.Verified, contentDescription = "Verified", tint = MistriEmerald)
                            }
                        }

                        Text(
                            text = if (isBangla) mistri.categoryTitleBn else mistri.categoryTitleEn,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MistriAmberDark,
                                fontWeight = FontWeight.SemiBold
                            )
                        )

                        Text(
                            text = mistri.area,
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Stats Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceLight, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = MistriAmber, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("${mistri.rating}", fontWeight = FontWeight.Bold)
                                }
                                Text(
                                    if (isBangla) "${mistri.reviewCount} রিভিউ" else "${mistri.reviewCount} Reviews",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }

                            Divider(
                                modifier = Modifier
                                    .height(30.dp)
                                    .width(1.dp)
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${mistri.completedJobs}", fontWeight = FontWeight.Bold, color = MistriEmerald)
                                Text(
                                    if (isBangla) "কাজ সম্পন্ন" else "Jobs Done",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }

                            Divider(
                                modifier = Modifier
                                    .height(30.dp)
                                    .width(1.dp)
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${mistri.experienceYears}+", fontWeight = FontWeight.Bold, color = MistriPrimary)
                                Text(
                                    if (isBangla) "বছর অভিজ্ঞতা" else "Years Exp",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }

            // 2. Pricing Transparency Card
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isBangla) "চার্জ ও ফির বিবরণ" else "Transparent Service Pricing",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(if (isBangla) "হোম ভিজিট ফি (পরিদর্শন ও যাচাই):" else "Home Visit & Inspection Fee:")
                            Text("৳ ${mistri.visitFee.toInt()}", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(if (isBangla) "বেসিক কাজের মজুরি (প্রথম ঘণ্টা):" else "Base Labor Rate (First Hour):")
                            Text("৳ ${mistri.hourlyRate.toInt()}", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(if (isBangla) "প্ল্যাটফর্ম সুরক্ষা ফি:" else "Platform Safety Guarantee Fee:")
                            Text(if (isBangla) "৮% (স্বচ্ছ)" else "8% (Included)", color = MistriEmerald, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isBangla)
                                "* কাজ শুরু করার পর অতিরিক্ত কোনো লুকানো চার্জ নেই। প্রয়োজনীয় মালামাল গ্রাহক সরাসরি কিনতে পারেন বা মিস্ত্রির মাধ্যমে ভাউচার সহ নিতে পারেন।"
                            else
                                "* No hidden fees. If spare parts are required, customer can provide them or technician can bill with official receipt.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                        )
                    }
                }
            }

            // 3. About & Verification Details
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isBangla) "মিস্ত্রির পরিচিতি" else "About the Professional",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = mistri.bio.ifBlank {
                                if (isBangla) "প্রশিক্ষণপ্রাপ্ত দক্ষ টেকনিশিয়ান। সততার সাথে কাজ করাই মূল লক্ষ্য।"
                                else "Certified expert technician dedicated to high quality and honest service."
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, lineHeight = 20.sp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (isBangla) "বিশেষ দক্ষতা (Skills):" else "Specialized Skills:",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = mistri.skills,
                            style = MaterialTheme.typography.bodySmall.copy(color = MistriPrimary, fontWeight = FontWeight.Medium)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MistriEmeraldLight,
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Badge, contentDescription = null, tint = MistriEmerald, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isBangla) "জাতীয় পরিচয়পত্র যাচাইকৃত" else "NID Verified",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MistriEmerald
                                    )
                                }
                            }

                            if (mistri.tradeLicense.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MistriPrimaryContainer,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = MistriPrimary, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isBangla) "ট্রেড লাইসেন্স প্রাপ্ত" else "Trade Certified",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MistriPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Customer Reviews Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBangla) "গ্রাহকদের মতামত ও রিভিউ (${reviews.size})" else "Customer Reviews (${reviews.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // 5. Reviews List
            if (reviews.isEmpty()) {
                item {
                    Text(
                        text = if (isBangla) "এখনো কোনো রিভিউ নেই।" else "No reviews yet.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            } else {
                items(reviews) { review ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
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
                                Text(review.customerName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    (1..review.rating).forEach {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = MistriAmber, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }

                            if (review.tags.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = review.tags,
                                    fontSize = 10.sp,
                                    color = MistriEmerald,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = review.comment,
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, fontSize = 13.sp)
                            )
                        }
                    }
                }
            }
        }
    }
}
