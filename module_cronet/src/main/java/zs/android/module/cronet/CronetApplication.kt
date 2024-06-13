package zs.android.module.cronet

import android.app.Application
import android.util.Log
import com.google.android.gms.net.CronetProviderInstaller

/**
 *
 * @author zhangshuai
 * @date 2024/5/13 17:07
 * @description 自定义类描述
 */
class CronetApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CronetProviderInstaller.installProvider(this).addOnCompleteListener {
            if (it.isSuccessful) {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "Successfully installed Play Services provider: $it")
                }
            } else{
                if (BuildConfig.DEBUG) {
                    Log.e("print_logs", "Unable to load Cronet from Play Services, it.exception")
                }
            }
        }
    }
}