package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppLanguage
import com.example.data.model.PaymentMethod
import com.example.ui.theme.*
import com.example.ui.viewmodel.MarketplaceViewModel

@Composable
fun CallSimulationDialog(
    targetName: String,
    targetPhone: String,
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    val isBangla = language == AppLanguage.BN
    var isMuted by remember { mutableStateOf(false) }
    var isSpeakerOn by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF1E293B),
            contentColor = Color.White,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isBangla) "কল সংযোগ হচ্ছে..." else "Calling...",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MistriAmberLight)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MistriPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Contact Avatar",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = targetName,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = targetPhone,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.7f))
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Call Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(
                        onClick = { isMuted = !isMuted },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(if (isMuted) Color.White.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mute",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = { isSpeakerOn = !isSpeakerOn },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(if (isSpeakerOn) MistriEmerald else Color.White.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Speaker",
                            tint = Color.White
                        )
                    }

                    // End call button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .testTag("btn_end_call")
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444))
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End Call",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewSubmissionDialog(
    jobId: String,
    mistriId: String,
    mistriName: String,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, comment: String, tags: String) -> Unit
) {
    val isBangla = language == AppLanguage.BN
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }
    val selectedTags = remember { mutableStateListOf<String>() }

    val availableTags = if (isBangla) {
        listOf("সময়নিষ্ঠ", "দক্ষ মিস্ত্রি", "ন্যায্য মূল্য", "পরিচ্ছন্ন কাজ", "ভদ্র আচরণ")
    } else {
        listOf("On-Time", "Expert Tech", "Fair Price", "Clean Work", "Polite")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = if (isBangla) "মিস্ত্রিকে রেটিং দিন" else "Rate Your Mistri",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = mistriName,
                    fontSize = 13.sp,
                    color = MistriPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Star Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    (1..5).forEach { star ->
                        IconButton(
                            onClick = { rating = star },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = if (star <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Star $star",
                                tint = if (star <= rating) MistriAmber else TextMuted,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                // Quick Tags
                Text(
                    text = if (isBangla) "সার্ভিসের অভিজ্ঞতা বেছে নিন:" else "Select quick compliments:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    availableTags.take(3).forEach { tag ->
                        val isSelected = selectedTags.contains(tag)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (isSelected) selectedTags.remove(tag) else selectedTags.add(tag)
                            },
                            label = { Text(tag, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    placeholder = {
                        Text(
                            if (isBangla) "আপনার মন্তব্য লিখুন (ঐচ্ছিক)..." else "Write your feedback (optional)...",
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("input_review_comment"),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(rating, comment, selectedTags.joinToString(", "))
                },
                modifier = Modifier.testTag("btn_submit_review"),
                colors = ButtonDefaults.buttonColors(containerColor = MistriPrimary)
            ) {
                Text(if (isBangla) "রিভিউ জমা দিন" else "Submit Review")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isBangla) "পরে দেব" else "Cancel")
            }
        }
    )
}

@Composable
fun AddSparePartsDialog(
    jobId: String,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onConfirm: (cost: Double) -> Unit
) {
    val isBangla = language == AppLanguage.BN
    var partsName by remember { mutableStateOf("") }
    var partsCostText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isBangla) "যন্ত্রাংশ / মালামালের খরচ যোগ করুন" else "Add Spare Parts Cost",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (isBangla)
                        "কাজে ব্যবহৃত নতুন মালামাল (যেমন: পাইপ, ক্যাপাসিটর, তার ইত্যাদি)-এর বিল যোগ করুন।"
                    else
                        "Add cost for materials installed (e.g. capacitor, copper pipe, wiring).",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                OutlinedTextField(
                    value = partsName,
                    onValueChange = { partsName = it },
                    label = { Text(if (isBangla) "মালামালের বিবরণ" else "Parts Description") },
                    placeholder = { Text(if (isBangla) "যেমন: ২.৫ ইউএফ ক্যাপাসিটর" else "e.g. 2.5uF Capacitor") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = partsCostText,
                    onValueChange = {
                        partsCostText = it
                        errorMessage = null
                    },
                    label = { Text(if (isBangla) "খরচ (টাকা / BDT)" else "Cost (BDT ৳)") },
                    placeholder = { Text("450") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_parts_cost"),
                    shape = RoundedCornerShape(10.dp)
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cost = partsCostText.toDoubleOrNull()
                    if (cost != null && cost > 0) {
                        onConfirm(cost)
                    } else {
                        errorMessage = if (isBangla) "সঠিক পরিমাণ টাকা লিখুন" else "Please enter a valid amount"
                    }
                },
                modifier = Modifier.testTag("btn_confirm_add_parts"),
                colors = ButtonDefaults.buttonColors(containerColor = MistriAmber)
            ) {
                Text(if (isBangla) "যোগ করুন" else "Add to Bill")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isBangla) "বাতিল" else "Cancel")
            }
        }
    )
}

@Composable
fun PaymentOptionsDialog(
    totalAmount: Double,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSelectPayment: (PaymentMethod) -> Unit
) {
    val isBangla = language == AppLanguage.BN

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = if (isBangla) "পেমেন্ট মাধ্যম বেছে নিন" else "Select Payment Method",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Text(
                    text = if (isBangla)
                        "মোট প্রদেয় বিল: ৳ ${com.example.data.model.Localization.formatNumber(totalAmount.toInt(), language)}"
                    else
                        "Total Payable: ৳ ${totalAmount.toInt()}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MistriPrimary
                    )
                )

                Divider()

                // bKash Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFDF2F8),
                    border = BorderStroke(1.dp, Color(0xFFE11D48)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelectPayment(PaymentMethod.BKASH) }
                        .testTag("btn_pay_bkash")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE11D48)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("bK", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("bKash (বিকাশ)", fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(
                                if (isBangla) "ইনস্ট্যান্ট ডিজিটাল পেমেন্ট" else "Instant Mobile Banking",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                // Nagad Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFF7ED),
                    border = BorderStroke(1.dp, Color(0xFFEA580C)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelectPayment(PaymentMethod.NAGAD) }
                        .testTag("btn_pay_nagad")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEA580C)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("নগদ", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Nagad (নগদ)", fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(
                                if (isBangla) "ডাক বিভাগ ডিজিটাল লেনদেন" else "Postal Digital Banking",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                // Cash Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFECFDF5),
                    border = BorderStroke(1.dp, MistriEmerald),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelectPayment(PaymentMethod.CASH) }
                        .testTag("btn_pay_cash")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MistriEmerald),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Payments, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(if (isBangla) "ক্যাশ অন সার্ভিস (নগদ টাকা)" else "Cash on Service", fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(
                                if (isBangla) "মিস্ত্রিকে সরাসরি নগদ প্রদান করুন" else "Pay in cash directly to mistri",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(if (isBangla) "বাতিল" else "Cancel")
                }
            }
        }
    }
}
