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
    suspend fun generateDefinition(word: String): String {
        if (apiKey.isEmpty() || apiKey == "dummy_key") return "AI tidak terkonfigurasi. Silakan tambahkan API Key."
        
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"
        
        return try {
            val response: GeminiResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(
                    GeminiRequest(
                        contents = listOf(
                            GeminiContent(
                                parts = listOf(
                                    GeminiPart(text = "Berikan definisi singkat, padat, dan puitis untuk kosa kata: $word. Gunakan bahasa Indonesia yang indah. Jangan gunakan markdown, berikan teks polos saja.")
                                )
                            )
                        ),
                        generationConfig = GeminiGenerationConfig(
                            temperature = 0.7,
                            maxOutputTokens = 500,
                            topP = 0.95
                        )
                    )
                )
            }.body()

            response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "AI gagal merumuskan makna."
        } catch (e: Exception) {
            "Gagal memanggil AI: ${e.message}"
        }
    }
}
