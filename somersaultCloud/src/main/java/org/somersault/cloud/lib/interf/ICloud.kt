package org.somersault.cloud.lib.interf

import android.app.Activity
import android.content.Context
import android.widget.FrameLayout

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/6/23 8:51
 * 描    述：
 * 修订历史：
 * ================================================
 */
interface ICloud {

    fun add(context:Context):ICloud

    fun remove():ICloud

    fun attach(activity: Activity):ICloud

    fun attach(container : FrameLayout?):ICloud

    fun detach(activity: Activity):ICloud

    fun detach(container : FrameLayout?):ICloud
}