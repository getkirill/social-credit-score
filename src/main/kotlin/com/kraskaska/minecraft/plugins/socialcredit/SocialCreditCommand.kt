package com.kraskaska.minecraft.plugins.socialcredit

import net.md_5.bungee.api.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.*

object SocialCreditCommand : CommandExecutor, TabCompleter {
    // This method is called, when somebody uses our command
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            if (sender !is Player) {
                val message =
                    ComponentBuilder().text("Консоль не может видеть свой социальный рейтинг. Используйте команду /socialcredit inspect <player>")
                        .color(ChatColor.AQUA).build()
                sender.spigot().sendMessage(message)
                return true
            }
            val message = ComponentBuilder().text("Ваш социальный рейтинг: ").color(ChatColor.AQUA)
                .text((SocialCreditData.get(sender.uniqueId)?.score() ?: 0).toString()).color(
                    if ((SocialCreditData.get(sender.uniqueId)?.score() ?: 0) >= 0) ChatColor.GREEN else ChatColor.RED
                ).build()
            sender.spigot().sendMessage(message)
            return true
        }// just show the score
        when (args[0].lowercase()) {
            "add" -> {
                if (!sender.hasPermission("socialcredit.add") && !sender.isOp) {
                    sender.spigot().sendMessage(
                        ComponentBuilder().text("У вас недостаточно прав чтобы выполнить эту команду.")
                            .color(ChatColor.RED).build()
                    )
                    return true
                }
                // add Kraskaska 1 two
                if (args.size < 3) {
                    sender.spigot().sendMessage(
                        ComponentBuilder().text("Синтаксис команды: /socialcredit add <player> <score> [reason]")
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
                    val message = ComponentBuilder().text("Неверный UUID.").color(ChatColor.RED).build()
                    sender.spigot().sendMessage(message)
                    return true
                }
                val score = args[2].toInt()
                val reason = if (args.size >= 4) args.slice(3..args.lastIndex).joinToString(" ") else null
                SocialCreditData.add(player.uniqueId, score, reason)
                val message =
                    player.name?.let {
                        ComponentBuilder().text("Социальный рейтинг игрока ").color(ChatColor.AQUA).text(it)
                            .color(ChatColor.LIGHT_PURPLE).text(" был увеличен на ${score} очков.")
                            .color(ChatColor.AQUA).build()
                    }
                sender.spigot().sendMessage(message)
            }
            "remove" -> {
                if (!sender.hasPermission("socialcredit.remove") && !sender.isOp) {
                    sender.spigot().sendMessage(
                        ComponentBuilder().text("У вас недостаточно прав чтобы выполнить эту команду.")
                            .color(ChatColor.RED).build()
                    )
                    return true
                }
                if (args.size < 3) {
                    sender.spigot().sendMessage(
                        ComponentBuilder().text("Синтаксис команды: /socialcredit remove <player> <score> [reason]")
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
                    val message = ComponentBuilder().text("Неверный UUID.").color(ChatColor.RED).build()
                    sender.spigot().sendMessage(message)
                    return true
                }
                val score = args[2].toInt()
                val reason = if (args.size >= 4) args.slice(3..args.lastIndex).joinToString(" ") else null
                SocialCreditData.remove(player.uniqueId, score, reason)
                val message =
                    player.name?.let {
                        ComponentBuilder().text("Социальный рейтинг игрока ").color(ChatColor.AQUA).text(it)
                            .color(ChatColor.LIGHT_PURPLE).text(" был уменьшен на ${score} очков.")
                            .color(ChatColor.AQUA).build()
                    }
                sender.spigot().sendMessage(message)
            }
            "set" -> {
                if (!sender.hasPermission("socialcredit.set") && !sender.isOp) {
                    sender.spigot().sendMessage(
                        ComponentBuilder().text("У вас недостаточно прав чтобы выполнить эту команду.")
                            .color(ChatColor.RED).build()
                    )
                    return true
                }
                if (args.size < 3) {
                    sender.spigot().sendMessage(
                        ComponentBuilder().text("Синтаксис команды: /socialcredit set <player> <score> [reason]")
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
                    val message = ComponentBuilder().text("Неверный UUID.").color(ChatColor.RED).build()
                    sender.spigot().sendMessage(message)
                    return true
                }
                val score = args[2].toInt()
                val reason = if (args.size >= 4) args.slice(3..args.lastIndex).joinToString(" ") else null
                SocialCreditData.set(player.uniqueId, score, reason)
                val message =
                    player.name?.let {
                        ComponentBuilder().text("Социальный рейтинг игрока ").color(ChatColor.AQUA).text(it)
                            .color(ChatColor.LIGHT_PURPLE).text(" был установлен на значении ${score} очков.")
                            .color(ChatColor.AQUA).build()
                    }
                sender.spigot().sendMessage(message)
            }
            "reset" -> {
                if (!sender.hasPermission("socialcredit.reset") && !sender.isOp) {
                    sender.spigot().sendMessage(
                        ComponentBuilder().text("У вас недостаточно прав чтобы выполнить эту команду.")
                            .color(ChatColor.RED).build()
                    )
                    return true
                }
                // reset Kraskaska two
                if (args.size < 2) {
                    sender.spigot().sendMessage(
                        ComponentBuilder().text("Синтаксис команды: /socialcredit reset <player> [reason]")
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
                    val message = ComponentBuilder().text("Неверный UUID.").color(ChatColor.RED).build()
                    sender.spigot().sendMessage(message)
                    return true
                }
                val reason = if (args.size >= 4) args.slice(3..args.lastIndex).joinToString(" ") else null
                SocialCreditData.reset(player.uniqueId, reason)
                val message =
                    player.name?.let {
                        ComponentBuilder().text("Социальный рейтинг игрока ").color(ChatColor.AQUA).text(it)
                            .color(ChatColor.LIGHT_PURPLE).text(" был сброшен в 0.")
                            .color(ChatColor.AQUA).build()
                    }
                sender.spigot().sendMessage(message)
            }
            "inspect" -> {
                if (!sender.hasPermission("socialcredit.inspect") && !sender.isOp) {
                    sender.spigot().sendMessage(
                        ComponentBuilder().text("У вас недостаточно прав чтобы выполнить эту команду.")
                            .color(ChatColor.RED).build()
                    )
                    return true
                }
                if (args.size < 2) {
                    sender.spigot().sendMessage(
                        ComponentBuilder().text("Синтаксис команды: /socialcredit inspect <player>")
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
                    val message = ComponentBuilder().text("Неверный UUID.").color(ChatColor.RED).build()
                    sender.spigot().sendMessage(message)
                    return true
                }
                val message = ComponentBuilder().text("${player.name}'s").color(ChatColor.LIGHT_PURPLE)
                    .text(" social credit score: ").color(ChatColor.AQUA)
                    .text((SocialCreditData.get(player.uniqueId)?.score() ?: 0).toString()).color(
                        if ((SocialCreditData.get(player.uniqueId)?.score()
                                ?: 0) >= 0
                        ) ChatColor.GREEN else ChatColor.RED
                    ).build()
                sender.spigot().sendMessage(message)
            }
            "history" -> {
                if (!sender.hasPermission("socialcredit.history") && !sender.isOp && !(args.size < 2 && sender is Player)) {
                    sender.spigot().sendMessage(
                        ComponentBuilder().text("У вас недостаточно прав чтобы выполнить эту команду.")
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
                    val message = ComponentBuilder().text("Неверный UUID.").color(ChatColor.RED).build()
                    sender.spigot().sendMessage(message)
                    return true
                }
                val message = if (args.size < 2) ComponentBuilder().text("История вашего рейтинга:")
                    .color(ChatColor.AQUA) else player.name?.let {
                    ComponentBuilder().text("История рейтинга игрока ")
                        .color(ChatColor.AQUA).text(it)
                        .color(ChatColor.LIGHT_PURPLE).text(": ")
                        .color(ChatColor.AQUA)
                }!!
                if (SocialCreditData.get(player.uniqueId)?.scores?.isEmpty() == true) message.text("истории изменения нет.")
                    .color(
                        ChatColor.GRAY
                    ).italics()
                SocialCreditData.get(player.uniqueId)?.scores?.forEach {
                    message.text("\n${it.first}").color(
                        if (it.first >= 0) ChatColor.GREEN else ChatColor.RED
                    )
                    message.text(" - ")
                    message.text(it.second ?: "Причина не указана").color(ChatColor.GRAY).italics()
                }
                sender.spigot().sendMessage(message.build())
            }
            else -> {
                val message =
                    ComponentBuilder().text("Неизвестная подкоманда ${args[1]}, должно быть: add, remove, set, reset, inspect, history")
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
                "add" -> sender.isOp || (sender is Player && sender.hasPermission("socialcredit.add"))
                "remove" -> sender.isOp || (sender is Player && sender.hasPermission("socialcredit.remove"))
                "set" -> sender.isOp || (sender is Player && sender.hasPermission("socialcredit.set"))
                "reset" -> sender.isOp || (sender is Player && sender.hasPermission("socialcredit.reset"))
                "inspect" -> sender.isOp || (sender is Player && sender.hasPermission("socialcredit.inspect"))
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