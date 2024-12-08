package dev.kingrabbit.punishmentManager.commands.mute

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.ReplaceOptions
import dev.kingrabbit.punishmentManager.ActivePunishments
import dev.kingrabbit.punishmentManager.data.Duration
import dev.kingrabbit.punishmentManager.data.MuteData
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

object TempMuteCommand {

    @Command("tempmute")
    @CommandPermission("punishmentManager.tempmute")
    fun mute(sender: CommandSender, target: OfflinePlayer, duration: Duration, @Optional reason: String?) {
        if (sender == target)
            return sender.sendMini(
                "messages.mute.failed.self".configString("<red>You cannot mute yourself!"))

        val userData = MongoDB.collection("users")
        val userDocument: UserData? = MongoSerializable.fromDocument<UserData>(
            userData
                .find(eq("uuid", target.uniqueId.toString()))
                .firstOrNull())
        val user: UserData = userDocument ?: UserData.blank(target.uniqueId)
        if (user.mutes.any { it.active })
            return sender.sendMini(
                "messages.mute.failed.already-muted".configString("<red><0> is already muted!"),
                target.name!!.toMini()
            )

        val muteData = MuteData(
            reason,
            duration.toMillis(),
            if (sender is Player) sender.uniqueId else UUID(0, 0),
            System.currentTimeMillis(),
            true,
            null
        )

        ActivePunishments.addMute(target.uniqueId, muteData)

        user.mutes.add(muteData)
        userData.replaceOne(eq("uuid", target.uniqueId.toString()), user.toDocument(), ReplaceOptions().upsert(true))

        if (reason != null) {
            sender.sendMini(
                "messages.mute.sender.temporary.with-reason".configString("<red>You have muted <white><0></white> for <white><1></white> for <white><2></white>!"),
                target.name!!.toMini(),
                reason.toMini(),
                duration.toString().toMini()
            )
            target.player?.sendMini(
                "messages.mute.receiver.temporary.with-reason.command".configString("<red>You have been muted by <white><0></white> for <white><1></white>. This expires in <white><2></white>."),
                sender.name(),
                reason.toMini(),
                duration.toString().toMini()
            )
        } else {
            sender.sendMini(
                "messages.mute.sender.temporary.without-reason".configString("<red>You have muted <white><0></white> for <white><1></white>!"),
                target.name!!.toMini(),
                duration.toString().toMini()
            )
            target.player?.sendMini(
                "messages.mute.receiver.temporary.without-reason.command".configString("<red>You have been muted by <white><0></white>. This expires in <white><1></white>."),
                sender.name(),
                duration.toString().toMini()
            )
        }

    }

}