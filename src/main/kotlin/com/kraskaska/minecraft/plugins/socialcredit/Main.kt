package com.kraskaska.minecraft.plugins.socialcredit

import org.bukkit.BanList
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.permissions.Permission
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import net.milkbowl.vault.permission.Permission as VPermission


class SCSPlugin : JavaPlugin() {
    companion object {
        var perms: VPermission? = null
    }
    override fun onDisable() {
        logger.info("Disabling!")
        SocialCreditData.save(File(dataFolder, "user-data.yml"))
    }

    override fun onEnable() {
        logger.info("Enabling!")
        perms = server.servicesManager.getRegistration(VPermission::class.java)?.provider
        server.pluginManager.registerEvents(SCSPlayerJoined(), this)
        server.pluginManager.addPermission(Permission("socialcredit.add"))
        server.pluginManager.addPermission(Permission("socialcredit.remove"))
        server.pluginManager.addPermission(Permission("socialcredit.set"))
        server.pluginManager.addPermission(Permission("socialcredit.reset"))
        server.pluginManager.addPermission(Permission("socialcredit.inspect"))
        server.pluginManager.addPermission(Permission("socialcredit.history"))
        server.pluginManager.addPermission(Permission("socialcredit.prevent-ban"))
        with(File(dataFolder.absolutePath)) {
            if(!exists()) mkdir()
        }
        SocialCreditData.load(File(dataFolder, "user-data.yml"))
        SocialCreditConfig.load(File(dataFolder, "config.yml"))
        getCommand("socialcredit")?.setExecutor(SocialCreditCommand)
        getCommand("socialcredit")?.tabCompleter = SocialCreditCommand
    }
}

class SCSPlayerJoined : Listener {
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        e.player.server.logger.info("Player joined: ${e.player.name}")
        if (((SocialCreditData.get(e.player.uniqueId)?.score()
                ?: SocialCreditConfig.config.resetAmount) < SocialCreditConfig.config.autoBanFloor) && SocialCreditConfig.config.autoBanEnabled && !Bukkit.getServer().getOfflinePlayer(e.player.uniqueId).isOp && SCSPlugin.perms?.playerHas(
                e.player,
                "socialcredit.prevent-ban"
            ) == false
        ) {
            e.player.server.getBanList(BanList.Type.NAME).addBan(
                e.player.name, "Social Credit Score Automatic Ban: Your social credit score (${
                    SocialCreditData.get(e.player.uniqueId)?.score()
                        ?: SocialCreditConfig.config.resetAmount
                }) is less than server's set minimum (${SocialCreditConfig.config.autoBanFloor})",
                null,
                null
            )
            e.player.kickPlayer("You have been automatically banned by Social Credit Score.")
        }
    }
}