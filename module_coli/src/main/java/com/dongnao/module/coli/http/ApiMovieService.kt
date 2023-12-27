package com.dongnao.module.coli.http

import com.dongnao.module.coli.model.movie.MovieBean
import retrofit2.http.GET

/**
 *
 * @author zhangshuai
 * @date 2023/12/25 16:24
 * @description 自定义类描述
 */
interface ApiMovieService {

    @GET("api/douban")
    suspend fun getData(): MovieBean?

}