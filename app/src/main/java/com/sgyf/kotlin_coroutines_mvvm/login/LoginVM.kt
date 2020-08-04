package com.sgyf.kotlin_coroutines_mvvm.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zs.base_library.base.BaseViewModel
import com.zs.wanandroid.entity.UserBean

class LoginVM :BaseViewModel(){
    /**
     * 登陆
     */
    val loginLiveData = MutableLiveData<UserBean>()

    private val repo by lazy { LoginRepo(viewModelScope,errorLiveData) }

    fun login(){
        repo.login("sgyf","1234",loginLiveData)
    }
}