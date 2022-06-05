package org.somersault.cloud.lib.core.anr

import android.content.Context
import android.os.Looper
import android.os.SystemClock
import android.util.Printer
import org.somersault.cloud.lib.core.anr.bean.MonitorMessage
import org.somersault.cloud.lib.core.anr.sample.SampleManager
import org.somersault.cloud.lib.manager.SCThreadManager
import java.util.concurrent.atomic.AtomicBoolean

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/6/3 14:46
 * 描    述：ANR门面类
 * 修订历史：
 * ================================================
 */
object ANRMonitor : Printer {

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
     * 初始化
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/3 16:44
     */
    fun init(context : Context):ANRMonitor {
        mContext = context
        //初始化时给出默认配置
        config(ANRConfig(
            anrTime = 3000,
            warnTime = 300,
            idleTime = 50,
            isOpenMonitor = true
        ))
        mSampleManager = SampleManager
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
        var tempStartTime = SystemClock.elapsedRealtime()
        triggerANRTime = tempStartTime + mConfig!!.anrTime
        mCurrentMessage = MessageParser.parsePrinterStart(log!!)
    }

    /**
     * Looper将消息处理完毕
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 11:13
     */
    private fun messageEnd(x: String?) {

    }
}