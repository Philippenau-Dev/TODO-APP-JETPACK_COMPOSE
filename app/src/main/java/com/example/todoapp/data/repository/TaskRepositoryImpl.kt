package com.example.todoapp.data.repository

import com.example.todoapp.data.db.TaskService
import com.example.todoapp.data.model.task.CreateTaskDto
import com.example.todoapp.data.model.task.Task
import com.example.todoapp.domain.repository.TaskRepository

class TaskRepositoryImpl(private val service: TaskService) : TaskRepository {

    override suspend fun getTasks(): List<Task> {
        return service.getTasks()
    }

    override suspend fun addTask(task: CreateTaskDto): Task {
        return service.addTask(task)
    }

    override suspend fun deleteTask(taskId: String): Boolean {
        return service.deleteTask(taskId)
    }

    override suspend fun updateTask(task: CreateTaskDto, taskId: String): Task {
        return service.updateTask(task, taskId)
    }
}