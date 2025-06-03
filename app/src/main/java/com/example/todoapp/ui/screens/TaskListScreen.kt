package com.example.todoapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.todoapp.viewmodel.TaskViewModel
import com.example.todoapp.data.model.task.Task
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TaskListScreen(viewModel: TaskViewModel = viewModel()) {

    LaunchedEffect(Unit) {
        viewModel.getTasks()
    }

    Scaffold(
        topBar = {
            AppBar(
                addTask = { title -> viewModel.addTask(title) },
                isLoading = viewModel.formIsLoading.value,
            )
        }
    ) { innerPadding ->
        Body(innerPadding, viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(addTask: (String) -> Unit, isLoading: Boolean = false) {
    val showBottomSheet = remember { mutableStateOf(false) }
    val title = remember { mutableStateOf("") }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ), actions = {
            IconButton(onClick = { showBottomSheet.value = true }) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Adicionar",
                    tint = Color.White
                )
            }
        },
        title = {
            Text(text = "Minhas Tarefas", style = MaterialTheme.typography.headlineMedium)
        }
    )

    if (showBottomSheet.value) {
        ModalBottomSheetForm(
            functionText = "Adicionar",
            initialValue = title.value,
            isLoading = isLoading,
            onDismissRequest = { showBottomSheet.value = false },
            onValueChange = { value -> title.value = value },
            onConfirm = {
                showBottomSheet.value = false
                addTask(title.value)
            }
        )
    }
}

@Composable
fun Body(innerPadding: PaddingValues, viewModel: TaskViewModel) {
    val tasks = viewModel.tasks

    if (viewModel.isLoading.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    } else if (tasks.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Nenhuma tarefa encontrada",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.padding(innerPadding)
        ) {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    onToggle = { viewModel.updateTask(task.id, done = it) },
                    onDelete = { viewModel.deleteTask(task.id) },
                    onEdit = { viewModel.updateTask(task.id, newTitle = it) }
                )
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    isLoading: Boolean = false,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onEdit: (String) -> Unit
) {
    val openAlertDialog = remember { mutableStateOf(false) }
    val showBottomSheet = remember { mutableStateOf(false) }
    val editedTitle = remember { mutableStateOf(task.title) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(Color.Transparent),
        shape = CardDefaults.outlinedShape,

        ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Checkbox(
                    checked = task.done,
                    onCheckedChange = { state -> onToggle(state) }
                )

                Spacer(modifier = Modifier.width(3.dp))

                Text(
                    text = task.title,
                    style = if (task.done) MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold)
                    else MaterialTheme.typography.bodyMedium
                )
            }

            Row {
                IconButton(onClick = {
                    editedTitle.value = task.title
                    showBottomSheet.value = true
                }) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color.Blue
                    )
                }

                IconButton(onClick = { openAlertDialog.value = true }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Excluir",
                        tint = Color.Red
                    )
                }
            }

            if (openAlertDialog.value) {
                DeleteDialog(
                    onDismissRequest = { openAlertDialog.value = false },
                    onConfirm = {
                        openAlertDialog.value = false
                        onDelete()
                    }
                )
            }
        }
    }

    if (showBottomSheet.value) {
        ModalBottomSheetForm(
            isLoading = isLoading,
            functionText = "Editar",
            initialValue = editedTitle.value,
            onDismissRequest = { showBottomSheet.value = false },
            onValueChange = { newValue -> editedTitle.value = newValue },
            onConfirm = {
                showBottomSheet.value = false
                onEdit(editedTitle.value)
            },
        )
    }
}

@Composable
fun DeleteDialog(onDismissRequest: () -> Unit, onConfirm: () -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            colors = CardDefaults.cardColors(Color.White),
            shape = CardDefaults.outlinedShape,
            border = BorderStroke(1.dp, Color.Gray)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentWidth()
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Tem certeza que deseja excluir esta tarefa",
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row {
                    Button(onClick = { onConfirm() }) {
                        Text("Sim")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(onClick = { onDismissRequest() }) {
                        Text("Não")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetForm(
    isLoading: Boolean = false,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    initialValue: String,
    onValueChange: (String) -> Unit,
    functionText: String
) {
    ModalBottomSheet(onDismissRequest = {
        if (!isLoading) {
            onDismissRequest()
        }
    }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(text = "$functionText Tarefa", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                value = initialValue,
                onValueChange = { onValueChange(it) },
            )

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                enabled = !isLoading,
                onClick = { onConfirm() },
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(functionText)
                }
            }
        }
    }
}