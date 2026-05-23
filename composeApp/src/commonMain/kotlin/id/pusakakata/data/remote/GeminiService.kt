package id.pusakakata.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
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
    val temperature: Double = 0.9, // Lebih kreatif agar mau menjawab kata pendek
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
        if (apiKey.isBlank()) return "API Key belum terisi di ApiConfig.kt."
        
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"
        
        return try {
            val httpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(
                    GeminiRequest(
                        contents = listOf(
                            GeminiContent(
                                parts = listOf(
                                    GeminiPart(text = "Berikan makna atau sapaan puitis singkat untuk kata: $word. Jika ini sapaan, balas dengan sapaan yang hangat. Gunakan bahasa Indonesia. Teks polos saja.")
                                )
                            )
                        )
                    )
                )
            }
            
            if (httpResponse.status.value != 200) {
                return "AI sibuk (Error ${httpResponse.status.value}). Silakan coba kata lain."
            }

            val response: GeminiResponse = httpResponse.body()
            val result = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            
            if (!result.isNullOrBlank()) {
                result.trim()
            } else if (response.promptFeedback?.blockReason != null) {
                "Kata ini disensor oleh AI: ${response.promptFeedback?.blockReason}"
            } else {
                "AI tidak bisa merumuskan makna untuk '$word'. Coba kata yang lebih spesifik."
            }
        } catch (e: Exception) {
            "Gagal terhubung ke pusat AI: ${e.message}"
        }
    }
}
