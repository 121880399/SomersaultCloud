package org.somersault.cloud.lib.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.ArrayRes;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.text.TextUtilsCompat;
import androidx.core.view.ViewCompat;


import org.somersault.cloud.lib.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * ================================================
 * 作    者：ZhouZhengyi
 * 创建日期：2020/12/8 18:11
 * 描    述：资源相关工具类
 * 修订历史：
 * ================================================
 */
public class ResUtils {

    /**
     * 缓存区大小
     */
    private static final int BUFFER_SIZE = 8192;

    /**
     * 获取字符串资源
     *
     * @param resId 字符串资源ID
     * @return 字符串
     */
    public static String getString(@StringRes int resId) {
        try {
            Resources resources = ApplicationUtils.getInstance().getApplication().getResources();
            if (null == resources || 0 == resId) {
                return "";
            }
            return resources.getString(resId);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取字符串资源
     *
     * @param resId 字符串资源ID
     * @param formatArgs 格式化
     * @return 字符串
     */
    public static String getString(@StringRes int resId, Object... formatArgs) {
        try {
            Resources resources = ApplicationUtils.getInstance().getApplication().getResources();
            if (null == resources || 0 == resId || null == formatArgs) {
                return "";
            }
            return resources.getString(resId, formatArgs);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取颜色资源
     *
     * @param resId 字颜色资源ID
     * @return 颜色值
     */
    public static int getColor(@ColorRes int resId) {
        Resources resources = ApplicationUtils.getInstance().getApplication().getResources();
        if (null == resources || 0 == resId) {
            resId = R.color.sc_default_text_color_1e242a;
        }
        return resources.getColor(resId);
    }

    /**
     * 获取颜色资源
     *
     * @param resId 字颜色资源ID
     * @return 颜色值
     */
    public static int getColor(Context context,@ColorRes int resId) {
        Resources resources = context.getResources();
        if (null == resources || 0 == resId) {
            resId = R.color.sc_default_text_color_1e242a;
        }
        return resources.getColor(resId);
    }

    /**
     * 获取Dimension
     *
     * @param resId Dimen资源ID
     * @return Dimen
     */
    public static int getDimensionPixelOffset(@DimenRes int resId) {
        try {
            Resources resources = ApplicationUtils.getInstance().getApplication().getResources();
            if (null == resources || 0 == resId) {
                return 0;
            }
            return resources.getDimensionPixelOffset(resId);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获取drawable资源
     *
     *
     * @param context
     * @param resId drawable资源ID
     * @return Drawable
     */
    public static Drawable getDrawable(Context context, @DrawableRes int resId) {
        try {
            Resources resources = context.getResources();
            if (null == resources || 0 == resId) {
                return null;
            }
            return resources.getDrawable(resId);
        } catch (Exception e) {
            return new ColorDrawable();
        }
    }

    /**
     * 获取字符串数组资源
     *
     * @param resId 字符串数组资源ID
     * @return 字符串数组
     */
    public static String[] getStringArray(@ArrayRes int resId) {
        try {
            Resources resources = ApplicationUtils.getInstance().getApplication().getResources();
            if (null == resources || 0 == resId) {
                return new String[]{};
            }
            return resources.getStringArray(resId);
        } catch (Exception e) {
            return new String[]{};
        }
    }

    /**
     * 从assets 中读取字符串
     *
     * @param assetsFilePath assets 文件路径
     * @return 内容
     */
    public static String readAssetsString(final String assetsFilePath) {
        InputStream is;
        try {
            is = ApplicationUtils.getInstance().getApplication().getAssets().open(assetsFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        byte[] bytes = is2Bytes(is);
        if (bytes == null) { return null; }
        return new String(bytes);
    }

    /**
     * 是否是字节数组
     *
     * @param is InputStream 输入流
     * @return 字节数组
     */
    private static byte[] is2Bytes(final InputStream is) {
        if (is == null) { return null; }
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            byte[] b = new byte[BUFFER_SIZE];
            int len;
            while ((len = is.read(b, 0, BUFFER_SIZE)) != -1) {
                os.write(b, 0, len);
            }
            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isRtl() {
        return TextUtilsCompat.getLayoutDirectionFromLocale(
                ApplicationUtils.getInstance().getApplication().getResources().getConfiguration().locale) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }


}
