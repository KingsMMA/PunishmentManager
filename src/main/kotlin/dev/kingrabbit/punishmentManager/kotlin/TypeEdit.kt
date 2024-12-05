package dev.kingrabbit.punishmentManager.kotlin

import dev.kingrabbit.punishmentManager.config.ConfigManager

fun String.config(): Any? = ConfigManager.config.get(this)
fun String.config(default: Any): Any = ConfigManager.config.get(this) ?: default
fun String.configString(): String? = ConfigManager.config.getString(this)
fun String.configString(default: String): String = ConfigManager.config.getString(this) ?: default
