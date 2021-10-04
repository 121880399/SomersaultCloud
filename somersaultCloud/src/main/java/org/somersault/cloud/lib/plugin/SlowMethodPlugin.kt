package org.somersault.cloud.lib.plugin

import android.content.Intent
import org.somersault.cloud.lib.R
import org.somersault.cloud.lib.core.slowmethod.SlowMethodActivity
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
class SlowMethodPlugin :IFunctionPlugin {

    override fun getIconResId(): Int {
       return R.mipmap.sc_ic_trace_method
    }

    override fun getTitle(): String {
        return "慢函数"
    }

    override fun onClick() {
        val currentActivity = ActivityManager.instance.getTopActivity()!!
        val intent = Intent()
        intent.setClass(currentActivity,SlowMethodActivity::class.java)
        currentActivity.startActivity(intent)
    }
}