package com.kraskaska.minecraft.plugins.socialcredit

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.bukkit.Bukkit
import java.io.File

object SocialCreditConfig {
    var config: SocialCreditConfigYAML = SocialCreditConfigYAML()

    fun load(file: File) {
        Bukkit.getServer().logger.info("File ${file.absolutePath}: exists? ${file.exists()}")
        if (!file.exists()) {
            file.writer(charset("utf-8")).apply {
                this@apply.write("reset-amount: 0\nauto-ban: no\nauto-ban-floor: 0")
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
        config = mapper.readValue(file, config.javaClass)
    }

//    fun save(file: File) {
//        val mapper = ObjectMapper(YAMLFactory()).registerModule(
//            KotlinModule.Builder().withReflectionCacheSize(512).configure(KotlinFeature.NullToEmptyCollection, true)
//                .configure(KotlinFeature.NullToEmptyMap, true).configure(KotlinFeature.NullIsSameAsDefault, true)
//                .configure(KotlinFeature.SingletonSupport, false).configure(KotlinFeature.StrictNullChecks, false)
//                .build()
//        )
//        mapper.findAndRegisterModules()
//        val tmpConfig = mapper.readValue(file, config.javaClass)
//        if (tmpConfig == config) {
//            mapper.findAndRegisterModules()
//            mapper.writeValue(file, config)
//        }
//    }
}

data class SocialCreditConfigYAML(
    @JsonProperty("reset-amount") var resetAmount: Int = 0,

    @JsonProperty("auto-ban") var autoBanEnabled: Boolean = false,

    @JsonProperty("auto-ban-floor") var autoBanFloor: Int = 0
)