package com.example.todoapp.data.model.task

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: String,
    val title: String,
    val done: Boolean = false
)

@Serializable
data class CreateTaskDto(
    val title: String,
    val done: Boolean = false
)