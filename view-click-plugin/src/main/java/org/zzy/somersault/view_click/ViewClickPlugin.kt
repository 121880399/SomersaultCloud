package org.zzy.somersault.view_click

import com.android.build.gradle.AppExtension
import com.ss.android.ugc.bytex.common.CommonPlugin
import org.gradle.api.Project

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/7/11 17:05
 * 描    述：
 * 修订历史：
 * ================================================
 */
class ViewClickPlugin : CommonPlugin<ViewClickExtension, ViewClickContext>() {


    override fun getContext(project: Project?, android: AppExtension?, extension: ViewClickExtension?): ViewClickContext {
        return ViewClickContext(project,android,extension)
    }
}