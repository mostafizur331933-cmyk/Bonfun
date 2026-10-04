package com.example.ui.screens.customer

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.*
import com.example.ui.components.CategoryIconHelper
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MarketplaceViewModel

@Composable
fun CustomerHomeScreen(
    viewModel: MarketplaceViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val allMistris by viewModel.allMistris.collectAsState()
    val customerJobs by viewModel.customerJobs.collectAsState()

    val isBangla = uiState.language == AppLanguage.BN

    val areas = if (isBangla) {
        listOf("সব এলাকা", "ধানমন্ডি", "মিরপুর", "গুলশান", "উত্তরা", "বনানী", "মোহাম্মদপুর")
    } else {
        listOf("All", "Dhanmondi", "Mirpur", "Gulshan", "Uttara", "Banani", "Mohammadpur")
    }

    // Filter mistris based on category, search and area
    val filteredMistris = remember(allMistris, uiState.selectedCategoryId, uiState.searchQuery, uiState.selectedArea) {
        allMistris.filter { mistri ->
            val matchCategory = uiState.selectedCategoryId == null || mistri.categoryId == uiState.selectedCategoryId
            val matchSearch = uiState.searchQuery.isBlank() ||
                    mistri.name.contains(uiState.searchQuery, ignoreCase = true) ||
                    mistri.categoryTitleEn.contains(uiState.searchQuery, ignoreCase = true) ||
                    mistri.categoryTitleBn.contains(uiState.searchQuery, ignoreCase = true) ||
                    mistri.skills.contains(uiState.searchQuery, ignoreCase = true)
            val matchArea = uiState.selectedArea == "All" || uiState.selectedArea == "সব এলাকা" ||
                    mistri.area.contains(uiState.selectedArea, ignoreCase = true)
            matchCategory && matchSearch && matchArea
        }
    }

    val activeJob = customerJobs.firstOrNull { it.status != JobStatus.COMPLETED && it.status != JobStatus.CONFIRMED && it.status != JobStatus.REVIEWED && it.status != JobStatus.CANCELLED }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // 1. Hero Banner with Image & Emergency Service Callout
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hero_banner_service_1791083508832),
                    contentDescription = "Service Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Black.copy(alpha = 0.75f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Surface(
                        color = MistriAmber,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isBangla) "★ বিশ্বস্ত লোকাল সার্ভিস" else "★ Trusted Local Service",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isBangla) "যেকোনো জরুরি প্রয়োজনে দক্ষ মিস্ত্রি ডাকুন" else "Book Verified Mistris For Any Home Repair",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBangla) "নিরাপদ কাজ • ন্যায্য মূল্য • গ্যারান্টি" else "Safe Work • Fair Rates • Guaranteed",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp
                        )

                        // Quick Emergency Call button
                        Button(
                            onClick = {
                                viewModel.triggerCall("জরুরি হেল্পলাইন (Hotline)", "16247")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("btn_emergency_call")
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isBangla) "জরুরি কল" else "Emergency", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 2. Active Job Floating Tracker Banner (if customer has an active booking)
        if (activeJob != null) {
            item {
                Surface(
                    color = MistriPrimaryContainer,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, MistriPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { viewModel.openJobTracking(activeJob.id) }
                        .testTag("banner_active_job")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MistriPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsRun,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isBangla) "চলমান কাজ: " else "Active Job: ",
                                    style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary)
                                )
                                Text(
                                    text = activeJob.id,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MistriPrimary
                                    )
                                )
                            }
                            Text(
                                text = if (isBangla) activeJob.serviceTitleBn else activeJob.serviceTitleEn,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = when (activeJob.status) {
                                    JobStatus.REQUESTED -> if (isBangla) "অপেক্ষমাণ • মিস্ত্রির গ্রহণ বাকি" else "Requested • Waiting acceptance"
                                    JobStatus.ACCEPTED -> if (isBangla) "মিস্ত্রি গ্রহণ করেছেন" else "Accepted by Mistri"
                                    JobStatus.ON_THE_WAY -> if (isBangla) "মিস্ত্রি রওনা হয়েছেন" else "Mistri is On the Way"
                                    JobStatus.IN_PROGRESS -> if (isBangla) "কাজ চলছে" else "Work in Progress"
                                    else -> ""
                                },
                                style = MaterialTheme.typography.bodySmall.copy(color = StatusOnTheWay)
                            )
                        }

                        Button(
                            onClick = { viewModel.openJobTracking(activeJob.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = MistriPrimary),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(if (isBangla) "ট্র্যাক করুন" else "Track", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 3. Search Bar
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = {
                        Text(
                            if (isBangla) "মিস্ত্রি বা সেবা খুঁজুন (যেমন: এসি, ওয়্যারিং, কল মেরামত)..."
                            else "Search mistri or service (e.g. AC, pipe, electrician)...",
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = MistriPrimary)
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_search_mistri")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Area Filter Horizontal List
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(areas) { area ->
                        val isSelected = uiState.selectedArea == area
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setSelectedArea(area) },
                            label = { Text(area, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            leadingIcon = if (isSelected) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MistriPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // 4. Categories Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBangla) "সার্ভিস ক্যাটাগরি" else "Service Categories",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                )

                if (uiState.selectedCategoryId != null) {
                    TextButton(onClick = { viewModel.selectCategory(null) }) {
                        Text(if (isBangla) "সব দেখুন" else "Clear Filter", color = MistriAmberDark)
                    }
                }
            }
        }

        // 5. Category Chips / Grid Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(categories) { category ->
                    val isSelected = uiState.selectedCategoryId == category.id
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) MistriPrimary else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, if (isSelected) MistriPrimary else BorderLight),
                        shadowElevation = if (isSelected) 4.dp else 1.dp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { viewModel.selectCategory(category.id) }
                            .width(108.dp)
                            .testTag("cat_card_${category.id}")
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color.White.copy(alpha = 0.2f) else MistriPrimaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = CategoryIconHelper.getIconForCategory(category.id),
                                    contentDescription = category.titleEn,
                                    tint = if (isSelected) Color.White else MistriPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (isBangla) category.titleBn else category.titleEn,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else TextPrimary
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = "৳ ${category.baseRate.toInt()}+",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 10.sp,
                                    color = if (isSelected) MistriAmberLight else TextMuted
                                )
                            )
                        }
                    }
                }
            }
        }

        // 6. Verified Mistris Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBangla) "অভিজ্ঞ ও ভেরিফাইড মিস্ত্রি (${filteredMistris.size} জন)" else "Verified Mistris (${filteredMistris.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                )
            }
        }

        // 7. Mistri Cards List
        if (filteredMistris.isEmpty()) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isBangla) "কোনো মিস্ত্রি পাওয়া যায়নি" else "No mistris found",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (isBangla) "ক্যাটাগরি বা এলাকা ফিল্টার পরিবর্তন করে চেষ্টা করুন" else "Try clearing search or changing the area filter",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                    }
                }
            }
        } else {
            items(filteredMistris) { mistri ->
                MistriCard(
                    mistri = mistri,
                    language = uiState.language,
                    onViewProfile = { viewModel.openMistriDetail(mistri) },
                    onDirectCall = { viewModel.triggerCall(mistri.name, mistri.phone) }
                )
            }
        }
    }
}

