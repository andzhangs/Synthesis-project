package zs.android.module.widget

import android.app.Application
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/7/20 17:51
 * @description
 */
class WidgetApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        QbSdk.initX5Environment(this,object :PreInitCallback{
            override fun onCoreInitFinished() {
//                if (BuildConfig.DEBUG) {
//                    Log.i("print_logs", "WidgetApplication::onCoreInitFinished: // 内核初始化完成，可能为系统内核，也可能为系统内核")
//                }
            }

            override fun onViewInitFinished(p0: Boolean) {
//                if (BuildConfig.DEBUG) {
//                    Log.i("print_logs", "WidgetApplication::onViewInitFinished: 预初始化结束")
//                }
            }
        })

    }
}