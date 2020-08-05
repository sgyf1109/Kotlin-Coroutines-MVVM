package com.zs.base_library.utils

import android.content.Context
import android.net.ConnectivityManager

class NetworkUtils {
    companion object{
        /**
         * 检测网络是否连接
         */
        fun isNetworkConnected(context: Context?): Boolean {
            if (context != null) {
                val mConnectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val mNetworkInfo = mConnectivityManager.activeNetworkInfo
                if (mNetworkInfo != null) {
                    return mNetworkInfo.isAvailable
                }
            }
            return false
        }

        /**
         * 判断WIFI是否可用
         */
        fun isWifiConnected(context: Context?): Boolean {
            if (context != null) {
                val mConnectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val mWiFiNetworkInfo =
                    mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                if (mWiFiNetworkInfo != null) {
                    return mWiFiNetworkInfo.isAvailable
                }
            }
            return false
        }

        /**
         * 判断当前网络连接类型
         */
        fun getConnectedType(context: Context?): Int {
            if (context != null) {
                val mConnectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val mNetworkInfo = mConnectivityManager.activeNetworkInfo
                if (mNetworkInfo != null && mNetworkInfo.isAvailable) {
                    return mNetworkInfo.type
                }
            }
            return -1
        }

    }
}