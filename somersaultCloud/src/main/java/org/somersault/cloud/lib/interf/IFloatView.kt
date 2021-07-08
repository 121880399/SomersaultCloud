package org.somersault.cloud.lib.interf

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import org.somersault.cloud.lib.widget.CustomLayoutParams

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/6/28 8:32
 * 描    述：悬浮窗接口
 * 修订历史：
 * ================================================
 */
interface IFloatView {

    /**
     * 可以在view创建之前提前做一些变量初始化
     * 不能进行view的操作
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/28 8:33
     */
    fun onCreate(context:Context)

    /**
     * 创建控件
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/28 8:34
     */
    fun onCreateView(context: Context):View


    /**
     * 此时控件已经添加到rootView,可以进行view的操作
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/28 8:35
     */
    fun onViewCreated(floatView: View)

    /**
     *
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/28 8:35
     */
    fun onResume(activity:Activity)

    /**
     * 当Activity onPause时调用
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/28 8:36
     */
    fun onPause()

    /**
     * 当Activity onStop时调用
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/28 8:36
     */
    fun onStop()

    /**
     * 是否能够拖动
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/28 8:37
     */
    fun canDrag():Boolean

    /**
     * 是否拦截返回按键
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/28 8:38
     */
    fun interceptBackKey():Boolean

    /**
     * interceptBackKey == true时调用
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/28 8:39
     */
    fun onBackPressed():Boolean

    /**
     * 悬浮窗销毁时调用
     * 作者:ZhouZhengyi
     * 创建时间: 2021/6/28 8:40
     */
    fun onDestroy()

    /**
     * 初始化悬浮窗的位置
     * 给不同的悬浮窗指定显示位置和定义特性参数
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/1 8:46
     */
    fun initFloatViewLayoutParams(params : CustomLayoutParams)
}