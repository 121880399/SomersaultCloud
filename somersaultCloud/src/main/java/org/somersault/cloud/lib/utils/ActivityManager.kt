package org.zzy.somersault.cloud.lib.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
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
       mActivityStack.push(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun onActivityResumed(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun onActivityPaused(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun onActivityStopped(activity: Activity) {
        TODO("Not yet implemented")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        TODO("Not yet implemented")
    }

    override fun onActivityDestroyed(activity: Activity) {
        mActivityStack.remove(activity)
    }
}