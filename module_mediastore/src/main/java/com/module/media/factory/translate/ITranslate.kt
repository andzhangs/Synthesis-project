package com.module.media.factory.translate

import com.orhanobut.logger.Logger
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

/**
 *
 * @author zhangshuai
 * @date 2024/8/7 15:01
 * @description 翻译抽象类
 */
interface ITranslate {

    suspend fun getChineseText(input: String, filePath: String): Triple<String, String, String>

    /**
     * 检测是否为中文
     */
    fun String.isChinese(): Boolean {
        val regex = "[\u4e00-\u9fa5]" // 中文 Unicode 范围
        return this.matches(Regex(regex))
    }

    /**
     * 发起网络请求
     */
    suspend fun getNetWork(urlWithParams: String): JSONObject {
        //拼接字符串后，转成MD5，并发起网络翻译
        val connection = URL(urlWithParams).openConnection().apply {
            setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
            setRequestProperty("Connection", "keep-alive")

        }
        val reader = BufferedReader(InputStreamReader(connection.getInputStream()))
        var line: String?
        val response = java.lang.StringBuilder()
        while (reader.readLine().also { line = it } != null) {
            response.append(line)
        }

        val result=response.toString()

//        if (BuildConfig.DEBUG) {
//            Log.i("print_logs", "translateChinese: $result")
//        }
        Logger.json(result)
        return JSONObject(result)
    }
}