package org.somersault.cloud.lib.core.anr.bean

import java.io.Serializable

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/6/5 11:20
 * 描    述：将Looper中Printer打印的日志封装成消息
 * @param handlerName Handler的名称
 * @param handlerAddress Handler的内存地址
 * @param callBackName CallBack的名称
 * @param messageWhat 消息的what
 * @param msgId 消息的id
 * 修订历史：
 * ================================================
 */
data class MonitorMessage(val handlerName:String,val handlerAddress : String,val callBackName:String,val messageWhat : Int ,var msgId : Int = 0) : Serializable