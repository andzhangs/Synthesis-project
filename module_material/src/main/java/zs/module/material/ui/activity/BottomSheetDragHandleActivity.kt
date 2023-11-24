package zs.module.material.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.BuildConfig
import com.google.android.material.bottomsheet.BottomSheetBehavior
import zs.module.material.R
import zs.module.material.databinding.ActivityBottomSheetDragHandleBinding

class BottomSheetDragHandleActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityBottomSheetDragHandleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_bottom_sheet_drag_handle)

        with(BottomSheetBehavior.from(mDataBinding.bottomSheet)) {
            state = BottomSheetBehavior.STATE_HIDDEN
            peekHeight = 120
            isHideable = false

            addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_SETTLING -> {
                            // 底部表单折叠时的操作
                            if (BuildConfig.DEBUG) {
                                Log.i(
                                    "print_logs",
                                    "BottomSheetDragHandleActivity::onStateChanged: 底部表单折叠时的操作"
                                )
                            }
                        }

                        BottomSheetBehavior.STATE_EXPANDED -> {
                            // 底部表单展开时的操作
                            if (BuildConfig.DEBUG) {
                                Log.i(
                                    "print_logs",
                                    "BottomSheetDragHandleActivity::onStateChanged: 底部表单展开时的操作"
                                )
                            }
                        }

                        BottomSheetBehavior.STATE_HIDDEN -> {
                            // 底部表单隐藏时的操作
                            if (BuildConfig.DEBUG) {
                                Log.i(
                                    "print_logs",
                                    "BottomSheetDragHandleActivity::onStateChanged: 底部表单隐藏时的操作"
                                )
                            }
                        }

                        BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                            if (BuildConfig.DEBUG) {
                                Log.i(
                                    "print_logs",
                                    "BottomSheetDragHandleActivity::onStateChanged: "
                                )
                            }
                        }


//                        BottomSheetBehavior.STATE_DRAGGING -> {
//                            if (BuildConfig.DEBUG) {
//                                Log.i(
//                                    "print_logs",
//                                    "BottomSheetDragHandleActivity::onStateChanged: "
//                                )
//                            }
//                        }
//
//                        BottomSheetBehavior.STATE_SETTLING -> {
//                            if (BuildConfig.DEBUG) {
//                                Log.i(
//                                    "print_logs",
//                                    "BottomSheetDragHandleActivity::onStateChanged: "
//                                )
//                            }
//                        }

                        else -> {
                            if (BuildConfig.DEBUG) {
                                Log.i(
                                    "print_logs",
                                    "BottomSheetDragHandleActivity::onStateChanged: "
                                )
                            }
                        }
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    // 处理拖动过程中的操作
                    if (BuildConfig.DEBUG) {
//                    Log.i("print_logs", "BottomSheetDragHandleActivity::onSlide: ")
                    }
                }
            })
        }
//        loadDragHandle()
    }

//    private fun loadDragHandle(){
//        with(mDataBinding.dragHandle) {
//            setOnClickListener {
//                Toast.makeText(this@BottomSheetDragHandleActivity, "点击了！", Toast.LENGTH_SHORT)
//                    .show()
//            }
//            setBackgroundColor(Color.CYAN)
//            elevation = 16f
//            setOnDragListener { _, event ->
//                when (event.action) {
//                    DragEvent.ACTION_DRAG_STARTED -> {
//                        //拖动开始时的操作
//                        if (BuildConfig.DEBUG) {
//                            Log.i("print_logs", "BottomSheetDragHandleActivity::onCreate: ")
//                        }
//                    }
//
//                    DragEvent.ACTION_DRAG_LOCATION -> {
//                        // 拖动位置改变时的操作
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "BottomSheetDragHandleActivity::onCreate: 拖动位置改变时的操作"
//                            )
//                        }
//                    }
//
//                    DragEvent.ACTION_DRAG_ENDED -> {
//                        // 拖动结束时的操作
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "BottomSheetDragHandleActivity::onCreate: 拖动结束时的操作"
//                            )
//                        }
//                    }
//
//                    DragEvent.ACTION_DRAG_ENTERED -> {
//
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "BottomSheetDragHandleActivity::onCreate: 进入拖动行为"
//                            )
//                        }
//                    }
//
//                    DragEvent.ACTION_DRAG_EXITED -> {
//                        if (BuildConfig.DEBUG) {
//                            Log.i(
//                                "print_logs",
//                                "BottomSheetDragHandleActivity::onCreate: 退出拖动行为"
//                            )
//                        }
//                    }
//
//                    else -> {}
//                }
//                true
//            }
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}