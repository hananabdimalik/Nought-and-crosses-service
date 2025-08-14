package com.example

import com.example.GameSessionManager
import com.example.utils.IdGeneratorImpl
import com.example.repository.NoughtAndCrossesRepository
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
