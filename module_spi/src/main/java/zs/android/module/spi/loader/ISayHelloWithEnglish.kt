package zs.android.module.spi.loader

import android.util.Log
import com.google.auto.service.AutoService


/**
 * @author zhangshuai@attrsense.com
 * @date 2023/2/6 16:03
 * @description
 */
@AutoService(value = [ISayHello::class])
class ISayHelloWithEnglish : ISayHello {
    override fun sayHello() {
        Log.d("print_logs", "ISayHelloWithEnglish::sayHell")
    }
}