package com.mateuszjanczak

import com.mateuszjanczak.Bootstrap.synchronizer
import com.mateuszjanczak.application.scheduler.scheduler
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.module() {
    bootstrap()
    configureRouting()
    configureScheduler()
}

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("OK")
        }
    }
}

fun Application.configureScheduler() {
    scheduler {
        task {
            cron("0 0 * * *") { // At 00:00.
                synchronizer.execute()
            }
        }
    }
}