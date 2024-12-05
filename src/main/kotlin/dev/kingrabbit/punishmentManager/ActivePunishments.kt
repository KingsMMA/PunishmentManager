package dev.kingrabbit.punishmentManager

import dev.kingrabbit.punishmentManager.data.BanData
import dev.kingrabbit.punishmentManager.data.MuteData
import java.util.*

object ActivePunishments {
    private val activeMutes = mutableMapOf<UUID, MuteData>()
    private val activeBans = mutableMapOf<UUID, BanData>()

    fun addMute(uuid: UUID, muteData: MuteData) {
        activeMutes[uuid] = muteData
    }

    fun removeMute(uuid: UUID) {
        activeMutes.remove(uuid)
    }

    fun addBan(uuid: UUID, banData: BanData) {
        activeBans[uuid] = banData
    }

    fun removeBan(uuid: UUID) {
        activeBans.remove(uuid)
    }

    fun isMuted(uuid: UUID): Boolean {
        return activeMutes.containsKey(uuid)
    }

    fun isBanned(uuid: UUID): Boolean {
        return activeBans.containsKey(uuid)
    }

    fun getMute(uuid: UUID): MuteData? {
        return activeMutes[uuid]
    }

    fun getBan(uuid: UUID): BanData? {
        return activeBans[uuid]
    }
}