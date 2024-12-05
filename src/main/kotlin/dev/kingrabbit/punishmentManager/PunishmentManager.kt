package dev.kingrabbit.punishmentManager

import dev.kingrabbit.punishmentManager.commands.KickCommand
import dev.kingrabbit.punishmentManager.commands.MuteCommand
import dev.kingrabbit.punishmentManager.config.ConfigManager
import dev.kingrabbit.punishmentManager.data.UserData
import dev.kingrabbit.punishmentManager.kotlin.MongoSerializable
import dev.kingrabbit.punishmentManager.listeners.ChatListener
import gg.flyte.twilight.data.MongoDB
import gg.flyte.twilight.event.event
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

        val commandHandler = BukkitLamp.builder(this).build()
        commandHandler.register(
            KickCommand,
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
    }

    override fun onDisable() {}
}
