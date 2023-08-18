package com.module.koin.presenter

import android.content.Context
import android.util.Log
import com.module.koin.BuildConfig

/**
 *
 * @author zhangshuai
 * @date 2023/8/18 11:41
 * @mark 自定义类描述
 */
class Presenter(private val mContext: Context? = null) {

    fun getFun() {
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "koin 基础调用了")
        }
    }

    fun getFunContext(){
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "koin Context 调用了 ${mContext?.packageName}")
        }
    }
}