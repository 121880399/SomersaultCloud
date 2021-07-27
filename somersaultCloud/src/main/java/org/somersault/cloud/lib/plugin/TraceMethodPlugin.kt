package org.somersault.cloud.lib.plugin

import android.content.Intent
import org.somersault.cloud.lib.R
import org.somersault.cloud.lib.core.tracemethod.TraceMethodActivity
import org.somersault.cloud.lib.interf.IFunctionPlugin
import org.somersault.cloud.lib.manager.ActivityManager

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/7/27 9:48
 * 描    述：慢函数插件
 * 修订历史：
 * ================================================
 */
class TraceMethodPlugin :IFunctionPlugin {

    override fun getIconResId(): Int {
       return R.mipmap.sc_ic_trace_method
    }

    override fun getTitle(): String {
        return "慢函数"
    }

    override fun onClick() {
        val currentActivity = ActivityManager.instance.getTopActivity()!!
        val intent = Intent()
        intent.setClass(currentActivity,TraceMethodActivity::class.java)
        currentActivity.startActivity(intent)
    }
}