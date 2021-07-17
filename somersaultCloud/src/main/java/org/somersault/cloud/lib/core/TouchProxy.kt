package org.somersault.cloud.lib.core

import android.view.MotionEvent
import android.view.View
import org.somersault.cloud.lib.interf.OnTouchEventListener
import org.somersault.cloud.lib.utils.ScreenUtils
import kotlin.math.abs

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/7/6 9:55
 * 描    述：
 * 修订历史：
 * ================================================
 */
class TouchProxy {

    companion object{
        const val MIN_DISTANCE_MOVE = 4
        const val MIN_TAP_TIME = 1000
    }

    private var mEventListener:OnTouchEventListener?=null

    private var mLastX = 0
    private var mLastY = 0
    private var mStartX = 0
    private var mStartY = 0
    private var mState =  TouchState.STATE_STOP

    constructor(eventListener: OnTouchEventListener){
        this.mEventListener = eventListener
    }

    fun setEventListener(eventListener: OnTouchEventListener){
        this.mEventListener = eventListener
    }

    fun onTouchEvent(view:View,event:MotionEvent):Boolean{
        val distance = ScreenUtils.dp2px(1F) * MIN_DISTANCE_MOVE
        val x = event.rawX.toInt()
        val y = event.rawY.toInt()
        when(event.action){
            MotionEvent.ACTION_DOWN -> {
                mStartX = x
                mStartY = y
                mLastX = x
                mLastY = y
                mEventListener?.onDown(x,y)
            }
            MotionEvent.ACTION_MOVE ->{
                if(abs(x - mStartX) < distance
                    && abs(y - mStartY) < distance){
                    if(mState == TouchState.STATE_STOP){
                        return true
                    }
                }else if(mState != TouchState.STATE_MOVE){
                    mState = TouchState.STATE_MOVE
                }
                if(mEventListener != null){
                    mEventListener?.onMove(mLastX,mLastY,x-mLastX,y-mLastY)
                }
                mLastX = x
                mLastY = y
                mState = TouchState.STATE_MOVE
            }
            MotionEvent.ACTION_UP ->{
                if(mEventListener  != null){
                    mEventListener?.onUp(x,y)
                }
                if(mState != TouchState.STATE_MOVE && (event.eventTime - event.downTime) < MIN_TAP_TIME){
                    view.performClick()
                }
                mState = TouchState.STATE_STOP
            }
        }
        return true
    }

    private enum class TouchState{
        STATE_MOVE,
        STATE_STOP
    }
}