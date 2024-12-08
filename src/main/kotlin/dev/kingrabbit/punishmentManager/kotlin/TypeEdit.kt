package dev.kingrabbit.punishmentManager.kotlin

import dev.kingrabbit.punishmentManager.config.ConfigManager
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.inventory.meta.ItemMeta
import java.util.Date
import java.util.UUID

fun String.config(): Any? = ConfigManager.config.get(this)
fun String.config(default: Any): Any = this.config() ?: default
fun String.configString(): String? = ConfigManager.config.getString(this)?.replace("\\n", "\n")
fun String.configString(default: String): String = this.configString() ?: default

fun UUID.toName(): String = Bukkit.getOfflinePlayer(this).name ?: "CONSOLE"

fun Date.toMini(): Component = this.toString().toMini()

fun ItemMeta.lores(lore: List<Component>): ItemMeta {
    this.lore(lore.map { it.decoration(TextDecoration.ITALIC, false) })
    return this
}
