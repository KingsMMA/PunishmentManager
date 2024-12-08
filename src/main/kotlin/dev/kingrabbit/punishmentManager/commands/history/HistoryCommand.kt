package dev.kingrabbit.punishmentManager.commands.history

import com.google.common.collect.HashMultimap
import com.mongodb.client.model.Filters.eq
import dev.kingrabbit.punishmentManager.data.Duration
import dev.kingrabbit.punishmentManager.data.UserData
import dev.kingrabbit.punishmentManager.kotlin.*
import gg.flyte.twilight.data.MongoDB
import gg.flyte.twilight.extension.asString
import gg.flyte.twilight.extension.glow
import gg.flyte.twilight.gui.GUI.Companion.openInventory
import gg.flyte.twilight.gui.gui
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import revxrsal.commands.annotation.Command
import revxrsal.commands.bukkit.annotation.CommandPermission
import java.util.*

object HistoryCommand {

    @Command("history chat")
    @CommandPermission("punishmentManager.history")
    fun historyChat(sender: CommandSender, target: OfflinePlayer) {
        val userData = MongoDB.collection("users")
            .find(eq("uuid", target.uniqueId.toString()))
            .firstOrNull()
            ?.let { MongoSerializable.fromDocument(it) as UserData? }
            ?: return sender.sendMini("messages.history.no-history".configString("<green>There is no history for <white><0></white>!",
                ), target.name!!.toMini())

        if (userData.kicks.isEmpty() && userData.mutes.isEmpty() && userData.bans.isEmpty())
            return sender.sendMini("messages.history.no-history".configString("<green>There is no history for <white><0></white>!",
                ), target.name!!.toMini())

        sender.sendMini("messages.history.header".configString("<gray>History for <white><0></white>:"), target.name!!.toMini())

        userData.kicks.forEach {
            sender.sendMini("messages.history.kick".configString("<gray>  - Kicked by <white><0></white> for <white><1></white> at <white><2></white>.",),
                it.kickedBy.toName().toMini(),
                Date(it.kickedAt).toMini(),
                (it.reason ?: "No reason provided").toMini()
            )
        }

        userData.mutes.forEach {
            val message: Component = if (it.duration != -1L) {
                "messages.history.mute.temporary".configString("<gray>  - Muted by <white><0></white> at <white><1></white> for <white><2></white>. Reason: <white><3></white>.")
                    .toMini(
                        it.mutedBy.toName().toMini(),
                        Date(it.mutedAt).toMini(),
                        Duration(it.duration).toString().toMini(),
                        (it.reason ?: "No reason provided").toMini()
                    )
            } else {
                "messages.history.permanent.mute".configString("<gray>  - Muted by <white><0></white> at <white><1></white>. Reason: <white><2></white>.")
                    .toMini(
                        it.mutedBy.toName().toMini(),
                        Date(it.mutedAt).toMini(),
                        (it.reason ?: "No reason provided.").toMini()
                    )
            }

            if (it.active) {
                sender.sendMessage(message)
            } else {
                sender.sendMini("<strikethrough>${message.asString()}</strikethrough> ${it.removedReason}")
            }
        }

        userData.bans.forEach {
            val message: Component = if (!it.ip) {
                if (it.duration != -1L) {
                    "messages.history.ban.temporary".configString("<gray>  - Banned by <white><0></white> at <white><1></white> for <white><2></white>. Reason: <white><3></white>.")
                        .toMini(
                            it.bannedBy.toName().toMini(),
                            Date(it.bannedAt).toMini(),
                            Duration(it.duration).toString().toMini(),
                            (it.reason ?: "No reason provided").toMini()
                        )
                } else {
                    "messages.history.ban.mute".configString("<gray>  - Banned by <white><0></white> at <white><1></white>. Reason: <white><2></white>.")
                        .toMini(
                            it.bannedBy.toName().toMini(),
                            Date(it.bannedAt).toMini(),
                            (it.reason ?: "No reason provided.").toMini()
                        )
                }
            } else "messages.history.history.entry.ipban".configString("<gray>  - IP banned by <white><0></white> at <white><1></white>. Reason: <white><2></white>.")
                .toMini(
                    it.bannedBy.toName().toMini(),
                    Date(it.bannedAt).toMini(),
                    (it.reason ?: "No reason provided.").toMini()
                )

            if (it.active) {
                sender.sendMessage(message)
            } else {
                sender.sendMini("<strikethrough>${message.asString()}</strikethrough> ${it.removedReason}")
            }
        }
    }

    @Command("history menu")
    @CommandPermission("punishmentManager.history")
    fun historyMenu(sender: Player, target: OfflinePlayer) {
        val userData = MongoDB.collection("users")
            .find(eq("uuid", target.uniqueId.toString()))
            .firstOrNull()
            ?.let { MongoSerializable.fromDocument(it) as UserData? }

        openHistoryMenu(sender, target, userData)
    }

