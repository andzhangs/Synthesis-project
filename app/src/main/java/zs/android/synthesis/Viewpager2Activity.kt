package zs.android.synthesis

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import zs.android.synthesis.databinding.ActivityViewpager2Binding
import zs.android.synthesis.fragment.CronetFragment
import zs.android.synthesis.fragment.CrossDeviceFragment
import zs.android.synthesis.fragment.ImageLabelerFragment

class Viewpager2Activity : AppCompatActivity() {

    companion object {
        const val RESULT_VALUE = "result_value"
    }

    private lateinit var mDataBinding: ActivityViewpager2Binding

    private val fragmentList = arrayListOf(
        ImageLabelerFragment.newInstance(),
        CrossDeviceFragment.newInstance(),
        CronetFragment.newInstance()
    )
    private lateinit var mAdapter: ViewPager2Adapter

    private var mCurrentPosition = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_viewpager2)
        loadViewPager2()

        mDataBinding.avBtnBack.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra(RESULT_VALUE, "I'm from Viewpager2Activity!")
            })
            finish()
        }
        mDataBinding.avBtnDelete.setOnClickListener {
            if (BuildConfig.DEBUG) {
                Log.d(
                    "print_logs",
                    "delete: $mCurrentPosition, ${fragmentList[mCurrentPosition].javaClass.simpleName}"
                )
            }
            mAdapter.removeItem(mCurrentPosition)
        }
    }

    private fun loadViewPager2() {
        mAdapter = ViewPager2Adapter(this@Viewpager2Activity, fragmentList)
        with(mDataBinding.acIvViewPager2) {
            offscreenPageLimit = fragmentList.size
            adapter = mAdapter
            registerOnPageChangeCallback(mPagerChangeListener)
            mAdapter.registerAdapterDataObserver(mAdapterDataObserver)
        }
    }

    private val mPagerChangeListener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            mCurrentPosition = position
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onPageSelected.mCurrentPosition: $mCurrentPosition")
            }
        }
    }

    private val mAdapterDataObserver = object : RecyclerView.AdapterDataObserver() {

        override fun onChanged() {
            super.onChanged()
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onChanged: ")
            }
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            super.onItemRangeChanged(positionStart, itemCount)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onItemRangeChanged1-: $positionStart, $itemCount")
            }
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            super.onItemRangeChanged(positionStart, itemCount, payload)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onItemRangeChanged-2: $positionStart, $itemCount, $payload")
            }
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onItemRangeInserted: $positionStart, $itemCount")
            }
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onItemRangeRemoved-1: $positionStart, $itemCount")
            }
            mAdapter.notifyDataSetChanged()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount)
            if (BuildConfig.DEBUG) {
                Log.i("print_logs", "onItemRangeMoved-2: $fromPosition, $toPosition, $itemCount")
            }
        }
    }

    class ViewPager2Adapter(
        activity: FragmentActivity,
        private val fragments: ArrayList<Fragment>
    ) :
        FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = fragments.size

        override fun createFragment(position: Int): Fragment = fragments[position]

        override fun getItemId(position: Int): Long {
            if (fragments.size == 0 || fragments.size <= position) {
                return 0
            }
            return fragments[position].hashCode().toLong()
        }

        fun removeItem(position: Int) {
            fragments.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        with(mDataBinding.acIvViewPager2) {
            unregisterOnPageChangeCallback(mPagerChangeListener)
            mAdapter.unregisterAdapterDataObserver(mAdapterDataObserver)
        }

    }
}