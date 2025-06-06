package zs.android.module.widget

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ChannelResult
import kotlinx.coroutines.flow.internal.ChannelFlow
import zs.android.base.lib.util.DataSize.Companion.BYTES
import zs.android.base.lib.util.DataSize.Companion.toDataSize
import zs.android.base.lib.util.DataUnit
import zs.android.module.widget.able.ResponseCloseable
import zs.android.module.widget.blur.BlurViewActivity
import zs.android.module.widget.blur.BlurryActivity
import zs.android.module.widget.blur.RealtimeBlurViewActivity
import zs.android.module.widget.blur.ShapeBlurViewActivity
import zs.android.module.widget.databinding.ActivityMainBinding
import zs.android.module.widget.databinding.ItemMainLayoutBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding
    private val mList: ArrayList<String> by lazy {
        arrayListOf(
            "SnackBar-底部提示信息",
            "自定义Toast",
            "Rebound",
            "滑动多选图片-DragSelectActivity",
            "Blurry",
            "BlurView",
            "RealtimeBlurView",
            "ShapeBlurView",
            "ScaleActivity",
            "GlideScaleActivity",
            "AlertActivity",
            "ViewFlipperActivity",
            "BannerActivity",
            "FlipActivity",
            "LinearActivity",
            "Coordinator+Nested",
            "PagerActivity",
            "DownloadActivity",
            "TextBytesActivity",
            "CrossDeviceActivity",
            "BatteryFlowActivity",
            "NetworkFlowActivity",
            "WebActivity",
            "SeekBarBitmapActivity"
        )
    }
    private val mAdapter = RvAdapter(mList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mDataBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mDataBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(mDataBinding.clRoot) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadRv()

        mDataBinding.progressBar.setFirstWidth(0.5f)
        mDataBinding.progressBar.setSecondWidth(0.2f)
    }

    private fun loadRv() {
        mAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onClick(position: Int, item: View) {
                when (position) {
                    0 -> {
//                        val result = (100.0.BYTES + 200.0.BYTES).toLong(DataUnit.BYTES)
//                        Toast.makeText(
//                            this@MainActivity,
//                            "DataSize：$result， ${1024.0.toDataSize(DataUnit.BYTES)}",
//                            Toast.LENGTH_SHORT
//                        ).show()

                        showSnackBar(item)
                    }

                    1 -> {
                        showToast()
                    }

                    2 -> {
                        jumpActivity(ReboundActivity::class.java)
                    }

                    3 -> {
                        jumpActivity(DragSelectActivity::class.java)
                    }

                    4 -> {
                        jumpActivity(BlurryActivity::class.java)
                    }

                    5 -> {
                        jumpActivity(BlurViewActivity::class.java)
                    }

                    6 -> {
                        jumpActivity(RealtimeBlurViewActivity::class.java)
                    }

                    7 -> {
                        jumpActivity(ShapeBlurViewActivity::class.java)
                    }

                    8 -> {
                        jumpActivity(ScaleActivity::class.java)
                    }

                    9 -> {
                        if (ContextCompat.checkSelfPermission(
                                this@MainActivity,
                                Manifest.permission.READ_MEDIA_IMAGES
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            jumpActivity(GlideScaleActivity::class.java)
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                ActivityCompat.requestPermissions(
                                    this@MainActivity,
                                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                                    100
                                )
                            }else{
                                ActivityCompat.requestPermissions(
                                    this@MainActivity,
                                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                    100
                                )
                            }
                        }
                    }

                    10 -> {
                        jumpActivity(AlertActivity::class.java)
                    }

                    11 -> {
                        jumpActivity(ViewFlipperActivity::class.java)
                    }

                    12 -> {
                        jumpActivity(BannerActivity::class.java)
                    }

                    13 -> {
                        jumpActivity(FlipActivity::class.java)
                    }

                    14 -> {
                        jumpActivity(LinearActivity::class.java)
                    }

                    15 -> {
                        jumpActivity(CoordinatorLayoutNestedScrollViewActivity::class.java)
                    }

                    16 -> {
                        jumpActivity(PagerActivity::class.java)
                    }

                    17 -> {
                        jumpActivity(DownloadActivity::class.java)
                    }

                    18 -> {
                        jumpActivity(TextBytesActivity::class.java)
                    }

                    19 -> {
                        jumpActivity(CrossDeviceActivity::class.java)
                    }

                    20 -> {
                        jumpActivity(BatteryFlowActivity::class.java)
                    }

                    21 -> {
                        jumpActivity(NetworkFlowActivity::class.java)
                    }

                    22 -> {
                        jumpActivity(WebActivity::class.java)
                    }

                    23 -> {
                        jumpActivity(SeekBarBitmapActivity::class.java)
                    }

                    else -> {}
                }
            }
        })

        with(mDataBinding.recyclerView) {
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(this@MainActivity, RecyclerView.VERTICAL))
            adapter = mAdapter
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            jumpActivity(GlideScaleActivity::class.java)
        }
    }

    private inner class RvAdapter(private val list: ArrayList<String>) :
        RecyclerView.Adapter<ItemViewHolder>() {

        private var mItemClickListener: OnItemClickListener? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val inflater = LayoutInflater.from(this@MainActivity)
            val itemMainLayoutBinding = DataBindingUtil.inflate<ItemMainLayoutBinding>(
                inflater,
                R.layout.item_main_layout,
                null,
                false
            )
            return ItemViewHolder(itemMainLayoutBinding)
        }

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            holder.setText(position, list[position])
            holder.setOnItemClickListener(mItemClickListener)
        }

        fun setOnItemClickListener(listener: OnItemClickListener?) {
            this.mItemClickListener = listener
        }
    }

    private class ItemViewHolder(private val mBinding: ItemMainLayoutBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        private var mItemClickListener: OnItemClickListener? = null
        private var mItemPosition: Int = 0

        init {
            mBinding.acTvContent.setOnClickListener {
                this.mItemClickListener?.onClick(mItemPosition, mBinding.root)
            }
        }

        fun setOnItemClickListener(listener: OnItemClickListener?) {
            this.mItemClickListener = listener
        }

        fun setText(position: Int, data: String) {
            this.mItemPosition = position
            mBinding.contentTxt = data
        }
    }


    interface OnItemClickListener {
        fun onClick(position: Int, item: View)
    }

    //----------------------------------------------------------------------------------------------

    private fun showSnackBar(view: View) {
        Snackbar.make(this, view, "你哈", Snackbar.LENGTH_LONG)
            .setAction("取消") {
                Toast.makeText(this@MainActivity, "触发 点击", Toast.LENGTH_SHORT).show()
            }
            .setActionTextColor(Color.GREEN)
            .setTextColor(Color.BLACK)
            .setBackgroundTint(Color.WHITE)
            .setAnchorView(view)
            .setDuration(Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun showToast() {
        val mToast: Toast by lazy {
            val view = AppCompatTextView(this).apply {
                text = "哈哈哈"
                setPadding(15, 10, 15, 10)
                setBackgroundColor(Color.BLACK)
                setTextColor(Color.WHITE)
                textSize = 18f
                gravity = Gravity.CENTER

                val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val point = Point().apply {

                }
                windowManager.defaultDisplay.getSize(point)

                minWidth = point.x
                minHeight = 48 * 2
            }

            Toast(this).apply {
                duration = Toast.LENGTH_SHORT
                setView(view)
                setGravity(Gravity.BOTTOM, 0, -50)
            }
        }

        mToast.view?.apply {
            if (!isShown) {
                mToast.show()
            } else {
                mToast.cancel()
            }
        }
    }

    private fun jumpActivity(targetActivity: Class<*>) {
        startActivity(Intent(this, targetActivity))
    }

    private fun loadBackPressed() {
        val backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.i("print_logs", "handleOnBackPressed: ")
            }
        }

        //锁定返回键
        onBackPressedDispatcher.addCallback(backPressedCallback)

//        mDataBinding.acBtnBackPressed.setOnClickListener {
//            Log.i(
//                "print_logs",
//                "handleOnBackPressed: ${backPressedCallback.handleOnBackPressed()}"
//            )
//            backPressedCallback.remove()
//            Toast.makeText(this, "再按一次退出!", Toast.LENGTH_SHORT).show()
//        }
    }

    private fun loadCloseableAndCloneable() {
        val responseCloseable = ResponseCloseable()
        responseCloseable.name = "Hello World!"
        responseCloseable.age = 18
        responseCloseable.toString()
        try {
            responseCloseable.use {
                it.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}