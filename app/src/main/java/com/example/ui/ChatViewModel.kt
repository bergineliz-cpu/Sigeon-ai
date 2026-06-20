package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ChatMessage
import com.example.data.ChatSession
import com.example.data.ChatRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface ChatUiState {
    object Idle : ChatUiState
    data class Active(
        val session: ChatSession,
        val messages: List<ChatMessage>
    ) : ChatUiState
}

data class DiagnosticReport(
    val nodeName: String,
    val status: String,
    val detail: String,
    val healthy: Boolean
)

class ChatViewModel(private val repository: ChatRepository) : ViewModel() {

    val allSessions: StateFlow<List<ChatSession>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentSessionId = MutableStateFlow<String?>(null)
    val currentSessionId: StateFlow<String?> = _currentSessionId.asStateFlow()

    private val _currentPersona = MutableStateFlow("Assistant") // "Assistant", "Nova", "Caramel"
    val currentPersona: StateFlow<String> = _currentPersona.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    private val _diagnosticReports = MutableStateFlow<List<DiagnosticReport>?>(null)
    val diagnosticReports: StateFlow<List<DiagnosticReport>?> = _diagnosticReports.asStateFlow()
    
    private val _isDiagnosing = MutableStateFlow(false)
    val isDiagnosing: StateFlow<Boolean> = _isDiagnosing.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeSessionState: StateFlow<ChatUiState> = combine(
        _currentSessionId,
        allSessions
    ) { sessionId, sessions ->
        Pair(sessionId, sessions)
    }.flatMapLatest { (sessionId, sessions) ->
        if (sessionId == null) {
            val mostRecent = sessions.firstOrNull()
            if (mostRecent != null) {
                _currentSessionId.value = mostRecent.id
                repository.getMessagesForSession(mostRecent.id).map {
                    ChatUiState.Active(mostRecent, it)
                }
            } else {
                flowOf(ChatUiState.Idle)
            }
        } else {
            val matched = sessions.find { it.id == sessionId }
            if (matched != null) {
                repository.getMessagesForSession(sessionId).map {
                    ChatUiState.Active(matched, it)
                }
            } else {
                flowOf(ChatUiState.Idle)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ChatUiState.Idle)

    fun onInputTextChanged(text: String) {
        _inputText.value = text
    }

    fun selectPersona(persona: String) {
        _currentPersona.value = persona
    }

    fun selectSession(sessionId: String) {
        _currentSessionId.value = sessionId
    }

    fun startNewSession(title: String = "Sigeon Assistant Chat") {
        viewModelScope.launch {
            val newId = UUID.randomUUID().toString()
            val session = ChatSession(id = newId, title = title)
            repository.createSession(session)
            _currentSessionId.value = newId
            
            val welcomeText = when (_currentPersona.value) {
                "Caramel" -> "Meow! Caramel is online! 🐾 How can I help you customize your Sigeon desktop or play with widget bubbles today?"
                "Nova" -> "Welcome to Sigeon OS Guide! 🌟 Nova is ready to walk you through deep system diagnostics or share Sigeon shortcuts!"
                else -> "Greetings, Sigeon citizen! 🌊 Sigeon OS Virtual Assistant is fully operational. How may I optimize your workflow?"
            }
            repository.saveMessage(
                ChatMessage(
                    sessionId = newId,
                    sender = _currentPersona.value.uppercase(),
                    text = welcomeText,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            repository.deleteSession(sessionId)
            if (_currentSessionId.value == sessionId) {
                _currentSessionId.value = null
            }
        }
    }

    fun sendMessage() {
        val query = _inputText.value.trim()
        val sessionId = _currentSessionId.value
        val persona = _currentPersona.value
        if (query.isEmpty() || sessionId == null || _isSending.value) return

        _inputText.value = ""
        _isSending.value = true

        viewModelScope.launch {
            val userMsg = ChatMessage(
                sessionId = sessionId,
                sender = "USER",
                text = query,
                timestamp = System.currentTimeMillis()
            )
            repository.saveMessage(userMsg)

            val currentMessages = (activeSessionState.value as? ChatUiState.Active)?.messages ?: emptyList()

            val aiMsg = repository.askSigeonAI(sessionId, currentMessages, query, persona)
            repository.saveMessage(aiMsg)
            _isSending.value = false
        }
    }

    fun triggerQuickAction(prompt: String) {
        _inputText.value = prompt
        sendMessage()
    }

    fun runSigeonDiagnostics() {
        if (_isDiagnosing.value) return
        _isDiagnosing.value = true
        _diagnosticReports.value = null
        
        viewModelScope.launch {
            kotlinx.coroutines.delay(1800)
            _diagnosticReports.value = listOf(
                DiagnosticReport("Killeen HQ Sync Gateway", "Active", "Synchronized over organic carbon fiber optical grids", true),
                DiagnosticReport("PlayStation Mall Trade Server", "Active", "99.98% uptime, 4ms latency", true),
                DiagnosticReport("Caramel Widget Bank Engine", "Standby", "All cute cat animations optimized at 120 FPS", true),
                DiagnosticReport("Nova Core Shell Interpreter", "Active", "Version 2026.6.19 fully operational", true),
                DiagnosticReport("Frutiger Aero Render Engine", "Active", "Specular glass-shading, real-time bubble reflections stabilized", true)
            )
            _isDiagnosing.value = false
        }
    }

    fun clearDiagnostics() {
        _diagnosticReports.value = null
    }
}

class ChatViewModelFactory(private val repository: ChatRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
