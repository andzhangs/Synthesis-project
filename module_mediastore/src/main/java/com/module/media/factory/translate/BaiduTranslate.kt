package com.module.media.factory.translate

import android.util.Log
import com.module.media.BuildConfig
import kotlinx.coroutines.delay
import java.security.MessageDigest

/**
 *
 * @author zhangshuai
 * @date 2024/8/7 15:02
 * @description 百度翻译
 * https://fanyi-api.baidu.com/product/113
 */
class BaiduTranslate : ITranslate {

    companion object {
        private const val BASE_URL = "https://fanyi-api.baidu.com/api/trans/vip/translate"

        private const val APP_ID = "20210520000835483"

        private const val SECRET = "vm6j6GCPhSlHxoOV51fb"
    }

    override suspend fun getChineseText(input: String, filePath: String): Triple<String, String, String> {
        return if (input.isChinese()) {  //本身就是中文
            Triple(input, input, filePath)
        }else{
            delay(1000L)

            try {
                val salt = System.currentTimeMillis().toString()
                val md5Sign = convertMd5(APP_ID + input + salt + SECRET)

                val urlWithParams = "$BASE_URL?q=${input}&from=auto&to=zh&appid=$APP_ID&salt=${salt}&sign=$md5Sign"

                val jsonObject = getNetWork(urlWithParams)
                val transResult = jsonObject.getJSONArray("trans_result")
                val transResultObj = transResult.getJSONObject(0)
                val dst = transResultObj.getString("dst")

                Triple(input, dst, filePath)
            } catch (e: Exception) {
                e.printStackTrace()
                if (BuildConfig.DEBUG) {
                    Log.e("print_logs", "BaiduTranslate:: $e")
                }
                Triple("", "", "")
            }
        }
    }

    /**
     * 将拼接字符串转换为MD5
     */
    private fun convertMd5(inputStr: String): String {
        // 创建 MessageDigest 对象，指定使用 MD5 算法
        val md5 = MessageDigest.getInstance("MD5")

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