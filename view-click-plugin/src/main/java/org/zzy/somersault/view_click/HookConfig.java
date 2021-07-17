package org.zzy.somersault.view_click;

import org.gradle.internal.impldep.com.google.api.client.util.ArrayMap;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/7/13 8:32
 * 描    述：预先定义需要Hook的方法，以后有新增的可以在这里添加
 * 修订历史：
 * ================================================
 */
public class HookConfig {

    public static String HOOK_CLASS = "org/somersault/cloud/lib/core/activity/ViewClickTrackHelper";

    public final static Map<String, MethodBean> INTERFACE_METHODS = new ArrayMap<>();
    public final static Map<String, MethodBean> CLASS_METHODS = new ArrayMap<>();
    /**
     * Fragment中的方法
     */
    public final static Map<String, MethodBean> FRAGMENT_METHODS = new ArrayMap<>();
    /**
     * android.gradle 3.2.1 版本中，针对 Lambda 表达式处理
     */

    public final static Map<String, MethodBean> LAMBDA_METHODS = new ArrayMap<>();

    //lambda 参数优化取样
    public final static ArrayList<MethodBean> SAMPLING_LAMBDA_METHODS = new ArrayList<>();

    static {
        addInterfaceMethod(new MethodBean(
                "onCheckedChanged",
                "(Landroid/widget/CompoundButton;Z)V",
                "android/widget/CompoundButton$OnCheckedChangeListener",
                "trackViewOnClick",
                "(Landroid/view/View;)V",
                1, 1,
                Arrays.asList(org.objectweb.asm.Opcodes.ALOAD)));
        addInterfaceMethod(new MethodBean(
                "onRatingChanged",
                "(Landroid/widget/RatingBar;FZ)V",
                "android/widget/RatingBar$OnRatingBarChangeListener",
                "trackViewOnClick",
                "(Landroid/view/View;)V",
                1, 1,
                Arrays.asList(org.objectweb.asm.Opcodes.ALOAD)));
        addInterfaceMethod(new MethodBean(
                "onStopTrackingTouch",
                "(Landroid/widget/SeekBar;)V",
                "android/widget/SeekBar$OnSeekBarChangeListener",
                "trackViewOnClick",
                "(Landroid/view/View;)V",
                1, 1,
                Arrays.asList(org.objectweb.asm.Opcodes.ALOAD)));
        addInterfaceMethod(new MethodBean(
                "onCheckedChanged",
                "(Landroid/widget/RadioGroup;I)V",
                "android/widget/RadioGroup$OnCheckedChangeListener",
                "trackRadioGroup",
                "(Landroid/widget/RadioGroup;I)V",
                1, 2,
                Arrays.asList(org.objectweb.asm.Opcodes.ALOAD, org.objectweb.asm.Opcodes.ILOAD)));
        addInterfaceMethod(new MethodBean(
                "onClick",
                "(Landroid/content/DialogInterface;I)V",
                "android/content/DialogInterface$OnClickListener",
                "trackDialog",
                "(Landroid/content/DialogInterface;I)V",
                1, 2,
                Arrays.asList(org.objectweb.asm.Opcodes.ALOAD, org.objectweb.asm.Opcodes.ILOAD)));
        addInterfaceMethod(new MethodBean(
                "onItemSelected",
                "(Landroid/widget/AdapterView;Landroid/view/View;IJ)V",
                "android/widget/AdapterView$OnItemSelectedListener",
                "trackListView",
                "(Landroid/widget/AdapterView;Landroid/view/View;I)V",
                1, 3,
                Arrays.asList(org.objectweb.asm.Opcodes.ALOAD, org.objectweb.asm.Opcodes.ALOAD, org.objectweb.asm.Opcodes.ILOAD)));
        addInterfaceMethod(new MethodBean(
                "onGroupClick",
                "(Landroid/widget/ExpandableListView;Landroid/view/View;IJ)Z",
                "android/widget/ExpandableListView$OnGroupClickListener",
                "trackExpandableListViewOnGroupClick",
                "(Landroid/widget/ExpandableListView;Landroid/view/View;I)V",
                1, 3,
                Arrays.asList(org.objectweb.asm.Opcodes.ALOAD, org.objectweb.asm.Opcodes.ALOAD, org.objectweb.asm.Opcodes.ILOAD)));
        addInterfaceMethod(new MethodBean(
                "onChildClick",
                "(Landroid/widget/ExpandableListView;Landroid/view/View;IIJ)Z",
                "android/widget/ExpandableListView$OnChildClickListener",
                "trackExpandableListViewOnChildClick",
                "(Landroid/widget/ExpandableListView;Landroid/view/View;II)V",
                1, 4,
                Arrays.asList(org.objectweb.asm.Opcodes.ALOAD, org.objectweb.asm.Opcodes.ALOAD, org.objectweb.asm.Opcodes.ILOAD, org.objectweb.asm.Opcodes.ILOAD)));
        addInterfaceMethod(new MethodBean(
                "onTabChanged",
                "(Ljava/lang/String;)V",
                "android/widget/TabHost$OnTabChangeListener",
                "trackTabHost",
                "(Ljava/lang/String;)V",
                1, 1,
                Arrays.asList(org.objectweb.asm.Opcodes.ALOAD)));
        addInterfaceMethod(new MethodBean(
                "onTabSelected",
                "(Landroid/support/design/widget/TabLayout$Tab;)V",
                "android/support/design/widget/TabLayout$OnTabSelectedListener",
                "trackTabLayoutSelected",
                "(Ljava/lang/Object;Ljava/lang/Object;)V",
                0, 2,
                Arrays.asList(org.objectweb.asm.Opcodes.ALOAD, org.objectweb.asm.Opcodes.ALOAD)));
        addInterfaceMethod(new MethodBean(
                "onTabSelected",
                "(Lcom/google/android/material/tabs/TabLayout$Tab;)V",
                "com/google/android/material/tabs/TabLayout$OnTabSelectedListener",
                "trackTabLayoutSelected",
                "(Ljava/lang/Object;Ljava/lang/Object;)V",
                0, 2,
                Arrays.asList(org.objectweb.asm.Opcodes.ALOAD, org.objectweb.asm.Opcodes.ALOAD)));
        addInterfaceMethod(new MethodBean(
                "onMenuItemClick",
                "(Landroid/view/MenuItem;)Z",
                "android/widget/Toolbar$OnMenuItemClickListener",
                "trackMenuItem",
                "(Landroid/view/MenuItem;)V",
                1, 1,
                Arrays.asList(org.objectweb.asm.Opcodes.ALOAD)));
        addInterfaceMethod(new MethodBean(
                "onMenuItemClick",
                "(Landroid/view/MenuItem;)Z",
                "android/support/v7/widget/Toolbar$OnMenuItemClickListener",
                "trackMenuItem",
                "(Landroid/view/MenuItem;)V",
                1, 1,
                Arrays.asList(org.objectweb.asm.Opcodes.ALOAD)));
        addInterfaceMethod(new MethodBean(
                "onMenuItemClick",
                "(Landroid/view/MenuItem;)Z",
                "androidx/appcompat/widget/Toolbar$OnMenuItemClickListener",
                "trackMenuItem",
                "(Landroid/view/MenuItem;)V",
                1, 1,
                Arrays.asList(org.objectweb.asm.Opcodes.ALOAD)));
        addInterfaceMethod(new MethodBean(
                "onClick",
                "(Landroid/content/DialogInterface;IZ)V",
                "android/content/DialogInterface$OnMultiChoiceClickListener",
                "trackDialog",
                "(Landroid/content/DialogInterface;I)V",
                1, 2,
                Arrays.asList(org.objectweb.asm.Opcodes.ALOAD, org.objectweb.asm.Opcodes.ILOAD)));
        addInterfaceMethod(new MethodBean(
                "onMenuItemClick",
                "(Landroid/view/MenuItem;)Z",
                "android/widget/PopupMenu$OnMenuItemClickListener",
                "trackMenuItem",
                "(Landroid/view/MenuItem;)V",
                1, 1,
                Arrays.asList(org.objectweb.asm.Opcodes.ALOAD)));
        addInterfaceMethod(new MethodBean(
                "onMenuItemClick",
                "(Landroid/view/MenuItem;)Z",
                "androidx/appcompat/widget/PopupMenu$OnMenuItemClickListener",
                "trackMenuItem",
                "(Landroid/view/MenuItem;)V",
                1, 1,
                Arrays.asList(org.objectweb.asm.Opcodes.ALOAD)));
        addInterfaceMethod(new MethodBean(
                "onMenuItemClick",
                "(Landroid/view/MenuItem;)Z",
                "android/support/v7/widget/PopupMenu$OnMenuItemClickListener",
                "trackMenuItem",
                "(Landroid/view/MenuItem;)V",
                1, 1,
                Arrays.asList(org.objectweb.asm.Opcodes.ALOAD)));
        addInterfaceMethod(new MethodBean(
                "onNavigationItemSelected",
                "(Landroid/view/MenuItem;)Z",
                "com/google/android/material/navigation/NavigationView$OnNavigationItemSelectedListener",
                "trackMenuItem",
                "(Landroid/view/MenuItem;)V",
                1, 1,
                Arrays.asList(org.objectweb.asm.Opcodes.ALOAD)));
        addInterfaceMethod(new MethodBean(
                "onNavigationItemSelected",
                "(Landroid/view/MenuItem;)Z",
                "android/support/design/widget/NavigationView$OnNavigationItemSelectedListener",
                "trackMenuItem",
                "(Landroid/view/MenuItem;)V",
                1, 1,
                Arrays.asList(org.objectweb.asm.Opcodes.ALOAD)));
        addInterfaceMethod(new MethodBean(
                "onNavigationItemSelected",
                "(Landroid/view/MenuItem;)Z",
                "android/support/design/widget/BottomNavigationView$OnNavigationItemSelectedListener",
                "trackMenuItem",
                "(Landroid/view/MenuItem;)V",
                1, 1,
                Arrays.asList(org.objectweb.asm.Opcodes.ALOAD)));
        addInterfaceMethod(new MethodBean(
                "onNavigationItemSelected",
                "(Landroid/view/MenuItem;)Z",
                "com/google/android/material/bottomnavigation/BottomNavigationView$OnNavigationItemSelectedListener",
                "trackMenuItem",
                "(Landroid/view/MenuItem;)V",
                1, 1,
                Arrays.asList(Opcodes.ALOAD)));
    }

