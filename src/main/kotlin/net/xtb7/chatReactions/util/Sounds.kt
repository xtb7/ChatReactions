package net.xtb7.chatReactions.util

import net.xtb7.chatReactions.ChatReactions
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.lang.Exception

class Sounds(val sound : Sound, val volume : Float, val pitch : Float) {
    companion object {
        fun Sounds.playFor(player: Player) {
            player.playSound(player.location, this.sound, this.volume, this.pitch)
        }

        fun getSound(name: String) : Sounds {
            try {
                val section = ChatReactions.instance.config.getConfigurationSection(name)!!
                return Sounds(Class.forName("org.bukkit.Sound").getDeclaredField(section.getString("name")!!).get(null) as Sound, section.getDouble("volume").toFloat(), section.getDouble("pitch").toFloat())
            }
            catch (_ : Exception) {
                throw IllegalArgumentException("Cannot find the sound $name!")
            }
        }
    }
}