package org.somersault.cloud.lib.plugin.inspection

import org.somersault.cloud.lib.R
import org.somersault.cloud.lib.interf.IFunctionPlugin

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/6/26 7:23
 * 描    述：Activity检查插件
 * 包括功能：
 *  1.Activity页面名称显示
 *  2.Fragment名称显示
 *  3.启动Activity的总时长以及详细数据
 *  4.切换Fragment的总时长以及详细数据
 * 修订历史：
 * ================================================
 */
class ActivityInspectionPlugin : IFunctionPlugin{

    override fun getIconResId(): Int {
        return R.mipmap.sc_ic_activity_inspection
    }

    override fun getTitle(): String {
        return "页面检查"
    }

    override fun onClick() {
        TODO("Not yet implemented")
    }
}