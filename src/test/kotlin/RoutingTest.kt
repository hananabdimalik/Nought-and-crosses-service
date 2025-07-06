import com.example.model.*
import com.example.model.GamePieces.Nought
import com.example.model.GamePieces.Unplayed
import com.example.module
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RoutingTest {

    val repo: NoughtAndCrossesRepository = mock()

    @BeforeTest
    fun setup() {
        repo.session = GameSession()
    }

    @Test
    fun `Post join returns 200 and adds a player`() = testApplication {
        val client = configureServerAndGetClient()
        val response = client.post("/join") {
            contentType(ContentType.Application.Json)
            setBody(Player(name = "Bob", id = "id"))
        }

        val responseBody: GameSessionState = response.body()
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(GameSessionState.Started, responseBody)
    }

    @Test
    fun `Get gameBoard, returns 200 and gameBoard`() = testApplication {
        val client = configureServerAndGetClient()
        whenever(repo.gameBoard).thenReturn(MutableList(9) { GameCell(GamePieces.Unplayed, it) })

        val response = client.get("/gameBoard") {
            contentType(ContentType.Application.Json)
        }

        val responseBody: MutableList<GameCell> = response.body()

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(MutableList(9) { GameCell(piece = Unplayed, it) }, responseBody)
    }

    @Test
    fun `updateBoard when player makes a move, returns a 200 with updated board`() = testApplication {
        val expected = MutableList(9) { if (it == 2) GameCell(Nought, 2) else GameCell(piece = Unplayed, it) }
        whenever(repo.session).thenReturn(GameSession(gameSessionState = GameSessionState.Started))
//        whenever(repo.updateGameBoard(any(), any())).thenReturn(expected)

        val client = configureServerAndGetClient()
        val response = client.post("/updateBoard/2") {
            contentType(ContentType.Application.Json)
            setBody(Player(name = "Bob", id = "newId", gamePiece = Nought))
        }

        val actual: MutableList<GameCell> = response.body()
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expected, actual)
    }

    @Test
    fun `updatedBoard, when player makes invalid move, returns a 400 error`() = testApplication {
        val client = configureServerAndGetClient()
        val response = client.post("/updateBoard/abc") {
            contentType(ContentType.Application.Json)
            setBody(Player(name = "Bob", id = "newId", gamePiece = Nought))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Invalid position", response.bodyAsText())
    }

    @Test
    fun `resetGame, returns 200 with new gameBoard`() = testApplication {
        whenever(repo.resetGame()).thenReturn(listOf())

        val client = configureServerAndGetClient()
        val response = client.get("/resetGame")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(List(9) { GameCell(Unplayed, it) }, response.body<List<GameCell>>())
    }

    @Test
    fun `restartGameSession, returns 200 with new gameSession`() = testApplication {
        whenever(repo.restartSession()).thenReturn(GameSession())

        val client = configureServerAndGetClient()
        val response = client.get("/restartGameSession")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(GameSession(), response.body<GameSession>())
    }

    private fun ApplicationTestBuilder.configureServerAndGetClient(): HttpClient {
        application { module() }
        val client = createClient {
            // use client side content negotiation
            install(ContentNegotiation) {
                json()
            }
        }
        return client
    }
}
