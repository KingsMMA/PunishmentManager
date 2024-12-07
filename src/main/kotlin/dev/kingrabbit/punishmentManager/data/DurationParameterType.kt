package dev.kingrabbit.punishmentManager.data

import org.bukkit.command.CommandSender
import revxrsal.commands.autocomplete.SuggestionProvider
import revxrsal.commands.bukkit.actor.BukkitCommandActor
import revxrsal.commands.exception.CommandErrorException
import revxrsal.commands.node.ExecutionContext
import revxrsal.commands.parameter.ParameterType
import revxrsal.commands.stream.MutableStringStream
import java.util.regex.Pattern

class DurationParameterType : ParameterType<BukkitCommandActor, Duration> {

    override fun parse(input: MutableStringStream, context: ExecutionContext<BukkitCommandActor>): Duration {
        val durationStr = input.readString()
        try {
            return Duration(durationStr)
        } catch (e: IllegalArgumentException) {
            throw CommandErrorException("Invalid duration: $durationStr")
        }
    }

    override fun defaultSuggestions(): SuggestionProvider<BukkitCommandActor> {
        return SuggestionProvider {
            context: ExecutionContext<BukkitCommandActor> ->
                    suggestions(context)

        }
    }

    private fun suggestions(context: ExecutionContext<BukkitCommandActor>): List<String> {
        val input = context.input().peekRemaining().split(Pattern.compile(" "), 3)[2]
        if (input.isEmpty()) {
            return listOf("1d2h3m4s", "1d", "1h", "1m", "1s")
        }

        val duration: Duration = try {
            Duration(input)
        } catch (e: IllegalArgumentException) {
            return listOf("§cInvalid duration: $input")
        }

        val suggestions = mutableListOf<String>()
        if (duration.days == 0) suggestions.add(duration.clone().withDays(1).toString())
        if (duration.hours == 0) suggestions.add(duration.clone().withHours(1).toString())
        if (duration.minutes == 0) suggestions.add(duration.clone().withMinutes(1).toString())
        if (duration.seconds == 0) suggestions.add(duration.clone().withSeconds(1).toString())
        return suggestions
    }

}