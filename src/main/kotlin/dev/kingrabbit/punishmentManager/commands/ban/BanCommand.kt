package dev.kingrabbit.punishmentManager.commands.ban

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.ReplaceOptions
import dev.kingrabbit.punishmentManager.ActivePunishments
import dev.kingrabbit.punishmentManager.data.BanData
import dev.kingrabbit.punishmentManager.data.UserData
import dev.kingrabbit.punishmentManager.kotlin.*
import gg.flyte.twilight.data.MongoDB
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Optional
import revxrsal.commands.bukkit.annotation.CommandPermission
import java.util.*

object BanCommand {

    @Command("ban")
    @CommandPermission("punishmentManager.ban")
    fun ban(sender: CommandSender, target: OfflinePlayer, @Optional reason: String?) {
        if (sender == target)
            return sender.sendMini(
                "messages.ban.failed.self".configString("<red>You cannot ban yourself!"))

        val userData = MongoDB.collection("users")
        val userDocument: UserData? = MongoSerializable.fromDocument<UserData>(
            userData
                .find(eq("uuid", target.uniqueId.toString()))
                .firstOrNull())
        val user: UserData = userDocument ?: UserData.blank(target.uniqueId)
        if (user.bans.any { it.active })
            return sender.sendMini(
                "messages.ban.failed.already-banned".configString("<red><0> is already banned!"),
                target.name!!.toMini()
            )

        val banData = BanData(
            reason,
            -1,
            if (sender is Player) sender.uniqueId else UUID(0, 0),
            System.currentTimeMillis(),
            ip = false,
            active = true,
            removedReason = null
        )

        ActivePunishments.addBan(target.uniqueId, banData)

        user.bans.add(banData)
        userData.replaceOne(eq("uuid", target.uniqueId.toString()), user.toDocument(), ReplaceOptions().upsert(true))

        if (reason != null) {
            sender.sendMini(
                "messages.ban.sender.permanent.with-reason".configString("<red>You have banned <white><0></white> for <white><1></white>!"),
                target.name!!.toMini(),
                reason.toMini()
            )
            target.player?.kick(
                "messages.ban.receiver.permanent.with-reason.command".configString("<red>You have been banned by <white><0></white> for <white><1></white>.").toMini(
                sender.name(),
                reason.toMini()
            ))
        } else {
            sender.sendMini(
                "messages.ban.sender.permanent.without-reason".configString("<red>You have banned <white><0></white>!"),
                target.name!!.toMini()
            )
            target.player?.kick(
                "messages.ban.receiver.permanent.without-reason.command".configString("<red>You have been banned by <white><0></white>.").toMini(
                sender.name()
            ))
        }

    }

}