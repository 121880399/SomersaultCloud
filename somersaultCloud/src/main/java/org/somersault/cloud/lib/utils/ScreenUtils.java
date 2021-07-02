package org.somersault.cloud.lib.utils;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2021/6/23 9:26
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ScreenUtils {

    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = ApplicationUtils.getInstance().getApplication().getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            result = ApplicationUtils.getInstance().getApplication().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getScreenWidth() {
        int screenWith = -1;
        try {
            screenWith = ApplicationUtils.getInstance().getApplication().getResources().getDisplayMetrics().widthPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screenWith;
    }

    public static int getScreenHeight() {
        int screenHeight = -1;
        try {
            screenHeight = ApplicationUtils.getInstance().getApplication().getResources().getDisplayMetrics().heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screenHeight;
    }

    public static float dp2px(float dp) {
        Resources res = ApplicationUtils.getInstance().getApplication().getResources();
        return TypedValue.applyDimension(1, dp, res.getDisplayMetrics());
    }

    public static boolean isPortrait(){
        return ApplicationUtils.getInstance().getApplication().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }
}
