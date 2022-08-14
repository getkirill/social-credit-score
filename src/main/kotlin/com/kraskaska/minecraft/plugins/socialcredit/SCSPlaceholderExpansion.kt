package com.kraskaska.minecraft.plugins.socialcredit

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer

class SCSPlaceholderExpansion: PlaceholderExpansion() {
    override fun getIdentifier(): String = "kraskaska_social_credit_score"

    override fun getAuthor(): String = "Kraskaska"

    override fun getVersion(): String = "v1.0"

    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        return super.onRequest(player, params)
    }
}