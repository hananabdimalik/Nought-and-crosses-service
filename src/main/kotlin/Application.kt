package com.example

import com.example.model.NoughtAndCrossesRepository
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val repository = NoughtAndCrossesRepository()
    configureSerialization()
    configureMonitoring()
    configureRouting(repository)
}