    private fun openHistoryMenu(sender: Player, target: OfflinePlayer, userData: UserData?) {
        if (userData == null || (userData.kicks.isEmpty() && userData.mutes.isEmpty() && userData.bans.isEmpty()))
            return sender.openInventory(gui("${target.name}'s history".toMini()) {
                set(13, ItemStack(Material.BARRIER).apply {
                    itemMeta = itemMeta.apply {
                        itemName("<red>No history found".toMini())
                        lores(listOf("<gray>This player has no history.".toMini()))
                    }
                })

                onClick {
                    isCancelled = true
                }
            })

        sender.openInventory(gui("${target.name}'s history".toMini()) {
            set(11, ItemStack(Material.PAPER).apply {
                itemMeta = itemMeta.apply {
                    itemName("<green>Kicks".toMini())
                    if (userData.kicks.isEmpty()) lores(listOf("<gray>This user has never been kicked.".toMini()))
                    else lores(listOf("<gray>This user has been kicked <white>${userData.kicks.size}</white> times.".toMini(),
                        "<gray>Click to view kicks.".toMini()))
                }
            }) { openKicksMenu(sender, target, userData) }

            set(13, ItemStack(Material.BOOK).apply {
                itemMeta = itemMeta.apply {
                    itemName("<green>Mutes".toMini())
                    if (userData.mutes.isEmpty()) lores(listOf("<gray>This user has never been muted.".toMini()))
                    else lores(listOf("<gray>This user has been muted <white>${userData.mutes.size}</white> times.".toMini(),
                        "<gray>Click to view mutes.".toMini()))
                }
            }) { openMutesMenu(sender, target, userData) }

            set(15, ItemStack(Material.IRON_BARS).apply {
                itemMeta = itemMeta.apply {
                    itemName("<green>Bans".toMini())
                    if (userData.bans.isEmpty()) lores(listOf("<gray>This user has never been banned.".toMini()))
                    else lores(listOf("<gray>This user has been banned <white>${userData.bans.size}</white> times.".toMini(),
                        "<gray>Click to view bans.".toMini()))
                }
            }) { openBansMenu(sender, target, userData) }

            onClick {
                isCancelled = true
            }
        })
    }

    private fun openKicksMenu(sender: Player, target: OfflinePlayer, userData: UserData) {
        sender.openInventory(gui("${target.name}'s kicks".toMini()) {
            userData.kicks.forEachIndexed { index, kick ->
                set(index, ItemStack(Material.PAPER).apply {
                    itemMeta = itemMeta.apply {
                        itemName("<green>Kicked by <white>${kick.kickedBy.toName()}".toMini())
                        lores(listOf("<gray>Reason: <white>${kick.reason ?: "No reason provided"}".toMini(),
                            "<gray>At: <white>${Date(kick.kickedAt)}".toMini()))
                    }
                }) { isCancelled = true }
            }

            set(26, ItemStack(Material.ARROW).apply {
                itemMeta = itemMeta.apply {
                    itemName("<red>Back".toMini())
                }
            }) {
                openHistoryMenu(sender, target, userData)
            }

            onClick {
                isCancelled = true
            }
        })
    }

    private fun openMutesMenu(sender: Player, target: OfflinePlayer, userData: UserData) {
        sender.openInventory(gui("${target.name}'s mutes".toMini()) {
            userData.mutes.forEachIndexed { index, mute ->
                set(index, ItemStack(Material.BOOK).apply {
                    if (mute.active) glow()
                    itemMeta = itemMeta.apply {
                        itemName("<green>Muted by <white>${mute.mutedBy.toName()}".toMini())
                        lores(listOf("<gray>Reason: <white>${mute.reason ?: "No reason provided"}".toMini(),
                            "<gray>At: <white>${Date(mute.mutedAt)}".toMini(),
                            "<gray>Duration: <white>${if (mute.duration == -1L) "Permanent" else Duration(mute.duration).toString()}".toMini(),
                            if (mute.active) "<red>Active".toMini() else "<green>Removed: <white>${mute.removedReason}".toMini()))
                        attributeModifiers = HashMultimap.create()
                        addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                    }
                }) { isCancelled = true }
            }

            set(26, ItemStack(Material.ARROW).apply {
                itemMeta = itemMeta.apply {
                    itemName("<red>Back".toMini())
                }
            }) {
                openHistoryMenu(sender, target, userData)
            }

            onClick {
                isCancelled = true
            }
        })
    }

    private fun openBansMenu(sender: Player, target: OfflinePlayer, userData: UserData) {
        sender.openInventory(gui("${target.name}'s bans".toMini()) {
            userData.bans.forEachIndexed { index, ban ->
                set(index, ItemStack(if (ban.ip) Material.IRON_DOOR else Material.IRON_BARS).apply {
                    if (ban.active) glow()
                    itemMeta = itemMeta.apply {
                        itemName("<green>${if (ban.ip) "IP b" else "B"}anned by <white>${ban.bannedBy.toName()}".toMini())
                        lores(listOf("<gray>Reason: <white>${ban.reason ?: "No reason provided"}".toMini(),
                            "<gray>At: <white>${Date(ban.bannedAt)}".toMini(),
                            "<gray>Duration: <white>${if (ban.duration == -1L) "Permanent" else Duration(ban.duration).toString()}".toMini(),
                            if (ban.active) "<red>Active".toMini() else "<green>Removed: <white>${ban.removedReason}".toMini()))
                        attributeModifiers = HashMultimap.create()
                        addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                    }
                }) { isCancelled = true }
            }

            set(26, ItemStack(Material.ARROW).apply {
                itemMeta = itemMeta.apply {
                    itemName("<red>Back".toMini())
                }
            }) {
                openHistoryMenu(sender, target, userData)
            }

            onClick {
                isCancelled = true
            }
        })
    }

}