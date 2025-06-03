package com.example.todoapp.domain.repository

import com.example.todoapp.data.model.task.CreateTaskDto
import com.example.todoapp.data.model.task.Task

interface  TaskRepository {
    suspend fun getTasks(): List<Task>
    suspend fun addTask(task: CreateTaskDto): Task
    suspend fun deleteTask(taskId: String): Boolean
    suspend fun updateTask(task: CreateTaskDto, taskId: String): Task
}
