package com.module.ktor.http

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get

/**
 *
 * @author zhangshuai
 * @date 2023/8/17 16:53
 * @mark 自定义类描述
 */
class CioHttp {
    companion object {
        @JvmStatic
        suspend fun main(args: Array<String>) {
            val client = HttpClient(CIO)
            val response=client.get("https://ktor.io/")
            println("输出：${response.status}")
            client.close()
        }
    }
}