package com.andorid.module.pick

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.selection.BandPredicate
import androidx.recyclerview.selection.FocusDelegate
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andorid.module.pick.selection.ImageAdapter
import com.andorid.module.pick.selection.ImageBean
import com.andorid.module.pick.selection.base.BaseItemDetailsLookup
import com.andorid.module.pick.selection.base.BaseItemKeyProvider
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var mShowInfo: AppCompatTextView

    private lateinit var mRecyclerView: RecyclerView
    private val mList = ArrayList<ImageBean>()
    private val mAdapter = ImageAdapter(mList)
    private lateinit var mSelectionTracker: SelectionTracker<ImageBean>

    private val KEY_CACHE = "cache_list"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        savedInstanceState?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val list = it.getParcelableArrayList(KEY_CACHE, ImageBean::class.java)
                if (list != null) {
                    mList.addAll(list)
                }
            } else {

            }
        }

        mShowInfo = findViewById(R.id.acTv_selected_info)

        createFile()
        selectSingle()
        selectMultiple()
        initRecyclerView()
    }

    //android-Q 新建文件
    private fun createFile() {
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                this.contentResolver.apply {
                    val contentValues = ContentValues().apply {
                        put(
                            MediaStore.MediaColumns.DISPLAY_NAME,
                            "content_${SystemClock.elapsedRealtimeNanos()}"
                        )
                        put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                        put(
                            MediaStore.MediaColumns.RELATIVE_PATH,
                            "${Environment.DIRECTORY_DOCUMENTS}${File.separator}${packageName}${File.separator}text"
                        )
                    }

                    val mUri = insert(MediaStore.Files.getContentUri("external"), contentValues)
                    mUri?.let { it1 ->
                        val outputStream = openOutputStream(it1)
                        outputStream?.write("${mShowInfo.text}".toByteArray())
                        outputStream?.close()
                        query(it1, null, null, null, null)?.also { cursor ->
                            while (cursor.moveToNext()) {
                                val path =
                                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                                if (BuildConfig.DEBUG) {
                                    Log.i("print_logs", "onCreate:path= $path")
                                }
                            }
                        }

                    }

                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "onCreate:mUri= $mUri")
                    }
                }
            }

        //创建文件
        findViewById<AppCompatButton>(R.id.acTv_Adapter_Q).setOnClickListener {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }

    //单项图片选择器
    private fun selectSingle() {
        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            if (it != null) {
                mList.clear()
                mList.add(ImageBean(it))
                mAdapter.notifyDataSetChanged()
            }
        }
        findViewById<AppCompatButton>(R.id.onPickVisualMedia).setOnClickListener {
            //验证照片选择器在设备上是否可用
            if (ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(this)) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
//            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))

//            val mimetype="image/gif"
//            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.SingleMimeType(mimetype)))
            } else {
                Log.i("print_logs", "setCallback: 系统不适用")
            }
        }
    }

    //多项图片选择器
    private fun selectMultiple() {
        val pickMultipleMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(10)) { list ->
                if (list.isNotEmpty()) {
                    mList.clear()
                    list.forEach { uri ->
                        mList.add(ImageBean(uri))
                    }
                    mAdapter.notifyDataSetChanged()
                }
            }
        findViewById<AppCompatButton>(R.id.onPickMultipleVisualMedia).setOnClickListener {
            //验证照片选择器在设备上是否可用
            if (ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable(this)) {
                pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
            } else {
                Log.i("print_logs", "setCallback: 系统不适用")
            }
        }
    }

    //recyclerview-selection
    private fun initRecyclerView() {
        mRecyclerView = findViewById(R.id.recyclerview)
        mRecyclerView.addItemDecoration(DividerItemDecoration(this, 1))
        mRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mRecyclerView.adapter = mAdapter

        mSelectionTracker = SelectionTracker.Builder(
            "image-items-selection",
            mRecyclerView,
            BaseItemKeyProvider(mList),
            BaseItemDetailsLookup(mRecyclerView),
            StorageStrategy.createParcelableStorage(ImageBean::class.java)
        ).withSelectionPredicate(SelectionPredicates.createSelectAnything()).build()
        //单选：SelectionPredicates.createSelectSingleAnything()
        //多选：SelectionPredicates.createSelectAnything()
        mAdapter.setSelectionTracker(mSelectionTracker)

//        mAdapter.setOnItemClickListener(object :
//            ImageAdapter.OnItemClickListener<ImageBean> {
//            override fun onClick(position: Int, bean: ImageBean) {
//                Log.i("print_logs", "onClick: $position, $bean")
//                if (!mSelectionTracker.isSelected(bean)) {
//                    Log.i("print_logs", "onClick: ")
//                    mSelectionTracker.select(bean)
//                }
//            }
//        })

        mSelectionTracker.addObserver(object : SelectionTracker.SelectionObserver<ImageBean>() {

            private var mLastKey: ImageBean? = null

            override fun onItemStateChanged(key: ImageBean, currentSelected: Boolean) {
                super.onItemStateChanged(key, currentSelected)
                if (BuildConfig.DEBUG) {
                    Log.i(
                        "print_logs",
                        "MainActivity::onItemStateChanged: ${key.uri}, $currentSelected"
                    )
                }

                if (!mSelectionTracker.hasSelection()) {
                    mSelectionTracker.select(key)
                }


                this.mLastKey = key
            }

            override fun onSelectionRefresh() {
                super.onSelectionRefresh()
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "MainActivity::onSelectionRefresh: ")
                }
            }

            override fun onSelectionChanged() {
                super.onSelectionChanged()
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "MainActivity::onSelectionChanged: ")
                }


            }

            override fun onSelectionRestored() {
                super.onSelectionRestored()
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "MainActivity::onSelectionRestored: ")
                }
            }
        })

        //是否有选中的
        findViewById<AppCompatButton>(R.id.acBtn_is_has_selected).setOnClickListener {
            Toast.makeText(
                this,
                "有选中的？：${mSelectionTracker.hasSelection()}",
                Toast.LENGTH_SHORT
            ).show()
        }

        //获取选中的数据

        findViewById<AppCompatButton>(R.id.acBtn_get_selected).setOnClickListener {
            val sb = StringBuilder()
            mSelectionTracker.selection.forEach {
                if (BuildConfig.DEBUG) {
                    sb.append(it.toString()).append("\n")
                }
            }

            mShowInfo.text = sb.toString()
        }

        //选中第二个
        findViewById<AppCompatButton>(R.id.acBtn_selected_second).setOnClickListener {
            if (mList.isNotEmpty() && mList.size >= 2) {
                mSelectionTracker.select(mList[1])
            }
        }

        //取消选中第二个
        findViewById<AppCompatButton>(R.id.acBtn_unselected_second).setOnClickListener {
            if (mList.isNotEmpty() && mList.size >= 2) {
                mSelectionTracker.deselect(mList[1])
            }
        }

        //清除选中的
        findViewById<AppCompatButton>(R.id.acBtn_clear_selected).setOnClickListener {
            mSelectionTracker.clearSelection()
        }

    }

    /**
     * 旋转后恢复数据
     * @param savedInstanceState Bundle
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mSelectionTracker.onRestoreInstanceState(savedInstanceState)
    }

    //旋转时保存数据
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mSelectionTracker.onSaveInstanceState(outState)
        outState.putParcelableArrayList(KEY_CACHE, mList)
    }


    //自定义选择规则
    private val custom = object : SelectionTracker.SelectionPredicate<ImageBean>() {

        override fun canSetStateForKey(key: ImageBean, nextState: Boolean): Boolean {
            if (BuildConfig.DEBUG) {
                Log.d("print_logs", "canSetStateForKey: $nextState")
            }
            return true
        }

        override fun canSetStateAtPosition(position: Int, nextState: Boolean): Boolean {
            if (BuildConfig.DEBUG) {
                Log.d("print_logs", "canSetStateAtPosition: $position, $nextState")
            }
            return true
        }

        override fun canSelectMultiple(): Boolean {
            if (BuildConfig.DEBUG) {
                Log.d("print_logs", "canSelectMultiple: ")
            }
            return true
        }
    }

}