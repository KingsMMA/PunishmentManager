package dev.kingrabbit.punishmentManager.commands.history

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.ReplaceOptions
import dev.kingrabbit.punishmentManager.data.Duration
import dev.kingrabbit.punishmentManager.data.KickData
import dev.kingrabbit.punishmentManager.data.UserData
import dev.kingrabbit.punishmentManager.kotlin.*
import gg.flyte.twilight.data.MongoDB
import gg.flyte.twilight.extension.asString
import net.kyori.adventure.text.Component
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Optional
import revxrsal.commands.bukkit.annotation.CommandPermission
import java.util.*

object HistoryCommand {

    @Command("history")
    @CommandPermission("punishmentManager.history")
    fun kick(sender: CommandSender, target: OfflinePlayer) {
        val userData = MongoDB.collection("users")
            .find(eq("uuid", target.uniqueId.toString()))
            .firstOrNull()
            ?.let { MongoSerializable.fromDocument(it) as UserData? }
            ?: return sender.sendMini("messages.history.no-history".configString("<green>There is no history for <white><0></white>!",
                ), target.name!!.toMini())

        if (userData.kicks.isEmpty() && userData.mutes.isEmpty() && userData.bans.isEmpty())
            return sender.sendMini("messages.history.no-history".configString("<green>There is no history for <white><0></white>!",
                ), target.name!!.toMini())

        sender.sendMini("messages.history.header".configString("<gray>History for <white><0></white>:"), target.name!!.toMini())

        userData.kicks.forEach {
            sender.sendMini("messages.history.kick".configString("<gray>  - Kicked by <white><0></white> for <white><1></white> at <white><2></white>.",),
                it.kickedBy.toName().toMini(),
                Date(it.kickedAt).toMini(),
                (it.reason ?: "No reason provided").toMini()
            )
        }

        userData.mutes.forEach {
            val message: Component = if (it.duration != -1L) {
                "messages.history.mute.temporary".configString("<gray>  - Muted by <white><0></white> at <white><1></white> for <white><2></white>. Reason: <white><3></white>.")
                    .toMini(
                        it.mutedBy.toName().toMini(),
                        Date(it.mutedAt).toMini(),
                        Duration(it.duration).toString().toMini(),
                        (it.reason ?: "No reason provided").toMini()
                    )
            } else {
                "messages.history.permanent.mute".configString("<gray>  - Muted by <white><0></white> at <white><1></white>. Reason: <white><2></white>.")
                    .toMini(
                        it.mutedBy.toName().toMini(),
                        Date(it.mutedAt).toMini(),
                        (it.reason ?: "No reason provided.").toMini()
                    )
            }

            if (it.active) {
                sender.sendMessage(message)
            } else {
                sender.sendMini("<strikethrough>${message.asString()}</strikethrough> ${it.removedReason}")
            }
        }

        userData.bans.forEach {
            val message: Component = if (it.duration != -1L) {
                "messages.history.ban.temporary".configString("<gray>  - Banned by <white><0></white> at <white><1></white> for <white><2></white>. Reason: <white><3></white>.")
                    .toMini(
                        it.bannedBy.toName().toMini(),
                        Date(it.bannedAt).toMini(),
                        Duration(it.duration).toString().toMini(),
                        (it.reason ?: "No reason provided").toMini()
                    )
            } else {
                "messages.history.ban.mute".configString("<gray>  - Banned by <white><0></white> at <white><1></white>. Reason: <white><2></white>.")
                    .toMini(
                        it.bannedBy.toName().toMini(),
                        Date(it.bannedAt).toMini(),
                        (it.reason ?: "No reason provided.").toMini()
                    )
            }

            if (it.active) {
                sender.sendMessage(message)
            } else {
                sender.sendMini("<strikethrough>${message.asString()}</strikethrough> ${it.removedReason}")
            }
        }
    }

}