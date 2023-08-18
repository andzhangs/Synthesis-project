package com.module.koin.app

import android.app.Application
import androidx.work.WorkerFactory
import org.koin.android.ext.android.getKoin

/**
 *
 * @author zhangshuai
 * @date 2023/8/18 14:54
 * @mark 自定义类描述
 */
fun Application.setupWorkManagerFactory() {
    getKoin().getAll<WorkerFactory>().forEach {

    }
}