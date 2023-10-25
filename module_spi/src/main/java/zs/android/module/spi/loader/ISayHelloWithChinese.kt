package zs.android.module.spi.loader

import android.util.Log
import com.google.auto.service.AutoService

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/2/6 16:03
 * @description
 */
@AutoService(value = [ISayHello::class])
class ISayHelloWithChinese : ISayHello {
    override fun sayHello() {
//        Thread {
            Log.w("print_logs", "ISayHelloWithChinese::sayHello：你好-1")
//            Thread.sleep(2000)
//            Log.w("print_logs", "ISayHelloWithChinese::sayHello：你好-2")
//        }.start()
//        Log.w("print_logs", "ISayHelloWithChinese::sayHello：你好-3")

    }
}