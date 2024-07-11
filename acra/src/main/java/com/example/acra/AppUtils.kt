package com.example.acra

import android.content.Context
import android.os.Build

/**
 *
 * @author zhangshuai
 * @date 2024/7/11 10:17
 * @description 自定义类描述
 */
object AppUtils {

    fun getAppVersion(context: Context): Pair<String, Long> {
        val packageManager = context.applicationContext.packageManager
        val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
        val versionName = packageInfo.versionName
        val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            packageInfo.versionCode.toLong()
        }

        return Pair(versionName, versionCode)
    }
}