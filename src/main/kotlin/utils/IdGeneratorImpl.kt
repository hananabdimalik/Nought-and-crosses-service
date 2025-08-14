package com.example.utils

import java.util.UUID

class IdGeneratorImpl : IdGenerator {
    override fun gameSessionId() = UUID.randomUUID().toString()
}