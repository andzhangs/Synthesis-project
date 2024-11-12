package zs.android.module.widget.screenshots

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import zs.android.module.widget.BuildConfig

class ScreenshotReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action=="android.intent.action.SCREENSHOT" || intent.action=="com.android.systemui.screenshot"){
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "ScreenshotReceiver::onReceive: ")
            }
        }
    }
}