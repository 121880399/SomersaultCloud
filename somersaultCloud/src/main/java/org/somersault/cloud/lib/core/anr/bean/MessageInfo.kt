package org.somersault.cloud.lib.core.anr.bean

import android.os.SystemClock
import androidx.annotation.IntDef
import org.somersault.cloud.lib.core.anr.bean.MessageInfo.MsgType.Companion.MSG_TYPE_ACTIVITY_THREAD_H
import org.somersault.cloud.lib.core.anr.bean.MessageInfo.MsgType.Companion.MSG_TYPE_ANR
import org.somersault.cloud.lib.core.anr.bean.MessageInfo.MsgType.Companion.MSG_TYPE_IDLE
import org.somersault.cloud.lib.core.anr.bean.MessageInfo.MsgType.Companion.MSG_TYPE_INFO
import org.somersault.cloud.lib.core.anr.bean.MessageInfo.MsgType.Companion.MSG_TYPE_JANK
import org.somersault.cloud.lib.core.anr.bean.MessageInfo.MsgType.Companion.MSG_TYPE_NONE
import org.somersault.cloud.lib.core.anr.bean.MessageInfo.MsgType.Companion.MSG_TYPE_WARN
import java.io.Serializable

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/6/5 15:27
 * 描    述：消息聚合，界面上每一个消息Item对应一个MessageInfo
 * 修订历史：
 * ================================================
 */
class MessageInfo : Serializable{
    /**
     * 用annotation来定义注解
     * 这里跟JAVA不太一样，JAVA是用@interface来定义注解
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 15:40
     */
    @IntDef(
        MSG_TYPE_NONE,
        MSG_TYPE_INFO,
        MSG_TYPE_WARN,
        MSG_TYPE_ANR,
        MSG_TYPE_JANK,
        MSG_TYPE_IDLE,
        MSG_TYPE_ACTIVITY_THREAD_H
    )
    annotation class MsgType {
        companion object {
            /**
             * 用于初始化
             * 作者:ZhouZhengyi
             * 创建时间: 2022/6/6 9:52
             */
            const val MSG_TYPE_NONE = 0X00
            /**
             * 普通的消息聚合消息
             * 作者:ZhouZhengyi
             * 创建时间: 2022/6/6 9:51
             */
            const val MSG_TYPE_INFO = 0X01

            /**
             * 警告
             * 作者:ZhouZhengyi
             * 创建时间: 2022/6/5 15:32
             */
            const val MSG_TYPE_WARN = 0X02

            /**
             * ANR
             * 作者:ZhouZhengyi
             * 创建时间: 2022/6/5 15:31
             */
            const val MSG_TYPE_ANR = 0X03

            /**
             * 掉帧
             * 作者:ZhouZhengyi
             * 创建时间: 2022/6/5 15:31
             */
            const val MSG_TYPE_JANK = 0X04

            /**
             * 连续两条消息之间的间隙
             * 作者:ZhouZhengyi
             * 创建时间: 2022/6/5 15:31
             */
            const val MSG_TYPE_IDLE = 0X05

            /**
             * 通过ActivityThread$H handler发送的消息
             * 这类消息属于关键消息，因为会调用引起ANR的组件
             * 如Activity，Service，Receiver，Provider
             * 作者:ZhouZhengyi
             * 创建时间: 2022/6/5 15:31
             */
            const val MSG_TYPE_ACTIVITY_THREAD_H = 0X06

        }
    }
    @MsgType
    var msgType: Int = MSG_TYPE_NONE

    /**
     * MessageInfo中包含多少条消息
     * 至少会有一条消息
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 16:03
     */
    var count: Int = 1

    /**
     * 消息执行耗时，包括等待时间
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 16:04
     */
    var wallTime = 0L

    /**
     * SystemClock.currentThreadTimeMillis()
     * 是当前线程执行方法的时间，不包括线程休眠，锁竞争等待
     * 是函数真正的执行时间
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 16:05
     */
    var cpuTime = 0L

    /**
     * MessageInfo被创建的时间
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 16:11
     */
    var msgCreateTime = SystemClock.elapsedRealtime()

    /**
     * 该时段内所包含的消息
     * 场景主要是主线程连续调用多个消息，
     * 并且每个消息耗时都很小的情况下，将这些
     * 消息聚合在一起，直到这些消息累计耗时超过
     * 设置的阈值，则汇总生成一条MessageInfo
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 16:09
     */
    val messages = ArrayList<MonitorMessage>()


    override fun toString(): String {
        return "MessageInfo{msgType=${msgTypeToString(msgType)}, count=$count, wallTime=$wallTime, cpuTime=$cpuTime, msgCreateTime=$msgCreateTime, messages=$messages}"
    }

    companion object {
        fun msgTypeToString(@MsgType msgType : Int ):String{
            return when(msgType){
                MSG_TYPE_NONE -> "MSG_TYPE_NONE"
                MSG_TYPE_INFO -> "MSG_TYPE_INFO"
                MSG_TYPE_WARN -> "MSG_TYPE_WARN"
                MSG_TYPE_ANR -> "MSG_TYPE_ANR"
                MSG_TYPE_JANK -> "MSG_TYPE_JANK"
                MSG_TYPE_IDLE -> "MSG_TYPE_GAP"
                MSG_TYPE_ACTIVITY_THREAD_H -> "MSG_TYPE_ACTIVITY_THREAD_H"
                else -> "MSG_TYPE_NONE"
            }
        }
    }
}