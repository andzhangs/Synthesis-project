package zs.android.module.widget

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.rebound.*
import zs.android.module.widget.databinding.ActivityReboundBinding

class ReboundActivity : AppCompatActivity(), ItemListDialogFragment.Listener {

    private lateinit var mDataBinding: ActivityReboundBinding
    private var mDialogFragment: ItemListDialogFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = ActivityReboundBinding.inflate(layoutInflater)
        setContentView(mDataBinding.root)

        mDataBinding.acImgView.setOnClickListener {
            singleReboundView()
        }

        mDataBinding.clGroup.setOnClickListener {
            multiReboundView()
        }
        mDataBinding.clGroup.viewTreeObserver.addOnGlobalLayoutListener {
            multiReboundView()
        }

        mDataBinding.acBtnDialog.setOnClickListener {
            mDialogFragment = ItemListDialogFragment.newInstance(8)
                .also { it.show(supportFragmentManager, "dialog") }
        }


    }

    /**
     * 单个动画
     */
    private fun singleReboundView() {
        SpringSystem.create().createSpring().apply {
            currentValue = 1.0
            //tension(拉力)值改成100(拉力值越小，晃动越慢)，friction(摩擦力)值改成1(摩擦力越小，弹动大小越明显)
            springConfig = SpringConfig.fromOrigamiTensionAndFriction(100.0, 1.0)
            //与上面一样，回弹效果更明显，但速度慢
//          setSpringConfig(new SpringConfig(100, 1));

            addListener(object : SimpleSpringListener() {
                override fun onSpringEndStateChange(spring: Spring?) {
                    super.onSpringEndStateChange(spring)
                }

                override fun onSpringUpdate(spring: Spring) {
                    super.onSpringUpdate(spring)
                    val value = spring.currentValue
                    val scale = 1F - (value * 0.5F)
                    with(mDataBinding.acImgView) {
                        scaleX = scale.toFloat()
                        scaleY = scale.toFloat()
                    }
                }
            })
            endValue=0.1
            Toast.makeText(this@ReboundActivity, "点击了！", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 多动画
     */
    private fun multiReboundView() {
        val springChain = SpringChain.create(50, 7, 35, 7)
        val childCount: Int = mDataBinding.clGroup.childCount
        for (i in 0 until childCount) {
            val view: View = mDataBinding.clGroup.getChildAt(i)
            springChain.addSpring(object : SimpleSpringListener() {
                override fun onSpringUpdate(spring: Spring) {
                    super.onSpringUpdate(spring)
                    Log.i("print_logs", "ReboundActivity::onSpringUpdate: ")
                    view.translationY = spring.currentValue.toFloat()
                }

                override fun onSpringAtRest(spring: Spring) {
                    super.onSpringAtRest(spring)
                    Log.i("print_logs", "ReboundActivity::onSpringAtRest: ")
                }

                override fun onSpringActivate(spring: Spring) {
                    super.onSpringActivate(spring)
                    Log.i("print_logs", "ReboundActivity::onSpringActivate: ")

                }

                override fun onSpringEndStateChange(spring: Spring) {
                    Log.i("print_logs", "ReboundActivity::onSpringEndStateChange: ")

                }
            })
        }
        val springs = springChain.allSprings
        for (i in springs.indices) {
            springs[i].currentValue = 400.0
        }
        /**
         * 从第几个子view开始
         */
        springChain.setControlSpringIndex(0)
        /**
         * 设置结束的位置
         */
        springChain.controlSpring.endValue = 0.0
    }

    override fun onItemClicked(position: Int) {
        mDialogFragment?.loadAnimation()
    }

//    animateViewDirection(view, 500, 0, 50, 4);
//    private void animateViewDirection(final View v, float from, float to, double tension, double friction) {
//        Spring spring = SpringSystem.create().createSpring();
//        spring.setCurrentValue(from);
//        spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(tension, friction));
//        spring.addListener(new SimpleSpringListener() {
//            @Override
//            public void onSpringUpdate(Spring spring) {
//                v.setTranslationY((float) spring.getCurrentValue());
//            }
//        });
//        spring.setEndValue(to);
//    }


}