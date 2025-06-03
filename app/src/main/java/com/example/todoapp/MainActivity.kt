package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.example.todoapp.data.db.TaskService
import com.example.todoapp.data.repository.TaskRepositoryImpl
import com.example.todoapp.ui.screens.TaskListScreen
import com.example.todoapp.ui.theme.TodoAppTheme
import com.example.todoapp.viewmodel.TaskViewModel
import com.example.todoapp.viewmodel.TaskViewModelFactory

class MainActivity : ComponentActivity() {
    val repository = TaskRepositoryImpl(service = TaskService())
    private lateinit var viewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val service = TaskService()
        val repository = TaskRepositoryImpl(service)
        val factory = TaskViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory)[TaskViewModel::class.java]
        enableEdgeToEdge()
        setContent {
            TodoAppTheme {
                TaskListScreen(viewModel)
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TodoAppTheme {
        TaskListScreen()
    }
}