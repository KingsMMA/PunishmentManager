package dev.kingrabbit.punishmentManager.config

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

object ConfigManager {

    lateinit var config: FileConfiguration
    lateinit var plugin: JavaPlugin

    fun initialise(config: FileConfiguration, plugin: JavaPlugin) {
        this.config = config
        this.plugin = plugin

        config.options().copyDefaults(true)
        plugin.saveDefaultConfig()

    }

}