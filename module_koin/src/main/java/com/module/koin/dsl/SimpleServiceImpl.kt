package com.module.koin.dsl

import android.util.Log
import com.module.koin.BuildConfig

/**
 *
 * @author zhangshuai
 * @date 2023/8/18 12:00
 * @mark 自定义类描述
 */
class SimpleServiceImpl : SimpleService {
    override fun onEvent() {
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "SimpleServiceImpl::onEvent: ")
        }
    }
}