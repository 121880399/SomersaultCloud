package org.zzy.somersault.view_click.visitor;

import org.apache.http.util.TextUtils;
import org.gradle.internal.impldep.com.google.api.client.util.ArrayMap;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.zzy.somersault.view_click.AsmUtils;
import org.zzy.somersault.view_click.HookConfig;
import org.zzy.somersault.view_click.MethodBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/7/12 9:21
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ViewClickMethodVisitor extends AdviceAdapter {

    private Map<String, MethodBean> mLambdaMethodCells = new ArrayMap<>();

    private HashSet<String> visitedFragMethods;

    /**
     * 方法名+方法描述
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/16 8:48
     */
    private String nameDesc;

    /**
     * 方法描述
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/16 8:48
     */
    private String des;

    private int access;

    private String className;

    /**
     * 所继承的父类
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/16 8:47
     */
    private String superClass;

    private MethodVisitor methodVisitor;

    //访问权限是public并且非静态
    private boolean pubAndNoStaticAccess;

    private boolean protectedAndNotStaticAccess;

    //nameDesc是'onClick(Landroid/view/View;)V'字符串
    private boolean isOnClickMethod = false;
    private int variableID = 0;

    //nameDesc是 onItemClick(Landroid/widget/AdapterView;Landroid/view/View;IJ)V
    private boolean isOnItemClickMethod = false;
    private ArrayList<Integer> localIds;


    private boolean isHasTracked = false;

    private String[] mInterfaces;


    public ViewClickMethodVisitor(MethodVisitor mv, int access, String name, String descriptor, String superClass,
                                  HashSet<String> visitedFragMethods, String className, String[] interfaces) {
        super(Opcodes.ASM5, mv, access, name, descriptor);
        nameDesc = name + descriptor;
        des = descriptor;
        this.access = access;
        methodVisitor = mv;
        this.superClass = superClass;
        this.visitedFragMethods = visitedFragMethods;
        this.className = className;
        this.mInterfaces = interfaces;
    }


    /**
     * 访问一个 invokedynamic 指令，一般是 Lambda 访问时
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/12 9:38
     */
    @Override
    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle,
                                       Object... bootstrapMethodArguments) {
        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
        try {
            String des = (String) bootstrapMethodArguments[0];
            MethodBean bean = HookConfig.LAMBDA_METHODS.get(Type.getReturnType(descriptor).getDescriptor() + name + des);
            if (bean != null) {
                Handle it = (Handle) bootstrapMethodArguments[1];
                mLambdaMethodCells.put(it.getName() + it.getDesc(), bean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 方法访问结束，结束时必须调用
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/12 9:38
     */
    @Override
    public void visitEnd() {
        super.visitEnd();
        if (mLambdaMethodCells.containsKey(nameDesc)) {
            mLambdaMethodCells.remove(nameDesc);
        }
    }


    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
        pubAndNoStaticAccess = AsmUtils.isPublic(access) && !AsmUtils.isStatic(access);
        protectedAndNotStaticAccess = AsmUtils.isProtected(access) && !AsmUtils.isStatic(access);

        if (pubAndNoStaticAccess) {
            if (nameDesc.equals("onClick(Landroid/view/View;)V")) {
                isOnClickMethod = true;
                variableID = newLocal(Type.getObjectType("java/lang/Integer"));
                //用于加载非基础类型的局部变量到操作数栈，这里加载的是view
                methodVisitor.visitVarInsn(ALOAD, 1);
                //用于弹出非基础类型的局部变量，并将它存储在由其索引 i 指定的局部变量中。
                methodVisitor.visitVarInsn(ASTORE, variableID);
            } else if (nameDesc == "onItemClick(Landroid/widget/AdapterView;Landroid/view/View;IJ)V") {
                localIds = new ArrayList<>();
                int first = newLocal(Type.getObjectType("android/widget/AdapterView"));
                //从局部变量表中读取AdapterView加载到操作数栈
                methodVisitor.visitVarInsn(ALOAD, 1);
                //将操作数栈中的AdapterView存放到新创建的局部变量中
                methodVisitor.visitVarInsn(ASTORE, first);
                localIds.add(first);

                int second = newLocal(Type.getObjectType("android/view/View"));
                methodVisitor.visitVarInsn(ALOAD, 2);
                methodVisitor.visitVarInsn(ASTORE, second);
                localIds.add(second);

                int third = newLocal(Type.INT_TYPE);
                methodVisitor.visitVarInsn(ILOAD, 3);
                methodVisitor.visitVarInsn(ISTORE, third);
                localIds.add(third);
            } else if (AsmUtils.isInstanceOfFragment(superClass) && HookConfig.FRAGMENT_METHODS.get(nameDesc) != null) {
                //Fragment生命周期方法
                MethodBean methodBean = HookConfig.FRAGMENT_METHODS.get(nameDesc);
                localIds = new ArrayList<>();
                //这块需要调试一下，测试position是否正确
                Type[] argumentTypes = Type.getArgumentTypes(des);
                for (int i = 1; i < methodBean.paramsCount; i++) {
                    int localId = newLocal(argumentTypes[i - 1]);
                    methodVisitor.visitVarInsn(methodBean.opcodes.get(i), i);
                    methodVisitor.visitVarInsn(AsmUtils.convertOpcodes(methodBean.opcodes.get(i)), localId);
                    localIds.add(localId);
                }
            } else if (nameDesc == "onCheckedChanged(Landroid/widget/RadioGroup;I)V") {
                localIds = new ArrayList<>();
                int firstLocalId = newLocal(Type.getObjectType("android/widget/RadioGroup"));
                methodVisitor.visitVarInsn(ALOAD, 1);
                methodVisitor.visitVarInsn(ASTORE, firstLocalId);
                localIds.add(firstLocalId);
                int secondLocalId = newLocal(Type.INT_TYPE);
                methodVisitor.visitVarInsn(ILOAD, 2);
                methodVisitor.visitVarInsn(ISTORE, secondLocalId);
                localIds.add(secondLocalId);
            } else if (nameDesc == "onCheckedChanged(Landroid/widget/CompoundButton;Z)V") {
                localIds = new ArrayList<>();
                int firstLocalId = newLocal(Type.getObjectType("android/widget/CompoundButton"));
                methodVisitor.visitVarInsn(ALOAD, 1);
                methodVisitor.visitVarInsn(ASTORE, firstLocalId);
                localIds.add(firstLocalId);
            } else if (nameDesc == "onClick(Landroid/content/DialogInterface;I)V") {
                localIds = new ArrayList<>();
                int firstLocalId = newLocal(Type.getObjectType("android/content/DialogInterface"));
                methodVisitor.visitVarInsn(ALOAD, 1);
                methodVisitor.visitVarInsn(ASTORE, firstLocalId);
                localIds.add(firstLocalId);
                int secondLocalId = newLocal(Type.INT_TYPE);
                methodVisitor.visitVarInsn(ILOAD, 2);
                methodVisitor.visitVarInsn(ISTORE, secondLocalId);
                localIds.add(secondLocalId);
            } else if (AsmUtils.isTargetMenuMethodDesc(nameDesc)) {
                localIds = new ArrayList<>();
                int firstLocalId = newLocal(Type.getObjectType("java/lang/Object"));
                methodVisitor.visitVarInsn(ALOAD, 0);
                methodVisitor.visitVarInsn(ASTORE, firstLocalId);
                localIds.add(firstLocalId);
                int secondLocalId = newLocal(Type.getObjectType("android/view/MenuItem"));
                methodVisitor.visitVarInsn(ALOAD, 1);
                methodVisitor.visitVarInsn(ASTORE, secondLocalId);
                localIds.add(secondLocalId);
            } else if (nameDesc == "onMenuItemClick(Landroid/view/MenuItem;)Z") {
                localIds = new ArrayList<>();
                int firstLocalId = newLocal(Type.getObjectType("android/view/MenuItem"));
                methodVisitor.visitVarInsn(ALOAD, 1);
                methodVisitor.visitVarInsn(ASTORE, firstLocalId);
                localIds.add(firstLocalId);
            } else if (nameDesc == "onGroupClick(Landroid/widget/ExpandableListView;Landroid/view/View;IJ)Z") {
                localIds = new ArrayList<>();
                int firstLocalId = newLocal(Type.getObjectType("android/widget/ExpandableListView"));
                methodVisitor.visitVarInsn(ALOAD, 1);
                methodVisitor.visitVarInsn(ASTORE, firstLocalId);
                localIds.add(firstLocalId);

                int secondLocalId = newLocal(Type.getObjectType("android/view/View"));
                methodVisitor.visitVarInsn(ALOAD, 2);
                methodVisitor.visitVarInsn(ASTORE, secondLocalId);
                localIds.add(secondLocalId);

                int thirdLocalId = newLocal(Type.INT_TYPE);
                methodVisitor.visitVarInsn(ILOAD, 3);
                methodVisitor.visitVarInsn(ISTORE, thirdLocalId);
                localIds.add(thirdLocalId);
            } else if (nameDesc == "onChildClick(Landroid/widget/ExpandableListView;Landroid/view/View;IIJ)Z") {
                localIds = new ArrayList<>();
                int firstLocalId = newLocal(Type.getObjectType("android/widget/ExpandableListView"));
                methodVisitor.visitVarInsn(ALOAD, 1);
                methodVisitor.visitVarInsn(ASTORE, firstLocalId);
                localIds.add(firstLocalId);

                int secondLocalId = newLocal(Type.getObjectType("android/view/View"));
                methodVisitor.visitVarInsn(ALOAD, 2);
                methodVisitor.visitVarInsn(ASTORE, secondLocalId);
                localIds.add(secondLocalId);

                int thirdLocalId = newLocal(Type.INT_TYPE);
                methodVisitor.visitVarInsn(ILOAD, 3);
                methodVisitor.visitVarInsn(ISTORE, thirdLocalId);
                localIds.add(thirdLocalId);

                int fourthLocalId = newLocal(Type.INT_TYPE);
                methodVisitor.visitVarInsn(ILOAD, 4);
                methodVisitor.visitVarInsn(ISTORE, fourthLocalId);
                localIds.add(fourthLocalId);
            } else if (nameDesc == "onItemSelected(Landroid/widget/AdapterView;Landroid/view/View;IJ)V"
                    || nameDesc == "onListItemClick(Landroid/widget/ListView;Landroid/view/View;IJ)V") {
                localIds = new ArrayList<>();
                int firstLocalId = newLocal(Type.getObjectType("java/lang/Object"));
                methodVisitor.visitVarInsn(ALOAD, 1);
                methodVisitor.visitVarInsn(ASTORE, firstLocalId);
                localIds.add(firstLocalId);

                int secondLocalId = newLocal(Type.getObjectType("android/view/View"));
                methodVisitor.visitVarInsn(ALOAD, 2);
                methodVisitor.visitVarInsn(ASTORE, secondLocalId);
                localIds.add(secondLocalId);

                int thirdLocalId = newLocal(Type.INT_TYPE);
                methodVisitor.visitVarInsn(ILOAD, 3);
                methodVisitor.visitVarInsn(ISTORE, thirdLocalId);
                localIds.add(thirdLocalId);
            } else if (nameDesc == "onStopTrackingTouch(Landroid/widget/SeekBar;)V") {
                localIds = new ArrayList<>();
                int firstLocalId = newLocal(Type.getObjectType("android/widget/SeekBar"));
                methodVisitor.visitVarInsn(ALOAD, 1);
                methodVisitor.visitVarInsn(ASTORE, firstLocalId);
                localIds.add(firstLocalId);
            }
        } else if (protectedAndNotStaticAccess) {
            if (nameDesc == "onListItemClick(Landroid/widget/ListView;Landroid/view/View;IJ)V") {
                localIds = new ArrayList<>();
                int firstLocalId = newLocal(Type.getObjectType("java/lang/Object"));
                methodVisitor.visitVarInsn(ALOAD, 1);
                methodVisitor.visitVarInsn(ASTORE, firstLocalId);
                localIds.add(firstLocalId);

                int secondLocalId = newLocal(Type.getObjectType("android/view/View"));
                methodVisitor.visitVarInsn(ALOAD, 2);
                methodVisitor.visitVarInsn(ASTORE, secondLocalId);
                localIds.add(secondLocalId);

                int thirdLocalId = newLocal(Type.INT_TYPE);
                methodVisitor.visitVarInsn(ILOAD, 3);
                methodVisitor.visitVarInsn(ISTORE, thirdLocalId);
                localIds.add(thirdLocalId);
            }
        }

        // Lambda 参数优化部分，对现有参数进行复制

        MethodBean methodBean = mLambdaMethodCells.get(nameDesc);
        if (methodBean != null) {
            //判断是否是在采样中，在采样中才会处理或者开关打开也统一处理
            if (HookConfig.SAMPLING_LAMBDA_METHODS.contains(methodBean)) {
                Type[] argumentTypes = Type.getArgumentTypes(methodBean.desc);
                int length = argumentTypes.length;
                Type[] lambdaTypes = Type.getArgumentTypes(des);
                // paramStart 为访问的方法参数的下标，从 0 开始
                int paramStart = lambdaTypes.length - length;
                if (paramStart < 0) {
                    return;
                } else {
                    for (int i = 0; i < length; i++) {
                        if (!lambdaTypes[paramStart + i].getDescriptor().equals(argumentTypes[i].getDescriptor()))
                            return;
                    }
                }
                boolean isStaticMethod = AsmUtils.isStatic(access);
                localIds = new ArrayList<>();

                for (int i = paramStart; i < paramStart + methodBean.paramsCount; i++) {
                    int localId = newLocal(argumentTypes[i - paramStart]);
                    methodVisitor.visitVarInsn(methodBean.opcodes.get(i - paramStart), getVisitPosition(lambdaTypes, i,
                            isStaticMethod));
                    methodVisitor.visitVarInsn(AsmUtils.convertOpcodes(methodBean.opcodes.get(i - paramStart)),
                            localId);
                    localIds.add(localId);
                }
            }
        }
        handleCode();
    }

    /**
     * 调用hooK方法
     * 作者:ZhouZhengyi
     * 创建时间: 2021/7/17 14:53
     */
    void handleCode() {

        /**
         * Fragment
         * 目前支持以下 Fragment 页面浏览事件：
         * android/app/Fragment，android/app/ListFragment， android/app/DialogFragment，
         * android/support/v4/app/Fragment，android/support/v4/app/ListFragment，android/support/v4/app/DialogFragment，
         * androidx/fragment/app/Fragment，androidx/fragment/app/ListFragment，androidx/fragment/app/DialogFragment
         */
        if (AsmUtils.isInstanceOfFragment(superClass)) {
            MethodBean methodBean = HookConfig.FRAGMENT_METHODS.get(nameDesc);
            if (methodBean != null) {
                visitedFragMethods.add(nameDesc);
                methodVisitor.visitVarInsn(ALOAD, 0);
                for (int i = 1; i < methodBean.paramsCount; i++) {
                    methodVisitor.visitVarInsn(methodBean.opcodes.get(i), localIds.get(i - 1));
                }
                //调用方法的指令，比如在一个方法中调用另一个方法；
                methodVisitor.visitMethodInsn(INVOKESTATIC, HookConfig.HOOK_CLASS, methodBean.agentName, methodBean.agentDesc,
                        false);
                isHasTracked = true;
                return;
            }
        }


        /**
         * 在 android.gradle 的 3.2.1 版本中，针对 view 的 setOnClickListener 方法 的 lambda 表达式做特殊处理。
         */

        MethodBean lambdaMethodCell = mLambdaMethodCells.get(nameDesc);
        if (lambdaMethodCell != null) {
            Type[] types = Type.getArgumentTypes(lambdaMethodCell.desc);
            int length = types.length;
            Type[] lambdaTypes = Type.getArgumentTypes(des);
            // paramStart 为访问的方法参数的下标，从 0 开始
            int paramStart = lambdaTypes.length - length;
            if (paramStart < 0) {
                return;
            } else {
                for (int i = 0; i < length; i++) {
                    if (!lambdaTypes[paramStart + i].getArgumentTypes().equals(types[i].getDescriptor())) {
                        return;
                    }
                }
            }
            boolean isStaticMethod = AsmUtils.isStatic(access);
            if (!isStaticMethod) {
                if (lambdaMethodCell.desc == "(Landroid/view/MenuItem;)Z") {
                    methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                    methodVisitor.visitVarInsn(Opcodes.ALOAD, getVisitPosition(lambdaTypes, paramStart, isStaticMethod));
                    methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, HookConfig.HOOK_CLASS, lambdaMethodCell.agentName,
                            "(Ljava/lang/Object;Landroid/view/MenuItem;)V", false);
                    isHasTracked = true;
                    return;
                }
            }
            //如果在采样中，就按照最新的处理流程来操作
            if (HookConfig.SAMPLING_LAMBDA_METHODS.contains(lambdaMethodCell)) {
                for (int i = paramStart; i < paramStart + lambdaMethodCell.paramsCount; i++) {
                    methodVisitor.visitVarInsn(lambdaMethodCell.opcodes.get(i - paramStart), localIds.get(i - paramStart));
                }
            } else {
                for (int i = paramStart; i < paramStart + lambdaMethodCell.paramsCount; i++) {
                    methodVisitor.visitVarInsn(lambdaMethodCell.opcodes.get(i - paramStart), getVisitPosition(lambdaTypes, i,
                            isStaticMethod));
                }
            }
            methodVisitor.visitMethodInsn(Opcodes.INVOKESTATIC, HookConfig.HOOK_CLASS, lambdaMethodCell.agentName,
                    lambdaMethodCell.agentDesc, false);
            isHasTracked = true;
            return;
        }


        if (!pubAndNoStaticAccess) {
            //如果是 protected 那么也需要处理
            if (protectedAndNotStaticAccess) {
                if (nameDesc == "onListItemClick(Landroid/widget/ListView;Landroid/view/View;IJ)V") {
                    methodVisitor.visitVarInsn(ALOAD, localIds.get(0));
                    methodVisitor.visitVarInsn(ALOAD, localIds.get(1));
                    methodVisitor.visitVarInsn(ILOAD, localIds.get(2));
                    methodVisitor.visitMethodInsn(INVOKESTATIC, HookConfig.HOOK_CLASS, "trackListView", "(Landroid/widget" +
                            "/AdapterView;Landroid/view/View;I)V", false);
                    isHasTracked = true;
                    return;
                }
            }
            return;
        }


        /**
         * Menu
         * 目前支持 onContextItemSelected(MenuItem item)、onOptionsItemSelected(MenuItem item)
         */
        if (AsmUtils.isTargetMenuMethodDesc(nameDesc)) {
            methodVisitor.visitVarInsn(ALOAD, localIds.get(0));
            methodVisitor.visitVarInsn(ALOAD, localIds.get(1));
            methodVisitor.visitMethodInsn(INVOKESTATIC, HookConfig.HOOK_CLASS, "trackMenuItem", "(Ljava/lang/Object;" +
                    "Landroid/view/MenuItem;)V", false);
            isHasTracked = true;
            return;
        }

        if (nameDesc == "onDrawerOpened(Landroid/view/View;)V") {
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKESTATIC, HookConfig.HOOK_CLASS, "trackDrawerOpened", "(Landroid/view/View;)V",
                    false);
            isHasTracked = true;
            return;
        } else if (nameDesc == "onDrawerClosed(Landroid/view/View;)V") {
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKESTATIC, HookConfig.HOOK_CLASS, "trackDrawerClosed", "(Landroid/view/View;)V",
                    false);
            isHasTracked = true;
            return;
        }

        if (isOnClickMethod && className == "android/databinding/generated/callback/OnClickListener") {
            trackViewOnClick(methodVisitor, 1);
            isHasTracked = true;
            return;
        }

        //暂时没有做白名单过滤逻辑
//        if (!AsmUtils.isTargetClassInSpecial(className)) {
//            if ((mClassName.startsWith('android/') || mClassName.startsWith('androidx/')) && !(mClassName.startsWith
//            ("android/support/v17/leanback") || mClassName.startsWith("androidx/leanback"))) {
//                return;
//            }
//        }

        if (nameDesc == "onItemSelected(Landroid/widget/AdapterView;Landroid/view/View;IJ)V" || nameDesc == "onListItemClick" +
                "(Landroid/widget/ListView;Landroid/view/View;IJ)V") {
            methodVisitor.visitVarInsn(ALOAD, localIds.get(0));
            methodVisitor.visitVarInsn(ALOAD, localIds.get(1));
            methodVisitor.visitVarInsn(ILOAD, localIds.get(2));
            methodVisitor.visitMethodInsn(INVOKESTATIC, HookConfig.HOOK_CLASS, "trackListView", "(Landroid/widget/AdapterView;" +
                    "Landroid/view/View;I)V", false);
            isHasTracked = true;
            return;
        }

        if (mInterfaces != null && mInterfaces.length > 0) {
            if (isOnItemClickMethod && Arrays.asList(mInterfaces).contains("android/widget/AdapterView$OnItemClickListener")) {
                methodVisitor.visitVarInsn(ALOAD, localIds.get(0));
                methodVisitor.visitVarInsn(ALOAD, localIds.get(1));
                methodVisitor.visitVarInsn(ILOAD, localIds.get(2));
                methodVisitor.visitMethodInsn(INVOKESTATIC, HookConfig.HOOK_CLASS, "trackListView", "(Landroid/widget" +
                        "/AdapterView;Landroid/view/View;I)V", false);
                isHasTracked = true;
                return;
            } else if (Arrays.asList(mInterfaces).contains("android/widget/RadioGroup$OnCheckedChangeListener")
                    && nameDesc.equals("onCheckedChanged(Landroid/widget/RadioGroup;I)V")) {
                MethodBean methodBean = HookConfig.INTERFACE_METHODS
                        .get("android/widget/RadioGroup$OnCheckedChangeListeneronCheckedChanged(Landroid/widget/RadioGroup;I)V");
                if (methodBean != null) {
                    methodVisitor.visitVarInsn(ALOAD, localIds.get(0));
                    methodVisitor.visitVarInsn(ILOAD, localIds.get(1));
                    methodVisitor.visitMethodInsn(INVOKESTATIC, HookConfig.HOOK_CLASS, methodBean.agentName,
                            methodBean.agentDesc, false);
                    isHasTracked = true;
                    return;
                }
            } else if (Arrays.asList(mInterfaces).contains("android/widget/CompoundButton$OnCheckedChangeListener")
                    && nameDesc == "onCheckedChanged(Landroid/widget/CompoundButton;Z)V") {
                MethodBean methodBean = HookConfig.INTERFACE_METHODS
                        .get("android/widget/CompoundButton$OnCheckedChangeListeneronCheckedChanged" +
                                "(Landroid/widget/CompoundButton;Z)V");
                if (methodBean != null) {
                    methodVisitor.visitVarInsn(ALOAD, localIds.get(0));
                    methodVisitor.visitMethodInsn(INVOKESTATIC, HookConfig.HOOK_CLASS, methodBean.agentName,
                            methodBean.agentDesc, false);
                    isHasTracked = true;
                    return;
                }
            } else if (Arrays.asList(mInterfaces).contains("android/content/DialogInterface$OnClickListener")
                    && nameDesc == "onClick(Landroid/content/DialogInterface;I)V") {
                MethodBean methodBean = HookConfig.INTERFACE_METHODS
                        .get("android/content/DialogInterface$OnClickListeneronClick(Landroid/content/DialogInterface;I)V");
                if (methodBean != null) {
                    methodVisitor.visitVarInsn(ALOAD, localIds.get(0));
                    methodVisitor.visitVarInsn(ILOAD, localIds.get(1));
                    methodVisitor.visitMethodInsn(INVOKESTATIC, HookConfig.HOOK_CLASS, methodBean.agentName,
                            methodBean.agentDesc, false);
                    isHasTracked = true;
                    return;
                }
            } else if (Arrays.asList(mInterfaces).contains("android/widget/ExpandableListView$OnGroupClickListener")
                    && nameDesc == "onGroupClick(Landroid/widget/ExpandableListView;Landroid/view/View;IJ)Z") {
                MethodBean methodBean = HookConfig.INTERFACE_METHODS
                        .get("android/widget/ExpandableListView$OnGroupClickListeneronGroupClick" +
                                "(Landroid/widget/ExpandableListView;Landroid/view/View;IJ)Z");
                if (methodBean != null) {
                    methodVisitor.visitVarInsn(ALOAD, localIds.get(0));
                    methodVisitor.visitVarInsn(ALOAD, localIds.get(1));
                    methodVisitor.visitVarInsn(ILOAD, localIds.get(2));
                    methodVisitor.visitMethodInsn(INVOKESTATIC, HookConfig.HOOK_CLASS, methodBean.agentName,
                            methodBean.agentDesc, false);
                    isHasTracked = true;
                    return;
                }
            } else if (Arrays.asList(mInterfaces).contains("android/widget/ExpandableListView$OnChildClickListener")
                    && nameDesc == "onChildClick(Landroid/widget/ExpandableListView;Landroid/view/View;IIJ)Z") {
                MethodBean methodBean = HookConfig.INTERFACE_METHODS
                        .get("android/widget/ExpandableListView$OnChildClickListeneronChildClick" +
                                "(Landroid/widget/ExpandableListView;Landroid/view/View;IIJ)Z");
                if (methodBean != null) {
                    methodVisitor.visitVarInsn(ALOAD, localIds.get(0));
                    methodVisitor.visitVarInsn(ALOAD, localIds.get(1));
                    methodVisitor.visitVarInsn(ILOAD, localIds.get(2));
                    methodVisitor.visitVarInsn(ILOAD, localIds.get(3));
                    methodVisitor.visitMethodInsn(INVOKESTATIC, HookConfig.HOOK_CLASS, methodBean.agentName,
                            methodBean.agentDesc, false);
                    isHasTracked = true;
                    return;
                }
            } else if (nameDesc == "onMenuItemClick(Landroid/view/MenuItem;)Z") {
                for (String interfaceName : mInterfaces) {
                    MethodBean methodBean = HookConfig.INTERFACE_METHODS.get(interfaceName + nameDesc);
                    if (methodBean != null) {
                        methodVisitor.visitVarInsn(ALOAD, localIds.get(0));
                        methodVisitor.visitMethodInsn(INVOKESTATIC, HookConfig.HOOK_CLASS, methodBean.agentName,
                                methodBean.agentDesc, false);
                        isHasTracked = true;
                        return;
                    }
                }
            } else if (Arrays.asList(mInterfaces).contains("android/widget/SeekBar$OnSeekBarChangeListener")
                    && nameDesc == "onStopTrackingTouch(Landroid/widget/SeekBar;)V") {
                MethodBean methodBean = HookConfig.INTERFACE_METHODS
                        .get("android/widget/SeekBar$OnSeekBarChangeListeneronStopTrackingTouch(Landroid/widget/SeekBar;)V");
                if (methodBean != null) {
                    methodVisitor.visitVarInsn(ALOAD, localIds.get(0));
                    methodVisitor.visitMethodInsn(INVOKESTATIC, HookConfig.HOOK_CLASS, methodBean.agentName,
                            methodBean.agentDesc, false);
                    isHasTracked = true;
                    return;
                }
            } else {
                for (String interfaceName : mInterfaces) {
                    MethodBean methodBean = HookConfig.INTERFACE_METHODS.get(interfaceName + nameDesc);
                    if (methodBean != null) {
                        visitMethodWithLoadedParams(methodVisitor, INVOKESTATIC, HookConfig.HOOK_CLASS, methodBean.agentName,
                                methodBean.agentDesc, methodBean.paramsStart, methodBean.paramsCount, methodBean.opcodes);
                        isHasTracked = true;
                        return;
                    }
                }
            }
        }
        handleClassMethod(className, nameDesc);
        if (isOnClickMethod) {
            trackViewOnClick(methodVisitor, variableID);
            isHasTracked = true;
        }
    }

    void handleClassMethod(String className, String nameDesc) {
        MethodBean methodBean = HookConfig.CLASS_METHODS.get(className + nameDesc);
        if (methodBean != null) {
            visitMethodWithLoadedParams(methodVisitor, INVOKESTATIC, HookConfig.HOOK_CLASS, methodBean.agentName, methodBean.agentDesc, methodBean.paramsStart, methodBean.paramsCount, methodBean.opcodes);
            isHasTracked = true;
        }
    }

    private static void visitMethodWithLoadedParams(MethodVisitor methodVisitor, int opcode, String owner, String methodName,
                                                    String methodDesc, int start, int count, List<Integer> paramOpcodes) {
        for (int i = start; i < start + count; i++) {
            methodVisitor.visitVarInsn(paramOpcodes.get(i - start), i);
        }
        methodVisitor.visitMethodInsn(opcode, owner, methodName, methodDesc, false);
    }


    void trackViewOnClick(MethodVisitor mv, int index) {
        mv.visitVarInsn(ALOAD, index);
        mv.visitMethodInsn(INVOKESTATIC, HookConfig.HOOK_CLASS, "trackViewOnClick", "(Landroid/view/View;)V", false);
    }

    /**
     * 获取方法参数下标为 index 的对应 ASM index
     *
     * @param types          方法参数类型数组
     * @param index          方法中参数下标，从 0 开始
     * @param isStaticMethod 该方法是否为静态方法
     * @return 访问该方法的 index 位参数的 ASM index
     */
    int getVisitPosition(Type[] types, int index, boolean isStaticMethod) {
        if (types == null || index < 0 || index >= types.length) {
            throw new Error("getVisitPosition error");
        }
        if (index == 0) {
            return isStaticMethod ? 0 : 1;
        } else {
            return getVisitPosition(types, index - 1, isStaticMethod) + types[index - 1].getSize();
        }
    }

}
