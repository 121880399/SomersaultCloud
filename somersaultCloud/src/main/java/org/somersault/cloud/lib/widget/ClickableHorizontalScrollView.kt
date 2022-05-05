package org.somersault.cloud.lib.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.HorizontalScrollView

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2022/5/4 9:13
 * 描    述：
 * 修订历史：
 * ================================================
 */
class ClickableHorizontalScrollView: HorizontalScrollView {

    constructor(context: Context):this(context,null)

    constructor(context: Context,attrs:AttributeSet?):this(context,attrs!!,0)

    constructor(context: Context,attrs: AttributeSet,defStyleAttr:Int):super(context,attrs,defStyleAttr)

    override fun onTouchEvent(ev: MotionEvent?): Boolean {

        return super.onTouchEvent(ev)
    }
}