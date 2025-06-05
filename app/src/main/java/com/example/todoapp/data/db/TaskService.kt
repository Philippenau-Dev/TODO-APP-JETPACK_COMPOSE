package com.example.todoapp.data.db

import com.example.todoapp.data.model.task.CreateTaskDto
import com.example.todoapp.data.model.task.Task
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class TaskService {
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun getTasks(): List<Task> {
        val response: HttpResponse = client.get("http://localhost:8000/tasks")
       return Json.decodeFromString(ListSerializer(Task.serializer()), response.body())
    }

    suspend fun addTask(task: CreateTaskDto): Task {
        val response: HttpResponse = client.post("http://localhost:8000/tasks") {
            contentType(io.ktor.http.ContentType.Application.Json)
            setBody(task)
        }
        return Json.decodeFromString(Task.serializer(), response.body())
    }

    suspend fun updateTask(task: CreateTaskDto, taskId: String): Task {
        val response: HttpResponse = client.put("http://localhost:8000/tasks/$taskId") {
            contentType(io.ktor.http.ContentType.Application.Json)
            setBody(task)
        }
        return Json.decodeFromString(Task.serializer(), response.body())
    }

    suspend fun deleteTask(taskId: String): Boolean {
        val response: HttpResponse = client.delete("http://localhost:8000/tasks/$taskId")
        return response.status == io.ktor.http.HttpStatusCode.NoContent
    }
}
