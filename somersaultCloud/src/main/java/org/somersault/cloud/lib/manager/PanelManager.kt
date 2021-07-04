 package org.somersault.cloud.lib.manager

import android.view.ViewGroup
import org.somersault.cloud.lib.ui.Panel
import org.somersault.cloud.lib.utils.ScreenUtils
import org.somersault.cloud.lib.widget.sheet.sweetpick.DimEffect
import org.somersault.cloud.lib.widget.sheet.sweetpick.SweetSheet
import org.somersault.cloud.lib.widget.sheet.widget.SweetView

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/6/24 8:35
 * 描    述：Panel面板管理类
 * 修订历史：
 * ================================================
 */
class PanelManager {

    private var mSweetSheet : SweetSheet ? = null

    fun setupPanel(parent : ViewGroup){
        mSweetSheet = SweetSheet(parent)
        val panel = Panel()
        panel.setContentHeight(ScreenUtils.getScreenHeight() / 2)
        mSweetSheet?.delegate = panel
        mSweetSheet?.setBackgroundEffect(DimEffect(8F))
    }

    fun toggle(){
        mSweetSheet?.toggle()
    }

    fun dismiss():Boolean{
        if(mSweetSheet == null || !mSweetSheet!!.isShow){
            return false
        }
        mSweetSheet?.dismiss()
        return true
    }
}