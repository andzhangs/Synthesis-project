package zs.android.app

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 *
 * @author zhangshuai
 * @date 2024/11/20 17:11
 * @description 自定义类描述
 *
 * https://blog.csdn.net/qq_42751010/article/details/139920083
 */
class TestCoroutine {

    companion object {
        @JvmStatic
        suspend fun start() {
            kotlin.runCatching {
                TestCoroutine().loadData()
            }.onSuccess {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "TestCoroutine::start: onSuccess")
                }
            }.onFailure {
                if (BuildConfig.DEBUG) {
                    Log.e("print_logs", "TestCoroutine::start::onFailure $it")
                }
            }
        }
    }

    suspend fun loadData() {
        val result1 = suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation {
                if (BuildConfig.DEBUG) {
                    Log.e("print_logs", "continuation.invokeOnCancellation: $it")
                }
            }

            CoroutineScope(Dispatchers.IO).launch {
                delay(500L)
                continuation.resume(101)
            }

//            continuation.cancel(Throwable("主动取消，会崩溃!"))
        }


        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "TestCoroutine::loadData: $result1")
        }


        val result2 = suspendCoroutine {
            it.resume(100)
        }
        if (BuildConfig.DEBUG) {
            Log.i(
                "print_logs",
                "loadData: $result2,  当前线程：${Thread.currentThread().name}"
            )
        }
    }

    private fun fetchUserDataAsync(): Deferred<String> {
        return CoroutineScope(Dispatchers.IO).async {
            //模拟异步操作
            delay(1000L)
            "User data fetched."
        }
    }

    private suspend fun fetchData(): String {
        return suspendCoroutine { continuation ->
            val defferred = fetchUserDataAsync()

            defferred.invokeOnCompletion {
                if (it == null) {
                    val result=defferred.getCompleted()
                    continuation.resume(result)  //返回正常结果，也就是一个String
                } else {
                    continuation.resumeWithException(it)  //返回异常，需要在函数掉用方捕获
                }
            }
        }
    }

    init {
        runBlocking {
            runCatching {
                fetchData()
            }.onSuccess {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "TestCoroutine::onSuccess: $it")
                }
            }.onFailure {
                if (BuildConfig.DEBUG) {
                    Log.e("print_logs", "TestCoroutine::onFailure: $it")
                }
            }
        }

    }
}