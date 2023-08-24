package com.module.media

import android.content.ContentResolver
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log


/**
 *
 * @author zhangshuai
 * @date 2023/8/24 10:09
 * @mark 自定义类描述
 */
class MediaContentObserver(private val mContext: Context) : ContentObserver(Handler(Looper.getMainLooper())) {

    companion object{
        private const val TAG = "MediaContentObserver"
    }

    override fun onChange(selfChange: Boolean, uri: Uri?, flags: Int) {
        super.onChange(selfChange, uri, flags)
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "MediaContentObserver::onChange: $uri")
        }
        if (uri == MediaStore.Images.Media.EXTERNAL_CONTENT_URI || uri == MediaStore.Video.Media.EXTERNAL_CONTENT_URI) {
            handleNewMedia()
        }
    }

    private fun handleNewMedia() {
        // 查询MediaStore获取最新的图片和视频
        val contentResolver: ContentResolver = mContext.getContentResolver()
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        val sortOrder = MediaStore.MediaColumns.DATE_ADDED + " DESC"
        val cursor = contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            projection,
            null,
            null,
            sortOrder
        )
        if (cursor != null && cursor.moveToFirst()) {
            // 遍历最新的图片和视频
            do {
                val filePath =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
                // 在这里处理新增的图片和视频
                Log.d(TAG, "New media file: $filePath")
            } while (cursor.moveToNext())
            cursor.close()
        }
    }
}