package org.somersault.cloud.lib.manager

import android.app.Activity
import android.view.View
import android.view.ViewGroup

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/7/4 10:09
 * 描    述：Ui层级管理器
 * 修订历史：
 * ================================================
 */
class UiHierarchyManager private constructor(){

    companion object{
        val instance = Holder.holder
    }

    private object Holder{
        val holder = UiHierarchyManager()
    }


    /**
    * 得到当前Activity XML最大层级
    * 作者: ZhouZhengyi
    * 创建时间: 2021/7/4 18:33
    */
    fun getMaxHierarchy(activity:Activity):Int{
        val currentXmlRootView = ActivityManager.instance.getRootView(activity)?.getChildAt(0)
        return getDeep(currentXmlRootView!!,1)
    }

    private fun getDeep(view:View,curr:Int):Int{
        if(view is ViewGroup){
            val childCount = view.childCount
            if(childCount > 0 ){
                var max = curr +1
                for(index in 0 until childCount){
                    var height = getDeep(view.getChildAt(index),curr+1)
                    max = max.coerceAtLeast(height)
                }
                return max
            }else{
                return curr
            }
        }else{
            return curr
        }
    }

}