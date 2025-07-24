import com.example.configureRouting
import com.example.configureSerialization
import com.example.model.*
import com.example.model.GamePieces.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals

class RoutingTest {
    val sessionManage = GameSessionManager(idGenerator = TestIdGenerator())
    val repo = NoughtAndCrossesRepository(sessionManage)

    @Test
    fun `Post host returns 200 and gameSession`() = testApplication {
        val client = configureServerAndGetClient(repo)
        val response = client.post("/hostSession") {
            contentType(ContentType.Application.Json)
            setBody(Player(name = "player1", id = "player-id"))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            GameSession(
                players = listOf(Player(name = "player1", id = "player-id", gamePiece = Nought)),
                gameSessionState = GameSessionState.Waiting
            ),
            response.body<GameSession>()
        )
    }

    @Test
    fun `Post Join returns 200 and gameSessionState Started`() = testApplication {
        val gameSession = GameSession(
            players = listOf(Player(name = "player1", id = "player1-id", gamePiece = Cross)),
            gameSessionState = GameSessionState.Waiting
        )
        repo.sessionManager.gameSession = gameSession
        val client = configureServerAndGetClient(repo)

        val response = client.post("/joinSession") {
            contentType(ContentType.Application.Json)
            setBody(Player(name = "player2", id = "player2-id", gamePiece = Cross))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(GameSessionState.Started, response.body<GameSessionState>())
    }

    @Test
    fun `Post join returns 200 and adds a player`() = testApplication {
        val session = GameSession(
            players = listOf(Player(name = "player1", id = "player1-id", gamePiece = Nought)),
            gameSessionState = GameSessionState.Waiting
        )
        whenever(sessionManage.hostSession(any())).then {
            sessionManage.gameSession = session
        }
        val client = configureServerAndGetClient(repo)
        val response = client.post("/joinSession") {
            contentType(ContentType.Application.Json)
            setBody(Player(name = "Bob", id = "id", gamePiece = Cross))
        }

        val responseBody: GameSessionState = response.body()
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(GameSessionState.Started, responseBody)
    }

    @Test
    fun `Get gameBoard, returns 200 and gameBoard`() = testApplication {
        val client = configureServerAndGetClient(repo)
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
//        whenever(repo.session).thenReturn(GameSession(gameSessionState = GameSessionState.Started))
//        whenever(repo.updateGameBoard(any(), any())).thenReturn(expected)

        val client = configureServerAndGetClient(repo)
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
        val client = configureServerAndGetClient(repo)
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

        val client = configureServerAndGetClient(repo)
        val response = client.get("/resetGame")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(List(9) { GameCell(Unplayed, it) }, response.body<List<GameCell>>())
    }

    @Test
    fun `restartGameSession, returns 200 with new gameSession`() = testApplication {
        val client = configureServerAndGetClient(repo)
        val response = client.get("/restartGameSession")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(GameSession(), response.body<GameSession>())
    }

    private fun ApplicationTestBuilder.configureServerAndGetClient(repository: NoughtAndCrossesRepository): HttpClient {
        application {
            configureRouting(repository)
            configureSerialization()
        }
        val client = createClient {
            // use client side content negotiation
            install(ContentNegotiation) {
                json()
            }
        }
        return client
    }

    class TestIdGenerator : IdGenerator {
        override fun gameSessionId() = "testId"
    }
}
