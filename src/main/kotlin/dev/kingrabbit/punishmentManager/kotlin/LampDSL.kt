package dev.kingrabbit.punishmentManager.kotlin

import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import revxrsal.commands.bukkit.actor.BukkitCommandActor
import revxrsal.commands.command.CommandActor
import revxrsal.commands.exception.CommandErrorException
import revxrsal.commands.exception.SendableException

class SendMiniException(override val message: String, private vararg val placeholders: Component = emptyArray()) : SendableException() {

    override fun sendTo(actor: CommandActor) {
        actor.sender.sendMini(message, *placeholders)
    }

}

fun commandSendMini(message: String): Nothing {
    throw SendMiniException(message)
}

fun commandError(message: String): Nothing {
    throw CommandErrorException(message)
}

fun commandSendMini(message: String, vararg placeholders: Component) {
    throw SendMiniException(message, *placeholders)
}

inline val CommandActor.sender: CommandSender get() = (this as BukkitCommandActor).sender()