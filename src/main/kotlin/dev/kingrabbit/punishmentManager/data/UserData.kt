package dev.kingrabbit.punishmentManager.data

import dev.kingrabbit.punishmentManager.kotlin.MongoSerializable
import gg.flyte.twilight.data.Id
import java.util.UUID

data class UserData(
    val uuid: UUID,
    val mutes: MutableList<MuteData>,
    val bans: MutableList<BanData>,
) : MongoSerializable {
}