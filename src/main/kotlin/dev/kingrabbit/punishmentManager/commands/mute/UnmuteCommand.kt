package dev.kingrabbit.punishmentManager.commands.mute

import com.mongodb.client.model.Filters.eq
import dev.kingrabbit.punishmentManager.ActivePunishments
import dev.kingrabbit.punishmentManager.data.UserData
import dev.kingrabbit.punishmentManager.kotlin.*
import gg.flyte.twilight.data.MongoDB
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import revxrsal.commands.annotation.Command
import revxrsal.commands.bukkit.annotation.CommandPermission

object UnmuteCommand {

    @Command("unmute")
    @CommandPermission("punishmentManager.unmute")
    fun unmute(sender: CommandSender, target: OfflinePlayer) {
        val userData = MongoDB.collection("users")
            .find(eq("uuid", target.uniqueId.toString()))
            .firstOrNull()
            ?.let { MongoSerializable.fromDocument(it) as UserData? }
            ?: return sender.sendMini("messages.unmute.failed.not-muted".configString("<red><0> is not muted!"))

        val activeMute = userData.mutes.find { it.active }
            ?: return sender.sendMini("messages.unmute.failed.not-muted".configString("<red><0> is not muted!"))

        activeMute.active = false
        activeMute.removedReason = "Unmuted by ${sender.name}"
        userData.toDocument().also {
            MongoDB.collection("users").replaceOne(eq("uuid", target.uniqueId.toString()), it)
        }

        ActivePunishments.removeMute(target.uniqueId)

        sender.sendMini("messages.unmute.sender".configString("<green>You have unmuted <white><0></white>!"), target.name!!.toMini())
        target.player?.sendMini("messages.unmute.receiver".configString("<green>You have been unmuted by <white><0></white>!"), sender.name())
    }

}