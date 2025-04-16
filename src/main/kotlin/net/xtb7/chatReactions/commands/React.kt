package net.xtb7.chatReactions.commands

import net.xtb7.chatReactions.ChatReactions
import net.xtb7.chatReactions.util.DBManager
import net.xtb7.chatReactions.util.Messages
import net.xtb7.chatReactions.util.Messages.Companion.sendMessage
import net.xtb7.chatReactions.util.Sounds
import net.xtb7.chatReactions.util.Sounds.Companion.playFor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.time.Instant.now

class React : CommandExecutor {
    override fun onCommand(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
        val player = (p0 as? Player) ?: return true
        val playerAttempt = p3.joinToString(" ")
        if(playerAttempt == ChatReactions.Companion.correctChatReaction) {
            if (!ChatReactions.userAttempts.contains(player.uniqueId)) DBManager.incrementData(player, DBManager.PLAYS, 1)
            DBManager.incrementData(player, DBManager.WINS, 1)
            val message = Messages.Companion.get("messages.winner-announcement", mapOf(
                "{player}" to player.name,
                "{seconds}" to ((now().toEpochMilli() - ChatReactions.chatReactTime).toFloat() / 1000).toString()
            ))
            for(onlinePlayer in Bukkit.getOnlinePlayers()) {
                onlinePlayer.sendMessage(message)
                val soundName = if(onlinePlayer != player) "sounds.other-player-won" else "sounds.self-player-won"
                Sounds.Companion.getSound(soundName).playFor(player)
            }
            Bukkit.dispatchCommand(ChatReactions.Companion.console, ChatReactions.Companion.instance.config.getString("reward-command").toString().replace("{player}", player.name))
            ChatReactions.correctChatReaction = null
            DBManager.incrementData(player, DBManager.ATTTEMPTS, 1)
        }
        else {
            val messagePath = if(ChatReactions.Companion.correctChatReaction == null) "messages.no-reaction" else {
                if (!ChatReactions.userAttempts.contains(player.uniqueId)) DBManager.incrementData(player, DBManager.PLAYS, 1)
                ChatReactions.userAttempts[player.uniqueId] = ChatReactions.userAttempts.getOrDefault(player.uniqueId, 0) + 1
                DBManager.incrementData(player, DBManager.ATTTEMPTS, 1)
                "messages.incorrect-reaction"
            }
            player.sendMessage(Messages.Companion.get(messagePath))
            Sounds.Companion.getSound("sounds.command-failed").playFor(player)
        }
        return true
    }
}