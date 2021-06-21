package org.somersault.cloud.lib.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.core.view.isNotEmpty
import androidx.viewpager.widget.ViewPager
import org.somersault.cloud.lib.R
import org.somersault.cloud.lib.adapter.CommonPagerAdapter
import org.somersault.cloud.lib.databinding.LayoutVpSweetBinding
import org.somersault.cloud.lib.interf.IPageView
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

    private var mPages : ArrayList<IPageView>? = null

    private var mCurrentPage : IPageView? = null

    override fun createView(): View {
        mLayoutBinding = LayoutVpSweetBinding.inflate(LayoutInflater.from(mParentVG.context))
        mLayoutBinding!!.sweetView.animationListener = AnimationImp()
        if(mContentViewHeight > 0){
            mLayoutBinding!!.freeGrowUpParentF.setContentHeight(mContentViewHeight)
        }
        return mLayoutBinding!!.root
    }

    override fun setData() {
        mPages!!.add(PerformancePageView())
        mPages!!.add(UIPageView())
        mPages!!.add(AppInfoPageView())
        mLayoutBinding!!.viewPager.adapter = CommonPagerAdapter(mPages!!)
        mLayoutBinding!!.indicatorView.setViewPager(mLayoutBinding!!.viewPager)
        mLayoutBinding!!.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                mCurrentPage = mPages!![position]
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })
    }

    override fun show() {
        super.show()
        val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        if(mLayoutBinding!!.root.parent!=null){
            mParentVG.removeView(mRootView)
        }

        mParentVG.addView(mRootView,layoutParams)
        mLayoutBinding!!.sweetView.show()
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