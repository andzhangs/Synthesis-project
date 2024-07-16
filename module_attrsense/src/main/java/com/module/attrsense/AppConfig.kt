package com.module.attrsense

import androidx.annotation.Keep

/**
 *
 * @author zhangshuai
 * @date 2024/7/16 17:16
 * @description 自定义类描述
 */
class AppConfig {

    init {
        println("Init AppConfig successes.")
    }

    @Keep
    fun getContent(){
        println("AppConfig getContent.")
    }

    fun getContent12(){
        println("AppConfig getContent12.")
    }
}