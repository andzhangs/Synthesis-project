package com.module.service

import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.os.IBinder
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import com.module.service.BuildConfig.*

class MyTileService : TileService() {

    override fun onBind(intent: Intent): IBinder? {
        return super.onBind(intent)
    }

    override fun onCreate() {
        super.onCreate()
        if (DEBUG) {
            Log.i("print_logs", "MyTileService::onCreate: ")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (DEBUG) {
            Log.i("print_logs", "MyTileService::onStartCommand: ")
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onClick() {
        super.onClick()
        if (DEBUG) {
            Log.i("print_logs", "MyTileService::onClick:起始状态： ${qsTile.state}")
        }

        qsTile.state =
            if (qsTile.state == Tile.STATE_INACTIVE) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE

        if (DEBUG) {
            Log.i("print_logs", "MyTileService::onClick:当前状态：${qsTile.state}")
        }

        //方式一：动态代码设置
        qsTile.icon = Icon.createWithResource(this, R.drawable.icon_tile_default)
        qsTile.label = this.getString(R.string.label_tile_service)

        //方式二：AndroidManifest.xml中设置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val state = if (qsTile.state == Tile.STATE_ACTIVE) "开启" else "关闭"
            qsTile.subtitle = "已$state"
        }
        qsTile.updateTile()

        mOnServiceCallback?.onCallback(qsTile.state == Tile.STATE_ACTIVE)
    }

    //----------------------------------------------------------------------------------------------

    interface OnServiceCallback {
        fun onCallback(isActive: Boolean)
    }

    private var mOnServiceCallback: OnServiceCallback? = null

    fun setOnServiceCallback(listener: OnServiceCallback) {
        this.mOnServiceCallback = listener
    }

    //----------------------------------------------------------------------------------------------


    /**
     * 用户将 Tile 从 Edit 中添加到设定栏中
     */
    override fun onTileAdded() {
        super.onTileAdded()
        if (DEBUG) {
            Log.i("print_logs", "MyTileService::onTileAdded: ${Thread.currentThread().name}")
        }
    }

    /**
     * 将 Tile 移除设定栏
     */
    override fun onTileRemoved() {
        super.onTileRemoved()
        if (DEBUG) {
            Log.i("print_logs", "MyTileService::onTileRemoved: ${Thread.currentThread().name}")
        }
    }

    /**
     * 下拉菜单时被调用，Tile 没有从 Editor 栏拖到设置栏中，则不会调用
     *  onTileAdded() 时会被调用一次
     */
    override fun onStartListening() {
        super.onStartListening()
        if (DEBUG) {
            Log.i("print_logs", "MyTileService::onStartListening: ${Thread.currentThread().name}")
        }
    }

    /**
     * 同上 同理
     */
    override fun onStopListening() {
        super.onStopListening()
        if (DEBUG) {
            Log.i("print_logs", "MyTileService::onStopListening: ${Thread.currentThread().name}")
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}