@Composable
fun MistriCard(
    mistri: User,
    language: AppLanguage,
    onViewProfile: () -> Unit,
    onDirectCall: () -> Unit
) {
    val isBangla = language == AppLanguage.BN

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, BorderLight),
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onViewProfile() }
            .testTag("mistri_card_${mistri.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mistri Avatar with Verified Badge
                Box {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(MistriPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = CategoryIconHelper.getIconForCategory(mistri.categoryId),
                            contentDescription = null,
                            tint = MistriPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    if (mistri.isVerified) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(MistriEmerald),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Verified",
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = mistri.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        if (mistri.isVerified) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MistriEmeraldLight
                            ) {
                                Text(
                                    text = if (isBangla) "ভেরিফাইড" else "NID Verified",
                                    color = MistriEmerald,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = if (isBangla) mistri.categoryTitleBn else mistri.categoryTitleEn,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MistriAmberDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Rating
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = MistriAmber, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${mistri.rating} (${mistri.reviewCount})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text("•", color = TextMuted, fontSize = 10.sp)

                        // Experience
                        Text(
                            text = if (isBangla) "${mistri.experienceYears} বছরের অভিজ্ঞতা" else "${mistri.experienceYears} yrs exp",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )

                        Text("•", color = TextMuted, fontSize = 10.sp)

                        // Location
                        Text(
                            text = mistri.area,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Skills snippet
            if (mistri.skills.isNotBlank()) {
                Text(
                    text = mistri.skills,
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Divider(color = BorderLight.copy(alpha = 0.6f))

            Spacer(modifier = Modifier.height(8.dp))

            // Pricing and CTA buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (isBangla) "ভিজিট ফি" else "Visit Fee",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp)
                    )
                    Text(
                        text = "৳ ${mistri.visitFee.toInt()}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MistriPrimary
                        )
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Call Button
                    OutlinedButton(
                        onClick = onDirectCall,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_call_mistri_${mistri.id}")
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = "Call", tint = MistriPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isBangla) "কল" else "Call", fontSize = 12.sp)
                    }

                    // Book / Profile Button
                    Button(
                        onClick = onViewProfile,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MistriPrimary),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_view_profile_${mistri.id}")
                    ) {
                        Text(if (isBangla) "বুক করুন" else "Book", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
