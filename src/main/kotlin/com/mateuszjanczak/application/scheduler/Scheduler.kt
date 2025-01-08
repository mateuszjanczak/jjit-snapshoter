package com.mateuszjanczak.application.scheduler

import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import io.ktor.server.application.*
import kotlinx.coroutines.*
import java.time.Duration
import java.time.ZonedDateTime

fun Application.scheduler(execute: Scheduler.() -> Unit) {
    val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    val scheduler = Scheduler(scope)
    scheduler.execute()
}

class Scheduler(private val scope: CoroutineScope) {
    fun task(configuration: TaskBuilder.() -> Unit) {
        val builder = TaskBuilder(scope)
        builder.configuration()
    }

    class TaskBuilder(private val scope: CoroutineScope) {
        fun cron(cronExpression: String, action: suspend () -> Unit) {
            val cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX)
            val cronParser = CronParser(cronDefinition)
            val cron = cronParser.parse(cronExpression)
            val executionTime = ExecutionTime.forCron(cron)

            scope.launch {
                while (isActive) {
                    val now = ZonedDateTime.now()
                    val nextExecution = executionTime.nextExecution(now).get()
                    val delayTime = Duration.between(now, nextExecution).toMillis()

                    if (delayTime > 0) {
                        delay(delayTime)
                        action()
                    }
                }
            }
        }
    }
}

