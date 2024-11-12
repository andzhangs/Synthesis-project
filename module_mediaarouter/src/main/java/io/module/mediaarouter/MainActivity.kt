package io.module.mediaarouter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * 主要用于媒体路由，例如将音频或视频投射到外部设备（如 Chromecast、蓝牙音箱等）
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}