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
    val candidates: List<GeminiCandidate>? = null,
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
        
        // Menggunakan v1 stable daripada v1beta agar lebih konsisten
        val url = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=$apiKey"
        
        return try {
            val response: GeminiResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(
                    GeminiRequest(
                        contents = listOf(
                            GeminiContent(
                                parts = listOf(
                                    GeminiPart(text = "Berikan definisi singkat untuk kosa kata: $word. Gunakan bahasa Indonesia. Teks polos saja tanpa markdown.")
                                )
                            )
                        )
                    )
                )
            }.body()
            
            val result = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            
            if (result != null) {
                result
            } else if (response.promptFeedback?.blockReason != null) {
                "AI tidak bisa menjawab karena kebijakan keamanan: ${response.promptFeedback.blockReason}"
            } else {
                "AI tidak menemukan jawaban untuk '$word'."
            }
        } catch (e: Exception) {
            "Terjadi gangguan koneksi ke AI: ${e.message}"
        }
    }
}
