package org.somersault.cloud.lib.core.log

import android.content.Context
import android.content.Intent

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/5/3 11:28
 * 描    述：日志打印Activity，比悬浮窗显示更多的功能
 * 修订历史：
 * ================================================
 */
class LogcatActivity {

    companion object{
        fun start(context: Context){
            var intent = Intent(context,LogcatActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}