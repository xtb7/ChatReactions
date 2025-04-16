package net.xtb7.chatReactions.util

import net.xtb7.chatReactions.ChatReactions
import org.bukkit.OfflinePlayer
import kotlin.use

class DBManager {
    companion object {
        const val WINS = "GamesWon"
        const val PLAYS = "GamesPlayed"
        const val ATTTEMPTS = "Attempts"

        fun ensureData(player: OfflinePlayer) {
            ChatReactions.connection.prepareStatement("INSERT OR IGNORE INTO data (UUID, GamesWon, GamesPlayed, Attempts) VALUES (?, 0, 0, 0)").use { statement ->
                statement.setString(1, player.uniqueId.toString())
                statement.executeUpdate()
            }
        }

        fun incrementData(player: OfflinePlayer, name : String, delta : Int) {
            ChatReactions.connection.prepareStatement("UPDATE data SET $name = $name + ? WHERE UUID = ?").use {
                it.setInt(1, delta)
                it.setString(2, player.uniqueId.toString())
                it.executeUpdate()
            }
        }

        fun getData(player: OfflinePlayer, name : String) : Int {
            ChatReactions.connection.prepareStatement("SELECT $name FROM data WHERE UUID = ?").use { statement ->
                statement.setString(1, player.uniqueId.toString())
                statement.executeQuery().use {
                    it.next()
                    return it.getInt(1)
                }
            }
        }
    }
}