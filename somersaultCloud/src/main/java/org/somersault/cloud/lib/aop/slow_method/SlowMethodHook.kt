package org.somersault.cloud.lib.aop.slow_method

import android.os.SystemClock
import android.util.Log
import org.somersault.cloud.lib.utils.Logger
import java.lang.Exception
import java.lang.StringBuilder
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
        private const val SPACE_0 = "********"
        private const val SPACE_1 = "*************"
        private const val SPACE_2 = "*****************"
        private const val SPACE_3 = "*********************"
        private const val SPACE_4 = "*************************"
        private const val SPACE_5 = "*****************************"
        private const val SPACE_6 = "*********************************"
        private const val SPACE_7 = "*************************************"
    }


    private val methodStacks: MutableList<ConcurrentHashMap<String, MethodInvokeNode>> by lazy {
        Collections.synchronizedList(mutableListOf<ConcurrentHashMap<String, MethodInvokeNode>>())
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
    fun methodCostStart(className: String, methodName: String, currentLevel: Int, totalLevel: Int) {
        try {
            createMethodStack(totalLevel)
            val methodNode = MethodInvokeNode()
            methodNode.className = className
            methodNode.methodName = methodName
            methodNode.startTime = System.currentTimeMillis()
            methodNode.currentThreadName = Thread.currentThread().name
            methodNode.level = currentLevel
            methodStacks[currentLevel][String.format("%s_%s", className, methodName)] = methodNode
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createMethodStack(totalLevel: Int) {
        if (methodStacks.size == totalLevel) {
            return
        }
        methodStacks.clear()
        for (i in 0 until totalLevel) {
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
    fun methodCostEnd(
        className: String,
        methodName: String,
        currentLevel: Int,
        thresholdTime: Int
    ) {
        try {
            val methodNode =
                methodStacks[currentLevel][String.format("%s_%s", className, methodName)]
            if (methodNode != null) {
                methodNode.endTime = System.currentTimeMillis()
                methodNode.costTime = (methodNode.endTime - methodNode.startTime).toInt()
                //如果第0级方法耗时就小于阈值，那么不用在bind节点，也不需要打印
                if (currentLevel == 0 && methodNode.costTime < thresholdTime) {
                    return
                }
                bindNode(currentLevel, methodNode, thresholdTime)
            }
            // TODO: 2022/1/9 这里需要确认是否只在第0级打印，因为有可能第1级的还没bind上，这时候打印出来的就只有第0级的方法
            if (currentLevel == 0) {

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 父子节点建立联系
     * 作者:ZhouZhengyi
     * 创建时间: 2021/12/10 9:06
     */
    private fun bindNode(currentLevel: Int, methodNode: MethodInvokeNode, thresholdTime: Int) {
        if (methodNode == null) {
            return
        }
        if (currentLevel >= 1) {
            val parentMethodNode = methodStacks[currentLevel - 1][getParentMethodNode(
                methodNode.className,
                methodNode.methodName
            )]
            if (parentMethodNode != null) {
                methodNode.parentNode = parentMethodNode
                parentMethodNode.childrenNode.add(methodNode)
            }
        }
    }

    /**
     * 得到当前方法的父方法
     * 作者:ZhouZhengyi
     * 创建时间: 2021/12/10 9:07
     */
    private fun getParentMethodNode(className: String?, methodName: String?): String {
        //得到当前线程的调用栈数组
        val stackTraceElements = Thread.currentThread().stackTrace
        var index = 0
        //遍历调用栈，找到当前方法的调用栈，因为每个方法会生成一个栈帧，栈帧会压入栈
        for (i in stackTraceElements.indices) {
            val stackTraceElement = stackTraceElements[i]
            if (className == stackTraceElement.className && methodName == stackTraceElement.methodName) {
                index = i
                break
            }
        }
        val parentStackTraceElement = stackTraceElements[index + 1]
        return String.format(
            "%s_%s",
            parentStackTraceElement.className,
            parentStackTraceElement.methodName
        )
    }

    /**
     * 打印方法调用栈
     * 作者:ZhouZhengyi
     * 创建时间: 2022/1/9 10:52
     */
    fun printStack(methodNode: MethodInvokeNode) {
        val logBuilder = StringBuilder()
        logBuilder.append("========SomersaultCloud慢方法========").append("\n")
        logBuilder.append(String.format("%s    %s    %s", "level", "time", "method")).append("\n")
        logBuilder.append(
            String.format(
                "%s%s%s%s%s",
                methodNode.level,
                SPACE_0,
                methodNode.costTime.toString() + "ms",
                getSpaceString(methodNode.level),
                methodNode.className + "&" + methodNode.methodName
            )
        ).append("\n")
        travelStack(logBuilder, methodNode.childrenNode)
        Logger.i(TAG, logBuilder.toString())
    }

    /**
     * 遍历打印方法调用栈
     * 作者:ZhouZhengyi
     * 创建时间: 2022/1/9 10:52
     */
    fun travelStack(logBuilder: StringBuilder, methodNodes: List<MethodInvokeNode>?) {
        if (methodNodes == null) {
            return
        }
        for (node in methodNodes) {
            logBuilder.append(
                String.format(
                    "%s%s%s%s%s",
                    node.level,
                    SPACE_0,
                    node.costTime.toString() + "ms",
                    getSpaceString(node.level),
                    node.className + "&" + node.methodName
                )
            ).append("\n")
            travelStack(logBuilder, node.childrenNode)
        }
    }

    /**
     * 返回占位
     * 作者:ZhouZhengyi
     * 创建时间: 2022/1/9 11:00
     */
    fun getSpaceString(level: Int): String {
        return when (level) {
            0 -> SPACE_0
            1 -> SPACE_1
            2 -> SPACE_2
            3 -> SPACE_3
            4 -> SPACE_4
            5 -> SPACE_5
            6 -> SPACE_6
            7 -> SPACE_7
            else -> SPACE_0
        }
    }

}
