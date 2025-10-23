package com.attrsense.module.leetcode

/**
 *
 * @author zhangshuai
 * @date 2025/3/25 21:02
 * @description 加一
 */
class Code66 {
    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            val result=plusOne(intArrayOf(8,9,9))
            System.out.println("Code1打印：$result")
        }

        @JvmStatic
        fun plusOne(digits: IntArray): IntArray {

            val endIndex = digits.size - 1
            val endData = digits[endIndex]
            val newData = endData + 1
            return if(newData == 10){
                val newArray = IntArray(digits.size+1)
                newArray[newArray.size-1]=0
                for(index in newArray.size - 1 downTo 0){
                    if (digits[index-1] == 10){
                        newArray[index] = 0
                        newArray[index-1] = digits[index-1]+1
                    }
                }
                newArray
            }else{
                digits[endIndex] = newData
                digits
            }
        }
    }
}