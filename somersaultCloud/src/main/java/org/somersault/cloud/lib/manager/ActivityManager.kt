package org.somersault.cloud.lib.manager

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.widget.FrameLayout
import org.somersault.cloud.lib.been.Operation
import org.somersault.cloud.lib.ui.Cloud
import java.lang.Exception
import java.util.*

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/6/17 21:36
 * 描    述：用于管理所有Activity实例
 * 修订历史：
 * ================================================
 */
class ActivityManager private constructor(): Application.ActivityLifecycleCallbacks {

    companion object{
        val instance = Holder.holder
    }

    private object Holder{
        val holder = ActivityManager()
    }

    /**
     * 存放所有Activity的栈
     * **/
    private val mActivityStack = Stack<Activity>()

    private var startedActivityCount : Int = 0

    private var createdActivityCount : Int = 0

    /**
     * 注册
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/17 22:42
     */
    fun register(app:Application){
        app.registerActivityLifecycleCallbacks(this)
    }

    /**
     * 反注册
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/17 22:43
     */
    fun unregister(app:Application){
        app.unregisterActivityLifecycleCallbacks(this)
    }


    /**
     * 查询Activity在栈中的位置
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/18 10:47
     */
    fun searchActivity(activity: Activity):Int{
        return mActivityStack.search(activity)
    }

    /**
     * 获取当前栈顶Activity,不会移除
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/18 10:51
     */
    fun getTopActivity(): Activity? {
        if(mActivityStack.empty()){
            return null
        }
        return mActivityStack.peek()
    }

    /**
     * 返回当前栈中Activity的数量
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/18 10:53
     */
    fun getStackSize():Int{
        if(!mActivityStack.empty()){
            return mActivityStack.size
        }
        return 0
    }


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        createdActivityCount++
        mActivityStack.push(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        startedActivityCount++
        Cloud.instance.attach(activity)
        FloatViewManager.instance.onActivityStart(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        OperationPathManager.instance.addOperation(Operation(activity.javaClass.simpleName))
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
        startedActivityCount--
        Cloud.instance.detach(activity)
        //先stop再detache 这个顺序不能变
        FloatViewManager.instance.onActivityStop(activity)
        FloatViewManager.instance.detachActivityFloatViews(activity)
        if(startedActivityCount ==0 && !activity.isChangingConfigurations && !activity.isFinishing){
            //用户按home键切换到后台，startedActivityCount的数量一定为0
            //因为切换横竖屏也会走onStop方法，所以这里要判断是否是切换横竖屏
            //因为结束一个Activity也要走onStop方法，所以这里要判断是否是结束Activity
            ActivityCostManager.instance.onBackground()
            OperationPathManager.instance.addOperation(Operation("onBackground"))
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        createdActivityCount--
        mActivityStack.remove(activity)
        if(createdActivityCount == 0 && !activity.isChangingConfigurations){
            //退出App时createdActivityCount一定为0
            //切换横竖屏也会走onDestory方法，所以这里要做判断
            OperationPathManager.instance.addOperation(Operation("exit App"))
        }
    }

    fun getRootView(activity : Activity): FrameLayout?{
        if(activity == null){
            return null
        }
        try{
            return activity.window.decorView.findViewById(android.R.id.content)
        }catch (e : Exception){
            e.printStackTrace()
        }
        return null
    }
}