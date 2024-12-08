package dev.kingrabbit.punishmentManager.commands.kick

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.ReplaceOptions
import dev.kingrabbit.punishmentManager.data.KickData
import dev.kingrabbit.punishmentManager.data.UserData
import dev.kingrabbit.punishmentManager.kotlin.*
import gg.flyte.twilight.data.MongoDB
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Optional
import revxrsal.commands.bukkit.annotation.CommandPermission
import java.util.*

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

        val userData = MongoDB.collection("users")
            .find(eq("uuid", target.uniqueId.toString()))
            .firstOrNull()
            ?.let { MongoSerializable.fromDocument(it) as UserData? }
            ?: UserData.blank(target.uniqueId)

        userData.kicks.add(
            KickData(
                reason,
                if (sender is Player) sender.uniqueId else UUID(0, 0),
                System.currentTimeMillis()
            )
        )

        userData.toDocument().also {
            MongoDB.collection("users").replaceOne(eq("uuid", target.uniqueId.toString()), it, ReplaceOptions().upsert(true))
        }
    }

}