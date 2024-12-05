package dev.kingrabbit.punishmentManager

import dev.kingrabbit.punishmentManager.commands.KickCommand
import dev.kingrabbit.punishmentManager.config.ConfigManager
import gg.flyte.twilight.event.event
import gg.flyte.twilight.twilight
import org.bukkit.plugin.java.JavaPlugin
import revxrsal.commands.bukkit.BukkitLamp

class PunishmentManager : JavaPlugin() {

    override fun onEnable() {
        val twilight = twilight(this) {
            env {
                useDifferentEnvironments = false
            }
        }

        val commandHandler = BukkitLamp.builder(this).build()
        commandHandler.register(
            KickCommand
        )

        ConfigManager.initialise(config, this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
