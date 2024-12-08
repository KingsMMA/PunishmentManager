package dev.kingrabbit.punishmentManager.listeners

import com.mongodb.client.model.Filters.eq
import dev.kingrabbit.punishmentManager.data.Duration
import dev.kingrabbit.punishmentManager.data.UserData
import dev.kingrabbit.punishmentManager.kotlin.MongoSerializable
import dev.kingrabbit.punishmentManager.kotlin.configString
import dev.kingrabbit.punishmentManager.kotlin.toMini
import dev.kingrabbit.punishmentManager.kotlin.toName
import gg.flyte.twilight.data.MongoDB
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

object LoginListener : Listener {

    @EventHandler
    fun prelogin(event: AsyncPlayerPreLoginEvent) {
        println(event.address.hostName)

        val userData = MongoDB.collection("users")
            .find(eq("uuid", event.uniqueId.toString()))
            .firstOrNull()
            ?.let { MongoSerializable.fromDocument(it) as UserData? }
            ?: return

        val activeBan = userData.bans.find { it.active }
            ?: return
        val bannedBy = activeBan.bannedBy.toName().toMini()

        if (activeBan.duration == -1L) {
            if (activeBan.reason == null) {
                event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                    "messages.ban.receiver.permanent.without-reason.login".configString("<red>You are currently banned by <white><0></white>.")
                        .toMini(bannedBy)
                )
            } else {
                event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                    "messages.ban.receiver.permanent.with-reason.login".configString("<red>You are currently banned by <white><0></white> for <white><1></white>.")
                        .toMini(bannedBy, activeBan.reason.toMini())
                )
            }
        } else {
            if (activeBan.reason == null) {
                event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                    "messages.ban.receiver.temporary.without-reason.login".configString("<red>You are currently banned by <white><0></white>. This expires in <white><1></white>.")
                        .toMini(
                            bannedBy,
                            Duration(activeBan.duration - (System.currentTimeMillis() - activeBan.bannedAt)).toString().toMini()
                        )
                )
            } else {
                event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                    "messages.ban.receiver.temporary.with-reason.login".configString("<red>You are currently banned by <white><0></white>. This expires in <white><1></white>.")
                        .toMini(
                            bannedBy,
                            activeBan.reason.toMini(),
                            Duration(activeBan.duration - (System.currentTimeMillis() - activeBan.bannedAt)).toString().toMini()
                        )
                )
            }
        }
    }

}