package com.mateuszjanczak.scheduler

import io.ktor.server.application.*
import kotlinx.coroutines.*
import java.time.Duration


class Scheduler(private val scope: CoroutineScope) {
    fun task(configuration: TaskBuilder.() -> Unit) {
        val builder = TaskBuilder(scope)
        builder.configuration()
    }

    class TaskBuilder(private val scope: CoroutineScope) {
        fun repeatEvery(interval: Duration, action: suspend () -> Unit) {
            scope.launch {
                while (isActive) {
                    action()
                    delay(interval.toMillis())
                }
            }
        }
    }
}

fun Application.scheduler(execute: Scheduler.() -> Unit) {
    val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    val scheduler = Scheduler(scope)
    scheduler.execute()
}
