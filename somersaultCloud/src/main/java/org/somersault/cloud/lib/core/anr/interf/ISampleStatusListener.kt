package org.somersault.cloud.lib.core.anr.interf

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/6/29 9:35
 * 描    述：采集状态监听器
 * 修订历史：
 * ================================================
 */
interface ISampleStatusListener {

    /**
     * 当ANR消息处理完毕
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/5 18:01
     */
    fun onHandleANRFinish()

}