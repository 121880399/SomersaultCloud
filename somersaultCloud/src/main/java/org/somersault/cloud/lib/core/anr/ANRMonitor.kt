package org.somersault.cloud.lib.core.anr

import android.annotation.SuppressLint
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
import java.util.concurrent.locks.ReentrantLock

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

    private var mContext: Context? = null

    /**
     * 配置信息
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/3 16:13
     */
    private var mConfig: ANRConfig? = null

    /**
     * 采样管理者
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/3 16:13
     */
    private var mSampleManager: SampleManager? = null

    /**
     * 记录是否已经开启，防止重复开启
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/3 16:42
     */
    @Volatile
    private var isStart = false

    /**
     * 是否是奇数，默认为偶数
     * 用来记录调用print方法是奇数
     * 还是偶数
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 10:16
     */
    private val isOdd: AtomicBoolean = AtomicBoolean(false)

    /**
     * 触发ANR的时间
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 11:17
     */
    private var triggerANRTime: Long = 0L

    /**
     * 当前正在处理的消息
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 11:31
     */
    private var mCurrentMessage: MonitorMessage? = null

    /**
     * 当前消息id
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 15:04
     */
    @Volatile
    private var mCurrentMsgId = 0L

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
    private var mMessageInfo: MessageInfo? = null

    /**
     * 每一帧的时间
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 18:34
     */
    private var mFrameIntervalNanos = 0f

    /**
     * 可重入锁，用来解决两个线程同时操作临界区资源带来的问题
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/17 19:49
     */
    private var mLock: ReentrantLock? = null

    /**
     * 初始化
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/3 16:44
     */
    fun init(context: Context): ANRMonitor {
        mContext = context.applicationContext
        //初始化时给出默认配置
        config(
            ANRConfig(
                anrTime = 3000,
                warnTime = 300,
                idleTime = 50,
                isOpenMonitor = true
            )
        )
        mSampleManager = SampleManager
        mLock = ReentrantLock()
        mFrameIntervalNanos = try {
            val fieldValue = Reflector.QuietReflector.on(Choreographer::class.java)
                .bind(Choreographer.getInstance()).field("mFrameIntervalNanos") as Long
            fieldValue * 0.000001f
        } catch (e: Exception) {
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
    fun config(config: ANRConfig): ANRMonitor {
        this.mConfig = config
        return this
    }

    /**
     * 开始监控ANR
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/3 16:44
     */
    fun start() {
        mLock!!.lock()
        if (isStart) return
        isStart = true
        Looper.getMainLooper().setMessageLogging(this)
        SCThreadManager.runOnANRMonitorThread(mANRRunnable)
        mLock!!.unlock()
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

        /**
         * 这里单独记录一个消息id，
         * 该id不主动自增，而是通过与
         * 外面的id对齐来改变值
         * 如果该id跟外面的id不一致
         * 说明外面的id发生了改变，
         * 也就说明Looper执行了下一
         * 条消息，如果该id跟外面的
         * id一致，说明超过ANR时间后
         * Looper还在执行之前的消息，
         * 说明发生了ANR
         * 作者:ZhouZhengyi
         * 创建时间: 2022/6/9 8:22
         */
        var msgId = 0L
        run {
            anrTime = SystemClock.elapsedRealtime() + mConfig!!.anrTime!!
            while (isStart) {
                var now = SystemClock.elapsedRealtime()
                if (now >= anrTime) {
                    //时间到了，如果id一致，说明Looper还在执行原消息，那么也就发生了ANR
                    if (mCurrentMsgId == msgId) {
                        if (isStart) {
                            //由于可能执行到这里的时候，主线程也执行完了消息，可能会同时操作，出现异常，所以这里加锁
                            mLock!!.lock()
                            if (mCurrentMsgId != msgId) {
                                //由于kotlin语法不支持continue在synchronized里用，所以这里使用ReentranLock
                                continue
                            }
                            //设置新的ANR到期时间
                            anrTime = now + mConfig!!.anrTime
                            Logger.d(TAG, "start dump stack")
                            //需要将之前的消息处理完
                            handleMessage()
                            mMessageInfo = MessageInfo()
                            mMessageInfo!!.wallTime = now - mStartTime
                            //这里取的是主线程的cpu执行时间，所以没办法获取到，暂时设置为-1吧
                            mMessageInfo!!.cpuTime = -1
                            mMessageInfo!!.msgType = MessageInfo.MsgType.MSG_TYPE_ANR
                            mMessageInfo!!.messages.add(mCurrentMessage!!)
                            //处理该ANR消息，这里不能等到messageEnd时才调用，因为那个方法是消息执行完成才会调用
                            //而发生ANR时，可能还没到消息执行完成APP就被杀死了，导致messageEnd不会被调用
                            // 所以这里监控到超时就应该输出ANR并且 dump各种信息
                            handleMessage()
                            //开始采集信息
                            mSampleManager!!.startANRSample(
                                mCurrentMsgId.toString(),
                                SystemClock.elapsedRealtime()
                            )
                            mLock!!.unlock()
                        }
                    } else {
                        //时间到了，发现id不一致，说明Looper已经执行了下一条消息，那么只需要进行对齐
                        msgId = mCurrentMsgId
                        anrTime = triggerANRTime
                    }
                }
                val sleepTime = anrTime - SystemClock.elapsedRealtime()
                if (sleepTime > 0) {
                    SystemClock.sleep(sleepTime)
                }
            }
            Logger.d(TAG, "ANR Monitor Thread finish")
        }

    }


    override fun println(x: String?) {
        //如果是偶数次调用，并且是结束标志，则返回
        if (x!!.contains("<<<<< Finished to") && !isOdd.get()) {
            return
        }
        //1.开始且偶数
        //2.开始且奇数
        //3.结束且奇数
        if (!isOdd.get()) {
            messageStart(x)
        } else {
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
        if (mTempStartTime - mLastEndTime > mConfig!!.idleTime && mLastEndTime != noInit) {
            //如果上次结束的时间和当前开始的时间差距大于配置的间隔时间，则认为是空闲时间,需要增加一个空闲消息，并且不会存在两个连续的空闲消息
            if (mMessageInfo != null) {
                //如果之前存在聚合的消息，则先处理掉
                handleMessage()
            }
            mMessageInfo = MessageInfo()
            mMessageInfo!!.msgType = MessageInfo.MsgType.MSG_TYPE_IDLE
            mMessageInfo!!.wallTime = mTempStartTime - mLastEndTime
            mMessageInfo!!.cpuTime = mCpuTempStartTime - mLastCpuEndTime
            mStartTime = mTempStartTime
            //idle消息不能聚合，所以马上处理
            handleMessage()
        }
        //处理完毕后，需要重新创建一条MessageInfo,因为这时候处理的是looper获取的新消息
        if (mMessageInfo == null) {
            //创建一条新的MessageInfo，重置所有的计时
            mMessageInfo = MessageInfo()
            mStartTime = SystemClock.elapsedRealtime()
            mTempStartTime = mStartTime
            mCpuStartTime = SystemClock.currentThreadTimeMillis()
            mCpuTempStartTime = mCpuStartTime
        }
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
        mLock!!.lock()
        mLastEndTime = SystemClock.elapsedRealtime()
        mLastCpuEndTime = SystemClock.currentThreadTimeMillis()
        var costTime = mLastEndTime - mStartTime
        handleJank(costTime)
        //判断是否是ActivityThread的消息
        val isActivityThreadMsg = MessageParser.isActivityThreadMsg(mCurrentMessage!!)
        if (mMessageInfo == null) {
            //如果在这个位置为空，只有是anr采集的时候将原来的messageInfo置为空了
            mMessageInfo = MessageInfo()
        }
        decideMessageType(costTime, isActivityThreadMsg)
        mCurrentMsgId++
        mLock!!.unlock()
    }


    private fun decideMessageType(costTime: Long, isActivityThreadMsg: Boolean) {
        //如果该消息的处理时间大于配置的警告时间
        if (costTime > mConfig!!.warnTime || isActivityThreadMsg) {
            if (mMessageInfo!!.count > 0) {
                //先将之前的MessageInfo作为一条普通的INFO消息处理掉
                mMessageInfo!!.msgType = MessageInfo.MsgType.MSG_TYPE_INFO
                handleMessage()
            }
            //然后再将该消息作为一条独立的警告消息(MessageInfo)
            mMessageInfo = MessageInfo()
            mMessageInfo!!.wallTime = costTime
            mMessageInfo!!.cpuTime = mLastCpuEndTime - mCpuStartTime
            mMessageInfo!!.msgType = MessageInfo.MsgType.MSG_TYPE_WARN
            mMessageInfo!!.messages.add(mCurrentMessage!!)
            val isANR = costTime > mConfig!!.anrTime
            when {
                isANR -> {
                    //如果该消息超过配置的ANR时间，则认为是ANR消息
                    mMessageInfo!!.msgType = MessageInfo.MsgType.MSG_TYPE_ANR
                }
                isActivityThreadMsg -> {
                    mMessageInfo!!.msgType = MessageInfo.MsgType.MSG_TYPE_ACTIVITY_THREAD_H
                }
            }
            handleMessage()
            if (isANR) {
                mSampleManager!!.onHandleANRFinish()
            }
        } else {
            if ((mMessageInfo!!.wallTime + costTime) > mConfig!!.warnTime) {
                //如果聚合后发现耗时超过了配置的阈值，则处理掉之前的消息，再生成一条新的MessageInfo
                mMessageInfo!!.msgType = MessageInfo.MsgType.MSG_TYPE_INFO
                handleMessage()
                mMessageInfo = MessageInfo()
                mMessageInfo!!.wallTime = costTime
                mMessageInfo!!.cpuTime = mLastCpuEndTime - mCpuStartTime
            } else {
                //如果消息聚合后没有超过配置的阈值，则将耗时累加
                mMessageInfo!!.wallTime += costTime
                mMessageInfo!!.cpuTime += mLastCpuEndTime - mCpuStartTime
            }
            mMessageInfo!!.messages.add(mCurrentMessage!!)
            mMessageInfo!!.count++
            mMessageInfo!!.msgType = MessageInfo.MsgType.MSG_TYPE_INFO
        }
    }


    private fun handleMessage() {
        if (mMessageInfo == null) {
            return
        }
        val tempMessage = mMessageInfo
        mSampleManager!!.onMsgSample(
            SystemClock.elapsedRealtimeNanos(),
            tempMessage!!
        )
        mMessageInfo = null
    }

    private fun handleJank(costTime: Long) {
        if (MessageParser.isDoFrame(mCurrentMessage!!) && costTime > mFrameIntervalNanos * mConfig!!.jankFrame) {
            val tempMessage = MessageInfo()
            tempMessage!!.msgType = MessageInfo.MsgType.MSG_TYPE_JANK
            tempMessage!!.wallTime = costTime
            tempMessage!!.cpuTime = mLastCpuEndTime - mCpuTempStartTime
            tempMessage!!.messages.add(mCurrentMessage!!)
            mSampleManager!!.onJankSample("$mCurrentMsgId", tempMessage!!)
        }
    }
}