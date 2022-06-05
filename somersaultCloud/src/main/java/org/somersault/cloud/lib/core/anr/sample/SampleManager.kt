package org.somersault.cloud.lib.core.anr.sample

import org.somersault.cloud.lib.core.anr.bean.MessageInfo
import org.somersault.cloud.lib.core.interf.ISampleListener

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/6/3 16:11
 * 描    述：专门进行采样
 * 修订历史：
 * ================================================
 */
object SampleManager : ISampleListener {

     fun startANRSample(msgId: String, time: Long) {
        TODO("Not yet implemented")
    }

    override fun onMsgSample(time: Long, msgId: String, msg: MessageInfo) {
        TODO("Not yet implemented")
    }

    override fun onJankSample(msgId: String, msg: MessageInfo) {
        TODO("Not yet implemented")
    }

    override fun onHandleANRFinish() {
        TODO("Not yet implemented")
    }
}