package zs.android.module.constraintlayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.databinding.DataBindingUtil
import zs.android.module.constraintlayout.databinding.ActivityLayerBinding

class LayerActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityLayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_layer)
        clickMethod()
    }

    private fun clickMethod() {
        mDataBinding.ImgFv1.setOnClickListener {
            val imgView = it as ImageFilterView
            imgView.round = if (imgView.round == 16f) {
                50f
            } else {
                16f
            }
        }
        mDataBinding.ImgFv2.setOnClickListener {
            val imgView = it as ImageFilterView
            imgView.roundPercent = if (imgView.roundPercent == 1f) {
                0.5f
            } else {
                1f
            }
        }
        mDataBinding.ImgFv3.setOnClickListener {
            val imgView = it as ImageFilterView
            imgView.imageZoom = if (imgView.imageZoom == 0.5f) {
                1.5f
            } else {
                0.5f
            }
        }
        mDataBinding.ImgFv4.setOnClickListener {
            val imgView = it as ImageFilterView
            imgView.imageRotate = if (imgView.imageRotate == 100f) {
                40f
            } else {
                100f
            }
        }
        mDataBinding.ImgFv5.setOnClickListener {
            val imgView = it as ImageFilterView
            imgView.crossfade = when (imgView.crossfade) {
                0f -> {
                    0.5f
                }

                0.5f -> {
                    1f
                }

                1f -> {
                    0f
                }

                else -> {
                    0.5f
                }
            }

        }
        mDataBinding.ImgFv6.setOnClickListener {
            val imgView = it as ImageFilterView
            imgView.saturation = if (imgView.saturation == 0f) {
                1f
            } else {
                0f
            }
        }
        mDataBinding.ImgFv7.setOnClickListener {
            val imgView = it as ImageFilterView
            imgView.brightness = when (imgView.brightness) {
                1f -> {
                    2f
                }

                2f -> {
                    3f
                }

                3f -> {
                    1f
                }

                else -> {
                    1f
                }
            }
        }
        mDataBinding.ImgFv8.setOnClickListener {
            val imgView = it as ImageFilterView
            imgView.warmth = when (imgView.warmth) {
                0f -> {
                    1f
                }

                1f -> {
                    2f
                }

                2f -> {
                    3f
                }

                3f -> {
                    0f
                }

                else -> {
                    1f
                }
            }
        }
        mDataBinding.ImgFv9.setOnClickListener {
            val imgView = it as ImageFilterView
            imgView.contrast = when (imgView.contrast) {
                0f -> {
                    1f
                }

                1f -> {
                    2f
                }

                2f -> {
                    0f
                }

                else -> {
                    1f
                }
            }
        }
        mDataBinding.ImgFv10.setOnClickListener {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}