package com.example.model

import java.util.*

class IdGeneratorImpl : IdGenerator {
    override fun gameSessionId() = UUID.randomUUID().toString()
}