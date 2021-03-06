package org.somersault.cloud.lib

import android.app.Activity
import android.app.Application
import me.weishu.reflection.Reflection
import org.somersault.cloud.lib.been.Operation
import org.somersault.cloud.lib.interf.IFunctionPlugin
import org.somersault.cloud.lib.manager.FloatViewManager
import org.somersault.cloud.lib.plugin.ActivityInspectionPlugin
import org.somersault.cloud.lib.core.Cloud
import org.somersault.cloud.lib.core.log.LogcatManager
import org.somersault.cloud.lib.utils.ApplicationUtils
import org.somersault.cloud.lib.manager.ActivityManager
import org.somersault.cloud.lib.manager.OperationPathManager
import org.somersault.cloud.lib.plugin.LogPlugin
import org.somersault.cloud.lib.plugin.SlowMethodPlugin

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/6/18 16:02
 * 描    述：
 * 修订历史：
 * ================================================
 */
object SomersaultCloud{


    private val performanceList : ArrayList<IFunctionPlugin> = ArrayList()

    private val uiList : ArrayList<IFunctionPlugin> = ArrayList()

    private val AppInfoList : ArrayList<IFunctionPlugin> = ArrayList()

    /**
     * 初始化筋斗云
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/18 16:03
     */
    fun init(app: Application){
        //解除系统隐藏api调用限制
        Reflection.unseal(app)
        ActivityManager.instance.register(app)
        ApplicationUtils.getInstance().application = app
        registerPlugin()
        //初始化功能悬浮窗管理器
        FloatViewManager.instance.init(app)
        OperationPathManager.instance.addOperation(Operation("Application"))
    }

    private fun registerPlugin(){
        //注册性能检测插件
        performanceList.add(ActivityInspectionPlugin())
        performanceList.add(SlowMethodPlugin())
        performanceList.add(LogPlugin())
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
    * 添加Log Tag信息的提示
     * 用于给业务层提前配置一些TAG信息
    * 作者: ZhouZhengyi
    * 创建时间: 2022/5/27 8:06
    */
    fun addLogTagTipConfig(map:Map<String,String>){
        LogcatManager.mLogTagTip = map
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