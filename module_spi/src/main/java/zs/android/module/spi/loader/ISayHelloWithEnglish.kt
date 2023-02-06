package zs.android.module.spi.loader

import android.util.Log
import zs.android.module.spi.loader.ISayHello

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/2/6 16:03
 * @description
 */
class ISayHelloWithEnglish: ISayHello {
    override fun sayHello() {
        Log.i("print_logs", "ISayHelloWithEnglish::sayHello：Hello")
    }
}