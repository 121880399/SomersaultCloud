package org.somersault.cloud.lib.core.interf

import org.somersault.cloud.lib.core.anr.bean.MessageInfo

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/6/5 17:55
 * 描    述：
 * 修订历史：
 * ================================================
 */
interface ISampleListener {

    fun onMsgSample(time:Long,msgId : String,msg:MessageInfo)

    fun onJankSample(msgId: String,msg : MessageInfo)

    /**
     * 当ANR消息处理完毕
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 18:01
     */
    fun onHandleANRFinish()

}