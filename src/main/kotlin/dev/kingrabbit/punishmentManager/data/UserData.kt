package dev.kingrabbit.punishmentManager.data

import dev.kingrabbit.punishmentManager.kotlin.MongoSerializable
import java.util.UUID

data class UserData(
    val uuid: UUID,
    val kicks: MutableList<KickData>,
    val mutes: MutableList<MuteData>,
    val bans: MutableList<BanData>,
) : MongoSerializable {
}