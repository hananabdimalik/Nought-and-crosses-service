package model

import com.example.model.IdGenerator
import kotlin.test.Test
import kotlin.test.assertEquals

class IdGeneratorImplTest {

    @Test
    fun `test idGenerator`() {
        val actual = IdGeneratorTest()
        assertEquals("1122", actual.gameSessionId())
    }
}

class IdGeneratorTest : IdGenerator {
    override fun gameSessionId() = "1122"
}
