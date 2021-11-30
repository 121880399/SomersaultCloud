package org.somersault.cloud.lib.aop.slow_method

import android.os.SystemClock
import java.util.concurrent.ConcurrentHashMap

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/11/30 11:02
 * 描    述：
 * 修订历史：
 * ================================================
 */
class SlowMethodHook {

    private val METHOD_COSTS : ConcurrentHashMap<String,Long?> by lazy { ConcurrentHashMap<String,Long?>() }

    /**
     * 每个方法开始时记录开始的时间
     * 作者:ZhouZhengyi
     * 创建时间: 2021/11/30 11:51
     */
    fun methodCostStart(methodName:String){
       METHOD_COSTS[methodName] = SystemClock.elapsedRealtime()
    }

    /**
     * 每个方法结束时判断该方法是否超过阈值
     * 如果超过阈值则为慢方法，需要打印出来
     * @param methodName 方法名称
     * @param thresholdTime 超时阈值
     * 作者:ZhouZhengyi
     * 创建时间: 2021/11/30 11:53
     */
    fun methodCostEnd(methodName: String,thresholdTime: Int){

    }
}