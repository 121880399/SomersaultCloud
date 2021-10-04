package org.zzy.slow_method

import com.android.build.gradle.AppExtension
import com.ss.android.ugc.bytex.common.BaseContext
import org.gradle.api.Project

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/10/3 10:15
 * 描    述：
 * 修订历史：
 * ================================================
 */
class SlowMethodContext(project: Project?, android: AppExtension?, extension: SlowMethodExtension?) :
    BaseContext<SlowMethodExtension>(project, android, extension) {
}