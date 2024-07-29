package com.dongnao.module.coli

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dongnao.module.coli.http.AWanAndroidApiService
import com.dongnao.module.coli.http.ApiManager
import com.dongnao.module.coli.http.HpImageApiService
import com.dongnao.module.coli.model.hpiamge.HpImageBean
import com.dongnao.module.coli.model.wanandroid.WanAndroidBean
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.nio.charset.StandardCharsets

/**
 *
 * @author zhangshuai
 * @date 2023/12/27 14:43
 * @description 自定义类描述
 */
class MainViewModel constructor(private val application: Application) :
    AndroidViewModel(application) {


    companion object {
        const val base_HPImage_Url = "https://cn.bing.com/"
        const val hpImageJson_Path = "HPImage.json"

        const val base_wanAndroid_Url = "https://www.wanandroid.com/"
        const val wanAndroidJson_Path = "WanAndroid.json"
    }


    private val mHpImageApiService: HpImageApiService by lazy {
        ApiManager.getService(
            base_HPImage_Url,
            HpImageApiService::class.java
        )
    }

    private val mAWanAndroidApiService: AWanAndroidApiService by lazy {
        ApiManager.getService(
            base_wanAndroid_Url,
            AWanAndroidApiService::class.java
        )
    }

    private val mGson: Gson by lazy { Gson() }

    val imageLiveData = MutableLiveData<HpImageBean>()

    fun getImageData(isNetWork: Boolean = false) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (isNetWork) {
                    Log.i("print_logs", "MainViewModel::getImageData: 图片-网络")
                    mHpImageApiService.getHPImage("js", 2, 10)
                } else {
                    Log.i("print_logs", "MainViewModel::getImageData: 图片-本地")
                    val jsonString = parseJsonFile(application, hpImageJson_Path)
                    mGson.fromJson(jsonString, HpImageBean::class.java)
                }
            }?.also {
                imageLiveData.value = it
                Logger.json(it.toString())
            }
        }
    }

    //----------------------------------------------------------------------------------------------

    val wanAndroidLiveData = MutableLiveData<WanAndroidBean>()

    fun getWanAndroidData(isNetWork: Boolean = false) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (isNetWork) {
                    Log.d("print_logs", "MainViewModel::getWanAndroidData: 玩安卓-网络")
                    mAWanAndroidApiService.getData(1, 1)
                } else {
                    Log.d("print_logs", "MainViewModel::getWanAndroidData: 玩安卓-本地")
                    val jsonString = parseJsonFile(application, wanAndroidJson_Path)
                    mGson.fromJson(jsonString, WanAndroidBean::class.java)
                }
            }?.also {
                wanAndroidLiveData.value = it
                Logger.json(it.toString())
            }
        }
    }


    //----------------------------------------------------------------------------------------------

    private fun parseJsonFile(context: Context, fileName: String): String? {
        try {
            // 获取AssetManager
            val assetManager = context.assets

            // 打开文件输入流
            val inputStream = assetManager.open(fileName)

            // 从InputStream中读取JSON数据
            val jsonString = readInputStream(inputStream)
            return jsonString
            // 使用JSONObject解析JSON数据
//            return JSONObject(jsonString)
        } catch (e: IOException) {
            e.printStackTrace()
            if (BuildConfig.DEBUG) {
                Log.e("print_logs", "parseJsonFile: $e")
            }
            return null
        }
    }

    private fun readInputStream(inputStream: java.io.InputStream): String {
        return inputStream.use {
            val size = it.available()
            val buffer = ByteArray(size)
            it.read(buffer)
            String(buffer, StandardCharsets.UTF_8)
        }
    }
}