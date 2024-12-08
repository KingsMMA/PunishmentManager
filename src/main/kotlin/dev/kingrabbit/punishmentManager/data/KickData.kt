package dev.kingrabbit.punishmentManager.data

import dev.kingrabbit.punishmentManager.kotlin.MongoSerializable
import java.util.UUID

class KickData(
    val reason: String?,
    val kickedBy: UUID,
    val kickedAt: Long,
) : MongoSerializable
