package com.example

import com.example.model.GameSessionManager
import com.example.model.IdGeneratorImpl
import com.example.model.NoughtAndCrossesRepository
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val sessionManager = GameSessionManager(idGenerator = IdGeneratorImpl())
    configureSerialization()
    configureMonitoring()
    configureRouting(NoughtAndCrossesRepository(sessionManager))
}
