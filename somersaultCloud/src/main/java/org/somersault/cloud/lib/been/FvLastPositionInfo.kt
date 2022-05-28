package org.somersault.cloud.lib.been

import org.somersault.cloud.lib.utils.ScreenUtils

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/6/30 14:38
 * 描    述：记录悬浮窗位置信息
 * 修订历史：
 * ================================================
 */
class FvLastPositionInfo(var isPortrait :Boolean = true, var viewWidth : Int = 0, var viewHeight :Int = 0, var startMarginPercent :Float = 0f, var topMarginPercent:Float = 0f){


    fun setStartMargin(startMargin:Int){
        this.startMarginPercent = (startMargin / ScreenUtils.getScreenWidth()).toFloat()
    }

    fun setTopMargin(topMargin:Int){
        this.topMarginPercent = (topMargin / ScreenUtils.getScreenHeight()).toFloat()
    }

    fun setPortrait(){
        this.isPortrait = ScreenUtils.isPortrait()
    }

}