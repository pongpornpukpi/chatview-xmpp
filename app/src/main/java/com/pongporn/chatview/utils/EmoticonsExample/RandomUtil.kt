package com.pongporn.chatview.utils.EmoticonsExample

import java.util.*

class RandomUtil {
    /**
     * Generates the random between two given integers.
     */
    fun generateRandomBetween(start: Int, end: Int): Int {

        val random = Random()
        var rand = random.nextInt(Integer.MAX_VALUE - 1) % end

        if (rand < start) {
            rand = start
        }
        return rand
    }
}