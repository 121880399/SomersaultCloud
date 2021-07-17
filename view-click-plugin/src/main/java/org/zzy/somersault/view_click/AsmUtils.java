package org.zzy.somersault.view_click;

import org.objectweb.asm.Opcodes;

import java.util.HashSet;

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/7/13 11:20
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class AsmUtils implements Opcodes {

    private static final HashSet<String> targetFragmentClass = new HashSet();
    private static final HashSet<String> targetMenuMethodDesc = new HashSet();


    static {
        /**
         * Menu
         */
        targetMenuMethodDesc.add("onContextItemSelected(Landroid/view/MenuItem;)Z");
        targetMenuMethodDesc.add("onOptionsItemSelected(Landroid/view/MenuItem;)Z");

        /**
         * For Android App Fragment
         */
        targetFragmentClass.add("android/app/Fragment");
        targetFragmentClass.add("android/app/ListFragment");
        targetFragmentClass.add("android/app/DialogFragment");

        /**
         * For Support V4 Fragment
         */
        targetFragmentClass.add("android/support/v4/app/Fragment");
        targetFragmentClass.add("android/support/v4/app/ListFragment");
        targetFragmentClass.add("android/support/v4/app/DialogFragment");

        /**
         * For AndroidX Fragment
         */
        targetFragmentClass.add("androidx/fragment/app/Fragment");
        targetFragmentClass.add("androidx/fragment/app/ListFragment");
        targetFragmentClass.add("androidx/fragment/app/DialogFragment");


    }

    /**
     * ACC_SYNTHETIC 标志值为0x1000，表示该类不是由用户编写的
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/13 11:23
     */
    public static boolean isSynthetic(int access) {
        return (access & ACC_SYNTHETIC) != 0;
    }

    /**
     * ACC_PRIVATE 标志值为0x0002，表示是否private
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/13 11:23
     */
    public static boolean isPrivate(int access) {
        return (access & ACC_PRIVATE) != 0;
    }

    /**
     * ACC_PUBLIC 标志值为0x0001，表示是否为public类型
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/13 11:23
     */
    public static boolean isPublic(int access) {
        return (access & ACC_PUBLIC) != 0;
    }

    /**
     * ACC_STATIC 标志值为0x0008，表示是否为static
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/13 11:26
     */
    public static boolean isStatic(int access) {
        return (access & ACC_STATIC) != 0;
    }


    /**
     * ACC_PROTECTED 标志值为0x0004，表示是否为protected
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/15 9:14
     */
    public static boolean isProtected(int access) {
        return (access & Opcodes.ACC_PROTECTED) != 0;
    }

    /**
     * 是否是Fragment实例
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/16 8:43
     */
    public static boolean isInstanceOfFragment(String superName) {
        return targetFragmentClass.contains(superName);
    }

    public static boolean isTargetMenuMethodDesc(String nameDesc) {
        return targetMenuMethodDesc.contains(nameDesc);
    }

    /**
     * 获取 LOAD 或 STORE 的相反指令，例如 ILOAD => ISTORE，ASTORE => ALOAD
     *
     * @param LOAD 或 STORE 指令
     * @return 返回相对应的指令
     */
    public static int convertOpcodes(int code) {
        int result = code;
        switch (code) {
            case Opcodes.ILOAD:
                result = Opcodes.ISTORE;
                break;
            case Opcodes.ALOAD:
                result = Opcodes.ASTORE;
                break;
            case Opcodes.LLOAD:
                result = Opcodes.LSTORE;
                break;
            case Opcodes.FLOAD:
                result = Opcodes.FSTORE;
                break;
            case Opcodes.DLOAD:
                result = Opcodes.DSTORE;
                break;
            case Opcodes.ISTORE:
                result = Opcodes.ILOAD;
                break;
            case Opcodes.ASTORE:
                result = Opcodes.ALOAD;
                break;
            case Opcodes.LSTORE:
                result = Opcodes.LLOAD;
                break;
            case Opcodes.FSTORE:
                result = Opcodes.FLOAD;
                break;
            case Opcodes.DSTORE:
                result = Opcodes.DLOAD;
                break;
        }
        return result;
    }
}
