package org.somersault.cloud.lib.widget.sheet.sweetpick;


import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;


import androidx.annotation.MenuRes;
import androidx.appcompat.widget.PopupMenu;

import org.somersault.cloud.lib.widget.sheet.entity.MenuEntity;

import java.util.ArrayList;
import java.util.List;


/**
 * @author zzz40500
 * @version 1.0
 * @date 2015/8/5.
 * @github: https://github.com/zzz40500
 */
public class SweetSheet {

    public static final String Tag="SweetSheet";

    private ViewGroup mParentVG;
    private Delegate mDelegate;
    private Effect mEffect=new NoneEffect();
    private boolean mIsBgClickEnable=true;


    public SweetSheet(ViewGroup parentVG) {
        mParentVG = parentVG;
    }

    public void setDelegate(Delegate delegate){
        mDelegate=delegate;
        mDelegate.init(mParentVG);
        setup();

    }

    public Delegate getDelegate() {
        return mDelegate;
    }


    private void setup() {

        mDelegate.setBackgroundEffect(mEffect);
        mDelegate.setBackgroundClickEnable(mIsBgClickEnable);
    }


    public void setBackgroundClickEnable(boolean isBgClickEnable){
        if(mDelegate != null){
            mDelegate.setBackgroundClickEnable(isBgClickEnable);
        }else{
            mIsBgClickEnable=isBgClickEnable;
        }
    }


    public void setBackgroundEffect(Effect effect) {

        if(mDelegate != null) {
            mDelegate.setBackgroundEffect(effect);
        }else{
            mEffect=effect;
        }
    }


    public void show() {

        if(mDelegate != null) {

            mDelegate.show();
        }else{
            Log.e(Tag,"you must setDelegate before");
        }
    }

    public void dismiss() {
        if(mDelegate != null) {

            mDelegate.dismiss();
        }else{
            Log.e(Tag,"you must setDelegate before");
        }

    }

    public void toggle() {
        if(mDelegate != null) {

            mDelegate.toggle();
        }else{
            Log.e(Tag,"you must setDelegate before");
        }

    }

    public boolean isShow() {

        if(mDelegate == null) {

           return  false;
        }
        if (mDelegate.getStatus() == Status.SHOW || mDelegate.getStatus() == Status.SHOWING) {
            return true;
        }
        return false;
    }


    public enum Status {
        SHOW, SHOWING,
        DISMISS, DISMISSING
    }

}
