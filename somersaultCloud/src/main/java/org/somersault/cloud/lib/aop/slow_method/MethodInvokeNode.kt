package org.somersault.cloud.lib.aop.slow_method

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/12/7 9:37
 * 描    述：将每一个方法抽象成一个Node
 * 每个方法都有一个父方法，也就是调用它的方法
 * 每个方法中都有可能调用其他的方法，所以这里孩子变量是一个List
 * 修订历史：
 * ================================================
 */
class MethodInvokeNode {
    //父节点
    var parentNode:MethodInvokeNode ?= null
    //在该方法中调用的其他方法，抽象为孩子节点
    var childrenNode:MutableList<MethodInvokeNode> = mutableListOf()
    //方法所在的类
    var className:String ?= null
    //方法名
    var methodName:String ?= null
    //方法执行的线程
    var currentThreadName:String?=null
    //当前方法所在的层级
    var level = 0
    //方法开始执行的时间ms
    var startTime:Long = 0
    //方法执行完毕的时间ms
    var endTime:Long = 0
    //方法执行耗时ms
    var costTime  = 0


}