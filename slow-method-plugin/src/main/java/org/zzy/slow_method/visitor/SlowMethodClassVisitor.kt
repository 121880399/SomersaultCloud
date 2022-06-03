package org.zzy.slow_method.visitor

import com.ss.android.ugc.bytex.common.visitor.BaseClassVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.zzy.slow_method.SlowMethodExtension

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/10/3 11:39
 * 描    述：
 * 修订历史：
 * ================================================
 */
class SlowMethodClassVisitor : BaseClassVisitor {

    private var mExtension : SlowMethodExtension

    /**
     * 类名称
     * */
    private var mClassName : String? = null

    constructor(extension: SlowMethodExtension){
        this.mExtension = extension
    }

    override fun visit(
        version: Int,
        access: Int,
        className: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, className, signature, superName, interfaces)
        mClassName = className
    }

    override fun visitMethod(
        access: Int,
        methodName: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val visitMethod = super.visitMethod(access, methodName, descriptor, signature, exceptions)
        return SlowMethodVisitor(visitMethod,mExtension)
    }
}