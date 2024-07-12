package com.example.acra

import android.util.JsonReader
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader

/**
 *
 * @author zhangshuai
 * @date 2024/7/12 10:18
 * @description 自定义类描述
 */
object JsonUtils {

    fun getJsonObj(filePath: String): String? {

        File(filePath).apply {
            if (this.exists() && this.isFile && !this.isDirectory) {
                var logPath = ""
                val fileInputStream = FileInputStream(filePath)
                val jsonReader = JsonReader(InputStreamReader(fileInputStream, "UTF-8"))
                jsonReader.beginObject()

                while (jsonReader.hasNext()) {
                    when (jsonReader.nextName()) {
                        "logPath" -> {
                            logPath=jsonReader.nextString()
                            if (BuildConfig.DEBUG) {
                                Log.i("print_logs", "logPath = $logPath ")
                            }
                        }

//                        "time" -> {
//                            if (BuildConfig.DEBUG) {
//                                Log.i("print_logs", "time = ${jsonReader.nextString()} ")
//                            }
//                        }
//
//                        "appVersion" -> {
//                            if (BuildConfig.DEBUG) {
//                                Log.i("print_logs", "appVersion = ${jsonReader.nextString()} ")
//                            }
//                        }
//
//                        "mobileDevice" -> {
//                            if (BuildConfig.DEBUG) {
//                                Log.i("print_logs", "mobileDevice = ${jsonReader.nextString()} ")
//                            }
//                        }
//
//                        "androidVersion" -> {
//                            if (BuildConfig.DEBUG) {
//                                Log.i("print_logs", "androidVersion = ${jsonReader.nextString()} ")
//                            }
//                        }
//
//                        "error" -> {
//                            if (BuildConfig.DEBUG) {
//                                Log.i("print_logs", "error = ${jsonReader.nextString()} ")
//                            }
//                        }

                        else -> {
                            jsonReader.skipValue()
                        }
                    }
                }
                jsonReader.endObject()
                jsonReader.close()
                return logPath
            }
        }

        return null
    }

}