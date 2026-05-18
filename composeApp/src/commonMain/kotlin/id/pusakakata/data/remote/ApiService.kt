package id.pusakakata.data.remote

import id.pusakakata.data.remote.dto.KbbiResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ApiService(private val client: HttpClient) {
    suspend fun fetchDefinition(word: String): KbbiResponse {
        return client.get("https://kbbi-api-zhirrr.vercel.app/api/kbbi?text=$word").body()
    }

    companion object {
        fun create(): ApiService {
            return ApiService(
                HttpClient {
                    install(ContentNegotiation) {
                        json(Json {
                            ignoreUnknownKeys = true
                            coerceInputValues = true
                        })
                    }
                }
            )
        }
    }
}
