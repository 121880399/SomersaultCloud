package org.somersault.cloud.lib.aop.slow_method

import android.os.SystemClock
import android.util.Log
import java.lang.Exception
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/11/30 11:02
 * 描    述：
 * 修订历史：
 * ================================================
 */
class SlowMethodHook {
    companion object {
        private const val TAG = "SLOW_METHOD"
    }


    private val methodStacks : MutableList<ConcurrentHashMap<String,MethodInvokeNode>> by lazy {
        Collections.synchronizedList(mutableListOf<ConcurrentHashMap<String,MethodInvokeNode>>())
    }

    /**
     * 每个方法开始时创建队列
     * 构建方法对应的Node类
     * @param className 类名
     * @param methodName 方法名
     * @param desc 方法描述
     * @param currentLevel 当前方法层级
     * @param thresholdTime 耗时阈值
     * @param totalLevel 需要输出的方法总层级
     * 作者:ZhouZhengyi
     * 创建时间: 2021/11/30 11:51
     */
    fun methodCostStart(className:String,methodName: String,currentLevel:Int,totalLevel:Int) {
        try {
            createMethodStack(totalLevel)
            val methodNode = MethodInvokeNode()
            methodNode.className = className
            methodNode.methodName = methodName
            methodNode.startTime = System.currentTimeMillis()
            methodNode.currentThreadName = Thread.currentThread().name
            methodNode.level = currentLevel
            methodStacks[currentLevel][String.format("%s_%s", className, methodName)] = methodNode
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun createMethodStack(totalLevel: Int){
        if(methodStacks.size == totalLevel){
            return
        }
        methodStacks.clear()
        for(i in 0 until totalLevel){
            methodStacks.add(i, ConcurrentHashMap())
        }
    }

    /**
     * 每个方法结束时判断该方法是否超过阈值
     * 如果超过阈值则为慢方法，需要打印出来
     * @param methodName 方法名称
     * @param thresholdTime 超时阈值
     * 作者:ZhouZhengyi
     * 创建时间: 2021/11/30 11:53
     */
    fun methodCostEnd(className:String,methodName: String, currentLevel: Int,thresholdTime: Int) {
        try {
            val methodNode = methodStacks[currentLevel][String.format("%s_%s",className,methodName)]
            if(methodNode!=null){
                methodNode.endTime = System.currentTimeMillis()
                methodNode.costTime = (methodNode.endTime - methodNode.startTime).toInt()
                bindNode(currentLevel,methodNode,thresholdTime)
            }

            if(currentLevel == 0){

            }
        }catch (e :Exception){
            e.printStackTrace()
        }
    }

    private fun bindNode(currentLevel: Int,methodNode : MethodInvokeNode,thresholdTime: Int){
        if(methodNode == null){
            return
        }


    }
}