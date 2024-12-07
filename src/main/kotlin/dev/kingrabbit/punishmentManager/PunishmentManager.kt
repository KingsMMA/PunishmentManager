package dev.kingrabbit.punishmentManager

import com.mongodb.client.model.Filters.eq
import dev.kingrabbit.punishmentManager.commands.KickCommand
import dev.kingrabbit.punishmentManager.commands.MuteCommand
import dev.kingrabbit.punishmentManager.commands.TempMuteCommand
import dev.kingrabbit.punishmentManager.config.ConfigManager
import dev.kingrabbit.punishmentManager.data.Duration
import dev.kingrabbit.punishmentManager.data.DurationParameterType
import dev.kingrabbit.punishmentManager.data.UserData
import dev.kingrabbit.punishmentManager.kotlin.MongoSerializable
import dev.kingrabbit.punishmentManager.kotlin.configString
import dev.kingrabbit.punishmentManager.kotlin.sendMini
import dev.kingrabbit.punishmentManager.listeners.ChatListener
import gg.flyte.twilight.data.MongoDB
import gg.flyte.twilight.event.event
import gg.flyte.twilight.scheduler.delay
import gg.flyte.twilight.scheduler.repeat
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
            KickCommand,
            TempMuteCommand,
            MuteCommand
        )

        ConfigManager.initialise(config, this)

        Bukkit.getPluginManager().registerEvents(ChatListener, this)

        MongoDB.collection("users").find()
            .forEach { document: Document ->
                run {
                    val userData = MongoSerializable.fromDocument<UserData>(document)
                    if (userData != null) {
                        userData.mutes.forEach() { muteData ->
                            if (muteData.active) {
                                ActivePunishments.addMute(userData.uuid, muteData)
                            }
                        }
                        userData.bans.forEach() { banData ->
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
                val user: UserData = userDocument ?: UserData(uuid, mutableListOf(), mutableListOf())
                val activeMute = user.mutes.find { it.active }
                activeMute?.active = false
                activeMute?.removedReason = "Expired"
                userData.replaceOne(eq("uuid", uuid.toString()), user.toDocument())

                val player = Bukkit.getOfflinePlayer(uuid)
                player.player?.sendMini(
                    "messages.mute.receiver.expired".configString("<red>Your mute has expired!"))
            }
        }

        delay(1, TimeUnit.SECONDS, true) {
            checkExpiredPunishments()
        }
    }

    override fun onDisable() {}
}
