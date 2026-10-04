package com.example.ui.screens.common

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.data.model.ChatMessage
import com.example.data.model.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.MarketplaceViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatScreen(
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val uiState by viewModel.uiState.collectAsState()
    val job by viewModel.currentJob.collectAsState()
    val messages by viewModel.chatMessages.collectAsState()
    val isBangla = uiState.language == AppLanguage.BN

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var inputText by remember { mutableStateOf("") }

    val quickReplies = if (isBangla) {
        listOf("আমি রওনা দিয়েছি", "ঠিকানা নিশ্চিত করুন", "লিফটের ৪ তলায় আসুন", "গেটে এসে কল দিন", "কাজ শেষের পথে")
    } else {
        listOf("I am on the way", "Please confirm address", "Come to 4th floor", "Call when at gate", "Almost done")
    }

    val currentJob = job ?: return

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            Surface(
                color = MistriPrimary,
                contentColor = Color.White,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(60.dp)
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = if (uiState.activeRole == UserRole.CUSTOMER) currentJob.mistriName else currentJob.customerName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                            )
                            Text(
                                text = "Job ID: #${currentJob.id}",
                                style = MaterialTheme.typography.labelSmall.copy(color = MistriAmberLight)
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            val targetName = if (uiState.activeRole == UserRole.CUSTOMER) currentJob.mistriName else currentJob.customerName
                            val targetPhone = if (uiState.activeRole == UserRole.CUSTOMER) currentJob.mistriPhone else currentJob.customerPhone
                            viewModel.triggerCall(targetName, targetPhone)
                        }
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = "Call", tint = Color.White)
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    // Quick reply chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        items(quickReplies) { reply ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = SurfaceLight,
                                border = BorderStroke(1.dp, BorderLight),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        viewModel.sendChatMessage(reply)
                                    }
                            ) {
                                Text(
                                    text = reply,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // Input Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = {
                                Text(
                                    if (isBangla) "মেসেজ লিখুন..." else "Type message...",
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_chat_message"),
                            shape = RoundedCornerShape(20.dp),
                            maxLines = 3
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    val text = inputText
                                    inputText = ""
                                    viewModel.sendChatMessage(text)
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MistriPrimary)
                                .testTag("btn_send_chat")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SurfaceLight),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { message ->
                val isMyMessage = if (uiState.activeRole == UserRole.CUSTOMER) {
                    message.senderRole == UserRole.CUSTOMER
                } else {
                    message.senderRole == UserRole.MISTRI
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMyMessage) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isMyMessage) 16.dp else 2.dp,
                            bottomEnd = if (isMyMessage) 2.dp else 16.dp
                        ),
                        color = if (isMyMessage) MistriPrimary else Color.White,
                        shadowElevation = 1.dp,
                        border = if (!isMyMessage) BorderStroke(1.dp, BorderLight) else null,
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            if (!isMyMessage) {
                                Text(
                                    text = message.senderName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MistriAmberDark
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                            }

                            Text(
                                text = message.messageText,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (isMyMessage) Color.White else TextPrimary
                                )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                            Text(
                                text = timeFormat.format(Date(message.timestamp)),
                                fontSize = 9.sp,
                                color = if (isMyMessage) Color.White.copy(alpha = 0.7f) else TextMuted,
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }
    }
}
