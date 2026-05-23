package id.pusakakata.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class GeminiRequest(val contents: List<Content>) {
    @Serializable data class Content(val parts: List<Part>)
    @Serializable data class Part(val text: String)
}

@Serializable
data class GeminiResponse(val candidates: List<Candidate>) {
    @Serializable data class Candidate(val content: Content)
    @Serializable data class Content(val parts: List<Part>)
    @Serializable data class Part(val text: String)
}

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
                setBody(GeminiRequest(listOf(GeminiRequest.Content(listOf(GeminiRequest.Part(
                    "Berikan definisi singkat, padat, dan puitis untuk kosa kata: $word. " +
                    "Gunakan bahasa Indonesia yang indah. Jangan gunakan markdown, berikan teks polos saja."
                ))))))
            }.body()
            response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "AI gagal merumuskan makna."
        } catch (e: Exception) {
            "Gagal memanggil AI: ${e.message}"
        }
    }
}
