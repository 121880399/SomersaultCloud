package org.somersault.cloud.lib.manager

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.util.ArrayMap
import android.view.View
import org.somersault.cloud.lib.been.FvCurrentPositionInfo
import org.somersault.cloud.lib.been.FvLastPositionInfo
import org.somersault.cloud.lib.ui.BaseFloatView
import org.somersault.cloud.lib.utils.ScreenUtils
import java.lang.Exception

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/6/29 10:09
 * 描    述：悬浮窗管理类
 * 其中包括已经打开的所有悬浮窗
 * 修订历史：
 * ================================================
 */
class FloatViewManager private constructor() {

    companion object {
        val instance = Holder.holder
    }

    private object Holder {
        val holder = FloatViewManager()
    }


    /**
     * 存放所有需要打开的悬浮窗
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/30 8:30
     */
    private var mFloatViews: ArrayMap<String, BaseFloatView>? = null

    /**
     * 某个Activity已经显示了的悬浮窗
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/30 9:30
     */
    private var mActivityFloatViews: ArrayMap<Activity, ArrayMap<String, BaseFloatView>>? = null

    /**
     * 保存上一次悬浮窗关闭时的位置，下一次打开以后还在同一个位置
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/30 16:44
     */
    private var mFloatViewsLastPosition: ArrayMap<String, FvLastPositionInfo>? = null

    /**
     * 每个悬浮窗在页面中的位置
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/30 17:28
     */
    private var mFloatViewsCurrentPosition: ArrayMap<String, FvCurrentPositionInfo>? = null

    private var mContext: Context? = null
    /**
     * 是否初始化
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/1 10:37
     */
    private var isInit = false

    fun init(context: Context) {
        if(isInit){
            return
        }
        this.mContext = context
        isInit = true
        mFloatViews = ArrayMap<String, BaseFloatView>()
        mActivityFloatViews = ArrayMap()
        mFloatViewsLastPosition = ArrayMap()
        mFloatViewsCurrentPosition = ArrayMap()
    }


    fun onActivityStart(activity: Activity) {
        if (mFloatViews == null || mFloatViews?.size == 0) {
            return
        }

        //将所有已经打开的悬浮窗添加到新建的Activity上
        mFloatViews?.forEach {
            attach(it.value)
        }

    }

    fun onActivityStop(activity: Activity){
        val currentFloatViews = mActivityFloatViews?.get(activity)
        currentFloatViews?.forEach {
            it.value.onStop()
        }
    }

    /**
     * 将指定的悬浮窗添加到Activity上
     * 该方法有两种情况会调用
     * 1.新打开一个Activity，需要把已经打开的悬浮窗添加上去
     * 2.在一个Activity显示时，添加新的悬浮窗
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/30 8:45
     */
    fun attach(floatView: BaseFloatView) {
        try {
            //如果悬浮窗不在map里面 说明是新添加的悬浮窗
            if (!mFloatViews!!.containsValue(floatView)) {
                mFloatViews?.put(floatView.mTag, floatView)
            }
            val currentActivity = ActivityManager.instance.getTopActivity()
            var currentActivityFloatViews: ArrayMap<String, BaseFloatView>
            if (mActivityFloatViews?.get(currentActivity) == null) {
                //为空说明当前Activity还没有添加任何的悬浮窗
                currentActivityFloatViews = ArrayMap()
                mActivityFloatViews?.put(currentActivity, currentActivityFloatViews)
            } else {
                currentActivityFloatViews = mActivityFloatViews!![currentActivity]!!
            }
            //不为空，说明已经在Activity上面显示
            if (currentActivityFloatViews[floatView.mTag] != null) {
                return
            }
            //表示当前悬浮窗已经在当前Activity显示
            currentActivityFloatViews[floatView.mTag] = floatView
            //将悬浮窗添加到当前Activity的根布局
            ActivityManager.instance.getRootView(currentActivity!!)?.addView(floatView.getFloatView())
            floatView.getFloatView()?.postDelayed(Runnable {
                floatView.onResume()
            }, 100)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 移除悬浮窗
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/1 21:36
     */
    fun detach(tag:String){
        if(mActivityFloatViews == null){
            return
        }
        mFloatViews?.remove(tag)
        //从Activity中移除指定悬浮窗
        val currentActivity = ActivityManager.instance.getTopActivity()
        val floatViews = mActivityFloatViews?.get(currentActivity)
        val view = floatViews?.get(tag) ?: return
        ActivityManager.instance.getRootView(currentActivity!!)?.removeView(view.getFloatView())
        //请求重新绘制
        getDecorView(currentActivity).requestLayout()
        //回调onDestory方法，里面可以销毁一些数据
        view.onDestroy()
        //从当前Activity显示的悬浮窗集合中移除
        floatViews.remove(tag)
    }


    fun detach(floatViewClass : Class<out BaseFloatView>){
        detach(floatViewClass.simpleName)
    }

    /**
     * 移除所有Activity的所有悬浮窗
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/1 21:36
     */
    fun detachAll(){
        if(mActivityFloatViews == null){
            return
        }

        mActivityFloatViews?.forEach {
            val floatViews = it.value
            val currentActivity = it.key
            val rootView = ActivityManager.instance.getRootView(currentActivity)
            floatViews.forEach{ it ->
                rootView?.removeView(it.value.getFloatView())
            }

           //清除数据
            floatViews.clear()
        }
        mActivityFloatViews?.clear()
        mFloatViews?.clear()
    }

    private fun getDecorView(activity: Activity): View {
        return activity.window.decorView
    }

    fun getFloatViewLastPosition(tag: String): FvLastPositionInfo? {
        return mFloatViewsLastPosition?.get(tag)
    }

    fun saveFloatViewLastPosition(key: String, lastPositionInfo: FvLastPositionInfo) {
        mFloatViewsLastPosition?.put(key, lastPositionInfo)
    }

    fun getFloatViewCurrentPosition(tag: String): FvCurrentPositionInfo? {
        return mFloatViewsCurrentPosition?.get(tag)
    }

    /**
     * 存储悬浮窗的当前位置
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/1 10:15
     */
    fun saveCurrentPosition(tag: String, marginLeft: Int, marginTop: Int) {
        if (mFloatViewsCurrentPosition == null) {
            return
        }
        var orientation = -1
        val portraitPoint = Point()
        val landscapePoint = Point()
        if (ScreenUtils.isPortrait()) {
            orientation = Configuration.ORIENTATION_PORTRAIT
            portraitPoint.x = marginLeft
            portraitPoint.y = marginTop
        }else{
            orientation = Configuration.ORIENTATION_LANDSCAPE
            landscapePoint.x = marginLeft
            landscapePoint.y = marginTop
        }

        if(mFloatViewsCurrentPosition?.get(tag) == null){
            val position = FvCurrentPositionInfo(orientation,portraitPoint,landscapePoint)
            mFloatViewsCurrentPosition?.put(tag,position)
        }else{
            val position = mFloatViewsCurrentPosition?.get(tag)
            if(position!=null){
                position.orientation = orientation
                position.portraitPoint = portraitPoint
                position.landscapePoint = landscapePoint
            }
        }
    }
}