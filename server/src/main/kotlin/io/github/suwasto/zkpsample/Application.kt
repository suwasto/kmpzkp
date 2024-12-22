package io.github.suwasto.zkpsample

import io.github.suwasto.zkpschnorrproofs.SchnorrMobileClient
import io.github.suwasto.zkpschnorrproofs.SchnorrServer
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * NOTE : THIS IS SAMPLE TO TEST THE ALGORITM NOT USED IN REAL SCENARIO
 */
fun main() {
    embeddedServer(Netty, port = SERVER_PORT, module = Application::module).start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }

    // this should happen in client
    val mockUser = listOf(
        User(
            username = "paw",
            password = "pawpaw"
        )
    )
    // on user registration client send verifier/public key to server
    val mockUserKeys = SchnorrMobileClient.deriveKeys(
        mockUser[0].username, mockUser[0].password
    )

    routing {
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }
        post("/login") {
            val loginRequest = call.receive<LoginRequest>()

            val user = mockUser.find { user: User -> user.username == loginRequest.username }

            if (user == null) {
                call.respond(HttpStatusCode.NotFound, "User not found")
                return@post
            }

            // this should be separate api
            // first user get challenge from server
            val challenge = SchnorrServer.generateChallenge(
                mockUserKeys.publicKey
            )

            // after get challenge client generate proof
            // this happen in client after getting challenge from server
            val generateKey = SchnorrMobileClient.deriveKeys(
                loginRequest.username, loginRequest.password
            )
            val proof = SchnorrMobileClient.generateProof(
                generateKey.privateKey, challenge)

            // after getting proof from client
            // client send proof to server (proof.first as commitment, proof.second as response)
            // server validate the proof
            val verified = SchnorrServer.verifyProof(
                commitment = proof.first,
                publicKey = mockUserKeys.publicKey,
                challenge = challenge,
                response = proof.second
            )

            call.respond(LoginResponse(
                challenge = challenge,
                commitment = proof.first,
                response = proof.second,
                verified = verified
            ))
        }
    }
}

data class User(
    val username: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val challenge: String,
    val commitment: String,
    val response: String,
    val verified: Boolean
)

// in real scenario user send username, commitment, response to server
// not send password
@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)