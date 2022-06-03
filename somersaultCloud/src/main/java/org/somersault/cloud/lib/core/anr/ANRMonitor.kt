package org.somersault.cloud.lib.core.anr

import android.content.Context
import org.somersault.cloud.lib.core.anr.sample.SampleManager

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/6/3 14:46
 * 描    述：ANR门面类
 * 修订历史：
 * ================================================
 */
object ANRMonitor {

    private var mContext : Context? = null

    /**
     * 配置信息
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/3 16:13
     */
    private var mConfig : ANRConfig? = null

    /**
     * 采样管理者
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/3 16:13
     */
    private var mSampleManager : SampleManager? = null

    /**
     * 记录是否已经开启，防止重复开启
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/3 16:42
     */
    @Volatile private var isStart = false

    /**
     * 初始化
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/3 16:44
     */
    fun init(context : Context):ANRMonitor {
        mContext = context
        //初始化时给出默认配置
        config(ANRConfig(
            anrTime = 3000,
            warnTime = 300,
            idleTime = 50,
            isOpenMonitor = true
        ))
        mSampleManager = SampleManager
        return this
    }

    /**
     * 设置配置信息
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/3 16:44
     */
    fun config(config : ANRConfig):ANRMonitor{
        this.mConfig = config
        return this
    }

    /**
     * 开始监控ANR
     * 作者:ZhouZhengyi
     * 创建时间: 2022/6/3 16:44
     */
    @Synchronized
    fun start(){
        if(isStart) return
        isStart = true

    }
}