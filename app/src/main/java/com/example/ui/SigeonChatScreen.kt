package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.ChatMessage
import com.example.data.ChatSession
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SigeonChatScreen(
    viewModel: ChatViewModel,
    modifier: Modifier = Modifier
) {
    val activeSessionState by viewModel.activeSessionState.collectAsStateWithLifecycle()
    val sessions by viewModel.allSessions.collectAsStateWithLifecycle()
    val currentSessionId by viewModel.currentSessionId.collectAsStateWithLifecycle()
    val currentPersona by viewModel.currentPersona.collectAsStateWithLifecycle()
    val inputText by viewModel.inputText.collectAsStateWithLifecycle()
    val isSending by viewModel.isSending.collectAsStateWithLifecycle()

    val isDiagnosing by viewModel.isDiagnosing.collectAsStateWithLifecycle()
    val diagnosticReports by viewModel.diagnosticReports.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var showHistoryDialog by remember { mutableStateOf(false) }

    // Floating bubbles animation parameters
    val bubbleOffset = rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "bubbles"
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = SigeonLightBg,
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight()
                    .testTag("sessions_drawer")
            ) {
                Spacer(modifier = Modifier.statusBarsPadding())
                
                // Drawer Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(SigeonBlue, SigeonCyan)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "SIGEON OS CORE",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif
                        )
                        Text(
                            text = "ESTABLISHED 1991",
                            color = SigeonLightBg.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // New Chat Button
                Button(
                    onClick = {
                        viewModel.startNewSession("Session #${sessions.size + 1}")
                        coroutineScope.launch { drawerState.close() }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SigeonGreen,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(48.dp)
                        .testTag("new_chat_button"),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New Session")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("START NEW SESSION", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ACTIVE SYNC CHANNELS",
                    color = SigeonTextDark.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )

                // Sessions List
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    items(sessions, key = { it.id }) { session ->
                        val isSelected = session.id == currentSessionId
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) SigeonBlue.copy(alpha = 0.15f)
                                    else Color.Transparent
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) SigeonBlue.copy(alpha = 0.4f) else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    viewModel.selectSession(session.id)
                                    coroutineScope.launch { drawerState.close() }
                                }
                                .padding(12.dp)
                                .testTag("session_item_${session.id}")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Home,
                                        contentDescription = "Session",
                                        tint = if (isSelected) SigeonBlue else SigeonTextDark.copy(alpha = 0.5f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = session.title,
                                        color = SigeonTextDark,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        maxLines = 1
                                    )
                                }
                                
                                // Only show delete if there are multiple sessions
                                if (sessions.size > 1) {
                                    IconButton(
                                        onClick = { viewModel.deleteSession(session.id) },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .testTag("delete_session_${session.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Session",
                                            tint = Color.Red.copy(alpha = 0.6f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Footnote
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sigeon Company © 1991 - 2026",
                        fontSize = 11.sp,
                        color = SigeonTextDark.copy(alpha = 0.4f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                // Shiny Glossy Gradient Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(SigeonBlue, SigeonCyan)
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(horizontal = 12.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { coroutineScope.launch { drawerState.open() } },
                                    modifier = Modifier.testTag("menu_button")
                                ) {
                                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Column {
                                    Text(
                                        text = "SIGEON AI",
                                        color = Color.White,
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 20.sp,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Vibrant Frutiger Aero OS Gateway",
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            
                            Row {
                                IconButton(
                                    onClick = { showHistoryDialog = true },
                                    modifier = Modifier.testTag("history_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Sigeon OS Lore",
                                        tint = Color.White
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.runSigeonDiagnostics() },
                                    modifier = Modifier.testTag("diagnostics_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Diagnostics Diagnostics",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            },
            modifier = modifier.fillMaxSize()
        ) { innerPadding ->
            // Main canvas background with glossy bubbles drawn behind
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(SigeonLightBg, Color.White)
                        )
                    )
            ) {
                // Bubbles Canvas
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val paintBlue = Color(0x3300E5FF)
                    val paintGreen = Color(0x2200E676)
                    val baseOffset = bubbleOffset.value
                    
                    // Bubble 1
                    drawCircle(
                        color = paintBlue,
                        radius = 80.dp.toPx(),
                        center = androidx.compose.ui.geometry.Offset(
                            x = size.width * 0.15f,
                            y = (size.height * 0.8f - baseOffset * 0.5f) % size.height
                        )
                    )
                    // Bubble 2
                    drawCircle(
                        color = paintGreen,
                        radius = 120.dp.toPx(),
                        center = androidx.compose.ui.geometry.Offset(
                            x = size.width * 0.85f,
                            y = (size.height * 0.5f - baseOffset * 0.8f) % size.height
                        )
                    )
                    // Bubble 3
                    drawCircle(
                        color = paintBlue,
                        radius = 60.dp.toPx(),
                        center = androidx.compose.ui.geometry.Offset(
                            x = size.width * 0.5f,
                            y = (size.height * 0.2f - baseOffset * 0.3f) % size.height
                        )
                    )
                    // Bubble 4
                    drawCircle(
                        color = Color(0x15FFFF00),
                        radius = 90.dp.toPx(),
                        center = androidx.compose.ui.geometry.Offset(
                            x = size.width * 0.3f,
                            y = (size.height * 0.6f + baseOffset * 0.4f) % size.height
                        )
                    )
                }

                // Screen Layout
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Persona Select Bar (Glassmorphism look)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(16.dp)),
                        color = Color.White.copy(alpha = 0.55f),
                        shadowElevation = 4.dp,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "SELECT SIGEON HOST PERSONA",
                                color = SigeonTextDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                PersonaTab(
                                    name = "Sigeon AI",
                                    iconId = R.drawable.img_sigeon_banner, // Reuses banner as icon backer
                                    fallbackIcon = Icons.Default.Person,
                                    isSelected = currentPersona == "Assistant",
                                    onClick = { viewModel.selectPersona("Assistant") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("persona_assistant")
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                PersonaTab(
                                    name = "Nova (OS Guide)",
                                    iconId = R.drawable.img_nova_avatar,
                                    fallbackIcon = Icons.Default.Person,
                                    isSelected = currentPersona == "Nova",
                                    onClick = { viewModel.selectPersona("Nova") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("persona_nova")
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                PersonaTab(
                                    name = "Caramel (Cat)",
                                    iconId = R.drawable.img_caramel_avatar,
                                    fallbackIcon = Icons.Default.Person,
                                    isSelected = currentPersona == "Caramel",
                                    onClick = { viewModel.selectPersona("Caramel") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("persona_caramel")
                                )
                            }
                        }
                    }

                    // Active Channel Contents
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        when (val state = activeSessionState) {
                            is ChatUiState.Idle -> {
                                // No Session Active empty state
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    // Stunning custom brand logo image
                                    Card(
                                        modifier = Modifier
                                            .size(240.dp, 135.dp)
                                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.img_sigeon_banner),
                                            contentDescription = "Sigeon corporate banner",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))
                                    Text(
                                        text = "Connecting to Killeen Global HQ...",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SigeonTextDark,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Start a fresh chat synchronization channel to chat with your Sigeon OS digital companions, Caramel and Nova!",
                                        fontSize = 13.sp,
                                        color = SigeonTextDark.copy(alpha = 0.65f),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Button(
                                        onClick = { viewModel.startNewSession("First Sigeon Sync") },
                                        shape = RoundedCornerShape(24.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = SigeonBlue,
                                            contentColor = Color.White
                                        ),
                                        modifier = Modifier
                                            .height(48.dp)
                                            .testTag("initial_sync_button")
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = "Sync")
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("INITIALIZE SECURE SYNC CHANNEL", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            is ChatUiState.Active -> {
                                val listState = rememberLazyListState()
                                
                                // Autoscroll to latest response
                                LaunchedEffect(state.messages.size) {
                                    if (state.messages.isNotEmpty()) {
                                        listState.animateScrollToItem(state.messages.size - 1)
                                    }
                                }

                                Column(modifier = Modifier.fillMaxSize()) {
                                    // Live Message History stream
                                    LazyColumn(
                                        state = listState,
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp)
                                    ) {
                                        items(state.messages) { message ->
                                            SpeechBubble(
                                                message = message,
                                                currentPersona = currentPersona
                                            )
                                        }
                                        
                                        if (isSending) {
                                            item {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 8.dp),
                                                    contentAlignment = Alignment.CenterStart
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        CircularProgressIndicator(
                                                            modifier = Modifier.size(20.dp),
                                                            color = SigeonBlue,
                                                            strokeWidth = 2.dp
                                                        )
                                                        Spacer(modifier = Modifier.width(12.dp))
                                                        Text(
                                                            text = "$currentPersona is synthesizing feedback...",
                                                            color = SigeonTextDark.copy(alpha = 0.6f),
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Quick action chips for easy interaction
                                    if (state.messages.size <= 1) {
                                        Text(
                                            text = "RECOMMENDED ENQUIRIES",
                                            color = SigeonTextDark.copy(alpha = 0.6f),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
                                        )
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 12.dp, vertical = 4.dp),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            // Dynamic quick questions based on active persona
                                            val queries = if (currentPersona == "Caramel") {
                                                listOf("Make list of widgets!", "Play bubble animation?")
                                            } else if (currentPersona == "Nova") {
                                                listOf("Sigeon OS Shortcuts", "Diagnose local gateway")
                                            } else {
                                                listOf("Sigeon history founded 1991", "Killeen HQ Status")
                                            }

                                            queries.forEach { query ->
                                                SuggestionChip(
                                                    onClick = { viewModel.triggerQuickAction(query) },
                                                    label = { Text(query, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                                        containerColor = Color.White.copy(alpha = 0.7f),
                                                        labelColor = SigeonBlue
                                                    ),
                                                    border = SuggestionChipDefaults.suggestionChipBorder(
                                                        enabled = true,
                                                        borderColor = SigeonBlue.copy(alpha = 0.3f),
                                                        borderWidth = 1.dp
                                                    ),
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Glass Bottom Input Area
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .shadow(4.dp)
                                            .background(Color.White.copy(alpha = 0.85f))
                                            .navigationBarsPadding(),
                                        color = Color.White.copy(alpha = 0.85f)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            TextField(
                                                value = inputText,
                                                onValueChange = { viewModel.onInputTextChanged(it) },
                                                placeholder = {
                                                    Text(
                                                        text = "Communicate with $currentPersona...",
                                                        color = SigeonTextDark.copy(alpha = 0.45f)
                                                    )
                                                },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .testTag("chat_input"),
                                                colors = TextFieldDefaults.colors(
                                                    focusedContainerColor = SigeonLightBg.copy(alpha = 0.5f),
                                                    unfocusedContainerColor = SigeonLightBg.copy(alpha = 0.25f),
                                                    focusedTextColor = SigeonTextDark,
                                                    unfocusedTextColor = SigeonTextDark,
                                                    focusedIndicatorColor = Color.Transparent,
                                                    unfocusedIndicatorColor = Color.Transparent
                                                ),
                                                shape = RoundedCornerShape(24.dp),
                                                keyboardOptions = KeyboardOptions(
                                                    imeAction = ImeAction.Send
                                                ),
                                                keyboardActions = KeyboardActions(
                                                    onSend = {
                                                        viewModel.sendMessage()
                                                        focusManager.clearFocus()
                                                    }
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            IconButton(
                                                onClick = {
                                                    viewModel.sendMessage()
                                                    focusManager.clearFocus()
                                                },
                                                enabled = inputText.isNotBlank() && !isSending,
                                                modifier = Modifier
                                                    .background(
                                                        brush = Brush.verticalGradient(
                                                            colors = if (inputText.isNotBlank() && !isSending)
                                                                listOf(SigeonBlue, SigeonCyan)
                                                            else
                                                                listOf(Color.LightGray, Color.LightGray)
                                                        ),
                                                        shape = CircleShape
                                                    )
                                                    .size(48.dp)
                                                    .testTag("send_button")
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Send,
                                                    contentDescription = "Send",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Interactive Sigeon OS Diagnostic report dialog sheet overlay
                if (diagnosticReports != null) {
                    AlertDialog(
                        onDismissRequest = { viewModel.clearDiagnostics() },
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Settings, contentDescription = "Diagnostics", tint = SigeonBlue, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "SIGEON CORE TELEMETRY",
                                    color = SigeonTextDark,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily.SansSerif
                                )
                            }
                        },
                        text = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(androidx.compose.foundation.rememberScrollState())
                            ) {
                                Text(
                                    text = "Local system integrity scan completed with clean parameters.",
                                    fontSize = 12.sp,
                                    color = SigeonTextDark.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                diagnosticReports!!.forEach { report ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = SigeonLightBg.copy(alpha = 0.5f)
                                        ),
                                        border = BorderStroke(1.dp, SigeonBlue.copy(alpha = 0.15f))
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = report.nodeName,
                                                    fontWeight = FontWeight.Bold,
                                                    color = SigeonTextDark,
                                                    fontSize = 13.sp
                                                )
                                                Text(
                                                    text = report.detail,
                                                    fontSize = 11.sp,
                                                    color = SigeonTextDark.copy(alpha = 0.6f)
                                                )
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Healthy",
                                                    tint = SigeonGreen,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = report.status,
                                                    color = SigeonGreen,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = { viewModel.clearDiagnostics() },
                                modifier = Modifier.testTag("dismiss_diagnostics_button")
                            ) {
                                Text("ACKNOWLEDGE SCAN", fontWeight = FontWeight.Black, color = SigeonBlue)
                            }
                        },
                        containerColor = Color.White,
                        shape = RoundedCornerShape(20.dp)
                    )
                }

                if (isDiagnosing) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier
                                .padding(32.dp)
                                .border(1.dp, Color.White.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp),
                            shadowElevation = 8.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = SigeonCyan)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "PINGING SIGEON CORE SERVERS...",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = SigeonTextDark
                                )
                                Text(
                                    text = "Analyzing Killeen Headquarters Gateway nodes",
                                    fontSize = 11.sp,
                                    color = SigeonTextDark.copy(alpha = 0.5f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Sigeon OS History Timeline dialog
                if (showHistoryDialog) {
                    AlertDialog(
                        onDismissRequest = { showHistoryDialog = false },
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = "Lore", tint = SigeonGreen, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "SIGEON CHRONOLOGY",
                                    color = SigeonTextDark,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp
                                )
                            }
                        },
                        text = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(androidx.compose.foundation.rememberScrollState())
                            ) {
                                Text(
                                    text = "Explore the vibrant history and tech-optimism of Sigeon Company:",
                                    fontSize = 12.sp,
                                    color = SigeonTextDark.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                
                                TimelineItem(
                                    year = "1991",
                                    title = "Sigeon Founding",
                                    desc = "Sigeon Company is established near Killeen. The initial bubbly computing prototype kernel launches."
                                )
                                TimelineItem(
                                    year = "1998",
                                    title = "PlayStation Mall Alliance",
                                    desc = "The legendary PlayStation Mall portal synchronizes, establishing the primary user networking workspace and trading community hub."
                                )
                                TimelineItem(
                                    year = "2012",
                                    title = "Nova OS System Launch",
                                    desc = "The official glassmorphism terminal launches under the guiding wisdom of holographic OS directory Guide model, Nova."
                                )
                                TimelineItem(
                                    year = "2026",
                                    title = "Vibrant AI Sync Node",
                                    desc = "Caramel's widget bank merges with Sigeon AI, unleashing high-fidelity tech-optimism across multi-layered aero canvases."
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = { showHistoryDialog = false },
                                modifier = Modifier.testTag("dismiss_history_button")
                            ) {
                                Text("CLOSE ARCHIVES", fontWeight = FontWeight.Bold, color = SigeonGreen)
                            }
                        },
                        containerColor = Color.White,
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }
        }
    }
}

// Person Tab glassmorphism selection button
@Composable
fun PersonaTab(
    name: String,
    iconId: Int,
    fallbackIcon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(if (isSelected) 1.05f else 0.95f, label = "scale")
    Surface(
        onClick = onClick,
        modifier = modifier
            .padding(2.dp)
            .shadow(if (isSelected) 4.dp else 1.dp, RoundedCornerShape(12.dp))
            .border(
                width = 2.dp,
                color = if (isSelected) SigeonBlue else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ),
        color = if (isSelected) SigeonBlue.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.85f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.6f))
                    .border(1.dp, SigeonCyan.copy(alpha = 0.3f), CircleShape)
            ) {
                // Display generated high-fidelity avatar image or fallback icon
                Image(
                    painter = painterResource(id = iconId),
                    contentDescription = name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = name,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                color = if (isSelected) SigeonBlue else SigeonTextDark.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// Glossy chat speech bubble
@Composable
fun SpeechBubble(
    message: ChatMessage,
    currentPersona: String
) {
    val isUser = message.sender == "USER"
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor = if (isUser) {
        Brush.horizontalGradient(colors = listOf(SigeonBlue, SigeonCyan))
    } else {
        Brush.horizontalGradient(
            colors = when (message.sender) {
                "CARAMEL" -> listOf(Color(0xFFFFB74D), Color(0xFFFFCC80)) // Warm orange/yellow for Caramel
                "NOVA" -> listOf(Color(0xFF80DEEA), SigeonCyan) // Glowing blue/cyan for Nova
                else -> listOf(Color.White, Color(0xFFE1F5FE)) // Classic aero white
            }
        )
    }
    
    val bubbleShape = if (isUser) {
        RoundedCornerShape(16.dp, 16.dp, 2.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 2.dp)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        contentAlignment = alignment
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            if (!isUser) {
                // Show cute persona-specific avatar
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, SigeonBlue.copy(alpha = 0.5f), CircleShape)
                ) {
                    val res = when (message.sender) {
                        "CARAMEL" -> R.drawable.img_caramel_avatar
                        "NOVA" -> R.drawable.img_nova_avatar
                        else -> R.drawable.img_sigeon_banner
                    }
                    Image(
                        painter = painterResource(id = res),
                        contentDescription = message.sender,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(
                horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
            ) {
                // Sender label
                Text(
                    text = if (isUser) "Citizen Sync Node" else when (message.sender) {
                        "CARAMEL" -> "Caramel (OS Kitty Mascot) 🐾"
                        "NOVA" -> "Nova (AI Guide) 🌟"
                        else -> "Sigeon Assistant OS 🌊"
                    },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SigeonTextDark.copy(alpha = 0.5f),
                    modifier = Modifier.padding(bottom = 2.dp, start = 4.dp, end = 4.dp)
                )
                
                Surface(
                    modifier = Modifier
                        .shadow(3.dp, bubbleShape)
                        .border(
                            width = 1.dp,
                            color = if (isUser) Color.White.copy(alpha = 0.4f) else SigeonBlue.copy(alpha = 0.15f),
                            shape = bubbleShape
                        ),
                    color = Color.Transparent,
                    shape = bubbleShape
                ) {
                    Box(
                        modifier = Modifier
                            .background(brush = bubbleColor, shape = bubbleShape)
                            .padding(14.dp)
                    ) {
                        Text(
                            text = message.text,
                            color = if (isUser) Color.White else SigeonTextDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            if (isUser) {
                Spacer(modifier = Modifier.width(8.dp))
                // User Avatar bubble
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SigeonBlue)
                        .border(1.5.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "U",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

// Timeline item rows
@Composable
fun TimelineItem(
    year: String,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Surface(
            color = SigeonGreen.copy(alpha = 0.15f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.size(54.dp, 36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = year,
                    color = SigeonGreen,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Black, color = SigeonTextDark, fontSize = 14.sp)
            Text(text = desc, fontSize = 12.sp, color = SigeonTextDark.copy(alpha = 0.7f), lineHeight = 16.sp)
        }
    }
}
