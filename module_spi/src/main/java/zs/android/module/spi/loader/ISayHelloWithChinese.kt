package zs.android.module.spi.loader

import android.util.Log
import zs.android.module.spi.loader.ISayHello

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/2/6 16:03
 * @description
 */
class ISayHelloWithChinese : ISayHello {
    override fun sayHello() {
        Thread{
            Log.i("print_logs", "ISayHelloWithChinese::sayHello：你好-1")
            Thread.sleep(2000)
            Log.i("print_logs", "ISayHelloWithChinese::sayHello：你好-2")
        }.start()
        Log.i("print_logs", "ISayHelloWithChinese::sayHello：你好-3")

    }
}