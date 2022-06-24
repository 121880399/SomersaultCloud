package org.somersault.cloud.lib.core.anr.sample

import org.somersault.cloud.lib.core.anr.interf.IANRSampleListener
import org.somersault.cloud.lib.core.anr.interf.ISampleResultListener
import java.lang.StringBuilder

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/6/18 15:39
 * 描    述：线程堆栈采集器
 * 修订历史：
 * ================================================
 */
class ThreadStackSampler:IANRSampleListener {

    private var mSampleResultListener: ISampleResultListener? = null

    /**
     * 采集当前进程中所有线程的堆栈
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/24 9:17
     */
    override fun doSample(msgId: String, anrTime: Long) {
        val stacks = Thread.getAllStackTraces() as Map<Thread, Array<StackTraceElement>>
        if(stacks.isEmpty()) {
            if(mSampleResultListener!=null){
                mSampleResultListener?.onSampleError("Thread count is zero!")
            }
            return
        }
        val result = StringBuilder()
        stacks.keys.forEach { it ->
            val stackTraceElements = stacks[it]
            result.append("Thread:${it.name}")
            stackTraceElements?.forEach {
                result.append("\n\t${it.className}.${it.methodName}")
            }
            result.append("\n")
        }
        if(mSampleResultListener!=null){
            mSampleResultListener?.onSampleSuccess(msgId,result.toString(),anrTime)
        }
    }

    override fun setSampleListener(resultListener: ISampleResultListener) {
        mSampleResultListener = resultListener
    }
}