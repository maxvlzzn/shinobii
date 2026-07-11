package com.shinobisim.network

import com.shinobisim.model.Clan
import com.shinobisim.model.SaveSummary
import com.shinobisim.model.Shinobi
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.intOrNull

class SupabaseRepository(private val client: HttpClient = createHttpClient()) {

    private val supabaseUrl = "https://mxotqciydawevrdeqwno.supabase.co"
    private val apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im14b3RxY2l5ZGF3ZXZyZGVxd25vIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODMzNjkwMzEsImV4cCI6MjA5ODk0NTAzMX0.lMkh7vsTDEHdzS-Pq20CsdOh-RR27Yp3-WGwUPdasjo"

    private val json = Json { ignoreUnknownKeys = true }

    @Serializable
    private data class SaveRow(
        val id: String,
        val shinobi_name: String,
        val clan: String,
        val age: Int,
        val rating: Int,
        val skill_points: Int,
        val clan_skills: JsonElement,
        val elemental_skills: JsonElement
    )

    @Serializable
    private data class SaveRequest(
        val shinobi_name: String,
        val clan: String,
        val age: Int,
        val rating: Int,
        val skill_points: Int,
        val clan_skills: Map<String, Int>,
        val elemental_skills: Map<String, Int>
    )

    private fun authHeader(): Map<String, String> = mapOf(
        "apikey" to apiKey,
        "Authorization" to "Bearer $apiKey"
    )

    suspend fun listSaves(): List<SaveSummary> {
        val response: HttpResponse = client.get("$supabaseUrl/rest/v1/game_saves?select=id,shinobi_name,clan,age,rating&order=updated_at.desc") {
            authHeader().forEach { (k, v) -> header(k, v) }
        }
        if (!response.status.isSuccess()) return emptyList()
        @Serializable
        data class SummaryRow(val id: String, val shinobi_name: String, val clan: String, val age: Int, val rating: Int)
        val rows: List<SummaryRow> = response.body()
        return rows.map {
            SaveSummary(
                id = it.id,
                name = it.shinobi_name,
                clan = Clan.valueOf(it.clan),
                age = it.age,
                rating = it.rating
            )
        }
    }

    suspend fun loadSave(id: String): Shinobi? {
        val response: HttpResponse = client.get("$supabaseUrl/rest/v1/game_saves?id=eq.$id&select=*") {
            authHeader().forEach { (k, v) -> header(k, v) }
        }
        if (!response.status.isSuccess()) return null
        val rows: List<SaveRow> = response.body()
        val row = rows.firstOrNull() ?: return null
        return rowToShinobi(row)
    }

    suspend fun createSave(shinobi: Shinobi): Shinobi {
        val request = shinobiToRequest(shinobi)
        val response: HttpResponse = client.post("$supabaseUrl/rest/v1/game_saves?select=*") {
            authHeader().forEach { (k, v) -> header(k, v) }
            header("Prefer", "return=representation")
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (!response.status.isSuccess()) {
            throw RuntimeException("createSave failed: ${response.status}")
        }
        val rows: List<SaveRow> = response.body()
        return rowToShinobi(rows.first())
    }

    suspend fun updateSave(shinobi: Shinobi): Boolean {
        if (shinobi.id.isEmpty()) return false
        val request = shinobiToRequest(shinobi)
        val response: HttpResponse = client.patch("$supabaseUrl/rest/v1/game_saves?id=eq.${shinobi.id}") {
            authHeader().forEach { (k, v) -> header(k, v) }
            header("Prefer", "return=representation")
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return response.status.isSuccess()
    }

    suspend fun deleteSave(id: String): Boolean {
        val response: HttpResponse = client.delete("$supabaseUrl/rest/v1/game_saves?id=eq.$id") {
            authHeader().forEach { (k, v) -> header(k, v) }
        }
        return response.status.isSuccess()
    }

    private fun shinobiToRequest(shinobi: Shinobi): SaveRequest = SaveRequest(
        shinobi_name = shinobi.name,
        clan = shinobi.clan.name,
        age = shinobi.age,
        rating = shinobi.rating,
        skill_points = shinobi.skillPoints,
        clan_skills = shinobi.clanSkills,
        elemental_skills = shinobi.elementalSkills
    )

    private fun jsonElementToMap(element: JsonElement): Map<String, Int> {
        val obj = element as? JsonObject ?: return emptyMap()
        return obj.entries.associate { (k, v) ->
            k to ((v as? JsonPrimitive)?.intOrNull ?: 0)
        }
    }

    private fun rowToShinobi(row: SaveRow): Shinobi = Shinobi(
        id = row.id,
        name = row.shinobi_name,
        clan = Clan.valueOf(row.clan),
        age = row.age,
        rating = row.rating,
        skillPoints = row.skill_points,
        clanSkills = jsonElementToMap(row.clan_skills),
        elementalSkills = jsonElementToMap(row.elemental_skills)
    )
}
