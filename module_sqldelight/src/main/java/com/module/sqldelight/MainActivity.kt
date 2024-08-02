package com.module.sqldelight

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * https://cashapp.github.io/sqldelight/2.0.2/
 * https://cashapp.github.io/sqldelight/2.0.2/android_sqlite/
 *
 * 当前版本：https://handstandsam.com/2019/08/23/sqldelight-1-x-quick-start-guide-for-android/
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun load(){
//        AndroidSqliteDriver(
//            schema = Database,
//            context = applicationContext,
//            name = "items.db"
//        )
    }
}