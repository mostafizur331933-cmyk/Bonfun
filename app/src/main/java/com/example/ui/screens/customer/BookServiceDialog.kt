package com.example.ui.screens.customer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppLanguage
import com.example.data.model.JobUrgency
import com.example.data.model.User
import com.example.ui.theme.*
import com.example.ui.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookServiceDialog(
    mistri: User,
    viewModel: MarketplaceViewModel,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isBangla = uiState.language == AppLanguage.BN

    var serviceTitle by remember {
        mutableStateOf(if (isBangla) "${mistri.categoryTitleBn} পরিদর্শন ও মেরামত" else "${mistri.categoryTitleEn} Service & Repair")
    }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("House 42, Road 9/A, Dhanmondi") }
    var area by remember { mutableStateOf("Dhanmondi, Dhaka") }
    var isEmergency by remember { mutableStateOf(false) }

    val dateOptions = if (isBangla) listOf("আজকে (Today)", "আগামীকাল (Tomorrow)", "পরশু (Day After)")
    else listOf("Today", "Tomorrow", "Day After")
    var selectedDateIndex by remember { mutableStateOf(0) }

    val timeSlots = if (isBangla) listOf("সকাল (9 AM - 12 PM)", "দুপুর (12 PM - 4 PM)", "বিকাল (4 PM - 8 PM)")
    else listOf("Morning (9 AM - 12 PM)", "Afternoon (12 PM - 4 PM)", "Evening (4 PM - 8 PM)")
    var selectedTimeSlotIndex by remember { mutableStateOf(0) }

    val visitFee = mistri.visitFee
    val laborCost = mistri.hourlyRate
    val emergencySurcharge = if (isEmergency) 150.0 else 0.0
    val subtotal = visitFee + laborCost + emergencySurcharge
    val platformFee = subtotal * 0.08
    val totalEstimated = subtotal + platformFee

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isBangla) "সার্ভিস বুকিং অনুরোধ" else "Request Service Booking",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${mistri.name} • ${if (isBangla) mistri.categoryTitleBn else mistri.categoryTitleEn}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MistriPrimary, fontWeight = FontWeight.SemiBold)
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Service Title
                    OutlinedTextField(
                        value = serviceTitle,
                        onValueChange = { serviceTitle = it },
                        label = { Text(if (isBangla) "কাজের শিরোনাম" else "Service Title") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Problem description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(if (isBangla) "সমস্যার বিস্তারিত বিবরণ" else "Problem Description") },
                        placeholder = {
                            Text(
                                if (isBangla) "যেমন: এসি থেকে পানি পড়ছে এবং ঠান্ডা হচ্ছে না..."
                                else "e.g. AC leaking water and not cooling...",
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        minLines = 2
                    )

                    // Address
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text(if (isBangla) "আপনার পূর্ণ ঠিকানা" else "Your Full Address") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = MistriPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Urgency Toggle Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isEmergency) Color(0xFFFEF2F2) else SurfaceLight,
                        border = BorderStroke(1.dp, if (isEmergency) Color(0xFFEF4444) else BorderLight),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { isEmergency = !isEmergency }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isEmergency,
                                onCheckedChange = { isEmergency = it },
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFFEF4444))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = if (isBangla) "জরুরি সার্ভিস প্রয়োজন (১ ঘণ্টার মধ্যে)" else "Emergency Fast Service (Within 1 hr)",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isEmergency) Color(0xFFEF4444) else TextPrimary,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = if (isBangla) "জরুরি চার্জ +৳১৫০ যোগ হবে" else "+৳150 urgency dispatch fee",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }

                    // Scheduled Date Chips
                    Text(
                        text = if (isBangla) "কাজের তারিখ:" else "Scheduled Date:",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        dateOptions.forEachIndexed { index, option ->
                            FilterChip(
                                selected = selectedDateIndex == index,
                                onClick = { selectedDateIndex = index },
                                label = { Text(option, fontSize = 11.sp) }
                            )
                        }
                    }

                    // Time Slot Chips
                    Text(
                        text = if (isBangla) "উপযুক্ত সময়:" else "Preferred Time Slot:",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        timeSlots.forEachIndexed { index, slot ->
                            FilterChip(
                                selected = selectedTimeSlotIndex == index,
                                onClick = { selectedTimeSlotIndex = index },
                                label = { Text(slot, fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Price Estimate Breakdown Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MistriPrimaryContainer.copy(alpha = 0.5f),
                        border = BorderStroke(1.dp, MistriPrimary.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (isBangla) "আনুমানিক মূল্য তালিকা (Estimated Bill)" else "Estimated Cost Breakdown",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MistriPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(if (isBangla) "হোম ভিজিট ফি:" else "Visit Fee:", fontSize = 12.sp)
                                Text("৳ ${visitFee.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(if (isBangla) "বেসিক কাজের চার্জ:" else "Base Labor:", fontSize = 12.sp)
                                Text("৳ ${laborCost.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            if (isEmergency) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(if (isBangla) "জরুরি এক্সপ্রেস ফি:" else "Emergency Fee:", fontSize = 12.sp, color = Color(0xFFEF4444))
                                    Text("+৳ 150", fontSize = 12.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(if (isBangla) "প্ল্যাটফর্ম ফি (৮%):" else "Platform Fee (8%):", fontSize = 12.sp)
                                Text("৳ ${platformFee.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Divider(modifier = Modifier.padding(vertical = 4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(if (isBangla) "মোট আনুমানিক বিল:" else "Total Estimated:", fontWeight = FontWeight.Bold)
                                Text("৳ ${totalEstimated.toInt()}", fontWeight = FontWeight.Bold, color = MistriPrimary, fontSize = 16.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Confirm CTA Button
                Button(
                    onClick = {
                        viewModel.requestService(
                            serviceTitleEn = serviceTitle,
                            serviceTitleBn = serviceTitle,
                            description = description,
                            address = address,
                            area = area,
                            urgency = if (isEmergency) JobUrgency.EMERGENCY else JobUrgency.NORMAL,
                            scheduledDate = dateOptions[selectedDateIndex],
                            scheduledTimeSlot = timeSlots[selectedTimeSlotIndex]
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_confirm_booking"),
                    colors = ButtonDefaults.buttonColors(containerColor = MistriPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBangla) "বুকিং নিশ্চিত করুন (৳ ${totalEstimated.toInt()})" else "Confirm Request (৳ ${totalEstimated.toInt()})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
