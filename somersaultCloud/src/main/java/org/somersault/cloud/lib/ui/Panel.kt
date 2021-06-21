package org.somersault.cloud.lib.ui

import android.view.LayoutInflater
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.view.isNotEmpty
import org.somersault.cloud.lib.R
import org.somersault.cloud.lib.databinding.LayoutVpSweetBinding
import org.somersault.cloud.lib.widget.sheet.sweetpick.Delegate
import org.somersault.cloud.lib.widget.sheet.sweetpick.SweetSheet
import org.somersault.cloud.lib.widget.sheet.widget.FreeGrowUpParentRelativeLayout
import org.somersault.cloud.lib.widget.sheet.widget.SweetView

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/6/19 10:50
 * 描    述：功能面板界面
 * 修订历史：
 * ================================================
 */
class Panel: Delegate() {

    private var mContentViewHeight : Int = 0

    private var mLayoutBinding : LayoutVpSweetBinding ? = null

    override fun createView(): View {
        mLayoutBinding = LayoutVpSweetBinding.inflate(LayoutInflater.from(mParentVG.context))
        mLayoutBinding!!.sweetView.animationListener = AnimationImp()
        if(mContentViewHeight > 0){
            mLayoutBinding!!.freeGrowUpParentF.setContentHeight(mContentViewHeight)
        }
        return mLayoutBinding!!.root
    }

    override fun setData() {

    }

    fun setContentHeight(height : Int){
        if(height > 0 && mLayoutBinding!!.freeGrowUpParentF != null){
            mLayoutBinding!!.freeGrowUpParentF.setContentHeight(height)
        }else{
            mContentViewHeight = height
        }
    }

    inner class AnimationImp : SweetView.AnimationListener{
        override fun onStart() {
            mLayoutBinding!!.freeGrowUpParentF.reset()
            mStatus = SweetSheet.Status.SHOWING
            mLayoutBinding!!.sliderIM.visibility = View.INVISIBLE
            mLayoutBinding!!.indicatorView.visibility = View.INVISIBLE
        }

        override fun onEnd() {
            if(mStatus == SweetSheet.Status.SHOWING){
                mLayoutBinding!!.indicatorView.alphaShow(true)
                mLayoutBinding!!.indicatorView.visibility = View.VISIBLE
                mLayoutBinding!!.indicatorView.circularReveal(
                    mLayoutBinding!!.indicatorView.width /2,
                    mLayoutBinding!!.indicatorView.height /2,
                    0F,
                    mLayoutBinding!!.indicatorView.width.toFloat(),2000,DecelerateInterpolator())
                mStatus = SweetSheet.Status.SHOW

                mLayoutBinding!!.sliderIM.visibility  = View.VISIBLE
                mLayoutBinding!!.sliderIM.circularReveal(mLayoutBinding!!.sliderIM.width /2 ,
                mLayoutBinding!!.sliderIM.height /2,
                    0F,
                    mLayoutBinding!!.sliderIM.width.toFloat()
                )

            }
        }

        override fun onContentShow() {
        }

    }
}