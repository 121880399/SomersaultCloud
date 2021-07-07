package org.somersault.cloud.lib.ui

import android.content.Context
import android.content.res.Configuration
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import org.somersault.cloud.lib.been.FvLastPositionInfo
import org.somersault.cloud.lib.interf.IFloatView
import org.somersault.cloud.lib.interf.OnTouchEventListener
import org.somersault.cloud.lib.manager.FloatViewManager
import org.somersault.cloud.lib.utils.ScreenUtils
import org.somersault.cloud.lib.widget.CloudFrameLayout
import org.somersault.cloud.lib.widget.CustomLayoutParams
import java.lang.Exception

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/6/28 8:42
 * 描    述：悬浮窗的父类，实现悬浮窗需要继承该方法
 * 修订历史：
 * ================================================
 */
abstract class BaseFloatView : IFloatView, OnTouchEventListener {

    /**
     * 悬浮窗布局 会将xml中的布局封装一层，最后添加到rootView中
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/28 10:05
     */
    private var mFloatView: FrameLayout? = null

    private var mLayoutParams: FrameLayout.LayoutParams? = null

    val mTag = this.javaClass.simpleName

    /**
     * 上一次悬浮窗的位置信息
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/30 16:38
     */
    private var mLastPositionInfo: FvLastPositionInfo? = null

    private var mViewTreeObserver: ViewTreeObserver? = null

    /**
     * 悬浮窗的实际宽度
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/30 18:01
     */
    private var mFloatViewWidth = 0

    /**
     * 悬浮窗的实际高度
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/30 18:01
     */
    private var mFloatViewHeight = 0

    /**
     * XML文件中的布局
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/30 20:23
     */
    private var mChildView: View? = null

    /**
     * 每个悬浮窗可以通过该属性初始化悬浮窗的值
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/1 8:54
     */
    private var mCustomLayoutParams: CustomLayoutParams? = null

    private val mTouchProxy = TouchProxy(this)

