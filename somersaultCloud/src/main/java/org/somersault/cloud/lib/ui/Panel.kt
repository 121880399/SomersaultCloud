package org.somersault.cloud.lib.ui

import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isNotEmpty
import org.somersault.cloud.lib.R
import org.somersault.cloud.lib.databinding.LayoutVpSweetBinding
import org.somersault.cloud.lib.widget.sheet.sweetpick.Delegate
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

    fun setContentHeight(height : Int){
        if(height > 0 && mLayoutBinding!!.freeGrowUpParentF != null){
            mLayoutBinding!!.freeGrowUpParentF.setContentHeight(height)
        }else{
            mContentViewHeight = height
        }
    }

    class AnimationImp : SweetView.AnimationListener{
        override fun onStart() {
            TODO("Not yet implemented")
        }

        override fun onEnd() {
            TODO("Not yet implemented")
        }

        override fun onContentShow() {
            TODO("Not yet implemented")
        }

    }
}