package zs.android.module.spi.loader

import android.util.Log
import com.google.auto.service.AutoService

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/2/6 17:51
 * @description
 */
@AutoService(value = [IClient::class])
class IClientIos : IClient {
    override fun onEvent() {
        Log.v("print_logs", "IClientIos::onEvent: ")
    }
}