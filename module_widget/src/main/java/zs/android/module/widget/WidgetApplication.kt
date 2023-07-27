package zs.android.module.widget

import android.app.Application

/**
 * @author zhangshuai@attrsense.com
 * @date 2023/7/20 17:51
 * @description
 */
class WidgetApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ToastUtils.init(this)
    }
}