package dev.kingrabbit.punishmentManager.data

import gg.flyte.twilight.data.Id
import gg.flyte.twilight.data.MongoSerializable
import java.util.UUID

data class UserData(
    @field:Id val uuid: UUID,
    val mutes: MutableList<MuteData>,
    val bans: MutableList<BanData>,
) : MongoSerializable {
}