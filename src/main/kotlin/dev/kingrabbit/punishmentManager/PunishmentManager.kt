package dev.kingrabbit.punishmentManager

import com.mongodb.client.model.Filters.eq
import dev.kingrabbit.punishmentManager.commands.ban.BanCommand
import dev.kingrabbit.punishmentManager.commands.ban.IpbanCommand
import dev.kingrabbit.punishmentManager.commands.ban.TempBanCommand
import dev.kingrabbit.punishmentManager.commands.ban.UnbanCommand
import dev.kingrabbit.punishmentManager.commands.history.HistoryCommand
import dev.kingrabbit.punishmentManager.commands.kick.KickCommand
import dev.kingrabbit.punishmentManager.commands.mute.MuteCommand
import dev.kingrabbit.punishmentManager.commands.mute.TempMuteCommand
import dev.kingrabbit.punishmentManager.commands.mute.UnmuteCommand
import dev.kingrabbit.punishmentManager.config.ConfigManager
import dev.kingrabbit.punishmentManager.data.Duration
import dev.kingrabbit.punishmentManager.data.DurationParameterType
import dev.kingrabbit.punishmentManager.data.UserData
import dev.kingrabbit.punishmentManager.kotlin.MongoSerializable
import dev.kingrabbit.punishmentManager.kotlin.config
import dev.kingrabbit.punishmentManager.kotlin.configString
import dev.kingrabbit.punishmentManager.kotlin.sendMini
import dev.kingrabbit.punishmentManager.listeners.ChatListener
import dev.kingrabbit.punishmentManager.listeners.LoginListener
import gg.flyte.twilight.data.MongoDB
import gg.flyte.twilight.scheduler.delay
import gg.flyte.twilight.time.TimeUnit
import gg.flyte.twilight.twilight
import org.bson.Document
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import revxrsal.commands.bukkit.BukkitLamp

class PunishmentManager : JavaPlugin() {

    override fun onEnable() {
        val twilight = twilight(this) {
            env {
                useDifferentEnvironments = false
            }
            mongo {}
            nameCache {}
        }

        val commandHandler = BukkitLamp.builder(this)
            .parameterTypes {
                it.addParameterType(Duration::class.java, DurationParameterType())
            }
            .build()
        commandHandler.register(
            HistoryCommand,
            KickCommand,
            TempMuteCommand,
            MuteCommand,
            UnmuteCommand,
            TempBanCommand,
            BanCommand,
            UnbanCommand,
            IpbanCommand
        )

        ConfigManager.initialise(config, this)

        Bukkit.getPluginManager().registerEvents(ChatListener, this)
        Bukkit.getPluginManager().registerEvents(LoginListener, this)

        MongoDB.collection("users").find()
            .forEach { document: Document ->
                run {
                    val userData = MongoSerializable.fromDocument<UserData>(document)
                    if (userData != null) {
                        userData.mutes.forEach { muteData ->
                            if (muteData.active) {
                                ActivePunishments.addMute(userData.uuid, muteData)
                            }
                        }
                        userData.bans.forEach { banData ->
                            if (banData.active) {
                                ActivePunishments.addBan(userData.uuid, banData)
                            }
                        }
                    }
                }
            }

        checkExpiredPunishments()
    }

    private fun checkExpiredPunishments() {
        for ((uuid, activeMute) in ActivePunishments.activeMutes) {
            if (activeMute.duration == -1L) {
                continue
            }
            if (activeMute.mutedAt + activeMute.duration <= System.currentTimeMillis()) {
                activeMute.active = false
                activeMute.removedReason = "Expired"

                ActivePunishments.removeMute(uuid)
                val userData = MongoDB.collection("users")
                val userDocument: UserData? = MongoSerializable.fromDocument<UserData>(
                    userData
                        .find(eq("uuid", uuid.toString()))
                        .firstOrNull())
                val user: UserData = userDocument ?: UserData.blank(uuid)
                val mongoMute = user.mutes.find { it.active }
                mongoMute?.active = false
                mongoMute?.removedReason = "Expired"
                userData.replaceOne(eq("uuid", uuid.toString()), user.toDocument())

                val player = Bukkit.getOfflinePlayer(uuid)
                player.player?.sendMini(
                    "messages.mute.receiver.temporary.expired".configString("<red>Your mute has expired!"))
            }
        }

        for ((uuid, activeBan) in ActivePunishments.activeBans) {
            if (activeBan.duration == -1L) {
                continue
            }
            if (activeBan.bannedAt + activeBan.duration <= System.currentTimeMillis()) {
                activeBan.active = false
                activeBan.removedReason = "Expired"

                ActivePunishments.removeBan(uuid)
                val userData = MongoDB.collection("users")
                val userDocument: UserData? = MongoSerializable.fromDocument<UserData>(
                    userData
                        .find(eq("uuid", uuid.toString()))
                        .firstOrNull())
                val user: UserData = userDocument ?: UserData.blank(uuid)
                val mongoBan = user.bans.find { it.active }
                mongoBan?.active = false
                mongoBan?.removedReason = "Expired"
                userData.replaceOne(eq("uuid", uuid.toString()), user.toDocument())
            }
        }

        delay("update-frequency".config(1) as Int, TimeUnit.SECONDS, true) {
            checkExpiredPunishments()
        }
    }

    override fun onDisable() {}
}
