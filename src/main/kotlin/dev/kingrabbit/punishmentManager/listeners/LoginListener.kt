package dev.kingrabbit.punishmentManager.listeners

import com.mongodb.client.model.Filters.*
import com.mongodb.client.model.ReplaceOptions
import dev.kingrabbit.punishmentManager.data.Duration
import dev.kingrabbit.punishmentManager.data.UserData
import dev.kingrabbit.punishmentManager.kotlin.MongoSerializable
import dev.kingrabbit.punishmentManager.kotlin.configString
import dev.kingrabbit.punishmentManager.kotlin.toMini
import dev.kingrabbit.punishmentManager.kotlin.toName
import gg.flyte.twilight.data.MongoDB
import gg.flyte.twilight.scheduler.async
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent

object LoginListener : Listener {

    @EventHandler
    fun prelogin(event: AsyncPlayerPreLoginEvent) {

        val userData = MongoDB.collection("users")
            .find(eq("uuid", event.uniqueId.toString()))
            .firstOrNull()
            ?.let { MongoSerializable.fromDocument(it) as UserData? }

        async {
            val data = userData ?: UserData.blank(event.uniqueId)
            if (!data.ips.contains(event.address.hostName)) {
                data.ips.add(event.address.hostName)
                MongoDB.collection("users").replaceOne(eq("uuid", event.uniqueId.toString()), data.toDocument(), ReplaceOptions().upsert(true))
            }
        }

        val activeBan = userData?.bans?.find { it.active }
        if (activeBan == null) {
            val ipBans = MongoDB.collection("users")
                .find(and(
                    eq("bans.ip", true),
                    eq("bans.active", true),
                    `in`("ips", listOf(*(userData?.ips ?: emptyList()).toTypedArray(), event.address.hostName))
                ))

            val ipBan = ipBans
                .firstOrNull()
                ?.let { MongoSerializable.fromDocument(it) as UserData? }
                ?.bans
                ?.find { it.active }
                ?: return

            val bannedBy = ipBan.bannedBy.toName().toMini()
            if (ipBan.reason == null) {
                event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                    "messages.ipban.receiver.without-reason.login".configString("<red>You are currently IP banned by <white><0></white>.")
                        .toMini(bannedBy)
                )
            } else {
                event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                    "messages.ipban.receiver.with-reason.login".configString("<red>You are currently IP banned by <white><0></white> for <white><1></white>.")
                        .toMini(bannedBy, ipBan.reason.toMini())
                )
            }

            return
        }

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