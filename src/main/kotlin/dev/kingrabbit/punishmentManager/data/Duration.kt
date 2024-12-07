package dev.kingrabbit.punishmentManager.data

class Duration : Cloneable {

    var days: Int
    var hours: Int
    var minutes: Int
    var seconds: Int

    constructor(days: Int, hours: Int, minutes: Int, seconds: Int) {
        this.days = days
        this.hours = hours
        this.minutes = minutes
        this.seconds = seconds

        fix()
    }

    constructor(millis: Long) {
        var seconds = millis / 1000
        val days = seconds / 86400
        seconds -= days * 86400
        val hours = seconds / 3600
        seconds -= hours * 3600
        val minutes = seconds / 60
        seconds -= minutes * 60

        this.days = days.toInt()
        this.hours = hours.toInt()
        this.minutes = minutes.toInt()
        this.seconds = seconds.toInt()
    }

    /**
     * Parses a string into a duration.
     * The string should match the regex `(\d+d)?(\d+h)?(\d+m)?(\d+s)?` (AKA `1d2h3m4s`) and should not be empty.
     *
     * @param string The string to parse.
     * @throws IllegalArgumentException If the string is empty or does not match the regex.
     */
    constructor(string: String) {
        if (string.isEmpty()) throw IllegalArgumentException("Duration string cannot be empty: $string")
        val matcher = Regex("(\\d+d)?(\\d+h)?(\\d+m)?(\\d+s)?").matchEntire(string) ?: throw IllegalArgumentException("Invalid duration string: $string")
        val groups = matcher.groupValues.drop(1)

        var days = 0
        var hours = 0
        var minutes = 0
        var seconds = 0

        for (group in groups) {
            if (group.isEmpty()) continue
            val value = group.dropLast(1).toInt()
            when (group.last()) {
                'd' -> days = value
                'h' -> hours = value
                'm' -> minutes = value
                's' -> seconds = value
            }
        }

        this.days = days
        this.hours = hours
        this.minutes = minutes
        this.seconds = seconds

        fix()
    }

    private fun fix() {
        var seconds = this.seconds
        var minutes = this.minutes
        var hours = this.hours
        var days = this.days

        if (seconds >= 60) {
            minutes += seconds / 60
            seconds %= 60
        }

        if (minutes >= 60) {
            hours += minutes / 60
            minutes %= 60
        }

        if (hours >= 24) {
            days += hours / 24
            hours %= 24
        }

        this.days = days
        this.hours = hours
        this.minutes = minutes
        this.seconds = seconds
    }

    fun withDays(days: Int): Duration {
        this.days = days
        return this
    }

    fun withHours(hours: Int): Duration {
        this.hours = hours
        return this
    }

    fun withMinutes(minutes: Int): Duration {
        this.minutes = minutes
        return this
    }

    fun withSeconds(seconds: Int): Duration {
        this.seconds = seconds
        return this
    }

    fun toMillis(): Long {
        return (days * 86400 + hours * 3600 + minutes * 60 + seconds) * 1000L
    }

    override fun toString(): String {
        return buildString {
            if (days > 0) append("${days}d")
            if (hours > 0) append("${hours}h")
            if (minutes > 0) append("${minutes}m")
            if (seconds > 0) append("${seconds}s")

            if (isEmpty()) append("0s")
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Duration) return false

        if (days != other.days) return false
        if (hours != other.hours) return false
        if (minutes != other.minutes) return false
        if (seconds != other.seconds) return false

        return true
    }

    public override fun clone(): Duration {
        return Duration(days, hours, minutes, seconds)
    }

}