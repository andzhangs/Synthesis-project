package zs.android.module.spi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import zs.android.module.spi.loader.IClient
import zs.android.module.spi.loader.ISayHello
import java.util.ServiceLoader

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ServiceLoader.load(ISayHello::class.java).onEach {
            it.sayHello()
        }

        ServiceLoader.load(IClient::class.java).onEach {
            it.onEvent()
        }
    }
}