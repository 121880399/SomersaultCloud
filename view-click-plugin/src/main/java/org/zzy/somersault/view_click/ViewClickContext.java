package org.zzy.somersault.view_click;

import com.android.build.gradle.AppExtension;
import com.ss.android.ugc.bytex.common.BaseContext;

import org.gradle.api.Project;

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/7/11 17:09
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ViewClickContext extends BaseContext<ViewClickExtension> {

    public ViewClickContext(Project project, AppExtension android, ViewClickExtension extension) {
        super(project, android, extension);
    }
}
