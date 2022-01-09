package org.somersault.cloud.lib.utils

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import java.lang.Exception

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/1/9 10:28
 * 描    述：
 * 修订历史：
 * ================================================
 */
class Debug {
    companion object{
        /**
         * 通过读取Manifest文件中是否是debuggable来判断当前状态
         * 作者:ZhouZhengyi
         * 创建时间: 2022/1/9 10:35
         */
        fun isDebug():Boolean{
            return try {
                val context = ApplicationUtils.getInstance().application ?: return false
                val applicationInfo = context.applicationInfo
                (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
            }catch (e:Exception){
                e.printStackTrace()
                false
            }
        }
    }
}