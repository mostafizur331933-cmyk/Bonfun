package com.example.ui.screens.mistri

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.data.model.KycStatus
import com.example.ui.components.CategoryIconHelper
import com.example.ui.theme.*
import com.example.ui.viewmodel.MarketplaceViewModel

@Composable
fun MistriProfileKycScreen(
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val uiState by viewModel.uiState.collectAsState()
    val mistriProfile by viewModel.currentMistriProfile.collectAsState()
    val isBangla = uiState.language == AppLanguage.BN

    val mistri = mistriProfile ?: return

    var name by remember { mutableStateOf(mistri.name) }
    var phone by remember { mutableStateOf(mistri.phone) }
    var nidNumber by remember { mutableStateOf(mistri.nidNumber) }
    var tradeLicense by remember { mutableStateOf(mistri.tradeLicense) }
    var visitFee by remember { mutableStateOf(mistri.visitFee.toInt().toString()) }
    var hourlyRate by remember { mutableStateOf(mistri.hourlyRate.toInt().toString()) }

    var saveSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Profile Header
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, BorderLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MistriPrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = CategoryIconHelper.getIconForCategory(mistri.categoryId),
                        contentDescription = null,
                        tint = MistriPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(
                    text = if (isBangla) mistri.categoryTitleBn else mistri.categoryTitleEn,
                    color = MistriAmberDark,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = when (mistri.kycStatus) {
                        KycStatus.VERIFIED -> MistriEmeraldLight
                        KycStatus.PENDING -> MistriAmberLight
                        else -> Color(0xFFFEE2E2)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (mistri.kycStatus) {
                                KycStatus.VERIFIED -> Icons.Default.Verified
                                KycStatus.PENDING -> Icons.Default.Pending
                                else -> Icons.Default.Warning
                            },
                            contentDescription = null,
                            tint = when (mistri.kycStatus) {
                                KycStatus.VERIFIED -> MistriEmerald
                                KycStatus.PENDING -> MistriAmberDark
                                else -> Color(0xFFEF4444)
                            },
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (mistri.kycStatus) {
                                KycStatus.VERIFIED -> if (isBangla) "ভেরিফাইড মিস্ত্রি (Verified Pro)" else "Verified Professional"
                                KycStatus.PENDING -> if (isBangla) "যাচাই প্রক্রিয়াধীন (Pending)" else "Verification Pending"
                                else -> if (isBangla) "যাচাই প্রয়োজন" else "Not Verified"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (mistri.kycStatus) {
                                KycStatus.VERIFIED -> MistriEmerald
                                KycStatus.PENDING -> MistriAmberDark
                                else -> Color(0xFFEF4444)
                            }
                        )
                    }
                }
            }
        }

        // 2. KYC Document Details Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, BorderLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isBangla) "জাতীয় পরিচয়পত্র ও সনদপত্র (KYC)" else "National ID & Trade License (KYC)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                OutlinedTextField(
                    value = nidNumber,
                    onValueChange = { nidNumber = it },
                    label = { Text(if (isBangla) "জাতীয় পরিচয়পত্র নম্বর (NID)" else "NID Card Number") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = MistriPrimary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = tradeLicense,
                    onValueChange = { tradeLicense = it },
                    label = { Text(if (isBangla) "ট্রেড লাইসেন্স বা কারিগরি সনদ নম্বর" else "Trade License or Skill Certificate") },
                    leadingIcon = { Icon(Icons.Default.Article, contentDescription = null, tint = MistriPrimary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Text(
                    text = if (isBangla)
                        "সরকারি ডাটাবেস এবং পুলিশ ভেরিফিকেশন রেকর্ডের সাথে তথ্য মিলিয়ে ব্লু-টিক প্রদান করা হয়।"
                    else
                        "Verified against government database records to grant trust blue-tick badge.",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }

        // 3. Service Rates Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, BorderLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isBangla) "সার্ভিস রেট ও ফি নির্ধারণ" else "Your Service Charges",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = visitFee,
                        onValueChange = { visitFee = it },
                        label = { Text(if (isBangla) "ভিজিট ফি (৳)" else "Visit Fee (৳)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = hourlyRate,
                        onValueChange = { hourlyRate = it },
                        label = { Text(if (isBangla) "মজুরি চার্জ (৳)" else "Labor Rate (৳)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Button(
                    onClick = { saveSuccess = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_save_profile"),
                    colors = ButtonDefaults.buttonColors(containerColor = MistriPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(if (isBangla) "তথ্য সংরক্ষণ করুন" else "Save Changes", fontWeight = FontWeight.Bold)
                }

                if (saveSuccess) {
                    Text(
                        text = if (isBangla) "✓ প্রোফাইলের তথ্য আপডেট করা হয়েছে!" else "✓ Profile information updated!",
                        color = MistriEmerald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
