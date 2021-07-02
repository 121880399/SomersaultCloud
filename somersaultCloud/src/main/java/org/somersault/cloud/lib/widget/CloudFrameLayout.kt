package org.somersault.cloud.lib.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/6/30 18:39
 * 描    述：
 * 修订历史：
 * ================================================
 */
class CloudFrameLayout : FrameLayout {
    companion object {
        const val ROOT = 1
        const val CHILD = 2
    }

    private var mFlag : Int = ROOT

    constructor(context: Context,flag : Int):super(context){
       this.mFlag = flag
    }

    constructor(context: Context,attrs:AttributeSet):super(context,attrs){

    }

    constructor(context: Context,attrs: AttributeSet,defStyleAttr:Int):super(context,attrs,defStyleAttr){

    }

}