    static {
        addClassMethod(new MethodBean(
                "performClick",
                "()Z",
                "androidx/appcompat/widget/ActionMenuPresenter$OverflowMenuButton",
                "trackViewOnClick",
                "(Landroid/view/View;)V",
                0, 1,
                Arrays.asList(Opcodes.ALOAD)));

        addClassMethod(new MethodBean(
                "performClick",
                "()Z",
                "android/support/v7/widget/ActionMenuPresenter$OverflowMenuButton",
                "trackViewOnClick",
                "(Landroid/view/View;)V",
                0, 1,
                Arrays.asList(Opcodes.ALOAD)));

        addClassMethod(new MethodBean(
                "performClick",
                "()Z",
                "android/widget/ActionMenuPresenter$OverflowMenuButton",
                "trackViewOnClick",
                "(Landroid/view/View;)V",
                0, 1,
                Arrays.asList(Opcodes.ALOAD)));
    }

    static {
        FRAGMENT_METHODS.put("onResume()V", new MethodBean(
                "onResume",
                "()V",
                "",// parent省略，均为 android/app/Fragment 或 android/support/v4/app/Fragment
                "trackFragmentResume",
                "(Ljava/lang/Object;)V",
                0, 1,
                Arrays.asList(Opcodes.ALOAD)));
        FRAGMENT_METHODS.put("setUserVisibleHint(Z)V", new MethodBean(
                "setUserVisibleHint",
                "(Z)V",
                "",// parent省略，均为 android/app/Fragment 或 android/support/v4/app/Fragment
                "trackFragmentSetUserVisibleHint",
                "(Ljava/lang/Object;Z)V",
                0, 2,
                Arrays.asList(Opcodes.ALOAD, Opcodes.ILOAD)));
        FRAGMENT_METHODS.put("onHiddenChanged(Z)V", new MethodBean(
                "onHiddenChanged",
                "(Z)V",
                "",
                "trackOnHiddenChanged",
                "(Ljava/lang/Object;Z)V",
                0, 2,
                Arrays.asList(Opcodes.ALOAD, Opcodes.ILOAD)));
        FRAGMENT_METHODS.put("onViewCreated(Landroid/view/View;Landroid/os/Bundle;)V", new MethodBean(
                "onViewCreated",
                "(Landroid/view/View;Landroid/os/Bundle;)V",
                "",
                "onFragmentViewCreated",
                "(Ljava/lang/Object;Landroid/view/View;Landroid/os/Bundle;)V",
                0, 3,
                Arrays.asList(Opcodes.ALOAD, Opcodes.ALOAD, Opcodes.ALOAD)));
    }


