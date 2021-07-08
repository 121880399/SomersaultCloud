package org.somersault.cloud.lib.plugin

import android.util.Log
import android.widget.Toast
import org.somersault.cloud.lib.R
import org.somersault.cloud.lib.interf.IFunctionPlugin
import org.somersault.cloud.lib.manager.FloatViewManager
import org.somersault.cloud.lib.ui.activity.ActivityInspectionView
import org.somersault.cloud.lib.manager.ActivityManager
import org.somersault.cloud.lib.ui.Cloud
import org.somersault.cloud.lib.ui.activity.HandlerHooker

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/6/26 7:23
 * 描    述：Activity检查插件
 * 包括功能：
 *  1.Activity页面名称显示
 *  2.Fragment名称显示
 *  3.启动Activity的总时长以及详细数据
 *  4.切换Fragment的总时长以及详细数据
 * 修订历史：
 * ================================================
 */
class ActivityInspectionPlugin : IFunctionPlugin{

    companion object{
        val TAG = ActivityInspectionView::class.simpleName
        var isOpen : Boolean = false
    }

    private var view : ActivityInspectionView ?=null

    override fun getIconResId(): Int {
        return R.mipmap.sc_ic_activity_inspection
    }

    override fun getTitle(): String {
        return "页面检查"
    }

    override fun onClick() {
        val currentActivity = ActivityManager.instance.getTopActivity()!!
        if(isOpen){
            Toast.makeText(currentActivity,"页面检测关闭!",Toast.LENGTH_LONG).show()
            FloatViewManager.instance.detach(ActivityInspectionView::class.java)
            view = null
            isOpen = false
        }else{
            Toast.makeText(currentActivity,"页面检测打开!",Toast.LENGTH_LONG).show()
            HandlerHooker.doHook()
            view = ActivityInspectionView()
            view?.init(currentActivity)
            FloatViewManager.instance.attach(view!!,currentActivity)
            isOpen = true
        }
        Cloud.instance.dismiss()
    }

}