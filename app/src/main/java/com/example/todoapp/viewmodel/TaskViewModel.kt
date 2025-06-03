package com.example.todoapp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.model.task.CreateTaskDto
import com.example.todoapp.data.model.task.Task
import com.example.todoapp.domain.repository.TaskRepository
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    private val _tasks = mutableStateListOf<Task>()
    val isLoading = mutableStateOf(false)
    val formIsLoading = mutableStateOf(false)

    val tasks: List<Task> get() = _tasks

    fun getTasks() {
        viewModelScope.launch {
            isLoading.value = true
            val tasks = repository.getTasks()
            _tasks.clear()
            _tasks.addAll(tasks)
            isLoading.value = false
        }
    }

    fun addTask(title: String) {
        viewModelScope.launch {
            formIsLoading.value = true
            val newTask = CreateTaskDto(title = title)
            val task = repository.addTask(newTask)
            _tasks.add(task)
            formIsLoading.value = false
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            isLoading.value = true
            val deleteSuccess = repository.deleteTask(taskId)
            if (deleteSuccess) {
                isLoading.value = false
                _tasks.removeIf { it.id == taskId }
            }
        }
    }

    fun updateTask(
        taskId: String,
        done: Boolean? = null,
        newTitle: String? = null
    ) {
        viewModelScope.launch {
            formIsLoading.value = true

            val currentTask = tasks.find { it.id == taskId }

            if (currentTask != null) {
                val updatedTitle = newTitle ?: currentTask.title
                val updatedDone = done ?: currentTask.done

                val changedTask = CreateTaskDto(
                    title = updatedTitle,
                    done = updatedDone
                )

                val index = tasks.indexOfFirst { it.id == taskId }
                if (index != -1) {
                    _tasks[index] = _tasks[index].copy(
                        title = changedTask.title,
                        done = changedTask.done
                    )
                }

                val task = repository.updateTask(changedTask, taskId)

                _tasks[index] = _tasks[index].copy(
                    title = task.title,
                    done = task.done
                )
            }
        }

        formIsLoading.value = false
    }
}
