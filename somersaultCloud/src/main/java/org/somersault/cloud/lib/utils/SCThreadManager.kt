package org.somersault.cloud.lib.utils

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

    init {
        mSaveLogSingleThreadExecutor = ThreadPoolExecutor(1,1,60L
            ,TimeUnit.SECONDS,LinkedBlockingQueue(), ThreadFactory {
                val thread = Thread(it)
                thread.name = "Save_Log_Thread"
                return@ThreadFactory thread
            })
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
}