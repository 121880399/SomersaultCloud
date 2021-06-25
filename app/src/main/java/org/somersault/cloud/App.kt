package org.somersault.cloud

import android.app.Application
import org.zzy.somersault.cloud.lib.SomersaultCloud

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
        SomersaultCloud.instance.init(this)
    }
}