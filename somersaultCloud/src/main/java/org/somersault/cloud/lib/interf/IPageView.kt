package org.somersault.cloud.lib.interf

import android.view.View
import android.view.ViewGroup

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/6/20 7:27
 * 描    述：
 * 修订历史：
 * ================================================
 */
interface IPageView {
    fun onCreateView(container:ViewGroup):View
}