package zs.android.module.permission

import android.annotation.TargetApi
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.loader.content.CursorLoader
import java.io.File


/**
 * @author zhangshuai@attrsense.com
 * @date 2022/11/17 14:25
 * @description
 */
object Uri2PathUtil {

    //复杂版处理  (适配多种API)
    fun getRealPathFromUri(context: Context, uri: Uri): String? {
        return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
            getRealPathFromUri_BelowApi11(context, uri)
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            getRealPathFromUri_Api11To18(context, uri)
        } else {
            getRealPathFromUri_AboveApi19(context, uri)
        }
    }

    /**
     * 适配api19以上,根据uri获取图片的绝对路径
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun getRealPathFromUri_AboveApi19(context: Context, uri: Uri): String? {
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                val contentUri: Uri
                contentUri = if ("image" == type) {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                } else {
                    MediaStore.Files.getContentUri("external")
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    /**
     * 适配api11-api18,根据uri获取图片的绝对路径
     */
    private fun getRealPathFromUri_Api11To18(context: Context, uri: Uri): String? {
        var filePath: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        //这个有两个包不知道是哪个。。。。不过这个复杂版一般用不到
        val loader = CursorLoader(context, uri, projection, null, null, null)
        val cursor = loader.loadInBackground()
        if (cursor != null) {
            cursor.moveToFirst()
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]))
            cursor.close()
        }
        return filePath
    }

    /**
     * 适配api11以下(不包括api11),根据uri获取图片的绝对路径
     */
    private fun getRealPathFromUri_BelowApi11(context: Context, uri: Uri): String? {
        var filePath: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]))
            cursor.close()
        }
        return filePath
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = MediaStore.MediaColumns.DATA
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(
                uri!!, projection, selection, selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    fun getAllImageFolders(context: Context): List<String> {
        val folderPaths: MutableSet<String> = HashSet()
        val projection =
            arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        val selection = MediaStore.Images.Media.MIME_TYPE + " LIKE ? "
        val selectionArgs = arrayOf("image/%")
        val sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " DESC"
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )
            if (cursor != null) {
                val folderPathIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)

                while (cursor.moveToNext()) {
                    val imagePath = cursor.getString(folderPathIndex)
                    File(imagePath).parent?.also { folderPath ->
                        if (!folderPaths.contains(folderPath)) {
                            if (BuildConfig.DEBUG) {
                                Log.d("print_logs", "folderPath: $folderPath")
                            }
                            getFileItem(folderPaths, File(folderPath))
                        }
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e("print_logs", "Error retrieving image folders: " + e.message)
        } finally {
            cursor?.close()
        }
        return folderPaths.toList()
    }

    private fun getFileItem(folderPaths: MutableSet<String>, fileParent: File): Boolean {
        val isDirectory = fileParent.isDirectory
        if (isDirectory) {
            fileParent.listFiles()?.forEach { childFile ->
                if (getFileItem(folderPaths, childFile)) {
                    folderPaths.add(childFile.toString())
                } else {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "childFile: ${childFile.name}")
                    }
                }
            }
        } else {
            folderPaths.add(fileParent.absolutePath.substringBeforeLast("/"))
        }
        return isDirectory
    }

    @JvmStatic
    fun getChildImage(parentPath: String): List<String> {
        val images: MutableSet<String> = HashSet()
        File(parentPath).listFiles()?.forEach {
            images.add(it.absolutePath)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "childImage: $it")
            }
        }
        return images.toList()
    }
}