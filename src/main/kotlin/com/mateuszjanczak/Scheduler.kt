package com.mateuszjanczak

import com.mateuszjanczak.scheduler.scheduler
import io.ktor.server.application.*
import java.time.Duration

fun Application.configureScheduler() {
    scheduler {
        task {
            repeatEvery(Duration.ofSeconds(5)) {
                println("Executing repeating task")
            }
        }
    }
}

