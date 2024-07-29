package com.dongnao.module.coli.http

import com.dongnao.module.coli.model.hpiamge.HpImageBean
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *
 * @author zhangshuai
 * @date 2023/12/25 16:24
 * @description 自定义类描述
 */
interface HpImageApiService {

    @GET("HPImageArchive.aspx") //format=js&idx=1&n=100
    suspend fun getHPImage(
        @Query("format") format: String,
        @Query("dx") dx: Int,
        @Query("n") n: Int
    ): HpImageBean?

}