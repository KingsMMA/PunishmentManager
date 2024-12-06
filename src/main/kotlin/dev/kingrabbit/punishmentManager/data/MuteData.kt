package dev.kingrabbit.punishmentManager.data

import gg.flyte.twilight.data.MongoSerializable
import java.util.UUID

class MuteData(
    val reason: String?,
    val duration: Long,
    val mutedBy: UUID,
    val mutedAt: Long,
    var active: Boolean,
    var removedReason: String?,
) : MongoSerializable {
}