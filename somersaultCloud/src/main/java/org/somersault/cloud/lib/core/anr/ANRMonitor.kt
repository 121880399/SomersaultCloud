package org.somersault.cloud.lib.core.anr

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Looper
import android.os.SystemClock
import android.util.Printer
import android.view.Choreographer
import org.somersault.cloud.lib.core.anr.bean.MessageInfo
import org.somersault.cloud.lib.core.anr.bean.MonitorMessage
import org.somersault.cloud.lib.core.anr.sample.SampleManager
import org.somersault.cloud.lib.manager.SCThreadManager
import org.somersault.cloud.lib.utils.Logger
import org.somersault.cloud.lib.utils.Reflector
import java.util.concurrent.atomic.AtomicBoolean

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/6/3 14:46
 * 描    述：ANR门面类
 * 修订历史：
 * ================================================
 */
@SuppressLint("StaticFieldLeak")
object ANRMonitor : Printer {
    private val TAG = "ANRMonitor"

    private var mContext : Context? = null

    /**
     * 配置信息
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/3 16:13
     */
    private var mConfig : ANRConfig? = null

    /**
     * 采样管理者
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/3 16:13
     */
    private var mSampleManager : SampleManager? = null

    /**
     * 记录是否已经开启，防止重复开启
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/3 16:42
     */
    @Volatile private var isStart = false

    /**
     * 是否是奇数，默认为偶数
     * 用来记录调用print方法是奇数
     * 还是偶数
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 10:16
     */
    private val isOdd : AtomicBoolean = AtomicBoolean(false)

    /**
     * 触发ANR的时间
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 11:17
     */
    private var triggerANRTime : Long = 0L

    /**
     * 当前正在处理的消息
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 11:31
     */
    private var mCurrentMessage : MonitorMessage? = null

    /**
     * 当前消息id
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 15:04
     */
    @Volatile private var mCurrentMsgId  = 0L

    /**
     * 未初始化的标记
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 15:11
     */
    private val noInit = -1L

    /**
     * 开始时间
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 15:12
     */
    private var mStartTime = noInit

    /**
     * 临时的开始时间
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 16:55
     */
    private var mTempStartTime = noInit

    /**
     * 上次结束的时间
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 15:12
     */
    private var mLastEndTime = noInit

    /**
     * CPU开始执行时间
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 16:47
     */
    private var mCpuStartTime = noInit

    /**
     * CPU临时的开始时间
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 16:57
     */
    private var mCpuTempStartTime = noInit

    /**
     * 上次CPU执行结束时间
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 16:47
     */
    private var mLastCpuEndTime = noInit

    /**
     * 每次消息处理完成后需要将该
     * 字段设置为空
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 16:24
     */
    private var mMessageInfo : MessageInfo? = null

    /**
     * 每一帧的时间
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 18:34
     */
    private var mFrameIntervalNanos = 0f

    /**
     * 初始化
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/3 16:44
     */
    fun init(context : Context):ANRMonitor {
        mContext = context.applicationContext
        //初始化时给出默认配置
        config(ANRConfig(
            anrTime = 3000,
            warnTime = 300,
            idleTime = 50,
            isOpenMonitor = true
        ))
        mSampleManager = SampleManager

        mFrameIntervalNanos = try {
            val fieldValue  = Reflector.QuietReflector.on(Choreographer::class.java).bind(Choreographer.getInstance()).field("mFrameIntervalNanos") as Long
            fieldValue *  0.000001f
        }catch (e : Exception){
            e.printStackTrace()
            16000000 * 0.000001f
        }
        return this
    }

    /**
     * 设置配置信息
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/3 16:44
     */
    fun config(config : ANRConfig):ANRMonitor{
        this.mConfig = config
        return this
    }

    /**
     * 开始监控ANR
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/3 16:44
     */
    @Synchronized
    fun start(){
        if(isStart) return
        isStart = true
        Looper.getMainLooper().setMessageLogging(this)
        SCThreadManager.runOnANRMonitorThread(mANRRunnable)

    }

    private val mANRRunnable = Runnable {
        /**
         * 到这个时间消息没处理完，我们
         * 就认为发生了ANR，
         * 该时间 = 消息开始时间+配置的ANR间隔时间
         * 作者:ZhouZhengyi
         * 创建时间: 2022/6/3 17:22
         */
        var anrTime = 0L
        run {
            anrTime = SystemClock.elapsedRealtime() + mConfig!!.anrTime!!
            while (isStart){
                var now = SystemClock.elapsedRealtime()
                if(now >= anrTime){
                    //时间到了

                }
            }
        }
    }


    override fun println(x: String?) {
        //如果是偶数次调用，并且是结束标志，则返回
       if(x!!.contains("<<<<< Finished to") && !isOdd.get()){
           return
       }
        //1.开始且偶数
        //2.开始且奇数
        //3.结束且奇数
        if(!isOdd.get()){
            messageStart(x)
        }else{
            messageEnd(x)
        }
        isOdd.set(!isOdd.get())
    }

