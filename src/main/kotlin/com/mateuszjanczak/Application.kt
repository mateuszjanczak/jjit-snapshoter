package com.mateuszjanczak

import com.mateuszjanczak.scheduling.configureTaskScheduling
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureRouting()
    configureTaskScheduling()
}
