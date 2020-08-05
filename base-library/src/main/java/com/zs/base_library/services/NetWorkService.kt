package com.zs.base_library.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.zs.base_library.utils.NetworkUtils
import java.util.*

class NetWorkService : Service() {
    //    实时监听网络状态变化
    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            val action = intent.action
            if (action == ConnectivityManager.CONNECTIVITY_ACTION) {
                val timer = Timer()
                timer.schedule(QunXTask(applicationContext), Date())
            }
        }
    }

    interface GetConnectState {
        //    网络状态改变之后，通过此接口的实例通知当前网络的状态，此接口在Activity中注入实例对象
        fun GetState(isConnected: Boolean)
    }

    private var onGetConnectState: GetConnectState? = null
    fun setOnGetConnectState(onGetConnectState: GetConnectState?) {
        this.onGetConnectState = onGetConnectState
    }

    private val binder: Binder = MyBinder()
    private var isContected = true
    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    override fun onCreate() { // 注册广播
        val mFilter = IntentFilter()
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION) // 添加接收网络连接状态改变的Action
        registerReceiver(mReceiver, mFilter)
    }

    internal inner class QunXTask(private val context: Context) :
        TimerTask() {
        override fun run() {
            isContected =
                NetworkUtils.isNetworkConnected(context) && NetworkUtils.isWifiConnected(context)
            if (onGetConnectState != null) {
                onGetConnectState!!.GetState(isContected) // 通知网络状态改变
                Log.i("mylog", "通知网络状态改变:$isContected")
            }
        }

    }

    inner class MyBinder : Binder() {
        val service: NetWorkService
            get() = this@NetWorkService
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver) // 删除广播
    }
}