package org.somersault.cloud.lib.been

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/7/10 8:24
 * 描    述：表示用户每一次的操作
 * 修订历史：
 * ================================================
 */
class Operation {

    /**
    * 本次操作是Activity切换
     * 还是用户点击
    * 作者: ZhouZhengyi
    * 创建时间: 2021/7/10 8:27
    */
    var type : Int = 0

    /**
    * 记录本次操作的名称
    * 作者: ZhouZhengyi
    * 创建时间: 2021/7/10 8:27
    */
    var name : String = ""

}