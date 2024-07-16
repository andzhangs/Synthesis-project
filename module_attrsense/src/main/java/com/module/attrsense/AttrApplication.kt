package com.module.attrsense

import android.app.Application
import com.module.attrsense.test1.Test1
import com.module.attrsense.test2.Test2

/**
 *
 * @author zhangshuai
 * @date 2024/7/11 16:52
 * @description 自定义类描述
 */
class AttrApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Test1().getHello()
        Test2.getWorld()
        AppConfig().apply {
            getContent()
            getContent12()
        }
    }
}