package id.pusakakata.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig = GeminiGenerationConfig()
)

@Serializable
data class GeminiContent(
    val parts: List<GeminiPart>,
    val role: String? = null
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
    private val jsonParser = Json { 
        ignoreUnknownKeys = true 
        coerceInputValues = true
    }

    suspend fun generateDefinition(word: String): String {
        if (apiKey.isBlank()) return "API Key belum terisi."
        
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"
        
        return try {
            val response: HttpResponse = client.post(url) {
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
            }
            
            val responseBody = response.bodyAsText()
            
            if (response.status.value != 200) {
                return "Error ${response.status.value}: $responseBody"
            }

            val geminiResponse = jsonParser.decodeFromString<GeminiResponse>(responseBody)
            val resultText = geminiResponse.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
            
            if (!resultText.isNullOrBlank()) {
                resultText.trim()
            } else {
                "AI tidak memberikan respon (Empty Result)."
            }
        } catch (e: Exception) {
            "Kesalahan: ${e.message}"
        }
    }
}
