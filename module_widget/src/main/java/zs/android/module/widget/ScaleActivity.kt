package zs.android.module.widget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.davemorrissey.labs.subscaleview.ImageSource
import zs.android.module.widget.databinding.ActivityScaleBinding

class ScaleActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityScaleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_scale)

//        mDataBinding.acIv.setImage(ImageSource.resource(R.drawable.icon_scale))

        val launcher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            it?.also { uri ->
                mDataBinding.acIv.setImage(ImageSource.uri(uri))
            }
        }

        if (ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable()) {
            launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}