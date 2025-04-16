package net.xtb7.chatReactions.util

import net.kyori.adventure.text.Component
import net.xtb7.chatReactions.ChatReactions
import org.bukkit.entity.Player
import kotlin.collections.iterator

class Messages {
    companion object {
        fun get(path: String, replace: Map<String, String> = mapOf<String, String>()) : Component {
            var string = ChatReactions.Companion.instance.config.getString(path).toString()
            for(entry in replace) {
                string = string.replace(entry.key, entry.value)
            }
            return ChatReactions.Companion.miniMessage.deserialize(string)
        }

        fun Player.sendMessage(message : Component) {
            ChatReactions.Companion.audiences.player(this).sendMessage(message)
        }
    }
}