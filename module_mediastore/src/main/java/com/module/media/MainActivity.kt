package com.module.media

import android.Manifest
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.MediaStore
import android.util.Log
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.database.getIntOrNull
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.module.media.databinding.ActivityMainBinding
import com.module.media.factory.TranslateFactory
import com.module.media.factory.TranslateType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileInputStream
import java.security.MessageDigest


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    private val mFolderNameMap = LinkedHashMap<String, Pair<String, MutableList<String>>>()

    private val mPlayHandler = Handler(Looper.getMainLooper())
    private val seekRunnable = object : Runnable {
        override fun run() {
            if (mBinding.videoView.isPlaying) {
                mBinding.acSeekBar.progress = mBinding.videoView.currentPosition
                mPlayHandler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (it.isNotEmpty()) {
                    if (it[Manifest.permission.READ_MEDIA_IMAGES]!! && it[Manifest.permission.READ_MEDIA_VIDEO]!!) {
                        initView()
                    }
                }
            }.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
            )
        }
    }

    private fun initView() {
        lifecycleScope.launch(Dispatchers.IO) {
            searchMediaData()
        }

        mBinding.acIvStart.setOnClickListener {
            mPlayHandler.postDelayed(seekRunnable, 0)
            mBinding.videoView.start()
        }

        mBinding.acBtnPause.setOnClickListener {
            mBinding.videoView.pause()
//            mBinding.videoView.suspend()
        }

        mBinding.acBtnResume.setOnClickListener {
            mBinding.acSeekBar.progress = 0
            mBinding.videoView.resume()
        }

        mBinding.acBtnStop.setOnClickListener {
            mBinding.videoView.stopPlayback()
            mBinding.acSeekBar.progress = 0
            mBinding.videoView.resume()
        }

        mBinding.acSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                if (BuildConfig.DEBUG) {
//                    Log.i("print_logs", "当前进度: $progress, fromUser= $fromUser")
//                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //当用户开始拖动SeekBar时，暂停视频播放
                if (mBinding.videoView.isPlaying) {
                    mBinding.videoView.pause()
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (!mBinding.videoView.isPlaying) {
                    mBinding.videoView.seekTo(seekBar!!.progress)
                    mPlayHandler.postDelayed(seekRunnable, 0)
                    mBinding.videoView.start()
                }
            }
        })
    }

    private suspend fun searchMediaData() {
        try {
            val projection = arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.DURATION,
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns.DATE_EXPIRES,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.DATE_TAKEN
            )
            val selection: String =
                (MediaStore.Files.FileColumns.MEDIA_TYPE + "=? OR " + MediaStore.Files.FileColumns.MEDIA_TYPE) + "=?"
            val selectionArgs = arrayOf(
                MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
                MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
            )
            val sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC"

            val cursor = contentResolver.query(
                MediaStore.Files.getContentUri("external"),
                projection,
                selection,
                selectionArgs,
                sortOrder
            )

            if (cursor != null) {
                var index = 1
                while (cursor.moveToNext()) {
                    //UriId
                    val ringtoneID =
                        cursor.getIntOrNull(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID))
                    //Uri
                    val fileUri = Uri.withAppendedPath(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        ringtoneID.toString()
                    ).toString()

                    //获取图片的名称
                    val fileName =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME))
                    //文件夹
                    val folderName =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME))
                    //获取图片的路径
                    val filePath =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))
                    //获取图片的大小
                    val fileSize =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE))
                    // 媒体类型
                    val mimeType =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE))

                    // 处理路径和媒体类型
                    val mediaType =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE))

                    //hash值-文件唯一性
                    val hash = calculateImageHash(filePath)

                    //视频时长
                    val duration =
                        cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DURATION))

                    val dateModified =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED))

                    val dateExpires =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_EXPIRES))
                    val dateAdded =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED))
                    val dateTaken =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_TAKEN))


                    if (fileName.contains("attr_IMG_6159")) {
                        withContext(Dispatchers.IO) {
                            translateChinese(folderName, filePath) {
                                val result = TranslateFactory.getChineseText(
                                    TranslateType.BAI_DU,
                                    folderName,
                                    filePath
                                )
                                if (result.second.isNotEmpty() && result.third == filePath) {
                                    mFolderNameMap[result.first] =
                                        Pair(result.second, mutableListOf(result.third))
                                } else {
                                    Log.d("print_logs", "百度翻译失败：$folderName")
                                }
                            }
                        }
                    } else { //if (fileName=="640.jpeg")
                        withContext(Dispatchers.IO) {
                            translateChinese(folderName, filePath) {
                                val result2 = TranslateFactory.getChineseText(
                                    TranslateType.YOU_DAO,
                                    folderName,
                                    filePath
                                )
                                if (result2.second.isNotEmpty() && result2.third == filePath) {
                                    mFolderNameMap[result2.first] =
                                        Pair(result2.second, mutableListOf(result2.third))
                                } else {
                                    Log.d("print_logs", "有道译失败：$folderName")
                                }
                            }
                        }
                    }


                    if (fileName == "attr_IMG_6159.mov" && mimeType.contains("video")) {
                        withContext(Dispatchers.Main) {
                            loadVideo("/storage/emulated/0/attr/livePhoto/attr_IMG_6159.mov")
                        }
                    }
                    ++index

