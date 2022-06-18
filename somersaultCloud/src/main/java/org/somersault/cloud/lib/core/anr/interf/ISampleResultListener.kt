package org.somersault.cloud.lib.core.anr.interf

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/6/18 15:31
 * 描    述：采样结果监听器
 * 修订历史：
 * ================================================
 */
interface ISampleResultListener {

    /**
     * 采样成功后返回结果
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/18 15:32
     */
    fun onSampleSuccess(msgId: String, result: String, anrTime: Long)

    /**
     * 采样失败后返回原因
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/18 15:32
     */
    fun onSampleError(errorInfo:String)
}