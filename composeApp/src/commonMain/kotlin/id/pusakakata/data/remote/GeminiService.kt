package id.pusakakata.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>
)

@Serializable
data class GeminiContent(
    val parts: List<GeminiPart>
)

@Serializable
data class GeminiPart(
    val text: String
)

@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate>? = null
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContent? = null
)

class GeminiService(
    private val client: HttpClient,
    private val apiKey: String
) {
    suspend fun generateDefinition(word: String): String {
        if (apiKey.isBlank()) return "API Key belum terisi."
        
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"
        
        return try {
            val response: GeminiResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(
                    GeminiRequest(
                        contents = listOf(
                            GeminiContent(
                                parts = listOf(
                                    GeminiPart(text = "Berikan definisi singkat satu paragraf dalam bahasa Indonesia untuk kosa kata: $word. Jangan gunakan markdown.")
                                )
                            )
                        )
                    )
                )
            }.body()
            
            val result = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            
            if (!result.isNullOrBlank()) {
                result.trim()
            } else {
                "AI tidak memberikan respon untuk '$word'."
            }
        } catch (e: Exception) {
            "Gagal memanggil AI: ${e.message}"
        }
    }
}
