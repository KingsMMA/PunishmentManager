package dev.kingrabbit.punishmentManager.commands.ban

import com.mongodb.client.model.Filters.eq
import dev.kingrabbit.punishmentManager.ActivePunishments
import dev.kingrabbit.punishmentManager.data.UserData
import dev.kingrabbit.punishmentManager.kotlin.*
import gg.flyte.twilight.data.MongoDB
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import revxrsal.commands.annotation.Command
import revxrsal.commands.bukkit.annotation.CommandPermission

object UnbanCommand {

    @Command("unban")
    @CommandPermission("punishmentManager.unban")
    fun unban(sender: CommandSender, target: OfflinePlayer) {
        val userData = MongoDB.collection("users")
            .find(eq("uuid", target.uniqueId.toString()))
            .firstOrNull()
            ?.let { MongoSerializable.fromDocument(it) as UserData? }
            ?: return sender.sendMini("messages.unban.failed.not-banned".configString("<red><0> is not banned!"))

        val activeBan = userData.bans.find { it.active }
            ?: return sender.sendMini("messages.unban.failed.not-banned".configString("<red><0> is not banned!"))

        activeBan.active = false
        activeBan.removedReason = "Ubanned by ${sender.name()}"
        userData.toDocument().also {
            MongoDB.collection("users").replaceOne(eq("uuid", target.uniqueId.toString()), it)
        }

        ActivePunishments.removeBan(target.uniqueId)

        sender.sendMini("messages.unban.sender".configString("<green>You have unbanned <white><0></white>!"), target.name!!.toMini())
    }

}