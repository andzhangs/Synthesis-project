package com.module.other

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.module.other.able.ResponseCloseable
import com.module.other.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
//        loadBackPressed()
//        loadCloseableAndCloneable()

        val mOldList = arrayListOf(1, 2, 3, 4, 5, 6)
        val mNewList = arrayListOf(1, 2, 3, 4, 5, 7, 8, 6, 9)

        val mDiffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return mOldList.size
            }

            override fun getNewListSize(): Int {
                return mNewList.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return mOldList[oldItemPosition] == mNewList[newItemPosition]
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return mOldList[oldItemPosition] == mNewList[newItemPosition]
            }

            override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
                val oldItem = mOldList[oldItemPosition]
                val newItem = mNewList[newItemPosition]

                if (BuildConfig.DEBUG) {
                    Log.d("print_logs", "getChangePayload: $oldItem, $newItem")
                }
                return null //super.getChangePayload(oldItemPosition, newItemPosition)
            }
        }
        val diffResult = DiffUtil.calculateDiff(mDiffCallback)

        diffResult.dispatchUpdatesTo(object : ListUpdateCallback {

            private val mCacheList = ArrayList<Int>()

            override fun onInserted(position: Int, count: Int) {
                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "onInserted: position= $position, count= $count")
                }

                if (BuildConfig.DEBUG) {
                    Log.i("print_logs", "MainActivity::onInserted: $mCacheList")
                }
            }

            override fun onRemoved(position: Int, count: Int) {
                if (BuildConfig.DEBUG) {
                    Log.e("print_logs", "删除: ${mOldList[position]}")
                }
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                if (BuildConfig.DEBUG) {
                    Log.v(
                        "print_logs",
                        "onMoved: fromPosition= $fromPosition, toPosition= $toPosition"
                    )
                }
            }

            override fun onChanged(position: Int, count: Int, payload: Any?) {
                if (BuildConfig.DEBUG) {
                    Log.w(
                        "print_logs",
                        "onChanged: position= $position, count= $count, payload= $payload"
                    )
                }
            }
        })
    }

    private fun loadBackPressed() {
        val backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.i("print_logs", "handleOnBackPressed: ")
            }
        }

        //锁定返回键
        onBackPressedDispatcher.addCallback(backPressedCallback)

        mDataBinding.acBtnBackPressed.setOnClickListener {
            Log.i(
                "print_logs",
                "handleOnBackPressed: ${backPressedCallback.handleOnBackPressed()}"
            )
            backPressedCallback.remove()
            Toast.makeText(this, "再按一次退出!", Toast.LENGTH_SHORT).show()
        }
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

    external fun getString(): String
}