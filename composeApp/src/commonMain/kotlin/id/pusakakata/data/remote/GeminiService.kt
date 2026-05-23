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
    private val jsonParser = Json { ignoreUnknownKeys = true }

    suspend fun generateDefinition(word: String): String {
        if (apiKey.isBlank()) return "API Key belum terisi."
        
        // Alamat v1beta seringkali lebih kompatibel untuk API Key gratisan
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
            
            if (response.status.value != 200) {
                return "AI gagal merespon (Status: ${response.status.value}). Coba lagi nanti."
            }

            val responseBody = response.bodyAsText()
            val geminiResponse = jsonParser.decodeFromString<GeminiResponse>(responseBody)
            val resultText = geminiResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            
            if (!resultText.isNullOrBlank()) {
                resultText.trim()
            } else {
                "AI Pusaka belum mengenal kata '$word'. Coba kosa kata lain."
            }
        } catch (e: Exception) {
            "Gagal memanggil AI: ${e.message}"
        }
    }
}
