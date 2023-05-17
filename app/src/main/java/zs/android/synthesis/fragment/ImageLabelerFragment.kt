package zs.android.synthesis.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.google.ambient.crossdevice.discovery.Discovery
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import zs.android.synthesis.R
import zs.android.synthesis.extend.assetsToBitmap

/**
 *
 */
class ImageLabelerFragment : Fragment() {

    private lateinit var mRootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mRootView = inflater.inflate(R.layout.fragment_image_labeler, container, false)

        val img = mRootView.findViewById<AppCompatImageView>(R.id.acIv_img)
        val bitmap = context?.assetsToBitmap("9426826.jpg")
        img.setImageBitmap(bitmap)

        val textOutput = mRootView.findViewById<AppCompatTextView>(R.id.acTv_info)
        val btnGetInfo = mRootView.findViewById<AppCompatButton>(R.id.acBtn_get_info)

        btnGetInfo.setOnClickListener {
            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
            val image = InputImage.fromBitmap(bitmap!!, 0)
            var outPutText = ""
            labeler.process(image)
                .addOnSuccessListener {
                    it.forEach { imageLabel ->
                        val index = imageLabel.index
                        val text = imageLabel.text
                        val confidence = imageLabel.confidence
                        outPutText += "$index，$text, $confidence \n"
                    }
                    textOutput.text = outPutText
                }.addOnFailureListener {
                    Log.e("print_logs", "MainActivity::onCreate: $it")
                }
        }
        return mRootView
    }

    companion object {

        @JvmStatic
        fun newInstance() = ImageLabelerFragment()
    }
}