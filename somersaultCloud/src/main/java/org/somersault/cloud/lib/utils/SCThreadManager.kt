package org.somersault.cloud.lib.utils

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/5/22 11:14
 * 描    述：线程管理器，管理需要的线程
 * 修订历史：
 * ================================================
 */
//使用object定义一个单例类
object SCThreadManager {

    /**
    * 用于保存日志到本地文件的专用单线程池
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/22 11:21
    */
    private var mSaveLogSingleThreadExecutor : ThreadPoolExecutor ? = null

    /**
    * 读取日志专用单线程池
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/25 7:49
    */
    private var mReadLogSingleThreadExecutor : ThreadPoolExecutor ? = null

    private var mMainThreadHandler : Handler ? = null

    init {
        mSaveLogSingleThreadExecutor = ThreadPoolExecutor(1,1,60L
            ,TimeUnit.SECONDS,LinkedBlockingQueue(), ThreadFactory {
                val thread = Thread(it)
                thread.name = "Save_Log_Thread"
                return@ThreadFactory thread
            })
        mReadLogSingleThreadExecutor = ThreadPoolExecutor(1,1,60L,
            TimeUnit.SECONDS,LinkedBlockingQueue(), ThreadFactory {
                val thread = Thread(it)
                thread.name = "Read_Log_Thread"
                return@ThreadFactory thread
            })
        mMainThreadHandler = Handler(Looper.getMainLooper())
    }

    /**
    * 得到保存日志文件的Dispatcher
     * 用于协程
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/22 11:32
    */
    fun getSaveLogDispatcher(): CoroutineContext {
        return mSaveLogSingleThreadExecutor!!.asCoroutineDispatcher()
    }

    /**
    * 在主线程中运行
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/24 9:43
    */
    fun runOnUIThread(runnable : Runnable){
        mMainThreadHandler!!.post(runnable)
    }

    /**
    * 在读取日志线程中运行
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/25 7:52
    */
    fun runOnReadLogThread(runnable: Runnable){
        mReadLogSingleThreadExecutor!!.execute(runnable)
    }
}