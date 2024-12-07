package dev.kingrabbit.punishmentManager.kotlin

import dev.kingrabbit.punishmentManager.config.ConfigManager

fun String.config(): Any? = ConfigManager.config.get(this)
fun String.config(default: Any): Any = this.config() ?: default
fun String.configString(): String? = ConfigManager.config.getString(this)?.replace("\\n", "\n")
fun String.configString(default: String): String = this.configString() ?: default
