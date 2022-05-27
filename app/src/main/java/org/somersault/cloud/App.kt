package org.somersault.cloud

import android.app.Application
import android.util.ArrayMap
import org.somersault.cloud.lib.SomersaultCloud

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/6/25 9:19
 * 描    述：
 * 修订历史：
 * ================================================
 */
class App :Application() {

    override fun onCreate() {
        super.onCreate()
        SomersaultCloud.init(this)
        val map = ArrayMap<String,String>()
        map["Activity"] = "查看Activity日志输出"
        map["View"] = "查看View的信息"
        SomersaultCloud.addLogTagTipConfig(map)
    }
}