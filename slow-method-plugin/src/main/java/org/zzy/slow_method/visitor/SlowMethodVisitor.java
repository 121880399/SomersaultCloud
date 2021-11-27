package org.zzy.slow_method.visitor;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.zzy.slow_method.SlowMethodExtension;

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/11/26 9:34
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class SlowMethodVisitor extends MethodVisitor {

    private SlowMethodExtension methodExtension;

    public SlowMethodVisitor(MethodVisitor mv, SlowMethodExtension methodExtension) {
        super(Opcodes.ASM5,mv);
        this.methodExtension = methodExtension;
    }

    /**
     * 开始访问方法
     * 在方法入口处插入方法开始的时间
     * 作者:ZhouZhengyi
     * 创建时间: 2021/11/27 11:21
     */
    @Override
    public void visitCode() {
        super.visitCode();
    }
}
