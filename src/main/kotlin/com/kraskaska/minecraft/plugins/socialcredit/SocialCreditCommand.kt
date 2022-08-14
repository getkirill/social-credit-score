package com.kraskaska.minecraft.plugins.socialcredit

import net.md_5.bungee.api.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.*

object SocialCreditCommand : CommandExecutor, TabCompleter {
    // This method is called, when somebody uses our command
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            if (sender !is Player) {
                val message =
                    ComponentBuilder().text("Console may not see its own social credit score. Use /socialcredit inspect <player> instead")
                        .color(ChatColor.AQUA).build()
                sender.spigot().sendMessage(message)
                return true
            }
            val message = ComponentBuilder().text("Your social credit score: ").color(ChatColor.AQUA)
                .text((SocialCreditData.get(sender.uniqueId)?.score() ?: SocialCreditConfig.config.resetAmount).toString()).color(
                    if ((SocialCreditData.get(sender.uniqueId)?.score() ?: SocialCreditConfig.config.resetAmount) >= 0) ChatColor.GREEN else ChatColor.RED
                ).build()
            sender.spigot().sendMessage(message)
            return true
        }// just show the score
        when (args[0].lowercase()) {
            "add" -> {
                if (sender is Player && SCSPlugin.perms?.playerHas(sender, "socialcredit.add") == false && !sender.isOp) {
                    sender.spigot().sendMessage(
                        ComponentBuilder().text("You don't have permissions to execute this command")
                            .color(ChatColor.RED).build()
                    )
                    return true
                }
                // add Kraskaska 1 two
                if (args.size < 3) {
                    sender.spigot().sendMessage(
                        ComponentBuilder().text("Usage: /socialcredit add <player> <score> [reason]")
                            .color(ChatColor.RED).build()
                    )
                    return true
                }
                val player: OfflinePlayer
                try {
                    player = sender.server.getPlayer(args[1]) ?: sender.server.getPlayer(UUID.fromString(args[1]))
                            ?: sender.server.offlinePlayers.firstOrNull { offlinePlayer -> offlinePlayer.name == args[1] }
                            ?: sender.server.getOfflinePlayer(UUID.fromString(args[1]))
                } catch (e: java.lang.IllegalArgumentException) {
                    val message = ComponentBuilder().text("This is not a valid UUID.").color(ChatColor.RED).build()
                    sender.spigot().sendMessage(message)
                    return true
                }
                val score = args[2].toInt()
                val reason = if (args.size >= 4) args.slice(3..args.lastIndex).joinToString(" ") else null
                SocialCreditData.add(player.uniqueId, score, reason)
                val message = ComponentBuilder().text("${player.name}'s").color(ChatColor.LIGHT_PURPLE)
                    .text(" social credit score has been increased by ${score}.").color(ChatColor.AQUA).build()
                sender.spigot().sendMessage(message)
            }
            "remove" -> {
                if (sender is Player && SCSPlugin.perms?.playerHas(sender, "socialcredit.remove") == false && !sender.isOp) {
                    sender.spigot().sendMessage(
                        ComponentBuilder().text("You don't have permissions to execute this command")
                            .color(ChatColor.RED).build()
                    )
                    return true
                }
                if (args.size < 3) {
                    sender.spigot().sendMessage(
                        ComponentBuilder().text("Usage: /socialcredit remove <player> <score> [reason]")
                            .color(ChatColor.RED).build()
                    )
                    return true
                }
                val player: OfflinePlayer
                try {
                    player = sender.server.getPlayer(args[1]) ?: sender.server.getPlayer(UUID.fromString(args[1]))
                            ?: sender.server.offlinePlayers.firstOrNull { offlinePlayer -> offlinePlayer.name == args[1] }
                            ?: sender.server.getOfflinePlayer(UUID.fromString(args[1]))
                } catch (e: java.lang.IllegalArgumentException) {
                    val message = ComponentBuilder().text("This is not a valid UUID.").color(ChatColor.RED).build()
                    sender.spigot().sendMessage(message)
                    return true
                }
                val score = args[2].toInt()
                val reason = if (args.size >= 4) args.slice(3..args.lastIndex).joinToString(" ") else null
                SocialCreditData.remove(player.uniqueId, score, reason)
                val message = ComponentBuilder().text("${player.name}'s").color(ChatColor.LIGHT_PURPLE)
                    .text(" social credit score has been decreased by ${score}.").color(ChatColor.AQUA).build()
                sender.spigot().sendMessage(message)
            }
            "set" -> {
                if (sender is Player && SCSPlugin.perms?.playerHas(sender, "socialcredit.set") == false && !sender.isOp) {
                    sender.spigot().sendMessage(
                        ComponentBuilder().text("You don't have permissions to execute this command")
                            .color(ChatColor.RED).build()
                    )
                    return true
                }
                if (args.size < 3) {
                    sender.spigot().sendMessage(
                        ComponentBuilder().text("Usage: /socialcredit set <player> <score> [reason]")
                            .color(ChatColor.RED).build()
                    )
                    return true
                }
                val player: OfflinePlayer
                try {
                    player = sender.server.getPlayer(args[1]) ?: sender.server.getPlayer(UUID.fromString(args[1]))
                            ?: sender.server.offlinePlayers.firstOrNull { offlinePlayer -> offlinePlayer.name == args[1] }
                            ?: sender.server.getOfflinePlayer(UUID.fromString(args[1]))
                } catch (e: java.lang.IllegalArgumentException) {
                    val message = ComponentBuilder().text("This is not a valid UUID.").color(ChatColor.RED).build()
                    sender.spigot().sendMessage(message)
                    return true
                }
                val score = args[2].toInt()
                val reason = if (args.size >= 4) args.slice(3..args.lastIndex).joinToString(" ") else null
                SocialCreditData.set(player.uniqueId, score, reason)
                val message = ComponentBuilder().text("${player.name}'s").color(ChatColor.LIGHT_PURPLE)
                    .text(" social credit score has been set to ${score}.").color(ChatColor.AQUA).build()
                sender.spigot().sendMessage(message)
            }
            "reset" -> {
                if (sender is Player && SCSPlugin.perms?.playerHas(sender, "socialcredit.reset") == false && !sender.isOp) {
                    sender.spigot().sendMessage(
                        ComponentBuilder().text("You don't have permissions to execute this command")
                            .color(ChatColor.RED).build()
                    )
                    return true
                }
                // reset Kraskaska two
                if (args.size < 2) {
                    sender.spigot().sendMessage(
                        ComponentBuilder().text("Usage: /socialcredit reset <player> [reason]").color(ChatColor.RED)
                            .build()
                    )
                    return true
                }
                val player: OfflinePlayer
                try {
                    player = sender.server.getPlayer(args[1]) ?: sender.server.getPlayer(UUID.fromString(args[1]))
                            ?: sender.server.offlinePlayers.firstOrNull { offlinePlayer -> offlinePlayer.name == args[1] }
                            ?: sender.server.getOfflinePlayer(UUID.fromString(args[1]))
                } catch (e: java.lang.IllegalArgumentException) {
                    val message = ComponentBuilder().text("This is not a valid UUID.").color(ChatColor.RED).build()
                    sender.spigot().sendMessage(message)
                    return true
                }
                val reason = if (args.size >= 4) args.slice(3..args.lastIndex).joinToString(" ") else null
                SocialCreditData.reset(player.uniqueId, reason)
                val message = ComponentBuilder().text("${player.name}'s").color(ChatColor.LIGHT_PURPLE)
                    .text(" social credit score has been reset.").color(ChatColor.AQUA).build()
                sender.spigot().sendMessage(message)
            }
            "inspect" -> {
                if (sender is Player && SCSPlugin.perms?.playerHas(sender, "socialcredit.inspect") == false && !sender.isOp) {
                    sender.spigot().sendMessage(
                        ComponentBuilder().text("You don't have permissions to execute this command")
                            .color(ChatColor.RED).build()
                    )
                    return true
                }
                if (args.size < 2) {
                    sender.spigot().sendMessage(
                        ComponentBuilder().text("Usage: /socialcredit inspect <player>").color(ChatColor.RED).build()
                    )
                    return true
                }
                val player: OfflinePlayer
                try {
                    player = sender.server.getPlayer(args[1]) ?: sender.server.getPlayer(UUID.fromString(args[1]))
                            ?: sender.server.offlinePlayers.firstOrNull { offlinePlayer -> offlinePlayer.name == args[1] }
                            ?: sender.server.getOfflinePlayer(UUID.fromString(args[1]))
                } catch (e: java.lang.IllegalArgumentException) {
                    val message = ComponentBuilder().text("This is not a valid UUID.").color(ChatColor.RED).build()
                    sender.spigot().sendMessage(message)
                    return true
                }
                val message = ComponentBuilder().text("${player.name}'s").color(ChatColor.LIGHT_PURPLE)
                    .text(" social credit score: ").color(ChatColor.AQUA)
                    .text((SocialCreditData.get(player.uniqueId)?.score() ?: SocialCreditConfig.config.resetAmount).toString()).color(
                        if ((SocialCreditData.get(player.uniqueId)?.score()
                                ?: SocialCreditConfig.config.resetAmount) >= 0
                        ) ChatColor.GREEN else ChatColor.RED
                    ).build()
                sender.spigot().sendMessage(message)
            }
            "history" -> {
                if (sender is Player && SCSPlugin.perms?.playerHas(sender, "socialcredit.history") == false && !sender.isOp && !(args.size < 2 && true)) {
                    sender.spigot().sendMessage(
                        ComponentBuilder().text("You don't have permissions to execute this command")
                            .color(ChatColor.RED).build()
                    )
                    return true
                }
                val player: OfflinePlayer
                try {
                    player = if (args.size < 2 && sender is Player) sender else sender.server.getPlayer(args[1])
                        ?: sender.server.getPlayer(UUID.fromString(args[1]))
                        ?: sender.server.offlinePlayers.firstOrNull { offlinePlayer -> offlinePlayer.name == args[1] }
                        ?: sender.server.getOfflinePlayer(UUID.fromString(args[1]))
                } catch (e: java.lang.IllegalArgumentException) {
                    val message = ComponentBuilder().text("This is not a valid UUID.").color(ChatColor.RED).build()
                    sender.spigot().sendMessage(message)
                    return true
                }
                val message = if (args.size < 2) ComponentBuilder().text("Your")
                    .color(ChatColor.AQUA) else ComponentBuilder().text("${player.name}'s")
                    .color(ChatColor.LIGHT_PURPLE)
                message.text(" social credit score change history: ").color(ChatColor.AQUA)
                if (SocialCreditData.get(player.uniqueId)?.scores?.isEmpty() == true) message.text("no changes on record.")
                    .color(
                        ChatColor.GRAY
                    ).italics()
                SocialCreditData.get(player.uniqueId)?.scores?.forEach {
                    message.text("\n${it.first}").color(
                        if (it.first >= 0) ChatColor.GREEN else ChatColor.RED
                    )
                    message.text(" - ")
                    message.text(it.second ?: "No reason specified").color(ChatColor.GRAY).italics()
                }
                sender.spigot().sendMessage(message.build())
            }
            else -> {
                val message =
                    ComponentBuilder().text("Unknown subcommand ${args[1]}, you can only use: add, remove, set, reset, inspect, history")
                        .color(ChatColor.RED)
                sender.spigot().sendMessage(message.build())
            }
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender, command: Command, label: String, args: Array<out String>
    ): MutableList<String>? {
        val toComplete = args[args.lastIndex]
        if (args.size < 2) return arrayOf("add", "remove", "set", "reset", "inspect", "history").filter { item ->
            item.lowercase().startsWith(toComplete.lowercase())
        }.filter {
                when (it) {
                    "add" -> sender.isOp || (sender is Player && SCSPlugin.perms?.playerHas(sender, "socialcredit.add") == true)
                    "remove" -> sender.isOp || (sender is Player && SCSPlugin.perms?.playerHas(sender, "socialcredit.add") == true)
                    "set" -> sender.isOp || (sender is Player && SCSPlugin.perms?.playerHas(sender, "socialcredit.add") == true)
                    "reset" -> sender.isOp || (sender is Player && SCSPlugin.perms?.playerHas(sender, "socialcredit.add") == true)
                    "inspect" -> sender.isOp || (sender is Player && SCSPlugin.perms?.playerHas(sender, "socialcredit.add") == true)
                    else -> true
                }
            }.toMutableList()

        when (args[1].lowercase()) {
            "add" -> {
                if (args.size == 2) {
                    return mutableListOf(sender.server.onlinePlayers.map { player -> player.name },
                        sender.server.onlinePlayers.map { player -> player.uniqueId.toString() },
                        sender.server.offlinePlayers.map { player -> player.name },
                        sender.server.offlinePlayers.map { player -> player.uniqueId.toString() }).flatten()
                        .filterNotNull().toMutableList()
                }
            }
            "remove" -> {
                if (args.size == 2) {
                    return mutableListOf(sender.server.onlinePlayers.map { player -> player.name },
                        sender.server.onlinePlayers.map { player -> player.uniqueId.toString() },
                        sender.server.offlinePlayers.map { player -> player.name },
                        sender.server.offlinePlayers.map { player -> player.uniqueId.toString() }).flatten()
                        .filterNotNull().toMutableList()
                }
            }
            "set" -> {
                if (args.size == 2) {
                    return mutableListOf(sender.server.onlinePlayers.map { player -> player.name },
                        sender.server.onlinePlayers.map { player -> player.uniqueId.toString() },
                        sender.server.offlinePlayers.map { player -> player.name },
                        sender.server.offlinePlayers.map { player -> player.uniqueId.toString() }).flatten()
                        .filterNotNull().toMutableList()
                }
            }
            "reset" -> {
                if (args.size == 2) {
                    return mutableListOf(sender.server.onlinePlayers.map { player -> player.name },
                        sender.server.onlinePlayers.map { player -> player.uniqueId.toString() },
                        sender.server.offlinePlayers.map { player -> player.name },
                        sender.server.offlinePlayers.map { player -> player.uniqueId.toString() }).flatten()
                        .filterNotNull().toMutableList()
                }
            }
            "inspect" -> {
                if (args.size == 2) {
                    return mutableListOf(sender.server.onlinePlayers.map { player -> player.name },
                        sender.server.onlinePlayers.map { player -> player.uniqueId.toString() },
                        sender.server.offlinePlayers.map { player -> player.name },
                        sender.server.offlinePlayers.map { player -> player.uniqueId.toString() }).flatten()
                        .filterNotNull().toMutableList()
                }
            }
            "history" -> {
                if (args.size == 2) {
                    return mutableListOf(sender.server.onlinePlayers.map { player -> player.name },
                        sender.server.onlinePlayers.map { player -> player.uniqueId.toString() },
                        sender.server.offlinePlayers.map { player -> player.name },
                        sender.server.offlinePlayers.map { player -> player.uniqueId.toString() }).flatten()
                        .filterNotNull().toMutableList()
                }
            }
        }
        return null
    }

}