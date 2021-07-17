package org.zzy.somersault.view_click;

import com.android.build.gradle.AppExtension;
import com.ss.android.ugc.bytex.common.CommonPlugin;
import com.ss.android.ugc.bytex.common.visitor.ClassVisitorChain;

import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;
import org.zzy.somersault.view_click.visitor.ViewClickClassVisitor;

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/7/12 8:09
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ViewClickPlugin extends CommonPlugin<ViewClickExtension,ViewClickContext> {

    @Override
    protected ViewClickContext getContext(Project project, AppExtension android, ViewClickExtension extension) {
        return new ViewClickContext(project,android,extension);
    }

    @Override
    public boolean transform(@NotNull String relativePath, @NotNull ClassVisitorChain chain) {
        chain.connect(new ViewClickClassVisitor(extension));
        return super.transform(relativePath, chain);
    }
}
