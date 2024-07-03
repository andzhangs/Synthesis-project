package com.module.photo

import android.Manifest
import android.content.ContentResolver
import android.media.ExifInterface
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.module.photo.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mDataBinding.lifecycleOwner = this

        mDataBinding.acBtnRequestPermission.setOnClickListener {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                ),
                100
            )
        }

        lifecycleScope.launch(Dispatchers.IO) {
            queryMediaStoreImages().collect{list ->
                list.forEach {mPair->
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "输出地址: ${mPair.first}, ${mPair.second}")
                    }
                }

            }

            val fileFolder = "${Environment.getExternalStoragePublicDirectory("")}${File.separator}media"
            val parentFile=File(fileFolder)
            if (parentFile.isDirectory) {
                val pathList=parentFile.listFiles()?.map {
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "文件: ${it.absolutePath}")
                    }
                    it.absolutePath
                } ?: emptyList<String>()

                notifySystem(pathList.toTypedArray())
            }

        }
    }

    fun notifySystem(
        fileUrls: Array<String>,
        block: (() -> Unit)? = null
    ) {

        val mimeTypes = arrayOf("image/*", "video/*")
        MediaScannerConnection.scanFile(
            this.applicationContext, fileUrls, mimeTypes
        ) { _, _ ->
            block?.invoke()
        }
    }

    private val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DATA,
        MediaStore.Images.Media.TITLE,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Files.FileColumns.MIME_TYPE,
        MediaStore.Files.FileColumns.SIZE,
        MediaStore.Files.FileColumns.TITLE,
        MediaStore.Files.FileColumns.DISPLAY_NAME
    )

    /**
     * 分页查询媒体库
     *  Android-8.0及以上使用queryArgs分页(以用来兼容Android-11,android-11不可用sortOrder分页)
     *  Android-8.0以下使用sortOrder分页page从0开始
     */
    fun queryMediaStoreImages(
        pageIndex: Int=0,
        pageSize: Int=50
    ): Flow<MutableList<Pair<String,String>>> {
        return flow {
            val list = mutableListOf<Pair<String,String>>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    val uri: Uri =
                        MediaStore.Files.getContentUri("external") //MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    val queryArgs = Bundle()// 设置倒序
                    queryArgs.putInt(
                        ContentResolver.QUERY_ARG_SORT_DIRECTION,
                        ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
                    )
                    // 设置倒序条件--文件添加时间
                    queryArgs.putStringArray(
                        ContentResolver.QUERY_ARG_SORT_COLUMNS,
                        arrayOf(MediaStore.Files.FileColumns.DATE_MODIFIED)
                    )
                    // 分页设置
                    queryArgs.putInt(ContentResolver.QUERY_ARG_OFFSET, pageIndex * pageSize)
                    queryArgs.putInt(ContentResolver.QUERY_ARG_LIMIT, pageSize)
                    this@MainActivity.applicationContext.contentResolver.query(uri, null, queryArgs, null)
                        ?.use { cursor ->
                            while (cursor.moveToNext()) {
                                val id =
                                    cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID))
                                val imgTitle =
                                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE))
                                val name =
                                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME))
                                val path =
                                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))
                                val title =
                                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE))

                                val dataAdded =
                                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED))

                                val dataModified =
                                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED))

                                val mediaType = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE))

                                list.add(Pair(path,"$dataAdded, $dataModified, ${File(path).lastModified()}, $mediaType"))
                                if (mediaType==MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
//                                    printImgInfo(path)
                                }
                            }
                        }
                } catch (e: Exception) {
                    Log.e("print_logs", "queryMediaStoreImages: $e")
                }
            } else {
                try {
                    val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    val sortOrder =
                        MediaStore.Images.Media.DATE_MODIFIED + " DESC limit " + pageSize + " offset " + pageIndex * pageSize
                    val cursor =
                        this@MainActivity.applicationContext.contentResolver.query(
                            uri,
                            null,
                            null,
                            null,
                            sortOrder
                        )?.use { cursor ->
                            while (cursor.moveToNext()) {
                                val id =
                                    cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                                val path =
                                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                                val title =
                                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE))
//                            Log.d(
//                                "print_logs",
//                                "android8.0-：id=${id.toLong()}, title=$title, path=$path"
//                            )

//                                list.add(path)
                            }
                        }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            emit(list)
        }.flowOn(Dispatchers.IO)
    }

    fun printImgInfo(imgPath: String?) {
        if (BuildConfig.DEBUG) {
            imgPath?.also {
                ExifInterface(it).also { exifInterface ->
                    val sb =
                        StringBuilder().apply {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                append(
                                    "${ExifInterface.TAG_ARTIST}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_ARTIST
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_BITS_PER_SAMPLE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_BITS_PER_SAMPLE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_COMPRESSION}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_COMPRESSION
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_COPYRIGHT}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_COPYRIGHT
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_DATETIME}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_DATETIME
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_IMAGE_DESCRIPTION}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_IMAGE_DESCRIPTION
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_IMAGE_LENGTH}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_IMAGE_LENGTH
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_IMAGE_WIDTH}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_IMAGE_WIDTH
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_MAKE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_MAKE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_MODEL}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_MODEL
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_ORIENTATION}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_ORIENTATION
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_PHOTOMETRIC_INTERPRETATION}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_PHOTOMETRIC_INTERPRETATION
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_PLANAR_CONFIGURATION}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_PLANAR_CONFIGURATION
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_PRIMARY_CHROMATICITIES}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_PRIMARY_CHROMATICITIES
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_REFERENCE_BLACK_WHITE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_REFERENCE_BLACK_WHITE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_RESOLUTION_UNIT}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_RESOLUTION_UNIT
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_ROWS_PER_STRIP}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_ROWS_PER_STRIP
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_SAMPLES_PER_PIXEL}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_SAMPLES_PER_PIXEL
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_SOFTWARE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_SOFTWARE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_STRIP_BYTE_COUNTS}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_STRIP_BYTE_COUNTS
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_STRIP_OFFSETS}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_STRIP_OFFSETS
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_TRANSFER_FUNCTION}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_TRANSFER_FUNCTION
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_WHITE_POINT}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_WHITE_POINT
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_X_RESOLUTION}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_X_RESOLUTION
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_Y_CB_CR_COEFFICIENTS}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_Y_CB_CR_COEFFICIENTS
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_Y_CB_CR_POSITIONING}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_Y_CB_CR_POSITIONING
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_Y_CB_CR_SUB_SAMPLING}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_Y_CB_CR_SUB_SAMPLING
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_Y_RESOLUTION}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_Y_RESOLUTION
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_APERTURE_VALUE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_APERTURE_VALUE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_BRIGHTNESS_VALUE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_BRIGHTNESS_VALUE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_CFA_PATTERN}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_CFA_PATTERN
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_COLOR_SPACE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_COLOR_SPACE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_COMPONENTS_CONFIGURATION}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_COMPONENTS_CONFIGURATION
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_COMPRESSED_BITS_PER_PIXEL}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_COMPRESSED_BITS_PER_PIXEL
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_CONTRAST}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_CONTRAST
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_CUSTOM_RENDERED}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_CUSTOM_RENDERED
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_DATETIME_DIGITIZED}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_DATETIME_DIGITIZED
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_DATETIME_ORIGINAL}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_DATETIME_ORIGINAL
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_DEVICE_SETTING_DESCRIPTION}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_DEVICE_SETTING_DESCRIPTION
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_DIGITAL_ZOOM_RATIO}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_DIGITAL_ZOOM_RATIO
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_EXIF_VERSION}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_EXIF_VERSION
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_EXPOSURE_BIAS_VALUE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_EXPOSURE_BIAS_VALUE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_EXPOSURE_INDEX}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_EXPOSURE_INDEX
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_EXPOSURE_MODE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_EXPOSURE_MODE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_EXPOSURE_PROGRAM}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_EXPOSURE_PROGRAM
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_EXPOSURE_TIME}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_EXPOSURE_TIME
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_F_NUMBER}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_F_NUMBER
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_FILE_SOURCE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_FILE_SOURCE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_FLASH}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_FLASH
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_FLASH_ENERGY}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_FLASH_ENERGY
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_FLASHPIX_VERSION}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_FLASHPIX_VERSION
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_FOCAL_LENGTH}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_FOCAL_LENGTH
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_FOCAL_LENGTH_IN_35MM_FILM}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_FOCAL_LENGTH_IN_35MM_FILM
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_FOCAL_PLANE_RESOLUTION_UNIT}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_FOCAL_PLANE_RESOLUTION_UNIT
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_FOCAL_PLANE_X_RESOLUTION}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_FOCAL_PLANE_X_RESOLUTION
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_FOCAL_PLANE_Y_RESOLUTION}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_FOCAL_PLANE_Y_RESOLUTION
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GAIN_CONTROL}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GAIN_CONTROL
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_IMAGE_UNIQUE_ID}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_IMAGE_UNIQUE_ID
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_LIGHT_SOURCE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_LIGHT_SOURCE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_MAKER_NOTE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_MAKER_NOTE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_MAX_APERTURE_VALUE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_MAX_APERTURE_VALUE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_METERING_MODE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_METERING_MODE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_OECF}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_OECF
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_PIXEL_X_DIMENSION}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_PIXEL_X_DIMENSION
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_PIXEL_Y_DIMENSION}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_PIXEL_Y_DIMENSION
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_RELATED_SOUND_FILE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_RELATED_SOUND_FILE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_SATURATION}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_SATURATION
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_SCENE_CAPTURE_TYPE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_SCENE_CAPTURE_TYPE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_SCENE_TYPE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_SCENE_TYPE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_SENSING_METHOD}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_SENSING_METHOD
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_SHARPNESS}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_SHARPNESS
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_SHUTTER_SPEED_VALUE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_SHUTTER_SPEED_VALUE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_SPATIAL_FREQUENCY_RESPONSE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_SPATIAL_FREQUENCY_RESPONSE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_SPECTRAL_SENSITIVITY}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_SPECTRAL_SENSITIVITY
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_SUBSEC_TIME}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_SUBSEC_TIME
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_SUBSEC_TIME_DIGITIZED}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_SUBSEC_TIME_DIGITIZED
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_SUBSEC_TIME_ORIGINAL}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_SUBSEC_TIME_ORIGINAL
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_SUBJECT_AREA}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_SUBJECT_AREA
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_SUBJECT_DISTANCE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_SUBJECT_DISTANCE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_SUBJECT_DISTANCE_RANGE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_SUBJECT_DISTANCE_RANGE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_SUBJECT_LOCATION}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_SUBJECT_LOCATION
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_USER_COMMENT}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_USER_COMMENT
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_WHITE_BALANCE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_WHITE_BALANCE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_ALTITUDE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_ALTITUDE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_ALTITUDE_REF}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_ALTITUDE_REF
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_AREA_INFORMATION}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_AREA_INFORMATION
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_DOP}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_DOP
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_DATESTAMP}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_DATESTAMP
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_DEST_BEARING}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_DEST_BEARING
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_DEST_BEARING_REF}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_DEST_BEARING_REF
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_DEST_DISTANCE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_DEST_DISTANCE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_DEST_DISTANCE_REF}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_DEST_DISTANCE_REF
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_DEST_LATITUDE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_DEST_LATITUDE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_DEST_LATITUDE_REF}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_DEST_LATITUDE_REF
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_DEST_LONGITUDE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_DEST_LONGITUDE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_DEST_LONGITUDE_REF}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_DEST_LONGITUDE_REF
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_DIFFERENTIAL}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_DIFFERENTIAL
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_IMG_DIRECTION}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_IMG_DIRECTION
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_IMG_DIRECTION_REF}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_IMG_DIRECTION_REF
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_LATITUDE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_LATITUDE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_LATITUDE_REF}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_LATITUDE_REF
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_LONGITUDE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_LONGITUDE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_LONGITUDE_REF}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_LONGITUDE_REF
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_MAP_DATUM}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_MAP_DATUM
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_MEASURE_MODE}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_MEASURE_MODE
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_PROCESSING_METHOD}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_PROCESSING_METHOD
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_SATELLITES}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_SATELLITES
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_SPEED}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_SPEED
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_SPEED_REF}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_SPEED_REF
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_STATUS}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_STATUS
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_TIMESTAMP}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_TIMESTAMP
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_TRACK}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_TRACK
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_TRACK_REF}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_TRACK_REF
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_GPS_VERSION_ID}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_GPS_VERSION_ID
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_INTEROPERABILITY_INDEX}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_INTEROPERABILITY_INDEX
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_THUMBNAIL_IMAGE_LENGTH}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_THUMBNAIL_IMAGE_LENGTH
                                        )
                                    }"
                                ).append("\n")
                                append(
                                    "${ExifInterface.TAG_THUMBNAIL_IMAGE_WIDTH}：${
                                        exifInterface.getAttribute(
                                            ExifInterface.TAG_THUMBNAIL_IMAGE_WIDTH
                                        )
                                    }"
                                ).append("\n")
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    append(
                                        "${ExifInterface.TAG_NEW_SUBFILE_TYPE}：${
                                            exifInterface.getAttribute(
                                                ExifInterface.TAG_NEW_SUBFILE_TYPE
                                            )
                                        }"
                                    ).append("\n")
                                    append(
                                        "${ExifInterface.TAG_SUBFILE_TYPE}：${
                                            exifInterface.getAttribute(
                                                ExifInterface.TAG_SUBFILE_TYPE
                                            )
                                        }"
                                    ).append("\n")
                                    append(
                                        "${ExifInterface.TAG_DNG_VERSION}：${
                                            exifInterface.getAttribute(
                                                ExifInterface.TAG_DNG_VERSION
                                            )
                                        }"
                                    ).append("\n")
                                    append(
                                        "${ExifInterface.TAG_DEFAULT_CROP_SIZE}：${
                                            exifInterface.getAttribute(
                                                ExifInterface.TAG_DEFAULT_CROP_SIZE
                                            )
                                        }"
                                    ).append("\n")
                                    append(
                                        "${ExifInterface.TAG_ORF_THUMBNAIL_IMAGE}：${
                                            exifInterface.getAttribute(
                                                ExifInterface.TAG_ORF_THUMBNAIL_IMAGE
                                            )
                                        }"
                                    ).append("\n")
                                    append(
                                        "${ExifInterface.TAG_ORF_PREVIEW_IMAGE_START}：${
                                            exifInterface.getAttribute(
                                                ExifInterface.TAG_ORF_PREVIEW_IMAGE_START
                                            )
                                        }"
                                    ).append("\n")
                                    append(
                                        "${ExifInterface.TAG_ORF_PREVIEW_IMAGE_LENGTH}：${
                                            exifInterface.getAttribute(
                                                ExifInterface.TAG_ORF_PREVIEW_IMAGE_LENGTH
                                            )
                                        }"
                                    ).append("\n")
                                    append(
                                        "${ExifInterface.TAG_ORF_ASPECT_FRAME}：${
                                            exifInterface.getAttribute(
                                                ExifInterface.TAG_ORF_ASPECT_FRAME
                                            )
                                        }"
                                    ).append("\n")
                                    append(
                                        "${ExifInterface.TAG_RW2_SENSOR_BOTTOM_BORDER}：${
                                            exifInterface.getAttribute(
                                                ExifInterface.TAG_RW2_SENSOR_BOTTOM_BORDER
                                            )
                                        }"
                                    ).append("\n")
                                    append(
                                        "${ExifInterface.TAG_RW2_SENSOR_LEFT_BORDER}：${
                                            exifInterface.getAttribute(
                                                ExifInterface.TAG_RW2_SENSOR_LEFT_BORDER
                                            )
                                        }"
                                    ).append("\n")
                                    append(
                                        "${ExifInterface.TAG_RW2_SENSOR_RIGHT_BORDER}：${
                                            exifInterface.getAttribute(
                                                ExifInterface.TAG_RW2_SENSOR_RIGHT_BORDER
                                            )
                                        }"
                                    ).append("\n")
                                    append(
                                        "${ExifInterface.TAG_RW2_SENSOR_TOP_BORDER}：${
                                            exifInterface.getAttribute(
                                                ExifInterface.TAG_RW2_SENSOR_TOP_BORDER
                                            )
                                        }"
                                    ).append("\n")
                                    append(
                                        "${ExifInterface.TAG_RW2_ISO}：${
                                            exifInterface.getAttribute(
                                                ExifInterface.TAG_RW2_ISO
                                            )
                                        }"
                                    ).append("\n")
                                    append(
                                        "${ExifInterface.TAG_RW2_JPG_FROM_RAW}：${
                                            exifInterface.getAttribute(
                                                ExifInterface.TAG_RW2_JPG_FROM_RAW
                                            )
                                        }"
                                    ).append("\n")
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    append(
                                        "${ExifInterface.TAG_XMP}：${
                                            exifInterface.getAttribute(
                                                ExifInterface.TAG_XMP
                                            )
                                        }"
                                    ).append("\n")
                                }
                            }
                        }
                    Log.i("print_logs", "printImgInfo: $sb")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}