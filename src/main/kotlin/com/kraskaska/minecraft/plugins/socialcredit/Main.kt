package com.kraskaska.minecraft.plugins.socialcredit

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class SCSPlugin : JavaPlugin() {
    override fun onDisable() {
        logger.info("Disabling!")
        SocialCreditData.save(File(dataFolder, "user-data.yml"))
    }

    override fun onEnable() {
        logger.info("Enabling!")
//        server.pluginManager.registerEvents(SCSPlayerJoined(), this)
        SocialCreditData.load(File(dataFolder, "user-data.yml"))
        getCommand("socialcredit")?.setExecutor(SocialCreditCommand)
        getCommand("socialcredit")?.tabCompleter = SocialCreditCommand
    }
}

//class SCSPlayerJoined: Listener {
//    @EventHandler
//    fun onPlayerJoin(e: PlayerJoinEvent) {
//        e.player.server.logger.info("HI BITCH")
//    }
//}