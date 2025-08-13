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
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertEquals

class RoutingTest {
    private val idGenerator = mock<IdGenerator>()
    private val sessionManage = GameSessionManager(idGenerator = idGenerator)
    private val repo = NoughtAndCrossesRepository(sessionManage)

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
        repo.sessionManager.sessions.put("session-id", gameSession)
        val client = configureServerAndGetClient(repo)

        val response = client.post("/joinSession") {
            contentType(ContentType.Application.Json)
            setBody(Player(name = "player2", id = "player2-id", gamePiece = Cross))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(
            GameSession(
                gameSessionState = GameSessionState.Started,
                players = listOf(
                    Player("player1", id = "player1-id", gamePiece = Cross),
                    Player("player2", id = "player2-id", gamePiece = Cross)
                )
            ), response.body<GameSession>()
        )
    }

    @Test
    fun `loadGameState, return 200 with gameSession`() = testApplication {
        val client = configureServerAndGetClient(repo)

        val response = client.get("/loadGameSession")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(GameSession(), response.body<GameSession>())

    }

    @Test
    fun `Get gameBoard, returns 200 and gameBoard`() = testApplication {
        val client = configureServerAndGetClient(repo)

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
        repo.sessionManager.gameSession = repo.sessionManager.gameSession.copy(
            sessionId = "some-sessionId",
            gameSessionState = GameSessionState.Started
        )

        val client = configureServerAndGetClient(repo)
        val response = client.post("/updateBoard/2/some-sessionId") {
            contentType(ContentType.Application.Json)
            setBody(Player(name = "Bob", id = "newId", gamePiece = Nought))
        }

        val actual: MutableList<GameCell> = response.body()
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(expected, actual)
    }

    @Test
    fun `updatedBoard, when player makes invalid move, returns a 400 error`() = testApplication {
        repo.sessionManager.gameSession = repo.sessionManager.gameSession.copy(
            sessionId = "some-sessionId",
            gameSessionState = GameSessionState.Started
        )

        val client = configureServerAndGetClient(repo)
        val response = client.post("/updateBoard/abc/some-sessionId") {
            contentType(ContentType.Application.Json)
            setBody(Player(name = "Bob", id = "newId", gamePiece = Nought))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals("Invalid position", response.bodyAsText())
    }

    @Test
    fun `resetGame, returns 200 with new gameBoard`() = testApplication {
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
        assertEquals(RestartGame(gameSession = GameSession(), gameBoard = List(9){ GameCell(Unplayed, it)}), response.body<RestartGame>())
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
}
