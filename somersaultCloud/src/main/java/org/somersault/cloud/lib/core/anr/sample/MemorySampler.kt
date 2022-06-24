package org.somersault.cloud.lib.core.anr.sample

import android.app.ActivityManager
import android.os.Debug
import org.somersault.cloud.lib.core.anr.interf.IANRSampleListener
import org.somersault.cloud.lib.core.anr.interf.ISampleResultListener
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/6/18 15:38
 * 描    述：内存信息采集器
 * 修订历史：
 * ================================================
 */
class MemorySampler : IANRSampleListener {

    private var mSampleResultListener: ISampleResultListener? = null

    /**
     * 读取/proc/meminfo中的数据
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/24 9:15
     */
    override fun doSample(msgId: String, anrTime: Long) {
        var memoryReader: BufferedReader? = null
        val result = StringBuilder()
        try {
            memoryReader = BufferedReader(InputStreamReader(FileInputStream("/proc/meminfo")), 1024)
            while (memoryReader.readLine() != null) {
                result.append(memoryReader.readLine())
                result.append("\n")
            }
            if(mSampleResultListener!=null){
                mSampleResultListener!!.onSampleSuccess(msgId,result.toString(),anrTime)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mSampleResultListener!!.onSampleError(e.toString())
        }
    }

    override fun setSampleListener(resultListener: ISampleResultListener) {
        mSampleResultListener = resultListener
    }
}