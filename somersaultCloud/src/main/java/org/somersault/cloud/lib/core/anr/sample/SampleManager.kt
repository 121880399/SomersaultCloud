package org.somersault.cloud.lib.core.anr.sample

import org.somersault.cloud.lib.core.anr.bean.MessageInfo
import org.somersault.cloud.lib.core.anr.handler.FileHandler
import org.somersault.cloud.lib.core.anr.handler.LogHandler
import org.somersault.cloud.lib.core.anr.interf.IANRSampleListener
import org.somersault.cloud.lib.core.anr.interf.ISampleHandlerListener
import org.somersault.cloud.lib.core.anr.interf.ISampleListener
import org.somersault.cloud.lib.core.anr.interf.ISampleResultListener
import org.somersault.cloud.lib.manager.SCThreadManager
import org.somersault.cloud.lib.utils.Logger

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/6/3 16:11
 * 描    述：专门进行采样
 * 修订历史：
 * ================================================
 */
object SampleManager : ISampleListener {
    const val TAG: String = "SampleManager"

    /**
     * 用来存放所有的采集器
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/18 15:57
     */
    private var mANRSampler: List<IANRSampleListener>? = null

    /**
     * 用来存放所有的处理器
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/18 16:11
     */
    private var mHandler: List<ISampleHandlerListener>? = null


    init {
        samplerInit()
        handlerInit()
    }

    /**
     * 采集器初始化
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/18 16:12
     */
    private fun samplerInit() {
        //初始化CPU采集器
        val cpuSampler = CpuSampler()
        cpuSampler.setSampleListener(object : ISampleResultListener {
            override fun onSampleSuccess(msgId: String, result: String, anrTime: Long) {
               mHandler?.forEach{
                   it.handleCpuSample(anrTime,msgId,result)
                   Logger.d(TAG,"CPU Sample finished!")
               }
            }

            override fun onSampleError(errorInfo: String) {
                Logger.d(TAG, "CPU Sample error! $errorInfo")
            }
        })
        //初始化内存采集器
        val memorySampler = MemorySampler()
        memorySampler.setSampleListener(object : ISampleResultListener {
            override fun onSampleSuccess(msgId: String, result: String, anrTime: Long) {
                mHandler?.forEach{
                    it.handleMemorySample(anrTime,msgId,result)
                    Logger.d(TAG,"Memory Sample finished!")
                }
            }

            override fun onSampleError(errorInfo: String) {
                Logger.d(TAG, "Memory Sample error! $errorInfo")
            }

        })
        //初始化消息队列采集器
        val messageQueueSampler = MessageQueueSampler()
        messageQueueSampler.setSampleListener(object : ISampleResultListener {
            override fun onSampleSuccess(msgId: String, result: String, anrTime: Long) {
                mHandler?.forEach{
                    it.handleMessageQueueSample(anrTime,msgId,result)
                    Logger.d(TAG,"MessageQueue Sample finished!")
                }
            }

            override fun onSampleError(errorInfo: String) {
                Logger.d(TAG, "MessageQueue Sample error! $errorInfo")
            }

        })
        //初始化线程堆栈采集器
        val threadStackSampler = ThreadStackSampler()
        threadStackSampler.setSampleListener(object : ISampleResultListener {
            override fun onSampleSuccess(msgId: String, result: String, anrTime: Long) {
                mHandler?.forEach{
                    it.handleThreadStackSample(anrTime,msgId,result)
                    Logger.d(TAG,"Thread stack Sample finished!")
                }
            }

            override fun onSampleError(errorInfo: String) {
                Logger.d(TAG, "Thread stack Sample error! $errorInfo")
            }
        })

        //初始化Activity采集器
        val activitySampler = ActivitySampler()
        activitySampler.setSampleListener(object : ISampleResultListener{
            override fun onSampleSuccess(msgId: String, result: String, anrTime: Long) {
                mHandler?.forEach{
                    it.handleThreadStackSample(anrTime,msgId,result)
                    Logger.d(TAG,"Activity stack Sample finished!")
                }
            }

            override fun onSampleError(errorInfo: String) {
                Logger.d(TAG, "Activity Sample error! $errorInfo")
            }

        })

        mANRSampler = mutableListOf(cpuSampler, memorySampler, messageQueueSampler, threadStackSampler)
    }

    /**
     * 处理器初始化
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/18 16:12
     */
    private fun handlerInit() {
        //初始化文件处理器
        val fileHandler = FileHandler()
        //初始化日志处理器
        val logHandler = LogHandler()
        mHandler = mutableListOf(fileHandler,logHandler)
    }

    /**
     * 开始ANR采集
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/18 12:11
     */
    fun startANRSample(msgId: String, anrTime: Long) {
        Logger.d(TAG, "start ANR Sample!")
        SCThreadManager.runOnANRSampleThread {
            mANRSampler!!.forEach {
                it.doSample(msgId,anrTime)
            }
        }
    }

    override fun onMsgSample(time: Long, msg: MessageInfo) {
        TODO("Not yet implemented")
    }

    override fun onJankSample(msgId: String, msg: MessageInfo) {
        TODO("Not yet implemented")
    }

    override fun onHandleANRFinish() {
        TODO("Not yet implemented")
    }
}