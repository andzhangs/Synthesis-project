package zs.android.app.app

import android.app.Application
import com.blankj.utilcode.util.Utils

/**
 *
 * @author zhangshuai
 * @date 2025/3/5 10:39
 * @description 自定义类描述
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
    }
}