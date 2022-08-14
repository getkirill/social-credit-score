package com.kraskaska.minecraft.plugins.socialcredit

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import java.io.File
import java.util.*

object SocialCreditData {
    var localData = mutableMapOf<UUID, SocialCreditScore>()
    fun get(uuid: UUID): SocialCreditScore? {
        if (!localData.containsKey(uuid)) localData[uuid] = SocialCreditScore(uuid)
        return localData[uuid]
    }

    fun set(uuid: UUID, value: Int, reason: String?) =
        add(uuid, value - (get(uuid)?.score() ?: SocialCreditConfig.config.resetAmount), reason)

    fun add(uuid: UUID, value: Int, reason: String?) {
        get(uuid)?.scores?.plusAssign(value to reason)
        if (((get(uuid)?.score()
                ?: SocialCreditConfig.config.resetAmount) < SocialCreditConfig.config.autoBanFloor) && SocialCreditConfig.config.autoBanEnabled && Bukkit.getServer()
                .getPlayer(uuid) != null && !Bukkit.getServer()
                .getOfflinePlayer(uuid).isOp && SCSPlugin.perms?.playerHas(
                Bukkit.getServer()
                    .getPlayer(uuid), "socialcredit.prevent-ban"
            ) == false
        ) {
            Bukkit.getServer().getPlayer(uuid)!!.spigot().sendMessage(
                ComponentBuilder().text("WARNING: ").color(ChatColor.RED).bold().text(
                    "Your social credit score (${
                        (get(uuid)?.score() ?: SocialCreditConfig.config.resetAmount)
                    }) is less than server's set minimum (${SocialCreditConfig.config.autoBanFloor}). If your score won't be bigger than server's minimum after you leave the server, you will be automatically banned the second you join the server again."
                ).color(ChatColor.RED).build()
            )
        }
    }

    fun remove(uuid: UUID, value: Int, reason: String?) = add(uuid, -value, reason)

    fun reset(uuid: UUID, reason: String?) = set(uuid, SocialCreditConfig.config.resetAmount, reason)

    fun save(file: File) {
        val mapper = ObjectMapper(YAMLFactory()).registerModule(
            KotlinModule.Builder().withReflectionCacheSize(512).configure(KotlinFeature.NullToEmptyCollection, true)
                .configure(KotlinFeature.NullToEmptyMap, true).configure(KotlinFeature.NullIsSameAsDefault, true)
                .configure(KotlinFeature.SingletonSupport, false).configure(KotlinFeature.StrictNullChecks, false)
                .build()
        )
        mapper.findAndRegisterModules()
        mapper.writeValue(file, SocialCreditScoreYAML(localData))
    }

    fun load(file: File) {
        if (!file.exists()) {
            file.writer(charset("utf-8")).apply {
                this@apply.write("user-data:")
                this@apply.flush()
                this@apply.close()
            }
        }
        val mapper = ObjectMapper(YAMLFactory()).registerModule(
            KotlinModule.Builder().withReflectionCacheSize(512).configure(KotlinFeature.NullToEmptyCollection, true)
                .configure(KotlinFeature.NullToEmptyMap, true).configure(KotlinFeature.NullIsSameAsDefault, true)
                .configure(KotlinFeature.SingletonSupport, false).configure(KotlinFeature.StrictNullChecks, false)
                .build()
        )
        mapper.findAndRegisterModules()
        val typeReference = object : TypeReference<SocialCreditScoreYAML>() {}
        val map = safe { mapper.readValue(file, typeReference) } ?: SocialCreditScoreYAML(mutableMapOf())
        localData = map.userData.toMutableMap()
    }
}

data class SocialCreditScore(
    val uuid: UUID, val scores: MutableList<Pair<Int, String?>> = mutableListOf()
) {
    fun score(): Int {
        var result = 0
        scores.forEach { score -> result += score.first }
        return result
    }
}

data class SocialCreditScoreYAML(
    @JsonProperty("user-data") val userData: MutableMap<UUID, SocialCreditScore>
)

fun <T> safe(code: () -> T?): T? {
    return try {
        code()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}