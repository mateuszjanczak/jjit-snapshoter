package com.mateuszjanczak.scheduling.common

import io.github.flaxoos.ktor.server.plugins.taskscheduling.managers.TaskExecutionToken
import io.github.flaxoos.ktor.server.plugins.taskscheduling.managers.TaskManager
import io.github.flaxoos.ktor.server.plugins.taskscheduling.managers.TaskManagerConfiguration
import io.github.flaxoos.ktor.server.plugins.taskscheduling.managers.TaskManagerConfiguration.TaskManagerName.Companion.toTaskManagerName
import io.github.flaxoos.ktor.server.plugins.taskscheduling.tasks.Task
import io.ktor.server.application.*
import korlibs.time.DateTime
import java.util.UUID

class BasicTaskManager(
    override val name: TaskManagerConfiguration.TaskManagerName,
    override val application: Application
) : TaskManager<TaskExecutionToken>() {
    override suspend fun attemptExecute(
        task: Task,
        executionTime: DateTime,
        concurrencyIndex: Int
    ): TaskExecutionToken {
        return BasicTaskExecutionToken()
    }

    override fun close() {
        // do nothing
    }

    override suspend fun init(tasks: List<Task>) {
        // do nothing
    }

    override suspend fun markExecuted(key: TaskExecutionToken) {
        // do nothing
    }
}

data class BasicTaskExecutionToken(override val name: String = UUID.randomUUID().toString(), override val concurrencyIndex: Int = 0) : TaskExecutionToken

class BasicTaskManagerConfig(val taskManagerName: String) : TaskManagerConfiguration<TaskExecutionToken>() {
    override fun createTaskManager(application: Application): TaskManager<out TaskExecutionToken> {
        return BasicTaskManager(taskManagerName.toTaskManagerName(), application)
    }
}

const val TASK_MANAGER_NAME = "default-task-manager"

val defaultTaskManagerConfig = BasicTaskManagerConfig(TASK_MANAGER_NAME)