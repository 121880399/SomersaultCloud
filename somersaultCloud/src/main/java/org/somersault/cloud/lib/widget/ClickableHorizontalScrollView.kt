package org.somersault.cloud.lib.widget

import android.content.Context
import android.util.AttributeSet
import android.util.SparseIntArray
import android.view.MotionEvent
import android.view.View
import android.widget.HorizontalScrollView

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/5/4 9:13
 * 描    述：能够点击的HorizontalScrollView
 * HorizontalScrollView本身不带点击事件和长按事件，而且还会
 * 消费掉donw事件，所以自己实现
 * 修订历史：
 * ================================================
 */
class ClickableHorizontalScrollView: HorizontalScrollView {

    /**
    * 记录上次滑动的X坐标，
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/8 11:56
    */
    var mLastScrollX = 0

    /**
    * Down事件按下时记录的X坐标
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/8 12:02
    */
    private var mStartX = 0

    /**
    * Down事件按下时记录的Y坐标
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/8 12:03
    */
    private var mStartY = 0

    /**
    * 点击监听器
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/8 12:11
    */
    private var mClickListener : OnClickListener? = null

    /**
    * 长按监听器
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/8 12:11
    */
    private var mLongCLickListener : OnLongClickListener ? = null

    /**
    * 是否发生了长按事件，发生了就不触发点击
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/8 19:49
    */
    private var isLongClick  = false

    /**
    * 长按点击任务
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/8 19:40
    */
    private val longClickRunnable  = Runnable {
        isLongClick = true
        if(mLongCLickListener!=null){
            mLongCLickListener!!.onLongClick(this)
        }
    }

    constructor(context: Context):this(context,null)

    constructor(context: Context,attrs:AttributeSet?):this(context,attrs!!,0)

    constructor(context: Context,attrs: AttributeSet,defStyleAttr:Int):super(context,attrs,defStyleAttr)

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        when(ev!!.action){
            MotionEvent.ACTION_DOWN -> {
                mStartX = ev.x.toInt()
                mStartY = ev.y.toInt()

                postDelayed(longClickRunnable,500)
            }
            MotionEvent.ACTION_MOVE ->{
                var currentX = ev.x.toInt()
                var currentY = ev.y.toInt()
                if(Math.abs(currentX - mStartX) > 100 || Math.abs(currentY - mStartY) > 100){
                    removeCallbacks(longClickRunnable)
                }
            }
            MotionEvent.ACTION_UP,MotionEvent.ACTION_CANCEL ->{
                //记录抬起手时的X坐标
                mLastScrollX = scrollX
                removeCallbacks(longClickRunnable)
                if(!isLongClick){
                    //如果没有触发长按事件，就触发点击事件
                    if(mClickListener!=null){
                        mClickListener!!.onClick(this)
                    }
                }
                //每次记录长按的标志位，在抬起手的时候都应该还原，该语句不能与上面的if语句交换位置
                isLongClick = false
            }
        }
        return super.onTouchEvent(ev)
    }

    /**
    * 设置点击监听器
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/8 12:13
    */
    fun setClickListener(listener:OnClickListener){
        mClickListener = listener
    }

    /**
    * 设置长按监听器
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/8 12:13
    */
    fun setLongClickListener(listener: OnLongClickListener){
        mLongCLickListener = listener
    }
}