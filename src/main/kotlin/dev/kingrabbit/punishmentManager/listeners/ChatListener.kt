package dev.kingrabbit.punishmentManager.listeners

import dev.kingrabbit.punishmentManager.ActivePunishments
import dev.kingrabbit.punishmentManager.kotlin.configString
import dev.kingrabbit.punishmentManager.kotlin.sendMini
import dev.kingrabbit.punishmentManager.kotlin.toMini
import gg.flyte.twilight.data.service.NameCacheService
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
            if (muteData.reason != null) {
                player.sendMini(
                    "messages.mute.receiver.permanent.with-reason.chat".configString("<red>You are muted by <white><0></white> for <white><1></white>."),
                    Bukkit.getOfflinePlayer(muteData.mutedBy).name?.toMini() ?: "CONSOLE".toMini(),
                    muteData.reason.toMini()
                )
            } else {
                player.sendMini(
                    "messages.mute.receiver.permanent.no-reason.chat".configString("<red>You are muted by <white><0></white>."),
                        Bukkit.getOfflinePlayer(muteData.mutedBy).name?.toMini() ?: "CONSOLE".toMini()
                )
            }
            return
        }
    }

}