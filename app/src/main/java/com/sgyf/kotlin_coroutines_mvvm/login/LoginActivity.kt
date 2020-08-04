package com.sgyf.kotlin_coroutines_mvvm.login

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.sgyf.kotlin_coroutines_mvvm.R
import com.zs.base_library.base.BaseVmActivity
import com.zs.base_library.common.toast
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseVmActivity(){
    private var loginVM: LoginVM? = null

    override fun getLayoutId()=R.layout.activity_login


    override fun initViewModel() {
        loginVM = getActivityViewModel(LoginVM::class.java)
    }

    override fun init(savedInstanceState: Bundle?) {
        initView()
    }

    //初始化点击事件
    private fun initView() {
        loginBtn.setOnClickListener {
            loginVM?.login()
        }
    }

    override fun observe() {
        loginVM?.loginLiveData?.observe(this, Observer {
            Toast.makeText(this,"登陆成功",Toast.LENGTH_SHORT).show()
        })
    }
}
