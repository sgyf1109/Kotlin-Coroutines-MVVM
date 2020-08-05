package com.sgyf.kotlin_coroutines_mvvm.login

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.sgyf.kotlin_coroutines_mvvm.BR

import com.sgyf.kotlin_coroutines_mvvm.R
import com.zs.base_library.base.BaseVmActivity
import com.zs.base_library.base.DataBindingConfig
import com.zs.base_library.common.clickNoRepeat
import com.zs.base_library.common.setNoRepeatClick
import com.zs.base_library.common.toast
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseVmActivity(){
    private var loginVM: LoginVM? = null

    override fun getLayoutId()=R.layout.activity_login


    override fun initViewModel() {
        loginVM = getActivityViewModel(LoginVM::class.java)
    }



    override fun getDataBindingConfig(): DataBindingConfig? {
        return DataBindingConfig(R.layout.activity_login, loginVM)
            .addBindingParam(BR.vm, loginVM)
    }

    override fun init(savedInstanceState: Bundle?) {
        initView()
    }

    //初始化点击事件
    private fun initView() {
        //注册多个
//        setNoRepeatClick(loginBtn){
//            when(it.id){
//                R.id.loginBtn->loginVM?.login()
//            }
//        }
        loginBtn.clickNoRepeat{
            loginVM?.login()
        }
    }

    override fun observe() {
        loginVM?.loginLiveData?.observe(this, Observer {
            Toasty.normal(applicationContext,"登陆成功").show()
        })
    }
}
