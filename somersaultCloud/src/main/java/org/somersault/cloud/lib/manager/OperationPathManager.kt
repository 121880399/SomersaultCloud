package org.somersault.cloud.lib.manager

import org.somersault.cloud.lib.been.Operation
import java.lang.StringBuilder
import java.util.*

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/7/10 8:20
 * 描    述：用户操作路径管理器，只做内存级别的缓存，不做持久化
 * 存储
 * 修订历史：v1.0 2021/7/10
 * 1.记录打开的Activity/Fragment
 * 2.记录用户的点击事件
 * 3.记录前后台切换
 * ================================================
 */
class OperationPathManager private constructor(){

    companion object{
        val instance = Holder.holder
    }

    private object Holder{
        val holder = OperationPathManager()
    }

    /**
     * 存放所有的操作
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/11 8:14
     */
    private val userOperations : LinkedList<Operation>  = LinkedList()


    fun addOperation(operation:Operation){
        userOperations?.add(operation)
    }

    fun toText():String{
        val strBuild = StringBuilder()
        for(index in 0 until userOperations.size){
            strBuild.append(userOperations[index])
            if (index != (userOperations.size-1)) {
                strBuild.append("->")
            }
        }
        return strBuild.toString()
    }

}