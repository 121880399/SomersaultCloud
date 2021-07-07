package org.somersault.cloud.lib.interf

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/7/6 10:00
 * 描    述：
 * 修订历史：
 * ================================================
 */
interface OnTouchEventListener {

    fun onMove(x:Int,y:Int,dx:Int,dy:Int)

    fun onUp(x:Int,y:Int)

    fun onDown(x: Int,y: Int)

}