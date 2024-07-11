package com.example.acra

import java.io.File

/**
 *
 * @author zhangshuai
 * @date 2024/7/11 10:31
 * @description 文件类扩展
 *
 */

fun File.create(): File {
    if (!this.exists()) {
        this.mkdirs()
    }
    return this
}