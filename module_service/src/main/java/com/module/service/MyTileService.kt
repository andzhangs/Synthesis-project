package com.module.service

import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.os.IBinder
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log

class MyTileService : TileService() {

    override fun onBind(intent: Intent): IBinder? {
        return super.onBind(intent)
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onClick() {
        super.onClick()
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "MyTileService::onClick:起始状态： ${qsTile.state}")
        }

        qsTile.state = if (qsTile.state == Tile.STATE_INACTIVE) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
//        qsTile.updateTile()

        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "MyTileService::onClick:当前状态：${qsTile.state}")
        }

        qsTile.icon = Icon.createWithResource(this, R.drawable.ic_launcher_foreground)
        qsTile.label = this.getString(R.string.label_tile_service)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val state = if (qsTile.state == Tile.STATE_ACTIVE) "开启" else "关闭"
            qsTile.subtitle = "已$state"
        }
        qsTile.updateTile()

    }

    /**
     * 用户将 Tile 从 Edit 中添加到设定栏中
     */
    override fun onTileAdded() {
        super.onTileAdded()
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "MyTileService::onTileAdded: ")
        }
    }

    /**
     * 将 Tile 移除设定栏
     */
    override fun onTileRemoved() {
        super.onTileRemoved()
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "MyTileService::onTileRemoved: ")
        }
    }

    /**
     * 下拉菜单时被调用，Tile 没有从 Editor 栏拖到设置栏中，则不会调用
     *  onTileAdded() 时会被调用一次
     */
    override fun onStartListening() {
        super.onStartListening()
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "MyTileService::onStartListening: ")
        }
    }

    /**
     * 同上 同理
     */
    override fun onStopListening() {
        super.onStopListening()
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "MyTileService::onStopListening: ")
        }
    }


    override fun onDestroy() {
        super.onDestroy()
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
}