    private val mOnGlobalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        if (mFloatView != null) {
            //每次布局发生变动重新赋值
            mFloatViewHeight = mFloatView!!.measuredHeight
            mFloatViewWidth = mFloatView!!.measuredWidth
            mLastPositionInfo?.viewWidth = mFloatViewWidth
            mLastPositionInfo?.viewHeight = mFloatViewHeight
        }
    }


    constructor() {
        if (FloatViewManager.instance.getFloatViewLastPosition(mTag) == null) {
            mLastPositionInfo = FvLastPositionInfo()
            FloatViewManager.instance.saveFloatViewLastPosition(mTag, mLastPositionInfo!!)
        } else {
            mLastPositionInfo = FloatViewManager.instance.getFloatViewLastPosition(mTag)
        }
    }

    /**
     * 初始化方法
     * 该方法只负责创建View,不负责把View
     * 添加到rootView,为了实现全局View
     * 该方法只调用一次，每个Activity只attach
     * 和detach
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/28 8:53
     */
    fun init(context: Context) {
        try {
            //调用onCreate方法 提前初始化一些变量
            onCreate(context)
            //创建悬浮窗的父布局
            mFloatView = CloudFrameLayout(context, CloudFrameLayout.CHILD)
            //添加viewTree监听
            addViewTreeObserverListener()
            //创建悬浮窗布局View
            mChildView = onCreateView(context)
            //将xml中布局添加进悬浮窗父布局
            mFloatView?.addView(mChildView)
            //设置手势拦截
            mFloatView?.setOnTouchListener(View.OnTouchListener { v, event ->
                if(mFloatView != null){
                    return@OnTouchListener mTouchProxy.onTouchEvent(v,event)
                }else{
                    return@OnTouchListener false
                }
            })
            //调用onViewCreated
            onViewCreated(mFloatView!!)
            mCustomLayoutParams = CustomLayoutParams()
            mLayoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            mLayoutParams?.gravity = Gravity.LEFT or Gravity.TOP
            mCustomLayoutParams?.gravity = Gravity.LEFT or Gravity.TOP
            //回调给不同悬浮窗设定值
            initFloatViewLayoutParams(mCustomLayoutParams!!)
            onLayoutParamsCreated(mLayoutParams!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addViewTreeObserverListener() {
        if (mViewTreeObserver == null && mFloatView != null && mOnGlobalLayoutListener != null) {
            mViewTreeObserver = mFloatView?.viewTreeObserver
            mViewTreeObserver?.addOnGlobalLayoutListener(mOnGlobalLayoutListener)
        }
    }

    private fun removeViewTreeObserverListener() {
        if (mViewTreeObserver != null && mOnGlobalLayoutListener != null) {
            if (mViewTreeObserver?.isAlive == true) {
                mViewTreeObserver?.removeOnGlobalLayoutListener(mOnGlobalLayoutListener)
            }
        }
    }

    override fun onResume() {

    }

    override fun onPause() {

    }

    override fun onDestroy() {

    }

    override fun canDrag(): Boolean {
        return true
    }

    override fun interceptBackKey(): Boolean {
        return false
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    fun getFloatView(): View? {
        return this.mFloatView
    }

    private fun onLayoutParamsCreated(params: FrameLayout.LayoutParams) {
        //如果悬浮窗自定义了初始化值，则使用自定义的
        params.width = mCustomLayoutParams?.width ?: 0
        params.height = mCustomLayoutParams?.height ?: 0
        params.gravity = mCustomLayoutParams!!.gravity

//        val floatViewCurrentPosition = FloatViewManager.instance.getFloatViewCurrentPosition(mTag)
//        //如果当前悬浮窗在Activity上有位置信息，则使用，否则使用用户自定义的
//        if (floatViewCurrentPosition != null) {
//            if (floatViewCurrentPosition.orientation == Configuration.ORIENTATION_PORTRAIT) {
//                params.leftMargin = floatViewCurrentPosition.portraitPoint.x
//                params.topMargin = floatViewCurrentPosition.portraitPoint.y
//            } else if (floatViewCurrentPosition.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                params.leftMargin = floatViewCurrentPosition.landscapePoint.x
//                params.topMargin = floatViewCurrentPosition.landscapePoint.y
//            }
//        } else {
//            params.leftMargin = mCustomLayoutParams!!.x
//            params.topMargin = mCustomLayoutParams!!.y
//        }
        portraitOrLandscape(params)
    }

    private fun portraitOrLandscape(params: FrameLayout.LayoutParams) {
        val floatViewCurrentPosition = FloatViewManager.instance.getFloatViewCurrentPosition(mTag)

        if (floatViewCurrentPosition != null) {
            //查看当前是否时竖屏
            if (ScreenUtils.isPortrait()) {
                if (mLastPositionInfo!!.isPortrait) {
                    //如果上一次也是竖屏，那么直接使用当前位置信息
                    params.leftMargin = floatViewCurrentPosition.portraitPoint.x
                    params.topMargin = floatViewCurrentPosition.portraitPoint.y
                } else {
                    //如果上一次是横屏，那么就要根据比例计算
                    params.leftMargin =
                        ((floatViewCurrentPosition.landscapePoint.x * mLastPositionInfo!!.leftMarginPercent).toInt())
                    params.topMargin = (floatViewCurrentPosition.landscapePoint.y * mLastPositionInfo!!.topMarginPercent).toInt()
                }
            } else {
                //如果当前是横屏,上一次是竖屏
                if (mLastPositionInfo!!.isPortrait) {
                    params.leftMargin = (floatViewCurrentPosition.portraitPoint.x * mLastPositionInfo!!.leftMarginPercent).toInt()
                    params.topMargin = (floatViewCurrentPosition.portraitPoint.y * mLastPositionInfo!!.topMarginPercent).toInt()
                } else {
                    params.leftMargin = floatViewCurrentPosition.landscapePoint.x
                    params.topMargin = floatViewCurrentPosition.landscapePoint.y
                }
            }
        } else {
            //如果没有当前悬浮窗在Activity的位置信息
            //查看当前是否时竖屏
            if (ScreenUtils.isPortrait()) {
                if (mLastPositionInfo!!.isPortrait) {
                    //如果上一次也是竖屏
                    params.leftMargin = mCustomLayoutParams!!.x
                    params.topMargin = mCustomLayoutParams!!.y
                } else {
                    //如果上一次是横屏，那么就要根据比例计算
                    params.leftMargin =
                        ((mCustomLayoutParams!!.x * mLastPositionInfo!!.leftMarginPercent).toInt())
                    params.topMargin = (mCustomLayoutParams!!.y * mLastPositionInfo!!.topMarginPercent).toInt()
                }
            } else {
                //如果当前是横屏,上一次是竖屏
                if (mLastPositionInfo!!.isPortrait) {
                    params.leftMargin = (mCustomLayoutParams!!.x * mLastPositionInfo!!.leftMarginPercent).toInt()
                    params.topMargin = (mCustomLayoutParams!!.y * mLastPositionInfo!!.topMarginPercent).toInt()
                } else {
                    params.leftMargin = mCustomLayoutParams!!.x
                    params.topMargin = mCustomLayoutParams!!.y
                }
            }
        }
        mLastPositionInfo?.setPortrait()
        mLastPositionInfo?.setLeftMargin(params.leftMargin)
        mLastPositionInfo?.setTopMargin(params.topMargin)

        FloatViewManager.instance.saveCurrentPosition(mTag,params.leftMargin,params.topMargin)
        mFloatView?.layoutParams = params
    }

    override fun onMove(x: Int, y: Int, dx: Int, dy: Int) {
        if(!canDrag()){
            return
        }
        mLayoutParams!!.leftMargin += dx
        mLayoutParams!!.topMargin += dy
        updateViewLayout(mTag)
    }

    override fun onUp(x: Int, y: Int) {
        if(!canDrag()){
            return
        }
        //手指抬起时保存当前悬浮窗位置
        FloatViewManager.instance.saveCurrentPosition(mTag,mLayoutParams!!.leftMargin,mLayoutParams!!.topMargin)
    }

    override fun onDown(x: Int, y: Int) {
       if(!canDrag()){
           return
       }
    }

    fun updateViewLayout(tag:String){
        if(mFloatView == null || mChildView == null || mLayoutParams == null){
            return
        }
        //更新位置前先保存当前信息
        mLastPositionInfo?.setPortrait()
        mLastPositionInfo?.setLeftMargin(mLayoutParams!!.leftMargin)
        mLastPositionInfo?.setTopMargin(mLayoutParams!!.topMargin)
        mLayoutParams?.width = mFloatViewWidth
        mLayoutParams?.height = mFloatViewHeight
        limitBorderLine()
        mFloatView?.layoutParams = mLayoutParams
    }

    private fun limitBorderLine(){
        if(mLayoutParams!!.topMargin <= 0){
            mLayoutParams?.topMargin = 0
        }
        if(ScreenUtils.isPortrait()){
            if(mLayoutParams!!.topMargin >= getScreenLongSideLength() - mFloatViewHeight){
                mLayoutParams?.topMargin = getScreenLongSideLength() - mFloatViewHeight
            }
        }else{
            if(mLayoutParams!!.topMargin >= getScreenShortSideLength() - mFloatViewHeight){
                mLayoutParams?.topMargin = getScreenShortSideLength() - mFloatViewHeight
            }
        }

        if(mLayoutParams!!.leftMargin <= 0){
            mLayoutParams?.leftMargin = 0
        }

        if(ScreenUtils.isPortrait()){
            if(mLayoutParams!!.leftMargin >= getScreenShortSideLength() - mFloatViewHeight){
                mLayoutParams?.leftMargin = getScreenShortSideLength() - mFloatViewHeight
            }
        }else{
            if(mLayoutParams!!.leftMargin >= getScreenLongSideLength() - mFloatViewHeight){
                mLayoutParams?.leftMargin = getScreenLongSideLength() - mFloatViewHeight
            }
        }
    }

    fun getScreenLongSideLength():Int{
        return if(ScreenUtils.isPortrait()){
            ScreenUtils.getScreenHeight()
        }else{
            ScreenUtils.getScreenWidth()
        }
    }

    fun getScreenShortSideLength():Int{
        return if(ScreenUtils.isPortrait()){
            ScreenUtils.getScreenWidth()
        }else{
            ScreenUtils.getScreenHeight()
        }
    }
}