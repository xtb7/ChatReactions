package net.xtb7.chatReactions

import net.xtb7.chatReactions.util.Messages
import net.xtb7.chatReactions.util.Messages.Companion.sendMessage
import net.xtb7.chatReactions.util.Sounds
import net.xtb7.chatReactions.util.Sounds.Companion.playFor
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import java.time.Instant.now
import kotlin.random.Random

class ChatTasks : BukkitRunnable() {
    companion object {
        fun parseRange(path : String) : IntRange {
            val section = ChatReactions.instance.config.getConfigurationSection(path) ?: return IntRange.EMPTY
            return section.getInt("start")..section.getInt("end")
        }

        fun getDelay() : Long {
            var waitPeriod = ChatReactions.instance.config.getInt("delay.fixed-delay")
            if (waitPeriod < 1) waitPeriod = parseRange("delay.random-delay-range").random()
            return waitPeriod * 20L
        }
    }

    override fun run() {
        ChatReactions.chatReactTime = now().toEpochMilli()
        ChatReactions.userAttempts.clear()
        while (true) {
            val message = when (Random.nextInt(1, 3)) {
                1 -> {
                    if (!ChatReactions.instance.config.getBoolean("text-reactions.enabled")) continue
                    val reactions = ChatReactions.instance.config.getStringList("text-reactions.reactions")
                    val reaction = reactions.random()
                    ChatReactions.correctChatReaction = reaction
                    Messages.get("messages.new-game", mapOf(
                        "{command}" to "/re $reaction"
                    ))
                }

                2 -> {
                    val (first, second, operator) = when (Random.nextInt(1, 5)) {
                        1 -> {
                            if (!ChatReactions.instance.config.getBoolean("math-reactions.addition.enabled")) continue
                            val first = parseRange("math-reactions.addition.number1-range").random()
                            val second = parseRange("math-reactions.addition.number2-range").random()
                            ChatReactions.correctChatReaction = "${first + second}"
                            Triple(first, second, "+")
                        }

                        2 -> {
                            if (!ChatReactions.instance.config.getBoolean("math-reactions.subtraction.enabled")) continue
                            val first = parseRange("math-reactions.subtraction.number1-range").random()
                            val second = parseRange("math-reactions.subtraction.number2-range").random()
                            ChatReactions.correctChatReaction = "${first - second}"
                            Triple(first, second, "-")
                        }

                        3 -> {
                            if (!ChatReactions.instance.config.getBoolean("math-reactions.multiplication.enabled")) continue
                            val first = parseRange("math-reactions.multiplication.number1-range").random()
                            val second = parseRange("math-reactions.multiplication.number2-range").random()
                            ChatReactions.correctChatReaction = "${first * second}"
                            Triple(first, second, "x")
                        }

                        4 -> {
                            if (!ChatReactions.instance.config.getBoolean("math-reactions.division.enabled")) continue
                            val first = parseRange("math-reactions.division.answer-range").random()
                            var second = parseRange("math-reactions.division.divisor-range").random()
                            if(second == 0) second = 1
                            ChatReactions.correctChatReaction = "$first"
                            Triple(first * second, second, "/")
                        }

                        else -> return
                    }

                    Messages.get("messages.new-game", mapOf(
                        "{command}" to "/re $first $operator $second"
                    ))
                }
                else -> return
            }
            val sound = Sounds.getSound("sounds.new-game-notification")
            for (player in Bukkit.getOnlinePlayers()) {
                player.sendMessage(message)
                sound.playFor(player)
            }


            val timeout = ChatReactions.instance.config.getInt("timeout")
//            println("t: $timeout")
            if (timeout > 0) {
                val timeoutMessage = Messages.get("messages.timeout")
                val timeoutSound = Sounds.getSound("sounds.timeout")
                Bukkit.getScheduler().runTaskLater(ChatReactions.instance, Runnable {
//                    println(ChatReactions.currentChatReactionTaskID)
//                    println(taskId)
                    if(this.isCancelled || ChatReactions.currentChatReactionTaskID != this.taskId) return@Runnable
                    ChatReactions.correctChatReaction = null
                    for (player in Bukkit.getOnlinePlayers()) {
                        player.sendMessage(timeoutMessage)
                        timeoutSound.playFor(player)
                    }
                }, timeout * 20L)
            }

            ChatReactions.currentChatReactionTaskID = ChatTasks().runTaskLater(ChatReactions.instance, getDelay()).taskId
            break
        }
    }
}