package zs.android.module.widget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.ImageLoader
import com.github.piasy.biv.loader.glide.GlideImageLoader
import com.github.piasy.biv.view.BigImageView
import com.github.piasy.biv.view.GlideImageViewFactory
import zs.android.module.widget.databinding.ActivityGlideScaleBinding
import java.io.File
import java.lang.Exception

class GlideScaleActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityGlideScaleBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        BigImageViewer.initialize(GlideImageLoader.with(applicationContext))

        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_glide_scale)
        mDataBinding.bigIv.setImageViewFactory(GlideImageViewFactory())
        mDataBinding.bigIv.setInitScaleType(BigImageView.INIT_SCALE_TYPE_CENTER_CROP)
        mDataBinding.bigIv.setImageLoaderCallback(mImageLoader)

//        mDataBinding.bigIv.showImage(Uri.parse("/storage/emulated/0/Camera/icon_scale.webp"))
        // "https://cn.bing.com/th?id=OHR.DeathValleySalt_ZH-CN8438207719_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp"


        val launcher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            it?.also { uri ->
                mDataBinding.bigIv.showImage(uri)
            }
        }

        mDataBinding.acBtnSelectImg.setOnClickListener {
            if (ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable()) {
                launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    }


    private val mImageLoader = object : ImageLoader.Callback {

        override fun onCacheHit(imageType: Int, image: File?) {
            // Image was found in the cache
        }

        override fun onCacheMiss(imageType: Int, image: File?) {
            // Image was downloaded from the network
        }

        override fun onStart() {
            // Image download has started
        }

        override fun onProgress(progress: Int) {
            // Image download progress has changed
        }

        override fun onFinish() {
            // Image download has finished
        }

        override fun onSuccess(image: File?) {
            // Image was retrieved successfully (either from cache or network)
        }

        override fun onFail(error: Exception?) {
            // Image download failed
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}