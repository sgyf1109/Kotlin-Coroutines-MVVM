package com.zs.base_library.base

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zs.base_library.utils.ColorUtils
import com.zs.base_library.utils.StatusUtils


/**
 * des mvvm 基础 activity
 * @date 2020/5/9
 * @author zs
 */
abstract class BaseVmActivity : FragmentActivity() {
    private var mBinding: ViewDataBinding? = null

    private var mActivityProvider: ViewModelProvider? = null
    private var dataBindingConfig: DataBindingConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        getLayoutId()?.let { setContentView(it) }
        setStatusColor()
        setSystemInvadeBlack()
        initViewModel()
        initViewDataBinding(savedInstanceState)
        observe()
        init(savedInstanceState)

    }

    private fun initViewDataBinding(savedInstanceState: Bundle?) {
        //DataBindingUtil类需要在project的build中配置 dataBinding {enabled true }, 同步后会自动关联android.databinding包
        mBinding = getLayoutId()?.let { DataBindingUtil.setContentView(this, it) }
        //将ViewDataBinding生命周期与activity绑定
        mBinding?.lifecycleOwner = this
        dataBindingConfig = getDataBindingConfig()
        dataBindingConfig?.apply {
            val bindingParams = bindingParams
            //TODO 将bindingParams逐个加入到ViewDataBinding中的Variable
            // 这一步很重要,否则xml中拿不到variable中内容
            for (i in 0 until bindingParams.size()) {
                mBinding?.setVariable(bindingParams.keyAt(i), bindingParams.valueAt(i))
            }
        }
    }


    /**
     * 设置状态栏背景颜色
     */
    open fun setStatusColor() {
        StatusUtils.setUseStatusBarColor(this, ColorUtils.parseColor("#00ffffff"))
    }

    /**
     * 沉浸式状态
     */
    open fun setSystemInvadeBlack() {
        //第二个参数是是否沉浸,第三个参数是状态栏字体是否为黑色。
        StatusUtils.setSystemStatus(this, true, true)
    }

    /**
     * 初始化viewModel
     * 之所以没有设计为抽象，是因为部分简单activity可能不需要viewModel
     * observe同理
     */
    open fun initViewModel() {

    }

    /**
     * 注册观察者
     */
    open fun observe() {

    }


    /**
     * 通过activity获取viewModel，跟随activity生命周期
     */
    protected fun <T : ViewModel?> getActivityViewModel(modelClass: Class<T>): T? {
        if (mActivityProvider == null) {
            mActivityProvider = ViewModelProvider(this)
        }
        return mActivityProvider?.get(modelClass)
    }

    /**
     * activity入口
     */
    abstract fun init(savedInstanceState: Bundle?)

    /**
     * 获取layout布局
     */
    abstract fun getLayoutId(): Int?


    /**
     * 获取dataBinding配置项
     */
   abstract fun getDataBindingConfig(): DataBindingConfig?

    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    fun startActivity(clz: Class<*>?) {
        startActivity(Intent(this, clz))
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    fun startActivity(clz: Class<*>?, bundle: Bundle?) {
        val intent = Intent(this, clz)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    fun startActivityForResult(clz: Class<*>?, bundle: Bundle?) {
        val intent = Intent(this, clz)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivityForResult(intent, 100)
    }

    override fun onDestroy() {
        super.onDestroy()
        hideSoftInput()
        mBinding?.unbind()
    }

    /**
     * 隐藏软件盘
     */
    protected open fun hideSoftInput() {
        val imm =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}