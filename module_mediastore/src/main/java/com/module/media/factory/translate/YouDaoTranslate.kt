package com.module.media.factory.translate

import android.util.Log
import com.module.media.BuildConfig
import java.security.MessageDigest
import java.util.Calendar
import java.util.TimeZone
import java.util.UUID

/**
 *
 * @author zhangshuai
 * @date 2024/8/7 15:02
 * @description 有道翻译
 * https://ai.youdao.com/DOCSIRMA/html/trans/api/wbfy/index.html
 */
class YouDaoTranslate : ITranslate {

    companion object {
        private const val BASE_URL = "https://openapi.youdao.com/api"

        private const val APP_KEY = "0d5699b42bd96fff"

        private const val SECRET = "ME5PpLm8UCMfij5xFyVAm8djZn7RZRml"

        private const val SIGN_TYPE = "v3"
    }

    override suspend fun getChineseText(input: String, filePath: String): Triple<String, String, String> {
        return if (input.isChinese()) {  //本身就是中文
            Triple(input, input, filePath)
        }else {
//            withContext(Dispatchers.Default) {
//                delay(1000L)

                try {
                    val salt = UUID.randomUUID().toString()
                    val currentTime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis /1000
                    val sign = convertSha256(APP_KEY + input + salt + currentTime + SECRET)

                    val urlWithParams = "$BASE_URL?q=${input}&from=auto&to=zh-CHS&appKey=$APP_KEY&salt=${salt}&sign=$sign&signType=${SIGN_TYPE}&curtime=${currentTime}"

                    val jsonObject=getNetWork(urlWithParams)

                    val errorCode = jsonObject.getString("errorCode")
                    if (errorCode=="0") {
                        val transResultObj = jsonObject.getJSONArray("translation")
                        val dst = transResultObj.getString(0)
                        Triple(input, dst, filePath)
                    }else{
                        Triple(input, "", filePath)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    if (BuildConfig.DEBUG) {
                        Log.e("print_logs", "YouDaoTranslate:: $e")
                    }
                    Triple(input, "", filePath)
                }
//            }
        }
    }

    /**
     * 将拼接字符串转换为MD5
     */
    private fun convertSha256(inputStr: String): String {
        // 创建 MessageDigest 对象，指定使用 MD5 算法
        val md5 = MessageDigest.getInstance("SHA-256")

        // 将输入字符串转换为字节数组并计算摘要
        val messageDigest = md5.digest(inputStr.toByteArray())

        // 将摘要转换为十六进制字符串
        val sb = StringBuilder()
        messageDigest.forEach {
            sb.append(String.format("%02x", it))
        }
        return sb.toString()
    }
}