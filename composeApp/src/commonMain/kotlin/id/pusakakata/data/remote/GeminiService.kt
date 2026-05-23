package id.pusakakata.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig = GeminiGenerationConfig()
)

@Serializable
data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String? = "user"
)

@Serializable
data class GeminiPart(
    val text: String? = null
)

@Serializable
data class GeminiGenerationConfig(
    val temperature: Double = 0.7,
    val maxOutputTokens: Int = 1000,
    val topP: Double = 0.95
)

@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate> = emptyList(),
    val promptFeedback: GeminiPromptFeedback? = null
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContent? = null,
    val finishReason: String? = null
)

@Serializable
data class GeminiPromptFeedback(
    val blockReason: String? = null
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
                                    GeminiPart(text = "Berikan definisi singkat dan menarik untuk kosa kata: $word. Gunakan bahasa Indonesia. Jangan gunakan markdown.")
                                )
                            )
                        )
                    )
                )
            }.body()
            
            val textResult = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
            
            if (!textResult.isNullOrBlank()) {
                textResult.trim()
            } else {
                "AI tidak bisa merumuskan makna untuk '$word'. Mungkin kata ini melanggar kebijakan keamanan."
            }
        } catch (e: Exception) {
            "Kesalahan teknis: ${e.message}"
        }
    }
}
