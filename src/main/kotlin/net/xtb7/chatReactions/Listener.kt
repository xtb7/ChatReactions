package net.xtb7.chatReactions

import com.google.gson.JsonParser
import net.kyori.adventure.text.Component
import net.xtb7.chatReactions.util.DBManager
import net.xtb7.chatReactions.util.Messages
import net.xtb7.chatReactions.util.Messages.Companion.sendMessage
import net.xtb7.chatReactions.util.Sounds
import net.xtb7.chatReactions.util.Sounds.Companion.playFor
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scheduler.BukkitRunnable
import java.net.HttpURLConnection
import java.net.URI

class Listener : Listener, BukkitRunnable() {
    var updateMessage : Component? = null

    @EventHandler
    fun onPlayerJoin(e : PlayerJoinEvent) {
        if(e.player.isOp && isUpdateAvailable()) {
            ChatReactions.audiences.player(e.player).sendMessage(updateMessage!!)
            Sounds.getSound("sounds.command-succeed-generic").playFor(e.player)
        }
        DBManager.ensureData(e.player)
    }

    fun isUpdateAvailable() : Boolean {
        try {
            val connection = URI.create("https://api.github.com/repos/xtb7/chatreactions/releases/latest").toURL().openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()
            assert(connection.responseCode == 200)
            val newVersion = JsonParser.parseReader(connection.inputStream.bufferedReader()).asJsonObject.get("name").asString
            val currentVersion = ChatReactions.instance.description.version
            if(newVersion != currentVersion) {
                if (updateMessage == null) {
                    updateMessage = Messages.get("messages.new-version", mapOf(
                        "{new-version}" to newVersion,
                        "{old-version}" to currentVersion
                    ))
                }
                ChatReactions.audiences.console().sendMessage(updateMessage!!)
                return true
            }
        }
        catch (_ : Exception) {
            ChatReactions.audiences.console().sendMessage(Messages.get("messages.no-internet"))
        }
        return false
    }

    override fun run() {
        if(isUpdateAvailable()) for (player in Bukkit.getOnlinePlayers()) if (player.isOp) player.sendMessage(Messages.get("messages.new-version"))
    }
}