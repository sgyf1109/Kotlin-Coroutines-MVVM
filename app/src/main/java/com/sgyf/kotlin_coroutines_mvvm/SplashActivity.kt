package com.sgyf.kotlin_coroutines_mvvm

import android.Manifest
import android.os.Bundle
import com.sgyf.kotlin_coroutines_mvvm.constants.Constants
import com.sgyf.kotlin_coroutines_mvvm.login.LoginActivity
import com.sgyf.kotlin_coroutines_mvvm.utils.DialogUtils
import com.zs.base_library.base.BaseVmActivity
import com.zs.base_library.base.DataBindingConfig
import com.zs.base_library.utils.PrefUtils
import com.zs.base_library.utils.StatusUtils
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit


class SplashActivity : BaseVmActivity(), EasyPermissions.PermissionCallbacks {
    private var disposable: Disposable? = null

    private val tips = "App现在要向您申请存储权限，用于访问您的本地音乐，您也可以在设置中手动开启或者取消。"
    private val perms =
        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)

    override fun onCreate(savedInstanceState: Bundle?) {
        changeTheme()//动态切换主题
        super.onCreate(savedInstanceState)
    }


    override fun init(savedInstanceState: Bundle?) {
        requestPermission()
    }

    override fun getLayoutId() =
        R.layout.activity_splash

    override fun getDataBindingConfig(): DataBindingConfig? {
        return DataBindingConfig(R.layout.activity_splash)
    }

    companion object {
        private const val REQUEST_CODE = 100
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    /**
     * 申请权限
     */
    private fun requestPermission() {
        //已申请
        if (EasyPermissions.hasPermissions(this, *perms)) {
            startIntent()
        } else {
            //为申请，显示申请提示语
            DialogUtils.tips(this,tips){
                RequestLocationAndCallPermission()
            }
        }
    }

    @AfterPermissionGranted(REQUEST_CODE)
    private fun RequestLocationAndCallPermission() {
        //数组中权限都已申请
        if (EasyPermissions.hasPermissions(this, *perms)) {
            startIntent()
        } else {
            EasyPermissions.requestPermissions(this, "请求相机和写入权限",
                REQUEST_CODE, *perms)//第一次全部或部分拒绝后走这里
        }
    }


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        // 可选）检查用户是否拒绝了任何权限并选中了“永不再次询问”。
        //这将显示一个对话框，指导他们启用应用程序设置中的权限。
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).setTitle("部分权限已被禁用")
                .setRationale("如果权限没有打开,可能导致部分功能无法正常运行，请至设置中权限管理处打开权限").build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        startIntent()
    }

    /**
     * 页面跳转
     */
    private fun startIntent() {

        disposable = Observable.timer(2000, TimeUnit.MILLISECONDS)
            .subscribe {
                startActivity(LoginActivity::class.java)
                finish()
            }
    }


    /**
     * 动态切换主题
     */
    private fun changeTheme() {
        val theme = PrefUtils.getBoolean(Constants.SP_THEME_KEY,false)
        if (theme) {
            setTheme(R.style.AppTheme_Night)
        } else {
            setTheme(R.style.AppTheme)
        }
    }

    /**
     * 沉浸式状态,随主题改变
     */
    override fun setSystemInvadeBlack() {
        val theme = PrefUtils.getBoolean(Constants.SP_THEME_KEY,false)
        if (theme) {
            StatusUtils.setSystemStatus(this, true, false)
        } else {
            StatusUtils.setSystemStatus(this, true, true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}
