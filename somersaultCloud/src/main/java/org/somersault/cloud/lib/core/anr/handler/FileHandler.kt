package org.somersault.cloud.lib.core.anr.handler

import android.app.ApplicationErrorReport
import org.somersault.cloud.lib.core.anr.bean.AnrInfo
import org.somersault.cloud.lib.core.anr.bean.MessageInfo
import org.somersault.cloud.lib.core.anr.interf.ISampleHandlerListener

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/6/18 16:09
 * 描    述：文件处理器，通过写文件的方式来处理采集结果
 * 修订历史：
 * ================================================
 */
class FileHandler:ISampleHandlerListener {

    private var anrInfo:AnrInfo? = AnrInfo()

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

    override fun handleMsgSample(time: Long,  msg: MessageInfo) {
       anrInfo?.messageInfoCache?.put(time,msg)
        if(MessageInfo.MsgType.MSG_TYPE_ANR == msg.msgType){
            saveAnrInfo()
        }
    }

    override fun hanleJankSample(time: Long, msg: MessageInfo) {
        TODO("Not yet implemented")
    }

    /**
     * 将ANR信息保存到本地文件
     * 作者:ZhouZhengyi
     * 创建时间: 2022/7/18 9:31
     */
    fun saveAnrInfo(){

    }
}