package org.somersault.cloud.lib.core.anr.interf

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/6/6 9:15
 * 描    述：
 * 修订历史：
 * ================================================
 */
interface IANRSampleListener {

    /**
     * 进行采样
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/18 15:30
     */
    fun doSample(msgId: String, anrTime: Long)

    /**
     * 设置采样结果监听器
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/18 15:35
     */
    fun setSampleListener(resultListener: ISampleResultListener)
}