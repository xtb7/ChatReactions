package net.xtb7.chatReactions.commands

import net.xtb7.chatReactions.ChatReactions
import net.xtb7.chatReactions.ChatTasks
import net.xtb7.chatReactions.util.DBManager
import net.xtb7.chatReactions.util.Messages
import net.xtb7.chatReactions.util.Sounds
import net.xtb7.chatReactions.util.Sounds.Companion.playFor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.UUID

class ChatReactionsCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String?>?): Boolean {
        when(args?.getOrNull(0)) {
            "force" -> {
                if (!sender.hasPermission("chatreactions.force")) return true
                if (ChatReactions.currentChatReactionTaskID != null) {
                    Bukkit.getScheduler().cancelTask(ChatReactions.currentChatReactionTaskID!!)
                }
                ChatTasks().runTask(ChatReactions.instance)
            }

            "stats" -> {
                val statsPlayerIdentifier = args.getOrNull(1)
                val statsPlayer = if (statsPlayerIdentifier != null && sender.hasPermission("chatreactions.stats.other")) {
                    if (statsPlayerIdentifier.length > 16) Bukkit.getOfflinePlayer(UUID.fromString(statsPlayerIdentifier)) else Bukkit.getOfflinePlayer(statsPlayerIdentifier)
                }
                else if (sender.hasPermission("chatreactions.stats.self")) sender as? Player ?: return true
                else return true

                try {
                    ChatReactions.audiences.sender(sender).sendMessage(Messages.get("messages.stats", mapOf(
                        "{player}" to statsPlayer.name.toString(),
                        "{won}" to DBManager.getData(statsPlayer, DBManager.WINS).toString(),
                        "{played}" to DBManager.getData(statsPlayer, DBManager.PLAYS).toString(),
                        "{attempts}" to DBManager.getData(statsPlayer, DBManager.ATTTEMPTS).toString(),
                    )))
                    if (sender is Player) Sounds.getSound("sounds.command-succeed-generic").playFor(sender)
                }
                catch (_ : Exception) {
                    ChatReactions.audiences.sender(sender).sendMessage(Messages.get("messages.never-played"))
                    if (sender is Player) Sounds.getSound("sounds.command-failed").playFor(sender)
                }
            }

            "reload" -> {
                if (!sender.hasPermission("chatreactions.reload")) return true
                ChatReactions.reloadConfig()
                ChatReactions.audiences.sender(sender).sendMessage(Messages.get("messages.reloaded"))
                if(sender is Player) Sounds.getSound("sounds.command-succeed-generic").playFor(sender)
            }
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String?>?): List<String?>? {
        val autocompletions = ArrayList<String>()
        when(args?.size) {
            1 -> {
                if (sender.hasPermission("chatreactions.force")) autocompletions.add("force")
                if (sender.hasPermission("chatreactions.reload")) autocompletions.add("reload")
                if (sender.hasPermission("chatreactions.stats.self") || sender.hasPermission("chatreactions.stats.other")) autocompletions.add("stats")
            }

            2 -> if(args[0] == "stats" && sender.hasPermission("chatreactions.stats.other")) {
                for (player in Bukkit.getOnlinePlayers()) autocompletions.add(player.name)
            }
            else -> return null
        }
        return autocompletions
    }
}