package com.module.koin.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import com.module.koin.BuildConfig

/**
 *
 * @author zhangshuai
 * @date 2023/8/18 14:09
 * @mark 自定义类描述
 */
class DetailParamsViewModel(id: String) : ViewModel() {

    init {
        if (BuildConfig.DEBUG) {
            Log.i("print_logs", "DetailParamsViewModel:::id= $id")
        }
    }
}