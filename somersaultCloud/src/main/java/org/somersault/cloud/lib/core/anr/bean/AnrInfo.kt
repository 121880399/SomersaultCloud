package org.somersault.cloud.lib.core.anr.bean

import org.somersault.cloud.lib.utils.TimeLruCache
import java.io.Serializable
import java.lang.StringBuilder

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/7/17 18:46
 * 描    述：当ANR发送时，需要存储的信息
 * 修订历史：
 * ================================================
 */
class AnrInfo: Serializable {
    /**
     * 用于缓存MessageInfo
     * 作者:ZhouZhengyi
     * 创建时间: 2022/7/17 18:55
     */
    var messageInfoCache : TimeLruCache<MessageInfo> = TimeLruCache(30L*1000)

    /**
     * 消息队列信息
     * 作者:ZhouZhengyi
     * 创建时间: 2022/7/17 18:57
     */
    var messageQueueInfo : StringBuilder = StringBuilder()

    /**
     * 线程堆栈信息
     * 作者:ZhouZhengyi
     * 创建时间: 2022/7/17 18:58
     */
    var threadStackInfo : String = String()

    var createTime : String = String()
}