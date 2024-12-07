package dev.kingrabbit.punishmentManager.listeners

import dev.kingrabbit.punishmentManager.ActivePunishments
import dev.kingrabbit.punishmentManager.data.Duration
import dev.kingrabbit.punishmentManager.kotlin.configString
import dev.kingrabbit.punishmentManager.kotlin.sendMini
import dev.kingrabbit.punishmentManager.kotlin.toMini
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

object ChatListener : Listener {

    @EventHandler
    fun onChat(event: AsyncPlayerChatEvent) {
        val player = event.player

        if (ActivePunishments.isMuted(player.uniqueId)) {
            event.isCancelled = true
            val muteData = ActivePunishments.getMute(player.uniqueId)!!
            val mutedBy = Bukkit.getOfflinePlayer(muteData.mutedBy).name?.toMini() ?: "CONSOLE".toMini()
            if (muteData.duration != -1L) {
                if (muteData.reason != null) {
                    player.sendMini(
                        "messages.mute.receiver.temporary.with-reason.chat".configString("<red>You are currently muted by <white><0></white> for <white><1></white>. This expires in <white><2></white>."),
                        mutedBy,
                        muteData.reason.toMini(),
                        Duration(muteData.duration - (System.currentTimeMillis() - muteData.mutedAt)).toString().toMini()
                    )
                } else {
                    player.sendMini(
                        "messages.mute.receiver.temporary.no-reason.chat".configString("<red>You are currently muted by <white><0></white>. This expires in <white><1></white>."),
                        mutedBy,
                        Duration(muteData.duration - (System.currentTimeMillis() - muteData.mutedAt)).toString().toMini()
                    )
                }
            } else {
                if (muteData.reason != null) {
                    player.sendMini(
                        "messages.mute.receiver.permanent.with-reason.chat".configString("<red>You are muted by <white><0></white> for <white><1></white>."),
                        mutedBy,
                        muteData.reason.toMini()
                    )
                } else {
                    player.sendMini(
                        "messages.mute.receiver.permanent.no-reason.chat".configString("<red>You are muted by <white><0></white>."),
                            mutedBy
                    )
                }
            }
            return
        }
    }

}