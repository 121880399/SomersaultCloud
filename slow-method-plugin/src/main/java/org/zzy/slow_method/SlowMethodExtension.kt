package org.zzy.slow_method

import com.ss.android.ugc.bytex.common.BaseExtension

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/10/3 10:14
 * 描    述：使用ByteX必须创建的类，用来存储配置信息
 * 修订历史：
 * ================================================
 */
class SlowMethodExtension : BaseExtension() {

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

    /**
     * 慢函数阈值
     * 作者:ZhouZhengyi
     * 创建时间: 2021/11/23 9:38
     */
    var slow_method_threshold = 10

    /**
     * 是否只检测在主线程执行的函数
     * 作者:ZhouZhengyi
     * 创建时间: 2021/11/23 9:39
     */
    var onCheckMainThread = false

    /**
     * 需要插桩的包名
     * 作者:ZhouZhengyi
     * 创建时间: 2021/11/23 9:45
     */
    var packageName : MutableSet<String> = mutableSetOf()

    /**
     * 是否是Release环境
     * 作者:ZhouZhengyi
     * 创建时间: 2021/11/24 10:06
     */
    var isRelease = false

    /**
     * 在build.gradle中使用的Extension名称
     * 作者:ZhouZhengyi
     * 创建时间: 2021/11/23 9:47
     */
    override fun getName(): String {
        return "slow_metod"
    }


}