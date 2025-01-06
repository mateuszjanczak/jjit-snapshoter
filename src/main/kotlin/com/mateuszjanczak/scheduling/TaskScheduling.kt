package com.mateuszjanczak.scheduling

import com.mateuszjanczak.scheduling.common.defaultTaskManagerConfig
import io.github.flaxoos.ktor.server.plugins.taskscheduling.TaskScheduling
import io.ktor.server.application.*

fun Application.configureTaskScheduling() {
    install(TaskScheduling){

        addTaskManager(
            taskManagerConfiguration = defaultTaskManagerConfig
        )

        task(taskManagerName = defaultTaskManagerConfig.taskManagerName) {
            name = "scraper"
            task = { taskExecutionTime ->
                log.info("My task is running: $taskExecutionTime")
            }
            kronSchedule = {
                seconds {
                    every(1)
                }
            }
            concurrency = 1
        }
    }
}



