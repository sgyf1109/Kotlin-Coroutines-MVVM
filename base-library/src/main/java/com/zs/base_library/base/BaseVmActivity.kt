package com.zs.base_library.base

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zs.base_library.services.NetWorkService
import com.zs.base_library.utils.ColorUtils
import com.zs.base_library.utils.NetworkUtils
import com.zs.base_library.utils.StatusUtils
import es.dmoral.toasty.Toasty


/**
 * des mvvm 基础 activity
 * @date 2020/5/9
 * @author zs
 */
/**
 * Open修饰的方法可以被重写,abstract修饰的方法必须被重写,没有修饰的方法可以被调用
 *
 */
abstract class BaseVmActivity : FragmentActivity() {
    private var mBinding: ViewDataBinding? = null

    private var mActivityProvider: ViewModelProvider? = null
    private var dataBindingConfig: DataBindingConfig? = null
    private var netWorkService: NetWorkService? = null
    var editText: EditText? = null

    // 记录当前连接状态，因为广播会接收所有的网络状态改变wifi/3g等等，所以需要一个标志记录当前状态
    private var connectState = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        getLayoutId()?.let { setContentView(it) }
        setStatusColor()
        setSystemInvadeBlack()
        initViewModel()
        //viewmodel和xml布局绑定,可以通过重写getDataBindingConfig实现或者重写不实现
        initViewDataBinding(savedInstanceState)
        observe()
        init(savedInstanceState)
        registerRxBus()
        bind()
    }

    private fun initViewDataBinding(savedInstanceState: Bundle?) {
        //DataBindingUtil类需要在project的build中配置 dataBinding {enabled true }, 同步后会自动关联android.databinding包
        mBinding = getLayoutId()?.let { DataBindingUtil.setContentView(this, it) }
        //将ViewDataBinding生命周期与activity绑定，不写xml文件无法随viewmodel变更
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
//   abstract fun getDataBindingConfig(): DataBindingConfig?
    open fun getDataBindingConfig(): DataBindingConfig?{//优化databing绑定方式,因为部分activity无需viewmodel
        return getLayoutId()?.let { DataBindingConfig(it) }
    }


    /**
     * 隐藏软件盘
     */
    protected open fun hideSoftInput() {
        val imm =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    /**
     * 注册RxBus
     */
    open fun registerRxBus(){}

    /**
     * 移除RxBus
     */
    open fun removeRxBus(){}



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

    //实现网络实时监听
    fun bind() {
        val intent = Intent(this@BaseVmActivity, NetWorkService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            netWorkService = (service as NetWorkService.MyBinder).service
            netWorkService?.setOnGetConnectState(object : NetWorkService.GetConnectState {
                override fun GetState(isConnected: Boolean) {
                    if (connectState != isConnected) {
                        connectState = isConnected
                        if (connectState) {
//                            已连接
                            handler.sendEmptyMessage(1)
                        } else {
//                            未连接
                            handler.sendEmptyMessage(2)
                        }
                    }
                }
            })
        }

        override fun onServiceDisconnected(name: ComponentName) {}
    }


    var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                1 -> {
                    val ct: Int = NetworkUtils.getConnectedType(
                        this@BaseVmActivity
                    )
                    if (ct == 1) {
                        Toasty.normal(applicationContext, "WIFI已经连接").show();
                    }
                    if (ct != 1 && ct != -1) {
                        Toasty.normal(applicationContext, "正在使用手机流量").show();
                    }
                }
                2 -> {
                    val ct2: Int = NetworkUtils.getConnectedType(
                        this@BaseVmActivity
                    )
                    if (ct2 == -1) {
                        Toasty.normal(applicationContext, "网络未连接").show();
                    }
                }
                else -> {
                }
            }
        }
    }

    //点击外部隐藏键盘
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideInput(v, ev)) {
                val imm =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm?.hideSoftInputFromWindow(v?.windowToken, 0)
                editText?.clearFocus()
            }
            return super.dispatchTouchEvent(ev)
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        return if (window.superDispatchTouchEvent(ev)) {
            true
        } else onTouchEvent(ev)
    }

    open fun isShouldHideInput(v: View?, event: MotionEvent): Boolean {
        if (v is EditText) {
            editText = v
            val leftTop = intArrayOf(0, 0)
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop)
            val left = leftTop[0]
            val top = leftTop[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            return if (event.x > left && event.x < right && event.y > top && event.y < bottom
            ) {
                // 点击的是输入框区域，保留点击EditText的事件
                false
            } else {
                true
            }
        }
        return false
    }

    fun unbind() {
        unbindService(serviceConnection)
    }

    override fun onDestroy() {
        super.onDestroy()
        hideSoftInput()
        mBinding?.unbind()
        removeRxBus();
        unbind()
    }
}