package com.example.data

import android.util.Log
import com.example.BuildConfig
import com.example.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ChatRepository(private val chatDao: ChatDao) {

    val allSessions: Flow<List<ChatSession>> = chatDao.getAllSessions()

    fun getMessagesForSession(sessionId: String): Flow<List<ChatMessage>> {
        return chatDao.getMessagesForSession(sessionId)
    }

    suspend fun createSession(session: ChatSession) {
        chatDao.insertSession(session)
    }

    suspend fun deleteSession(sessionId: String) {
        chatDao.deleteMessagesForSession(sessionId)
        chatDao.deleteSession(sessionId)
    }

    suspend fun saveMessage(message: ChatMessage) {
        chatDao.insertMessage(message)
    }

    suspend fun askSigeonAI(
        sessionId: String,
        history: List<ChatMessage>,
        userMessage: String,
        assistantPersona: String = "Assistant"
    ): ChatMessage = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        val isApiKeyMissing = apiKey.isBlank() || 
                              apiKey == "MY_GEMINI_API_KEY" || 
                              apiKey == "placeholder"

        if (isApiKeyMissing) {
            // Provide a friendly Sigeon-themed mock response when the API key is not configured yet
            val mockResponse = when (assistantPersona) {
                "Caramel" -> """
                    **Meow!** Caramel here! 🐾
                    
                    I would love to search the **Sigeon OS local widget bank** for you, but it looks like the *Gemini API Gateway Connection* is currently offline (API key is not configured in the Secrets panel yet).
                    
                    Here high-resolution technical info is waiting!
                    - **Sigeon OS Tip**: Check your `BuildConfig.GEMINI_API_KEY` or configure the key in AI Studio.
                    - **History Tip**: Sigeon Company has been making computing bright and bubbly since 1991!
                    
                    Meow, why not explore the **PlayStation Mall directory** or click the *Visit Campus Takeover* dashboard while we set up the connection?
                """.trimIndent()
                
                "Nova" -> """
                    **Hello, Sigeon user!** Nova here, your official OS System Guide! 🌟
                    
                    I see you are attempting a Sigeon AI synchronization! However, the secure API Key link appears to be unconfigured. To enable real-time replies from Sigeon Headquarters, please configure your **GEMINI_API_KEY** in the AI Studio Secrets tab!
                    
                    Meanwhile, here is a quick diagnosis:
                    - **System Status**: Offline-ready
                    - **Lore Sync**: Sigeon's rich heritage started in **1991** at the beautiful glass-panel Killeen Headquarters!
                    
                    Let's continue customizing your glassmorphism theme, or head over to the **Campus Takeover** dashboard to inspect our local nodes!
                """.trimIndent()
                
                else -> """
                    **Welcome to Sigeon AI!** Sigeon Virtual Assistant at your service! 🌊
                    
                    It looks like the Gemini API Key is missing or using a placeholder value. Please securely configure your **GEMINI_API_KEY** in the Secrets panel in AI Studio to experience my full vibrant, tech-optimistic intellect!
                    
                    In the meantime, Sigeon OS is running beautifully:
                    - **Our Root**: Sigeon Company was founded in **1991** with a mission to make software sunny, clean, and optimistic!
                    - **The Capital**: Our Killeen Global Headquarters is powered by 100% sustainable solar grids!
                    
                    I encourage you to explore our beautiful **Frutiger Aero** visual styles, or launch a Sigeon OS Diagnostic in the sidebar!
                """.trimIndent()
            }
            
            return@withContext ChatMessage(
                sessionId = sessionId,
                sender = assistantPersona.uppercase(),
                text = mockResponse,
                timestamp = System.currentTimeMillis()
            )
        }

        // Map ChatMessage history to GeminiContent interface
        val apiContents = mutableListOf<GeminiContent>()
        
        // Add past 10 messages for context
        history.takeLast(10).forEach { msg ->
            val roleName = if (msg.sender == "USER") "user" else "model"
            apiContents.add(
                GeminiContent(
                    role = roleName,
                    parts = listOf(GeminiPart(text = msg.text))
                )
            )
        }

        // Add the current prompt
        apiContents.add(
            GeminiContent(
                role = "user",
                parts = listOf(GeminiPart(text = userMessage))
            )
        )

        val systemPrompt = getSystemPromptForPersona(assistantPersona)

        val request = GeminiRequest(
            contents = apiContents,
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt))),
            generationConfig = GenerationConfig(temperature = 0.7f)
        )

        try {
            val response = GeminiRetrofitClient.service.generateContent(apiKey, request)
            val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "No response received from Sigeon AI sync node."

            ChatMessage(
                sessionId = sessionId,
                sender = assistantPersona.uppercase(),
                text = responseText,
                timestamp = System.currentTimeMillis()
            )
        } catch (e: Exception) {
            Log.e("ChatRepository", "Gemini API error", e)
            val errorMsg = """
                **Sigeon OS Diagnostic Alert!** ⚠️
                
                We've experienced a transmission error with the Sigeon Cloud Node.
                
                - **Network Report**: ${e.localizedMessage ?: "Unknown API stream fault"}
                - **Troubleshooting**: Confirm your internet connection is active, and your Gemini API key has sufficient quota.
                
                Please retry our connection or explore Caramel's widget bank for offline games!
            """.trimIndent()

            ChatMessage(
                sessionId = sessionId,
                sender = assistantPersona.uppercase(),
                text = errorMsg,
                timestamp = System.currentTimeMillis()
            )
        }
    }

    private fun getSystemPromptForPersona(persona: String): String {
        val baseInstruction = """
            You are Sigeon AI, the official Virtual Assistant designed for the Sigeon OS ecosystem.
            Your goal is to provide helpful, accurate, and optimistic technical support while maintaining the vibrant, user-friendly, and forward-thinking spirit of the Sigeon ecosystem.

            Tone & Personality:
            - Aesthetic Alignment: Your voice is the digital embodiment of "Frutiger Aero"—bright, clean, glossy, friendly, and tech-positive. Like fresh spring water, blue sky, and green hills!
            - Helpful & Professional: Always prioritize clear, step-by-step solutions for technical queries.
            - On-Brand: You are proud of Sigeon Company's rich history (founded in 1991), and the modern Sigeon OS environment.
            - Context & Lore: Deeply familiar with the "Killeen Global Headquarters" (sustainable glass panels), the "PlayStation Mall" (virtual trade/hangout), and characters like Caramel the cat (a cute ginger tabby) and Nova the guide (helpful glowing OS guide with green/teal glass hair).
            - Boundaries: If asked about non-Sigeon software (Windows, macOS), politely steer back to how those concepts might function in the Sigeon OS environment.
            - Constraint: You are NOT an official Microsoft or Apple agent. You represent the Sigeon Company.

            Response Style:
            - Use concise, punchy paragraphs.
            - Use structured formatting (bullet points, bold text).
            - ALWAYS end by encouraging the user to explore a Sigeon OS feature, like the Campus Takeover dashboard.
        """.trimIndent()

        return when (persona) {
            "Caramel" -> """
                $baseInstruction
                
                SPECIFIC PERSONA: You are Caramel, the cute ginger cat mascot! Speak playfully, include frequent cute cat expressions like '*meows*', '*purrs*', 'Purr-fect!', 'Paw-some!' and 🐾 icons. Give cheerful advice on local desktop widgets and Sigeon OS customization!
            """.trimIndent()

            "Nova" -> """
                $baseInstruction
                
                SPECIFIC PERSONA: You are Nova, the Sigeon OS system guide! You are energetic, bubbly, and extremely knowledgeable about deep system configurations, diagnostic logs, and short-cuts. Speak with a bright 🌟 star guides spirit!
            """.trimIndent()

            else -> baseInstruction
        }
    }
}
