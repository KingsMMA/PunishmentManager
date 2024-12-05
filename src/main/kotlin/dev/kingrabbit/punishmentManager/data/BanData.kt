package dev.kingrabbit.punishmentManager.data

import dev.kingrabbit.punishmentManager.kotlin.MongoSerializable
import java.util.UUID

class BanData(
    val reason: String,
    val duration: Long,
    val mutedBy: UUID,
    val mutedAt: Long,
    var active: Boolean,
    var removedReason: String?,
) : MongoSerializable {
}