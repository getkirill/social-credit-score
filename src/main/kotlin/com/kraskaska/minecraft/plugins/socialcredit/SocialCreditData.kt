package com.kraskaska.minecraft.plugins.socialcredit

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.SingletonSupport
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.File
import java.util.*

object SocialCreditData {
    var localData = mutableMapOf<UUID, SocialCreditScore>()
    fun get(uuid: UUID): SocialCreditScore? {
        if(!localData.containsKey(uuid)) localData[uuid] = SocialCreditScore(uuid)
        return localData[uuid]
    }

    fun set(uuid: UUID, value: Int, reason: String?) = add(uuid, value - (get(uuid)?.score() ?: 0), reason)

    fun add(uuid: UUID, value: Int, reason: String?) {
        get(uuid)?.scores?.plusAssign(value to reason)
    }

    fun remove(uuid: UUID, value: Int, reason: String?) = add(uuid, -value, reason)

    fun reset(uuid: UUID, reason: String?) = remove(uuid, (get(uuid)?.score() ?: 0), reason)

    fun save(file: File) {
        val mapper = ObjectMapper(YAMLFactory()).registerModule(
            KotlinModule.Builder()
                .withReflectionCacheSize(512)
                .configure(KotlinFeature.NullToEmptyCollection, true)
                .configure(KotlinFeature.NullToEmptyMap, true)
                .configure(KotlinFeature.NullIsSameAsDefault, true)
                .configure(KotlinFeature.SingletonSupport, false)
                .configure(KotlinFeature.StrictNullChecks, false)
                .build()
        )
        mapper.findAndRegisterModules()
        mapper.writeValue(file, SocialCreditScoreYAML(localData))
    }
    fun load(file: File) {
        if(!file.exists()) {
            file.writer(charset("utf-8")).apply {
                this.write("user-data:")
                this.flush()
                this.close()
            }
        }
        val mapper = ObjectMapper(YAMLFactory()).registerModule(
            KotlinModule.Builder()
                .withReflectionCacheSize(512)
                .configure(KotlinFeature.NullToEmptyCollection, true)
                .configure(KotlinFeature.NullToEmptyMap, true)
                .configure(KotlinFeature.NullIsSameAsDefault, true)
                .configure(KotlinFeature.SingletonSupport, false)
                .configure(KotlinFeature.StrictNullChecks, false)
                .build()
        )
        mapper.findAndRegisterModules()
        val typeReference = object : TypeReference<SocialCreditScoreYAML>() {}
        val map = safe { mapper.readValue(file, typeReference) } ?: SocialCreditScoreYAML(mutableMapOf())
        localData = map.userData.toMutableMap()
    }
}

data class SocialCreditScore(
    val uuid: UUID,
    val scores: MutableList<Pair<Int, String?>> = mutableListOf()
) {
    fun score(): Int {
        var result = 0
        scores.forEach { score -> result += score.first }
        return result
    }
}

data class SocialCreditScoreYAML(
    @JsonProperty("user-data")
    val userData: MutableMap<UUID, SocialCreditScore>
)

fun <T> safe(code: () -> T?): T? {
    return try {
        code()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}