    static {
        addLambdaMethod(new MethodBean(
                "onClick",
                "(Landroid/view/View;)V",
                "Landroid/view/View$OnClickListener;",
                "trackViewOnClick",
                "(Landroid/view/View;)V",
                1, 1,
                Arrays.asList(Opcodes.ALOAD)));
        SAMPLING_LAMBDA_METHODS.add(new MethodBean(
                "onClick",
                "(Landroid/view/View;)V",
                "Landroid/view/View$OnClickListener;",
                "trackViewOnClick",
                "(Landroid/view/View;)V",
                1, 1,
                Arrays.asList(Opcodes.ALOAD)));
        addLambdaMethod(new MethodBean(
                "onCheckedChanged",
                "(Landroid/widget/CompoundButton;Z)V",
                "Landroid/widget/CompoundButton$OnCheckedChangeListener;",
                "trackViewOnClick",
                "(Landroid/view/View;)V",
                1, 1,
                Arrays.asList(Opcodes.ALOAD)));
        addLambdaMethod(new MethodBean(
                "onRatingChanged",
                "(Landroid/widget/RatingBar;FZ)V",
                "Landroid/widget/RatingBar$OnRatingBarChangeListener;",
                "trackViewOnClick",
                "(Landroid/view/View;)V",
                1, 1,
                Arrays.asList(Opcodes.ALOAD)));
        addLambdaMethod(new MethodBean(
                "onCheckedChanged",
                "(Landroid/widget/RadioGroup;I)V",
                "Landroid/widget/RadioGroup$OnCheckedChangeListener;",
                "trackRadioGroup",
                "(Landroid/widget/RadioGroup;I)V",
                1, 2,
                Arrays.asList(Opcodes.ALOAD, Opcodes.ILOAD)));
        SAMPLING_LAMBDA_METHODS.add(new MethodBean(
                "onCheckedChanged",
                "(Landroid/widget/RadioGroup;I)V",
                "Landroid/widget/RadioGroup$OnCheckedChangeListener;",
                "trackRadioGroup",
                "(Landroid/widget/RadioGroup;I)V",
                1, 2,
                Arrays.asList(Opcodes.ALOAD, Opcodes.ILOAD)));
        addLambdaMethod(new MethodBean(
                "onClick",
                "(Landroid/content/DialogInterface;I)V",
                "Landroid/content/DialogInterface$OnClickListener;",
                "trackDialog",
                "(Landroid/content/DialogInterface;I)V",
                1, 2,
                Arrays.asList(Opcodes.ALOAD, Opcodes.ILOAD)));
        addLambdaMethod(new MethodBean(
                "onItemClick",
                "(Landroid/widget/AdapterView;Landroid/view/View;IJ)V",
                "Landroid/widget/AdapterView$OnItemClickListener;",
                "trackListView",
                "(Landroid/widget/AdapterView;Landroid/view/View;I)V",
                1, 3,
                Arrays.asList(Opcodes.ALOAD, Opcodes.ALOAD, Opcodes.ILOAD)));
        SAMPLING_LAMBDA_METHODS.add(new MethodBean(
                "onItemClick",
                "(Landroid/widget/AdapterView;Landroid/view/View;IJ)V",
                "Landroid/widget/AdapterView$OnItemClickListener;",
                "trackListView",
                "(Landroid/widget/AdapterView;Landroid/view/View;I)V",
                1, 3,
                Arrays.asList(Opcodes.ALOAD, Opcodes.ALOAD, Opcodes.ILOAD)));
        addLambdaMethod(new MethodBean(
                "onGroupClick",
                "(Landroid/widget/ExpandableListView;Landroid/view/View;IJ)Z",
                "Landroid/widget/ExpandableListView$OnGroupClickListener;",
                "trackExpandableListViewOnGroupClick",
                "(Landroid/widget/ExpandableListView;Landroid/view/View;I)V",
                1, 3,
                Arrays.asList(Opcodes.ALOAD, Opcodes.ALOAD, Opcodes.ILOAD)));
        addLambdaMethod(new MethodBean(
                "onChildClick",
                "(Landroid/widget/ExpandableListView;Landroid/view/View;IIJ)Z",
                "Landroid/widget/ExpandableListView$OnChildClickListener;",
                "trackExpandableListViewOnChildClick",
                "(Landroid/widget/ExpandableListView;Landroid/view/View;II)V",
                1, 4,
                Arrays.asList(Opcodes.ALOAD, Opcodes.ALOAD, Opcodes.ILOAD, Opcodes.ILOAD)));
        addLambdaMethod(new MethodBean(
                "onTabChanged",
                "(Ljava/lang/String;)V",
                "Landroid/widget/TabHost$OnTabChangeListener;",
                "trackTabHost",
                "(Ljava/lang/String;)V",
                1, 1,
                Arrays.asList(Opcodes.ALOAD)));
        addLambdaMethod(new MethodBean(
                "onNavigationItemSelected",
                "(Landroid/view/MenuItem;)Z",
                "Lcom/google/android/material/navigation/NavigationView$OnNavigationItemSelectedListener;",
                "trackMenuItem",
                "(Landroid/view/MenuItem;)V",
                1, 1,
                Arrays.asList(Opcodes.ALOAD)));
        addLambdaMethod(new MethodBean(
                "onNavigationItemSelected",
                "(Landroid/view/MenuItem;)Z",
                "Landroid/support/design/widget/NavigationView$OnNavigationItemSelectedListener;",
                "trackMenuItem",
                "(Landroid/view/MenuItem;)V",
                1, 1,
                Arrays.asList(Opcodes.ALOAD)));
        addLambdaMethod(new MethodBean(
                "onNavigationItemSelected",
                "(Landroid/view/MenuItem;)Z",
                "Landroid/support/design/widget/BottomNavigationView$OnNavigationItemSelectedListener;",
                "trackMenuItem",
                "(Landroid/view/MenuItem;)V",
                1, 1,
                Arrays.asList(Opcodes.ALOAD)));
        addLambdaMethod(new MethodBean(
                "onNavigationItemSelected",
                "(Landroid/view/MenuItem;)Z",
                "Lcom/google/android/material/bottomnavigation/BottomNavigationView$OnNavigationItemSelectedListener;",
                "trackMenuItem",
                "(Landroid/view/MenuItem;)V",
                1, 1,
                Arrays.asList(Opcodes.ALOAD)));
        addLambdaMethod(new MethodBean(
                "onMenuItemClick",
                "(Landroid/view/MenuItem;)Z",
                "Landroid/widget/Toolbar$OnMenuItemClickListener;",
                "trackMenuItem",
                "(Landroid/view/MenuItem;)V",
                1, 1,
                Arrays.asList(Opcodes.ALOAD)));
        addLambdaMethod(new MethodBean(
                "onMenuItemClick",
                "(Landroid/view/MenuItem;)Z",
                "Landroid/support/v7/widget/Toolbar$OnMenuItemClickListener;",
                "trackMenuItem",
                "(Landroid/view/MenuItem;)V",
                1, 1,
                Arrays.asList(Opcodes.ALOAD)));
        addLambdaMethod(new MethodBean(
                "onMenuItemClick",
                "(Landroid/view/MenuItem;)Z",
                "Landroidx/appcompat/widget/Toolbar$OnMenuItemClickListener;",
                "trackMenuItem",
                "(Landroid/view/MenuItem;)V",
                1, 1,
                Arrays.asList(Opcodes.ALOAD)));
        addLambdaMethod(new MethodBean(
                "onClick",
                "(Landroid/content/DialogInterface;IZ)V",
                "Landroid/content/DialogInterface$OnMultiChoiceClickListener;",
                "trackDialog",
                "(Landroid/content/DialogInterface;I)V",
                1, 2,
                Arrays.asList(Opcodes.ALOAD, Opcodes.ILOAD)));
        addLambdaMethod(new MethodBean(
                "onMenuItemClick",
                "(Landroid/view/MenuItem;)Z",
                "Landroid/widget/PopupMenu$OnMenuItemClickListener;",
                "trackMenuItem",
                "(Landroid/view/MenuItem;)V",
                1, 1,
                Arrays.asList(Opcodes.ALOAD)));
        addLambdaMethod(new MethodBean(
                "onMenuItemClick",
                "(Landroid/view/MenuItem;)Z",
                "Landroidx/appcompat/widget/PopupMenu$OnMenuItemClickListener;",
                "trackMenuItem",
                "(Landroid/view/MenuItem;)V",
                1, 1,
                Arrays.asList(Opcodes.ALOAD)));
        addLambdaMethod(new MethodBean(
                "onMenuItemClick",
                "(Landroid/view/MenuItem;)Z",
                "Landroid/support/v7/widget/PopupMenu$OnMenuItemClickListener;",
                "trackMenuItem",
                "(Landroid/view/MenuItem;)V",
                1, 1,
                Arrays.asList(Opcodes.ALOAD)));

        // Todo: 扩展
    }

    static void addInterfaceMethod(MethodBean bean) {
        if (bean != null) {
            INTERFACE_METHODS.put(bean.parent + bean.name + bean.desc, bean);
        }
    }

    static void addClassMethod(MethodBean bean) {
        if (bean != null) {
            CLASS_METHODS.put(bean.parent + bean.name + bean.desc, bean);
        }
    }

    static void addLambdaMethod(MethodBean bean) {
        if (bean != null) {
            LAMBDA_METHODS.put(bean.parent + bean.name + bean.desc, bean);
        }
    }
}



