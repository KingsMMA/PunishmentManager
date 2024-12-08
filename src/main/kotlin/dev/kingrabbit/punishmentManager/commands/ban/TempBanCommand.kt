package dev.kingrabbit.punishmentManager.commands.ban

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.ReplaceOptions
import dev.kingrabbit.punishmentManager.ActivePunishments
import dev.kingrabbit.punishmentManager.data.Duration
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

object TempBanCommand {

    @Command("tempban")
    @CommandPermission("punishmentManager.tempban")
    fun tempban(sender: CommandSender, target: OfflinePlayer, duration: Duration, @Optional reason: String?) {
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
            duration.toMillis(),
            if (sender is Player) sender.uniqueId else UUID(0, 0),
            System.currentTimeMillis(),
            true,
            null
        )

        ActivePunishments.addBan(target.uniqueId, banData)

        user.bans.add(banData)
        userData.replaceOne(eq("uuid", target.uniqueId.toString()), user.toDocument(), ReplaceOptions().upsert(true))

        if (reason != null) {
            sender.sendMini(
                "messages.ban.sender.temporary.with-reason".configString("<red>You have banned <white><0></white> for <white><1></white> for <white><2></white>!"),
                target.name!!.toMini(),
                reason.toMini(),
                duration.toString().toMini()
            )
            target.player?.kick(
                "messages.ban.receiver.temporary.with-reason.command".configString("<red>You have been banned by <white><0></white> for <white><1></white>. This expires in <white><2></white>.").toMini(
                sender.name(),
                reason.toMini(),
                duration.toString().toMini()
            ))
        } else {
            sender.sendMini(
                "messages.ban.sender.temporary.without-reason".configString("<red>You have banned <white><0></white> for <white><1></white>!"),
                target.name!!.toMini(),
                duration.toString().toMini()
            )
            target.player?.kick(
                "messages.ban.receiver.temporary.without-reason.command".configString("<red>You have been banned by <white><0></white>. This expires in <white><1></white>.").toMini(
                sender.name(),
                duration.toString().toMini()
            ))
        }

    }

}