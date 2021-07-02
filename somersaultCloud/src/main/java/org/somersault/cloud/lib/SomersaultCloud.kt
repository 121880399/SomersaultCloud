package org.somersault.cloud.lib

import android.app.Activity
import android.app.Application
import org.somersault.cloud.lib.interf.IFunctionPlugin
import org.somersault.cloud.lib.manager.FloatViewManager
import org.somersault.cloud.lib.plugin.ActivityInspectionPlugin
import org.somersault.cloud.lib.ui.Cloud
import org.somersault.cloud.lib.utils.ApplicationUtils
import org.somersault.cloud.lib.manager.ActivityManager

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/6/18 16:02
 * 描    述：
 * 修订历史：
 * ================================================
 */
class SomersaultCloud private constructor(){

    companion object{
        val instance = Holder.holder
    }

    private object Holder{
        val holder = SomersaultCloud()
    }

    private val performanceList : ArrayList<IFunctionPlugin> = ArrayList()

    private val uiList : ArrayList<IFunctionPlugin> = ArrayList()

    private val AppInfoList : ArrayList<IFunctionPlugin> = ArrayList()

    /**
     * 初始化筋斗云
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/18 16:03
     */
    fun init(app: Application){
        ActivityManager.instance.register(app)
        ApplicationUtils.getInstance().application = app
        registerPlugin()
        //初始化功能悬浮窗管理器
        FloatViewManager.instance.init(app)
    }

    private fun registerPlugin(){
        //注册性能检测插件
        performanceList.add(ActivityInspectionPlugin())
        //注册ui检查插件
        //注册App信息插件
    }

    fun getPerformancePlugins():ArrayList<IFunctionPlugin>{
        return performanceList
    }

    fun getUIPlugins():ArrayList<IFunctionPlugin>{
        return uiList
    }

    fun getAppInfoPlugins():ArrayList<IFunctionPlugin>{
        return AppInfoList
    }

    /**
     * 显示筋斗云
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/23 8:48
     */
    fun show(activity : Activity){
        Cloud.instance.attach(activity).add(activity)
    }

}