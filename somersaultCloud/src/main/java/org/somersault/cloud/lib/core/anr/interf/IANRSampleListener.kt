package org.somersault.cloud.lib.core.anr.interf

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/6/6 9:15
 * 描    述：用于监听ANR处理的接口
 * 实现该接口的类具备：
 * 1.采集主线程队列中在发生ANR后剩余的消息
 * 2.发生ANR时CPU信息的采集
 * 3.发生ANR时内存信息的采集
 * 4.发生ANR时线程堆栈的采集
 * 修订历史：
 * ================================================
 */
interface IANRSampleListener {

    /**
     * 队列中剩余未处理消息的采集
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/6 9:20
     */
    fun onMessageQueueSample(time:Long,msgId:String,msg:String)

    /**
     * CPU信息的采集
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/6 9:20
     */
    fun onCpuSample(time:Long,msgId: String,msg: String)

    /**
     *  内存信息的采集
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/6 9:20
     */
    fun onMemorySample(time:Long,msgId: String,msg: String)

    /**
     * 线程堆栈的采集
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/6 9:20
     */
    fun onThreadStackSample(time:Long,msgId: String,msg: String)
}