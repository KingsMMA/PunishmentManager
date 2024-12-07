package dev.kingrabbit.punishmentManager.commands

import dev.kingrabbit.punishmentManager.kotlin.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Optional
import revxrsal.commands.bukkit.annotation.CommandPermission

object KickCommand {

    @Command("kick")
    @CommandPermission("punishmentManager.kick")
    fun kick(sender: CommandSender, target: Player, @Optional reason: String?) {
        if (sender == target)
            return sender.sendMini("messages.kick.failed.self".configString("<red>You cannot kick yourself!"))

        if (reason != null) {
            target.kick(
                "messages.kick.receiver.with-reason".configString("<red>You have been kicked.").toMini(sender.name(), reason.toMini()))
            sender.sendMini(
                "messages.kick.sender.with-reason".configString(
                    "<red>You have kicked <white><0></white> for <white><1></white>!"),
                target.name(), reason.toMini())
        } else {
            target.kick("messages.kick.receiver.without-reason".configString("<red>You have been kicked.").toMini(sender.name()))
            sender.sendMini(
                "messages.kick.sender.without-reason".configString(
                    "<red>You have kicked <white><0></white>!"),
                target.name())
        }
    }

}