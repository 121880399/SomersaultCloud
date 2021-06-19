package org.zzy.somersault.cloud.lib

import android.app.Application
import org.zzy.somersault.cloud.lib.utils.ActivityManager

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/6/18 16:02
 * 描    述：
 * 修订历史：
 * ================================================
 */
class SomersaultCloud {

    /**
     * 初始化筋斗云
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/18 16:03
     */
    fun init(app: Application){
        ActivityManager.instance.register(app)
    }
}