package org.somersault.cloud.lib.plugin

import android.widget.Toast
import org.somersault.cloud.lib.R
import org.somersault.cloud.lib.core.Cloud
import org.somersault.cloud.lib.core.log.LogView
import org.somersault.cloud.lib.interf.IFunctionPlugin
import org.somersault.cloud.lib.manager.ActivityManager
import org.somersault.cloud.lib.manager.FloatViewManager

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/5/24 8:34
 * 描    述：日志显示插件
 * 功能：
 * 1.显示LOGCAT输出的日志
 * 2.有悬浮窗和Actiity两种形式，Activity的功能更丰富
 * 3.可以通过关键字查找日志
 * 4.可以过滤日志等级
 * 5.可以删除日志，分享日志
 * 修订历史：
 * ================================================
 */
class LogPlugin : IFunctionPlugin {

    companion object{
        val TAG = LogPlugin::class.simpleName
        var isOpen  = false
    }

    private var view : LogView? = null

    override fun getIconResId(): Int {
        return R.mipmap.sc_ic_log
    }

    override fun getTitle(): String {
        return "Logcat日志"
    }

    override fun onClick() {
        val currentActivity = ActivityManager.instance.getTopActivity()
        if(isOpen){
            Toast.makeText(currentActivity,"Logcat关闭!", Toast.LENGTH_SHORT).show()
            FloatViewManager.instance.detach(LogView::class.java)
            view = null
            isOpen = false
        }else{
            Toast.makeText(currentActivity,"Logcat开启!", Toast.LENGTH_SHORT).show()
            view = LogView()
            view!!.init(currentActivity!!)
            FloatViewManager.instance.attach(view!!,currentActivity)
            isOpen = true
        }
        //关闭云弹窗
        Cloud.instance.dismiss()
    }
}