//                    Log.i(
//                        "print_logs",
//                        "fileName: $fileName,\n" +
//                                "folderName：$folderName,\n" +
//                                "filePath: $filePath,\n" +
//                                "fileSize: $fileSize,\n" +
//                                "mimeType: $mimeType,\n" +
//                                "mediaType：$mediaType,\n" +
//                                "hash: $hash,\n" +
//                                "fileUri: $fileUri,\n" +
//                                "duration: $duration,\n" +
//                                "dateModified: $dateModified,\n" +
//                                "dateExpires: $dateExpires,\n" +
//                                "dateAdded: $dateAdded,\n" +
//                                "dateTaken: $dateTaken"
//                    )
                }
                cursor.close()
            } else {
                Log.e("print_logs", "MainActivity::getMedia: null.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("print_logs", "MainActivity::getMedia: $e")
        } finally {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MainActivity::searchMediaData: finally")
            }

            mFolderNameMap.forEach { (t, u) ->
                if (BuildConfig.DEBUG) {
                    Log.d("print_logs", "文件夹: $t, ${u.first}")
                }
                u.second.forEach {
                    if (BuildConfig.DEBUG) {
                        Log.w("print_logs", "包含: $it")
                    }
                }
            }
        }
    }

    private suspend fun translateChinese(
        wordTxt: String,
        filePath: String,
        block: suspend () -> Unit
    ) {
        if (mFolderNameMap.contains(wordTxt)) {
            if (BuildConfig.DEBUG) {
                Log.d("print_logs", "translateChinese，已经存在：$wordTxt")
            }
            mFolderNameMap[wordTxt]?.second?.add(filePath)
        } else {
            block()
        }
    }


    private fun loadVideo(filePath: String) {
        mBinding.videoView.setVideoPath(filePath)
        mBinding.videoView.requestFocus()

        mBinding.videoView.setOnPreparedListener {
            //循环播放
//            it.isLooping = true
            //将视频定位到第一帧
            mBinding.videoView.seekTo(0)

            //视频播放缩放模式
//            it.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)

            //保湿常亮
            it.setScreenOnWhilePlaying(true)
            it.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mBinding.acSeekBar.min = 0
            }

            mBinding.acSeekBar.max = it.duration

//            if (BuildConfig.DEBUG) {
//                Log.i(
//                    "print_logs",
//                    "MainActivity::setOnPreparedListener: 总时长= ${it.duration}"
//                )
//            }
        }


        mBinding.videoView.setOnInfoListener { mp, what, extra ->
//            if (BuildConfig.DEBUG) {
//                Log.i("print_logs", "MainActivity::setOnInfoListener: $what")
//            }
            when (what) {
                MediaPlayer.MEDIA_INFO_UNKNOWN -> {

                }

                MediaPlayer.MEDIA_INFO_STARTED_AS_NEXT -> {

                }

                MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {

                }

                MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING -> {

                }

                MediaPlayer.MEDIA_INFO_BUFFERING_START -> {

                }

                MediaPlayer.MEDIA_INFO_BUFFERING_END -> {

                }
//                MediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH->{}
                MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING -> {

                }

                MediaPlayer.MEDIA_INFO_NOT_SEEKABLE -> {

                }

                MediaPlayer.MEDIA_INFO_METADATA_UPDATE -> {

                }
//                MediaPlayer.MEDIA_INFO_EXTERNAL_METADATA_UPDATE->{}
                MediaPlayer.MEDIA_INFO_AUDIO_NOT_PLAYING -> {

                }

                MediaPlayer.MEDIA_INFO_VIDEO_NOT_PLAYING -> {

                }
//                MediaPlayer.MEDIA_INFO_TIMED_TEXT_ERROR->{}
                MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE -> {

                }

                MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT -> {

                }

                else -> {}
            }
            true
        }


        mBinding.videoView.setOnCompletionListener {
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "MainActivity::setOnCompletionListener: ")
            }
            mBinding.acSeekBar.progress = mBinding.videoView.duration
            //复位到开始播放
//            mBinding.videoView.resume()

            mPlayHandler.removeCallbacks(seekRunnable)
        }


        mBinding.videoView.setOnErrorListener { mp, what, extra ->
            if (BuildConfig.DEBUG) {
                Log.e("print_logs", "MainActivity::setOnErrorListener: $what")
            }
            mBinding.acSeekBar.progress = 0
            //复位到开始播放
            mBinding.videoView.resume()

            mPlayHandler.removeCallbacks(seekRunnable)
            true
        }
    }

    private fun calculateImageHash(filePath: String): String {
        val fileInputStream = FileInputStream(filePath)
        val buffer = ByteArray(8192)
        val digest = MessageDigest.getInstance("SHA-256")

        var bytesRead = fileInputStream.read(buffer)
        while (bytesRead != -1) {
            digest.update(buffer, 0, bytesRead)
            bytesRead = fileInputStream.read(buffer)
        }

        fileInputStream.close()

        val hashBytes = digest.digest()
        val stringBuilder = StringBuilder()
        for (byte in hashBytes) {
            stringBuilder.append(String.format("%02x", byte))
        }
        return stringBuilder.toString()
    }

    override fun onStop() {
        super.onStop()
        mBinding.videoView.stopPlayback()
        mPlayHandler.removeCallbacks(seekRunnable)
    }

    override fun onDestroy() {
        mFolderNameMap.clear()
        super.onDestroy()
    }
}