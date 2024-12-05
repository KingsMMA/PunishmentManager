package dev.kingrabbit.punishmentManager.kotlin

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder

val miniMessage = MiniMessage.builder().build()

fun String.toComponent(): TextComponent = Component.text(this)

fun String.toMini(): Component = miniMessage.deserialize(this)

fun String.toMini(vararg placeholders: Component): Component {
    val components = placeholders.mapIndexed { i, it -> Placeholder.component(i.toString(), it) }.toTypedArray()
    return miniMessage.deserialize(this, *components)
}

fun Audience.sendMini(string: String) {
    this.sendMessage(string.toMini())
}

fun Audience.sendMini(message: String, vararg placeholders: Component) {
    this.sendMessage(message.toMini(*placeholders))
}