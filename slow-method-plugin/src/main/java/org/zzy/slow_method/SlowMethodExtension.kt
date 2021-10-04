package org.zzy.slow_method

import com.ss.android.ugc.bytex.common.BaseExtension

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/10/3 10:14
 * 描    述：
 * 修订历史：
 * ================================================
 */
class SlowMethodExtension : BaseExtension{

    /**
    * 筋斗云插件是否打开
    * 作者: ZhouZhengyi
    * 创建时间: 2021/10/3 11:19
    */
    var somersaultcloud_plugin_switch = true

    /**
    * 慢函数检测是否打开
    * 作者: ZhouZhengyi
    * 创建时间: 2021/10/3 11:19
    */
    var slow_method_switch = true

    override fun getName(): java.lang.String {
        return "slow_metod"
    }


}