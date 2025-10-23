package com.attrsense.module.leetcode

import androidx.annotation.IntegerRes
import com.attrsense.module.leetcode.Code66.Companion.plusOne

/**
 *
 * @author zhangshuai
 * @date 2025/10/23 19:01
 * @description 两数之和
 */
class Code1 {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val target = 26
            val nums = intArrayOf(2, 7, 11, 15)
            val map = mutableMapOf<Int, Int>()
            val resultArray = intArrayOf(-1, -1)

            nums.forEachIndexed { index, value ->
                val diff = target - value
                map.get(diff)?.let { mapIndex ->
                    resultArray[0] = mapIndex
                    resultArray[1] = index
                } ?: map.put(value, index)
            }

            System.out.println("打印：${resultArray[0]}, ${resultArray[1]}");
        }
    }
}