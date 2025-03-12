package com.module.mlkit.app

import android.app.Application
import android.util.Log

/**
 *
 * @author zhangshuai
 * @date 2025/3/12 14:24
 * @description 自定义类描述
 */
class MyApplication : Application() {

//    var trustedTimeClient: TrustedTimeClient? = null
//        private set

    override fun onCreate() {
        super.onCreate()
//        val initializeTrustedTimeClientTask = TrustedTime.createClient(this)
//        initializeTrustedTimeClientTask.addOnCompleteListener {
//            Log.i("print_logs", "完成: ${it.isSuccessful}")
//            if (it.isSuccessful) {
//                trustedTimeClient=it.result
//            }else{
//                val e=initializeTrustedTimeClientTask.exception
//            }
//
//        }.addOnSuccessListener {
//            it.dispose()
//        }.addOnFailureListener {
//            Log.e("print_logs", "失败: $it")
//        }
    }
}