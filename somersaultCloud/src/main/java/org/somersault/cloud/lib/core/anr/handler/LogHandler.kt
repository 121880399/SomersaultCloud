package org.somersault.cloud.lib.core.anr.handler

import org.somersault.cloud.lib.core.anr.bean.MessageInfo
import org.somersault.cloud.lib.core.anr.interf.ISampleHandlerListener

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/6/18 16:09
 * 描    述：日志处理器，通过LOG的方式来处理采集结果
 * 修订历史：
 * ================================================
 */
class LogHandler:ISampleHandlerListener {

    override fun handleCpuSample(time: Long, msgId: String, msg: String) {
        TODO("Not yet implemented")
    }

    override fun handleMemorySample(time: Long, msgId: String, msg: String) {
        TODO("Not yet implemented")
    }

    override fun handleMessageQueueSample(time: Long, msgId: String, msg: String) {
        TODO("Not yet implemented")
    }

    override fun handleThreadStackSample(time: Long, msgId: String, msg: String) {
        TODO("Not yet implemented")
    }

    override fun handleMsgSample(time: Long, msg: MessageInfo) {
        TODO("Not yet implemented")
    }

    override fun hanleJankSample(time: Long, msg: MessageInfo) {
        TODO("Not yet implemented")
    }
}