    /**
     * Looper取到了新的消息，开始执行
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 11:13
     */
    private fun messageStart(log: String?) {
        mTempStartTime = SystemClock.elapsedRealtime()
        triggerANRTime = mTempStartTime + mConfig!!.anrTime
        mCurrentMessage = MessageParser.parsePrinterStart(log!!)
        mCurrentMessage!!.msgId = mCurrentMsgId
        mCpuTempStartTime = SystemClock.currentThreadTimeMillis()
        if(mTempStartTime - mLastEndTime > mConfig!!.idleTime && mLastEndTime != noInit){
            //如果上次结束的时间和当前开始的时间差距大于配置的间隔时间，则认为是空闲时间,需要增加一个空闲消息，并且不会存在两个连续的空闲消息
            if(mMessageInfo != null){
                handleMessage()
            }
            mMessageInfo = MessageInfo()
            mMessageInfo!!.msgType = MessageInfo.MsgType.MSG_TYPE_GAP
            mMessageInfo!!.wallTime = mTempStartTime - mLastEndTime
            mMessageInfo!!.cpuTime = mCpuTempStartTime - mLastCpuEndTime
            mStartTime = mTempStartTime
            handleMessage()
        }
        if(mMessageInfo == null){
            //创建一条新的MessageInfo，重置所有的计时
            mMessageInfo = MessageInfo()
            mStartTime = SystemClock.elapsedRealtime()
            mTempStartTime = mStartTime
            mCpuStartTime = SystemClock.currentThreadTimeMillis()
            mCpuTempStartTime = mCpuStartTime
        }
        // TODO: 注意，如果mMessage不为null,并且没有超过idleTime，则不会被执行，这里之后看看有没问题
    }

    /**
     * Looper将消息处理完毕
     * 判断是否将消息合并
     * 还是作为独立的WARN
     * 还是作为独立的ANR
     * 或者ActivityThread消息
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 11:13
     */
    private fun messageEnd(x: String?) {
        //这里添加日志信息，看看该方法是否是多线程在调用
        Logger.d(TAG,"messageEnd ThreadId:${Thread.currentThread().id}")
        mLastEndTime = SystemClock.elapsedRealtime()
        mLastCpuEndTime = SystemClock.currentThreadTimeMillis()
        var costTime = mLastEndTime - mTempStartTime
        handleJank(costTime)
        //判断是否是ActivityThread的消息
        val isActivityThreadMsg = MessageParser.isActivityThreadMsg(mCurrentMessage!!)
        if(mMessageInfo == null){
            //如果在这个位置为空，只有是anr采集的时候将原来的messageInfo置为空了
            mMessageInfo = MessageInfo()
        }
        //如果该消息的处理时间大于配置的警告时间
        if(costTime > mConfig!!.warnTime || isActivityThreadMsg){
            if(mMessageInfo!!.count > 1){
                //先将之前的MessageInfo作为一条普通的INFO消息处理掉
                mMessageInfo!!.msgType = MessageInfo.MsgType.MSG_TYPE_INFO
                handleMessage()
            }
            //然后再将该消息作为一条独立的警告消息(MessageInfo)
            mMessageInfo = MessageInfo()
            mMessageInfo!!.msgType = MessageInfo.MsgType.MSG_TYPE_WARN
            mMessageInfo!!.wallTime = costTime
            mMessageInfo!!.cpuTime = mLastCpuEndTime - mCpuTempStartTime
            mMessageInfo!!.messages.add(mCurrentMessage!!)
            val isANR = costTime > mConfig!!.anrTime
            if(isANR){
                //如果该消息超过配置的ANR时间，则认为是ANR消息
                mMessageInfo!!.msgType = MessageInfo.MsgType.MSG_TYPE_ANR
            }else if (isActivityThreadMsg){
                mMessageInfo!!.msgType = MessageInfo.MsgType.MSG_TYPE_ACTIVITY_THREAD_H
            }
            handleMessage()
            if(isANR){
                mSampleManager!!.onHandleANRFinish()
            }
        }
    }

    private fun handleMessage(){
        if(mMessageInfo == null){
            return
        }
        val tempMessage = mMessageInfo
        mSampleManager!!.onMsgSample(SystemClock.elapsedRealtimeNanos(),"$mCurrentMsgId",tempMessage!!)
        mMessageInfo = null
    }

    private fun handleJank(costTime : Long){
        if(MessageParser.isDoFrame(mCurrentMessage!!) && costTime > mFrameIntervalNanos * mConfig!!.jankFrame){
            val tempMessage = mMessageInfo
            mMessageInfo = MessageInfo()
            mMessageInfo!!.msgType = MessageInfo.MsgType.MSG_TYPE_JANK
            mMessageInfo!!.wallTime = costTime
            mMessageInfo!!.cpuTime = mLastCpuEndTime - mCpuTempStartTime
            mMessageInfo!!.messages.add(mCurrentMessage!!)
            mSampleManager!!.onJankSample("$mCurrentMsgId",mMessageInfo!!)
            mMessageInfo = tempMessage
        }
    }
}