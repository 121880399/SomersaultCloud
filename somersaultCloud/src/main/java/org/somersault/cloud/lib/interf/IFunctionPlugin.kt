package org.somersault.cloud.lib.interf

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/6/20 7:51
 * 描    述：
 * 修订历史：
 * ================================================
 */
interface IFunctionPlugin {
    /**
    * 得到icon图标资源id
    * 作者: ZhouZhengyi
    * 创建时间: 2021/6/20 8:11
    */
    fun getIconResId():Int

    /**
    * 得到该功能名称
    * 作者: ZhouZhengyi
    * 创建时间: 2021/6/20 8:12
    */
    fun getTitle():String

    /**
    * plugin 被点击时调用
    * 作者: ZhouZhengyi
    * 创建时间: 2021/6/20 8:13
    */
    fun onClick()
}