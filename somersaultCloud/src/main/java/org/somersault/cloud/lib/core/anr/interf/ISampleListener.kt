package org.somersault.cloud.lib.core.anr.interf

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

    fun onMsgSample(time:Long,msg:MessageInfo)

    fun onJankSample(msgId: String,msg : MessageInfo)

}