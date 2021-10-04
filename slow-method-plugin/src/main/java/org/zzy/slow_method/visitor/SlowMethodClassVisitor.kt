package org.zzy.slow_method.visitor

import com.ss.android.ugc.bytex.common.visitor.BaseClassVisitor
import org.objectweb.asm.ClassVisitor
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

    var mExtension : SlowMethodExtension

    constructor(extension: SlowMethodExtension){
        this.mExtension = extension
    }
}