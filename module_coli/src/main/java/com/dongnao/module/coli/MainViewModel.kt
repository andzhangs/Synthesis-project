package com.dongnao.module.coli

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dongnao.module.coli.http.ApiAWanAndroidService
import com.dongnao.module.coli.http.ApiGithubService
import com.dongnao.module.coli.http.ApiManager
import com.dongnao.module.coli.http.ApiMovieService
import com.dongnao.module.coli.model.movie.MovieBean
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

    private val mApiMovieService: ApiMovieService by lazy {
        ApiManager.getService(
            "https://api.vvhan.com/",
            ApiMovieService::class.java
        )
    }

    private val mApiAWanAndroidService: ApiAWanAndroidService by lazy {
        ApiManager.getService(
            "https://www.wanandroid.com/",
            ApiAWanAndroidService::class.java
        )
    }

    private val mApiGithubService: ApiGithubService by lazy {
        ApiManager.getService("https://api.github.com//", ApiGithubService::class.java)
    }

    fun getUserData() {
        viewModelScope.launch(Dispatchers.IO) {
//            mApiGithubService.getData()?.also {
//                Log.i("print_logs", "MainViewModel::getUserData: $it")
//            }
        }
    }

    private val mGson: Gson by lazy { Gson() }

    val movieLiveData = MutableLiveData<MovieBean>()

    fun getMovieData(isLocal: Boolean = true) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (isLocal) {
                    Log.i("print_logs", "MainViewModel::getMovieData: 电影-本地")
                    val jsonString = parseJsonFile(application, "Movie.json")
                    mGson.fromJson(jsonString, MovieBean::class.java)
                } else {
                    Log.i("print_logs", "MainViewModel::getMovieData: 电影-网络")
                    mApiMovieService.getData()
                }
            }?.also {
                movieLiveData.value = it
                Logger.json(it.toString())
            }
        }
    }

    //----------------------------------------------------------------------------------------------

    val wanAndroidLiveData = MutableLiveData<WanAndroidBean>()

    fun getWanAndroidData(isLocal: Boolean = true) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (isLocal) {
                    Log.d("print_logs", "MainViewModel::getWanAndroidData: 玩安卓-本地")
                    val jsonString = parseJsonFile(application, "WanAndroid.json")
                    mGson.fromJson(jsonString, WanAndroidBean::class.java)
                } else {
                    Log.d("print_logs", "MainViewModel::getWanAndroidData: 玩安卓-网络")
                    mApiAWanAndroidService.getData(1, 1)
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
            return null
        }
    }

    private fun readInputStream(inputStream: java.io.InputStream): String {
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer, StandardCharsets.UTF_8)
    }
}