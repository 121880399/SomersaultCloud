package org.somersault.cloud.lib.core.anr

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/6/3 15:18
 * 描    述：用于配置ANR监控的配置信息
 * 修订历史：
 * ================================================
 */
class ANRConfig (

    /**
     * 是否开启ANR监控
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/3 15:30
     */
    val isOpenMonitor: Boolean = true,

    /**
     * 超过这个时间就认为发生了ANR
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/3 15:21
     */
    val anrTime : Int= 3000,

    /**
     * 超过这个时间输出警告，并且将超过该时间的消息
     * 单独显示出来
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/3 15:22
     */
    val warnTime : Int = 300,

    /**
     * 为了避免将消息调度时idel状态
     * 合并到普通消息中，造成消息时长
     * 偏差，idle时间如果较长需要
     * 单独显示，如果短的话可以忽略
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/3 15:29
     */
    val idleTime : Int = 50,

    /**
     * 执行完三大流程后的耗时
     * 如果超过这个帧数，就判定为
     * jank
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 18:52
     */
    val jankFrame : Int = 30
)