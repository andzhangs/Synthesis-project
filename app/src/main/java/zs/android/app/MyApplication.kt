package zs.android.app

import android.app.Application
import com.huawei.agconnect.AGConnectInstance
import com.huawei.agconnect.AGConnectOptionsBuilder

/**
 *
 * @author zhangshuai
 * @date 2024/12/11 14:21
 * @description 自定义类描述
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        try {
            AGConnectInstance.initialize(this, AGConnectOptionsBuilder().apply {
                val inputStream=assets.open("agconnect-services.json")
                this.setInputStream(inputStream)
                this.setClientId("client_id")
                this.setClientSecret("client_secret")
                this.setApiKey("api_key")
                this.setCPId("cp_id")
                this.setProductId("product_id")
                this.setAppId("app_id")
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }finally {
//            AccessNetworkManager.getInstance().setAccessNetwork(true)
        }
    }
}