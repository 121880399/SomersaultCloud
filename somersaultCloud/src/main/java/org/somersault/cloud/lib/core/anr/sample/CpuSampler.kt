package org.somersault.cloud.lib.core.anr.sample

import org.somersault.cloud.lib.core.anr.interf.IANRSampleListener
import org.somersault.cloud.lib.core.anr.interf.ISampleResultListener

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/6/18 15:36
 * 描    述：CPU信息采集器
 * 修订历史：
 * ================================================
 */
class CpuSampler:IANRSampleListener {

    override fun doSample(msgId: String, anrTime: Long) {
        TODO("Not yet implemented")
    }

    override fun setSampleListener(resultListener: ISampleResultListener) {
        TODO("Not yet implemented")
    }
}