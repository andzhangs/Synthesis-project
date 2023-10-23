package zs.android.module.widget

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import zs.android.module.widget.drag.DragSelectTouchListener2
import zs.android.module.widget.drag.DragSelectionProcessor2
import zs.android.module.widget.drag.TestAutoDataAdapter2
import java.lang.reflect.Array.setLong


class DragSelectActivity : AppCompatActivity() {
    private var mMode = DragSelectionProcessor2.Mode.FirstItemDependent

    private var mDragSelectTouchListener2: DragSelectTouchListener2? = null
    private var mAdapter2: TestAutoDataAdapter2? = null

    private var mDragSelectionProcessor2: DragSelectionProcessor2? = null

    private val mBtnCanceledSelectAll :AppCompatButton by lazy{findViewById(R.id.acBtn_select_all_canceled)}

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drag_select)

        mBtnCanceledSelectAll.setOnClickListener {
            mAdapter2?.apply {
                if (isMultiSelect()) {
                    deselectAll()
                    setMultiSelectEnable(false)
                }
            }
        }

        // 1) Prepare the RecyclerView (init LayoutManager and set Adapter)
        val rvData = findViewById<View>(R.id.rvData) as RecyclerView
        rvData.layoutManager = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
        mAdapter2 = TestAutoDataAdapter2(this, 500)
        rvData.adapter = mAdapter2
        mAdapter2!!.setClickListener(object : TestAutoDataAdapter2.ItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                mAdapter2!!.toggleSelection(position)
            }

            override fun onItemLongClick(view: View?, position: Int): Boolean {
                mAdapter2!!.setMultiSelectEnable(true)
                mDragSelectTouchListener2!!.startDragSelection(position)
                return true
            }
        })

        // 2) Add the DragSelectListener
        mDragSelectionProcessor2 = DragSelectionProcessor2(object : DragSelectionProcessor2.ISelectionHandler {

            override val selection: HashSet<Int> = mAdapter2!!.getSelection()

            override fun isSelected(index: Int): Boolean {
                    return mAdapter2!!.getSelection().contains(index)
                }

                override fun updateSelection(
                    start: Int,
                    end: Int,
                    isSelected: Boolean,
                    calledFromOnStart: Boolean
                ) {
                    mAdapter2!!.selectRange(start, end, isSelected)
                }
            }).withMode(mMode)

        mDragSelectTouchListener2 = DragSelectTouchListener2()
            .withSelectListener(mDragSelectionProcessor2)
            .withScrollAboveTopRegion(true)
            .withScrollBelowTopRegion(true)

//        mDragSelectionProcessor!!.withMode(mMode)

        rvData.addOnItemTouchListener(mDragSelectTouchListener2!!)
    }
}