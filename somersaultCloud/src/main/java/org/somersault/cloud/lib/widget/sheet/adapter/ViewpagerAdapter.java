package org.somersault.cloud.lib.widget.sheet.adapter;


import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import java.util.List;


/**
 * @author zzz40500
 * @version 1.0
 * @date 2015/8/5.
 * @github: https://github.com/zzz40500
 *
 */
public class ViewpagerAdapter extends PagerAdapter {


    List<MenuListViewHandler> mMenuListViewHandlers = null;

    public ViewpagerAdapter(List<MenuListViewHandler> menuListViewHandlers) {
        mMenuListViewHandlers = menuListViewHandlers;
    }

    @Override
    public int getCount() {
        return mMenuListViewHandlers.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView(mMenuListViewHandlers.get(position).onCreateView(container));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mMenuListViewHandlers.get(position).onCreateView(container));

        return mMenuListViewHandlers.get(position).onCreateView(container);
    }
}
