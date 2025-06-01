package com.example

import com.codahale.metrics.*
import io.ktor.server.application.*
import io.ktor.server.metrics.dropwizard.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.concurrent.TimeUnit

fun Application.configureSerialization() {
}
