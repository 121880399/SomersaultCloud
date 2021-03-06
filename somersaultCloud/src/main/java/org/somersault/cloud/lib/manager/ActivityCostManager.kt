package org.somersault.cloud.lib.manager

import android.os.SystemClock

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/7/2 16:39
 * 描    述：Activity启动耗时管理类
 * 主要通过三个阶段来分析耗时，以A Activity打开B Activity为例，
 * 第一阶段是A的onPause耗时
 * 第二阶段是B的Launch操作，主要是onCreate和onResume耗时(onCreate-onResume)
 * 第三阶段是B的window渲染操作，调用DecorView的post方法进行统计(onResume-第一帧绘制完成)
 * 这里忽略ASM,WMS进行通讯的耗时，主要是因为这部分耗时无法通过非侵入式的方法统计，
 * 而且用户也无法对这部分耗时做优化，除非重写Activity调用AMS的逻辑
 * 所以总耗时=pause+launch+render+other
 * https://mp.weixin.qq.com/s/Rq3aTBMV4drU1KGXaO84SA
 * 修订历史：
 * ================================================
 */
class ActivityCostManager private constructor(){

    companion object{
        val instance = Holder.holder
    }

    private object Holder{
        val holder = ActivityCostManager()
    }

    private var mStartTime :Long = 0
    private var mPauseCostTime :Long = 0
    private var mLaunchStartTime :Long = 0
    private var mLaunchCostTime :Long = 0
    private var mRenderStartTime :Long = 0
    private var mRenderCostTime :Long = 0
    private var mTotalCostTime :Long = 0
    private var mOtherCostTime :Long = 0

    private var mPreActivity :String? = null
    private var mCurrentActivity :String? = null

    /**
     * 发送跳转时开始执行前一个Activity
     * onPause方法时调用
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/2 17:22
     */
    fun pause(){
        mStartTime = SystemClock.elapsedRealtime()
        mPauseCostTime = 0
        mLaunchCostTime = 0
        mRenderCostTime = 0
        mOtherCostTime = 0
        mLaunchStartTime = 0
        mTotalCostTime = 0
        mRenderStartTime = 0
        val topActivity = ActivityManager.instance.getTopActivity()
        if(topActivity != null){
            //记录onPause的Activity
            mPreActivity = topActivity.javaClass.simpleName
        }
    }

    /**
     * 执行完上一个Activity onPause方法后调用
     * 用于记录Pause阶段的耗时
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/2 17:25
     */
    fun paused(){
        mPauseCostTime = SystemClock.elapsedRealtime() - mStartTime
    }

    /**
     * 如果从通知栏打开Activity，是不走pause方法的
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/2 17:27
     */
    fun launch(){
        if(mStartTime == 0L){
            //开始时间为0，就是没走onPause方法
            mStartTime = SystemClock.elapsedRealtime()
            mPauseCostTime = 0
            mRenderCostTime = 0
            mOtherCostTime = 0
            mTotalCostTime = 0
            mRenderStartTime = 0
        }
        mLaunchStartTime = SystemClock.elapsedRealtime()
        mLaunchCostTime = 0
    }

    /**
     * 进入后台将开始时间设置为0
     * 作者: ZhouZhengyi
     * 创建时间: 2021/7/3 8:06
     */
    fun onBackground(){
        mStartTime = 0
    }

    /**
     * 启动新Activity后调用该方法
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/2 17:33
     */
    fun launchEnd(){
       mLaunchCostTime = SystemClock.elapsedRealtime() - mLaunchStartTime
       //启动完成以后执行render方法，统计渲染耗时
       render()
    }

    fun render(){
        mRenderStartTime = SystemClock.elapsedRealtime()
        val topActivity = ActivityManager.instance.getTopActivity()
        if(topActivity!=null && topActivity.window !=null){
            mCurrentActivity = topActivity.javaClass.simpleName
            //第一次post在performTraversals开始被执行，执行时机早于measure,layout,draw
            //所以需要再post一次，等带measure,layout,draw完成之后执行rendEnd方法
            //这时候第一帧就绘制完成
            topActivity?.window?.decorView?.post {
                topActivity?.window?.decorView?.post {
                    renderEnd()
                }
            }
        }else{
            renderEnd()
        }
    }

    fun renderEnd(){
        //计算渲染耗时
        mRenderCostTime = SystemClock.elapsedRealtime() - mRenderStartTime
        //计算总耗时
        mTotalCostTime = SystemClock.elapsedRealtime() - mStartTime
        //计算其他耗时，可能是于AMS通信之类的耗时
        mOtherCostTime = mTotalCostTime - mPauseCostTime - mLaunchCostTime - mRenderCostTime
    }

    /**
    * 得到pause耗时
    * 作者: ZhouZhengyi
    * 创建时间: 2021/7/4 9:48
    */
    fun getPauseCost():Long{
        return mPauseCostTime
    }

    /**
    * 得到启动耗时
    * 作者: ZhouZhengyi
    * 创建时间: 2021/7/4 9:49
    */
    fun getLaunchCost():Long{
        return mLaunchCostTime
    }

    /**
    * 得到渲染耗时
    * 作者: ZhouZhengyi
    * 创建时间: 2021/7/4 9:49
    */
    fun getRenderCost():Long{
        return mRenderCostTime
    }

    /**
    * 得到总耗时
    * 作者: ZhouZhengyi
    * 创建时间: 2021/7/4 9:49
    */
    fun getTotalCost():Long{
        return mTotalCostTime
    }

    /**
    * 得到其它耗时
    * 作者: ZhouZhengyi
    * 创建时间: 2021/7/4 9:49
    */
    fun getOtherCost():Long{
        return mOtherCostTime
    }

}