package net.xtb7.chatReactions

import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.minimessage.MiniMessage
import net.xtb7.chatReactions.bstats.Metrics
import net.xtb7.chatReactions.commands.ChatReactionsCommand
import net.xtb7.chatReactions.commands.React
import org.bukkit.Bukkit
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.util.UUID

class ChatReactions : JavaPlugin() {
    companion object {
        lateinit var instance : ChatReactions
        lateinit var audiences : BukkitAudiences
        lateinit var console : ConsoleCommandSender
        lateinit var connection : Connection
        val miniMessage : MiniMessage = MiniMessage.miniMessage()
        var correctChatReaction : String? = null
        var chatReactTime = 0L
        val userAttempts = HashMap<UUID, Int>()
        var currentChatReactionTaskID : Int? = null

        fun reloadConfig() {
            instance.reloadConfig()
        }
    }

    override fun onEnable() {
        instance = this
        audiences = BukkitAudiences.create(this)
        console = Bukkit.getConsoleSender()
        if (!File(dataFolder, "config.yml").exists()) saveResource("config.yml", false)
        var dbPath = config.getString("database-path")
        if (dbPath == "null") {
            saveResource("stats.db", false)
            config.set("database-path", "${dataFolder.absolutePath}/stats.db")
            connection = DriverManager.getConnection("jdbc:sqlite:${config.getString("database-path")}")
            saveConfig()
        }
        else connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")
        getCommand("react")?.setExecutor(React())
        val commandHandler = ChatReactionsCommand()
        getCommand("chatreactions")?.setExecutor(commandHandler)
        getCommand("chatreactions")?.tabCompleter = commandHandler
        val listener = Listener()
        listener.runTaskTimer(this, 0L, 144000L)
        server.pluginManager.registerEvents(listener, this)
        if(config.getBoolean("bstats-enabled")) Metrics(this, 25474)
        logger.info("ChatReactions ${description.version} Enabled!")
    }

    override fun onDisable() {
        connection.close()
        logger.info("ChatReactions ${description.version} Disabled!")
    }
}
