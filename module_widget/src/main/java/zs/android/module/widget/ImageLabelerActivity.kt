package zs.android.module.widget

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import zs.android.module.widget.ext.assetsToBitmap

class ImageLabelerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_labeler)

        val img = findViewById<AppCompatImageView>(R.id.acIv_img)
        val bitmap = assetsToBitmap("9426826.png")
        img.setImageBitmap(bitmap)

        val textOutput = findViewById<AppCompatTextView>(R.id.acTv_info)
        val btnGetInfo = findViewById<AppCompatButton>(R.id.acBtn_get_info)

        btnGetInfo.setOnClickListener {
            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
            val image = InputImage.fromBitmap(bitmap!!, 0)
            val stringBuilder = StringBuilder()
            labeler.process(image)
                .addOnSuccessListener {
                    it.forEach { imageLabel ->
                        val index = imageLabel.index
                        val text = imageLabel.text
                        val confidence = imageLabel.confidence
                        stringBuilder.append("$index，$text, $confidence," ).append("\n")
                    }
                    textOutput.text = stringBuilder.toString()
                }.addOnFailureListener {
                    Log.e("print_logs", "MainActivity::onCreate: $it")
                }
        }
    }
}