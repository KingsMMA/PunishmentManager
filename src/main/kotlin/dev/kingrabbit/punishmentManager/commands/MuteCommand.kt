package dev.kingrabbit.punishmentManager.commands

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.ReplaceOptions
import dev.kingrabbit.punishmentManager.ActivePunishments
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

object MuteCommand {

    @Command("mute")
    @CommandPermission("punishmentManager.unmute")
    fun mute(sender: CommandSender, target: OfflinePlayer, @Optional reason: String?) {
        if (sender == target)
            return sender.sendMini(
                "messages.mute.failed.self".configString("<red>You cannot mute yourself!"))

        val userData = MongoDB.collection<UserData>("users")
        val userDocument: UserData? = userData
                .findByIdSync(target.uniqueId.toString())
                .firstOrNull()
        val user: UserData = userDocument ?: UserData(target.uniqueId, mutableListOf(), mutableListOf())
        if (user.mutes.any { it.active })
            return sender.sendMini(
                "messages.mute.failed.already-muted".configString("<red><0> is already muted!"),
                target.name!!.toMini()
            )

        val muteData = MuteData(
            reason,
            -1,
            if (sender is Player) sender.uniqueId else UUID(0, 0),
            System.currentTimeMillis(),
            true,
            null
        )

        ActivePunishments.addMute(target.uniqueId, muteData)

        user.mutes.add(muteData)
        user.saveSync()

        if (reason != null) {
            sender.sendMini(
                "messages.mute.sender.permanent.with-reason".configString("<red>You have muted <white><0></white> for <white><1></white>!"),
                target.name!!.toMini(),
                reason.toMini()
            )
            target.player?.sendMini(
                "messages.mute.receiver.permanent.with-reason.command".configString("<red>You have been muted by <white><0></white> for <white><1></white>."),
                sender.name(),
                reason.toMini()
            )
        } else {
            sender.sendMini(
                "messages.mute.sender.permanent.without-reason".configString("<red>You have muted <white><0></white>!"),
                target.name!!.toMini()
            )
            target.player?.sendMini(
                "messages.mute.receiver.permanent.without-reason.command".configString("<red>You have been muted by <white><0></white>."),
                sender.name()
            )
        }

    }

}