package id.pusakakata.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val generationConfig: GeminiGenerationConfig = GeminiGenerationConfig(),
    val safetySettings: List<SafetySetting> = listOf(
        SafetySetting("HARM_CATEGORY_HARASSMENT", "BLOCK_NONE"),
        SafetySetting("HARM_CATEGORY_HATE_SPEECH", "BLOCK_NONE"),
        SafetySetting("HARM_CATEGORY_SEXUALLY_EXPLICIT", "BLOCK_NONE"),
        SafetySetting("HARM_CATEGORY_DANGEROUS_CONTENT", "BLOCK_NONE")
    )
)

@Serializable
data class SafetySetting(val category: String, val threshold: String)

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
        
        // Menggunakan endpoint v1 stable
        val url = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=$apiKey"
        
        return try {
            val response: GeminiResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(
                    GeminiRequest(
                        contents = listOf(
                            GeminiContent(
                                parts = listOf(
                                    GeminiPart(text = "Berikan definisi singkat, padat, dan menarik untuk kosa kata: $word. Gunakan bahasa Indonesia yang baik. Jangan gunakan markdown, berikan teks polos saja.")
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
                "AI Pusaka sedang merenungkan makna '$word'. Silakan coba kata lain atau ulangi sesaat lagi."
            }
        } catch (e: Exception) {
            "Gagal memanggil AI: ${e.message}"
        }
    }
}
