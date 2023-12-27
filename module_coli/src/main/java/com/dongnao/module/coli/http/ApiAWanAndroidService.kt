package com.dongnao.module.coli.http

import com.dongnao.module.coli.model.wanandroid.WanAndroidBean
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 *
 * @author zhangshuai
 * @date 2023/12/25 16:24
 * @description 自定义类描述
 *
 * https://www.wanandroid.com/project/list/1/json?cid=1
 */
interface ApiAWanAndroidService {

    @GET("project/list/{page}/json")
    suspend fun getData(@Path("page") page: Int, @Query("cid") cid: Int): WanAndroidBean?

}