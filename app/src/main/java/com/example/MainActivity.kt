package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.ChatRepository
import com.example.ui.ChatViewModel
import com.example.ui.ChatViewModelFactory
import com.example.ui.SigeonChatScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Initialize secure Room Database for Chat History Persistence
    val db = Room.databaseBuilder(
      applicationContext,
      AppDatabase::class.java, "sigeon_chat_db"
    ).build()
    
    val repository = ChatRepository(db.chatDao())
    val factory = ChatViewModelFactory(repository)
    
    val viewModel = ViewModelProvider(this, factory)[ChatViewModel::class.java]

    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        SigeonChatScreen(viewModel = viewModel)
      }
    }
  }
}
