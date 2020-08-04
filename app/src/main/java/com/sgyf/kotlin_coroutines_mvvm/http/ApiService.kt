package com.zs.zs_jetpack.http

import com.zs.wanandroid.entity.*
import retrofit2.http.*

/**
 * @date 2020/5/9
 * @author zs
 */
interface ApiService {

    /**
     * 登录
     */
    @FormUrlEncoded
    @POST("/user/login")
    suspend fun login(@Field("username") username: String,
                      @Field("password") password: String): ApiResponse<UserBean>
}