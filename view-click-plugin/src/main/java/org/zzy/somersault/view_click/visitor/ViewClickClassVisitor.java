package org.zzy.somersault.view_click.visitor;

import com.ss.android.ugc.bytex.common.visitor.BaseClassVisitor;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.zzy.somersault.view_click.AsmUtils;
import org.zzy.somersault.view_click.HookConfig;
import org.zzy.somersault.view_click.MethodBean;
import org.zzy.somersault.view_click.ViewClickExtension;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/7/12 8:11
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ViewClickClassVisitor extends BaseClassVisitor {

    private ViewClickExtension extension;

    private String[] mInterfaces;

    /**
     * 父类名称
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/17 15:05
     */
    private String superClass;

    private HashSet<String> visitedFragMethods = new HashSet<>();// 无需判空

    private String className;

    public ViewClickClassVisitor(ViewClickExtension extension){
        this.extension = extension;
    }


    /**
     * 该方法是当扫描类时第一个拜访的方法，主要用于类声明使用
     * @param version 表示类版本：51，表示 “.class” 文件的版本是 JDK 1.7
     * @param access 类的修饰符：修饰符在 ASM 中是以 “ACC_” 开头的常量进行定义。
     *                          可以作用到类级别上的修饰符有：ACC_PUBLIC（public）、ACC_PRIVATE（private）、ACC_PROTECTED（protected）、
     *                          ACC_FINAL（final）、ACC_SUPER（extends）、ACC_INTERFACE（接口）、ACC_ABSTRACT（抽象类）、
     *                          ACC_ANNOTATION（注解类型）、ACC_ENUM（枚举类型）、ACC_DEPRECATED（标记了@Deprecated注解的类）、ACC_SYNTHETIC
     * @param name 类的名称：通常我们的类完整类名使用 “org.test.mypackage.MyClass” 来表示，但是到了字节码中会以路径形式表示它们 “org/test/mypackage/MyClass” 。
     *                      值得注意的是虽然是路径表示法但是不需要写明类的 “.class” 扩展名。
     * @param signature 表示泛型信息，如果类并未定义任何泛型该参数为空
     * @param superName 表示所继承的父类：由于 Java 的类是单根结构，即所有类都继承自 java.lang.Object。 因此可以简单的理解为任何类都会具有一个父类。
     *                  虽然在编写 Java 程序时我们没有去写 extends 关键字去明确继承的父类，但是 JDK在编译时 总会为我们加上 “ extends Object”。
     * @param interfaces 表示类实现的接口，在 Java 中类是可以实现多个不同的接口因此此处是一个数组。
     */
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        mInterfaces = interfaces;
        superClass = superName;
        className = name;
    }

    /**
     * 该方法是当扫描器扫描到类的方法时进行调用
     * @param access 表示方法的修饰符
     * @param name 表示方法名，在 ASM 中 “visitMethod” 方法会处理（构造方法、静态代码块、私有方法、受保护的方法、共有方法、native类型方法）。
     *                  在这些范畴中构造方法的方法名为 “<init>”，静态代码块的方法名为 “<clinit>”。
     * @param desc 表示方法签名，方法签名的格式如下：“(参数列表)返回值类型”
     * @param signature 凡是具有泛型信息的方法，该参数都会有值。并且该值的内容信息基本等于第三个参数的拷贝，只不过不同的是泛型参数被特殊标记出来
     * @param exceptions 用来表示将会抛出的异常，如果方法不会抛出异常，则该参数为空
     * @return
     */
    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        mv = new ViewClickMethodVisitor(mv,access,name,descriptor,superClass,visitedFragMethods,className,mInterfaces);
        return mv;
    }

    /**
     * 该方法是当扫描器完成类扫描时才会调用，如果想在类中追加某些方法，可以在该方法中实现。
     */
    @Override
    public void visitEnd() {
        super.visitEnd();
        //判断父类是否是Fragment
        if (AsmUtils.isInstanceOfFragment(superClass)) {
            MethodVisitor mv;
            // 添加剩下的方法，确保super.onHiddenChanged(hidden);等先被调用
            Iterator<Map.Entry<String, MethodBean>> iterator = HookConfig.FRAGMENT_METHODS.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, MethodBean> entry = iterator.next();
                String key = entry.getKey();
                MethodBean methodCell = entry.getValue();
                if (visitedFragMethods.contains(key)) {
                    continue;
                }
                mv = this.visitMethod(Opcodes.ACC_PUBLIC, methodCell.name, methodCell.desc, null, null);
                //开始访问方法
                mv.visitCode();
                // call super
                visitMethodWithLoadedParams(mv, Opcodes.INVOKESPECIAL, superClass, methodCell.name, methodCell.desc, methodCell.paramsStart, methodCell.paramsCount, methodCell.opcodes);
                // call injected method
                visitMethodWithLoadedParams(mv, Opcodes.INVOKESTATIC, HookConfig.HOOK_CLASS, methodCell.agentName, methodCell.agentDesc, methodCell.paramsStart, methodCell.paramsCount, methodCell.opcodes);
                //访问一个字节码指令，比如 IADD、ISUB、F2L、LSHR 等；
                mv.visitInsn(Opcodes.RETURN);
                //定义执行栈帧的局部变量表和操作数栈的大小
                mv.visitMaxs(methodCell.paramsCount, methodCell.paramsCount);
                //方法访问结束
                mv.visitEnd();
                //访问字段上的注解
                mv.visitAnnotation("Lcom/sensorsdata/analytics/android/sdk/SensorsDataInstrumented;", false);
            }
        }

//        for (cell in methodCells) {
//            transformHelper.sensorsAnalyticsHookConfig."${cell.agentName}"(classVisitor, cell)
//        }
//        if (Logger.debug) {
//            Logger.info("结束扫描类：${mClassName}\n")
//        }
    }

    private static void visitMethodWithLoadedParams(MethodVisitor methodVisitor, int opcode, String owner, String methodName,
                                                    String methodDesc, int start, int count, List<Integer> paramOpcodes) {
        for (int i = start; i < start + count; i++) {
            methodVisitor.visitVarInsn(paramOpcodes.get(i - start), i);
        }
        methodVisitor.visitMethodInsn(opcode, owner, methodName, methodDesc, false);
    }
}
