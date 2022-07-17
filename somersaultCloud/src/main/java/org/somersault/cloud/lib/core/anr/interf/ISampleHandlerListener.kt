package org.somersault.cloud.lib.core.anr.interf

import org.somersault.cloud.lib.core.anr.bean.MessageInfo

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/6/18 14:41
 * 描    述：采样处理器接口
 * 修订历史：
 * ================================================
 */
interface ISampleHandlerListener {

    /**
     * 处理CPU采样结果
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/18 14:44
     */
    fun handleCpuSample(time:Long,msgId:String,msg:String)

    /**
     * 处理内存采样结果
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/18 14:45
     */
    fun handleMemorySample(time:Long,msgId: String,msg: String)

    /**
     * 处理消息队列中未处理的消息采样结果
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/18 14:45
     */
    fun handleMessageQueueSample(time:Long,msgId: String,msg: String)

    /**
     * 处理各线程堆在采集结果
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/18 14:46
     */
    fun handleThreadStackSample(time:Long,msgId: String,msg: String)

    /**
     * 处理消息采样，这里的消息时我们自己定义的MessageInfo
     * 作者:ZhouZhengyi
     * 创建时间: 2022/7/17 18:27
     */
    fun handleMsgSample(time:Long,msg: MessageInfo)

    /**
     * 处理jank采用结果
     * 作者:ZhouZhengyi
     * 创建时间: 2022/7/17 18:28
     */
    fun hanleJankSample(time:Long,msg: MessageInfo)
}