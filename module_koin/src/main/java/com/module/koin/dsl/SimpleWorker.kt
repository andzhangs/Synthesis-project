package com.module.koin.dsl

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**
 *
 * @author zhangshuai
 * @date 2023/8/18 13:37
 * @mark 自定义类描述
 */
class SimpleWorker(
    private val simpleService: SimpleService,
    appContent: Context,
    private val params: WorkerParameters
) : CoroutineWorker(appContent, params) {

    override suspend fun doWork(): Result {
        return Result.success()
    }
}