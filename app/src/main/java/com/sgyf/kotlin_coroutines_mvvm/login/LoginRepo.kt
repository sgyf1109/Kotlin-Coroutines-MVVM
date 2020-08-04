package com.sgyf.kotlin_coroutines_mvvm.login

import androidx.lifecycle.MutableLiveData
import com.sgyf.kotlin_coroutines_mvvm.constants.Constants
import com.zs.base_library.base.BaseRepository
import com.zs.base_library.http.ApiException
import com.zs.base_library.utils.PrefUtils
import com.zs.wanandroid.entity.UserBean

import com.zs.zs_jetpack.http.ApiService
import com.zs.zs_jetpack.http.RetrofitManager
import kotlinx.coroutines.CoroutineScope
import org.greenrobot.eventbus.EventBus

/**
 * des 登陆
 * @date 2020/7/9
 * @author zs
 */
class LoginRepo(coroutineScope: CoroutineScope, errorLiveData: MutableLiveData<ApiException>) :
    BaseRepository(coroutineScope, errorLiveData) {

    fun login(username: String, password: String,loginLiveData : MutableLiveData<UserBean>) {
        launch(
            block = {
                RetrofitManager.getApiService(ApiService::class.java)
                    .login(username,password)
                    .data()
            },
            success = {
                //登陆成功保存用户信息，并发送消息
                PrefUtils.setObject(Constants.USER_INFO,it)
                //更改登陆状态
                PrefUtils.setBoolean(Constants.LOGIN,true)
                loginLiveData.postValue(it)
            }
        )
    }

}