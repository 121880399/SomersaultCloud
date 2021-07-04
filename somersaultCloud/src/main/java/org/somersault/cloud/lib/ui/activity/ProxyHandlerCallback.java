package org.somersault.cloud.lib.ui.activity;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import org.somersault.cloud.lib.manager.ActivityCostManager;
import org.somersault.cloud.lib.utils.Reflector;


/**
* ActivityCostManager中的
 * launch和pause 状态都从这里触发
* 作者: ZhouZhengyi
* 创建时间: 2021/7/3 8:43
*/
class ProxyHandlerCallback implements Handler.Callback {
    private static final String TAG = "ProxyHandlerCallback";
    /**
     * Android 28开始 变量从110开始
     */
    private static final int LAUNCH_ACTIVITY = 100;
    /**
     * Android 28开始 变量从110开始
     */
    private static final int PAUSE_ACTIVITY = 101;
    private static final int EXECUTE_TRANSACTION = 159;
    private static final String LAUNCH_ITEM_CLASS = "android.app.servertransaction.ResumeActivityItem";
    private static final String PAUSE_ITEM_CLASS = "android.app.servertransaction.PauseActivityItem";

    private final Handler.Callback mOldCallback;
    public final Handler mHandler;

    ProxyHandlerCallback(Handler.Callback oldCallback, Handler handler) {
        mOldCallback = oldCallback;
        mHandler = handler;
    }

    /**
    * 代理了ActivityThread的Handler
     * 处理Handler消息会走这个方法
     * 在执行系统HandlerMessage方法之前
     * 我们回调我们自己相应的方法
     * 等系统HandLerMessage处理完之后
     * 我们再次回调我们的方法
    * 作者: ZhouZhengyi
    * 创建时间: 2021/7/3 8:58
    */
    @Override
    public boolean handleMessage(Message msg) {
        int msgType = preDispatch(msg);
        if (mOldCallback != null && mOldCallback.handleMessage(msg)) {
            postDispatch(msgType);
            return true;
        }
        mHandler.handleMessage(msg);
        postDispatch(msgType);
        return true;
    }

    private int preDispatch(Message msg) {
        switch (msg.what) {
            case LAUNCH_ACTIVITY:
                //这里只需要拦截Launch_activity就行，因为在handleLaunchActivity
                //方法里会执行onCreate-onStart-onResume
                ActivityCostManager.Companion.getInstance().launch();
                break;
            case PAUSE_ACTIVITY:
                ActivityCostManager.Companion.getInstance().pause();
                break;
            //兼容 Android SDK 28及以上
            case EXECUTE_TRANSACTION:
                return handlerActivity(msg);
            default:
                break;
        }
        return msg.what;
    }

    private int handlerActivity(Message msg) {
        Object obj = msg.obj;

        Object activityCallback = Reflector.QuietReflector.with(obj).method("getLifecycleStateRequest").call();
        if (activityCallback != null) {
            String transactionName = activityCallback.getClass().getCanonicalName();
            if (TextUtils.equals(transactionName, LAUNCH_ITEM_CLASS)) {
                ActivityCostManager.Companion.getInstance().launch();
                return LAUNCH_ACTIVITY;
            } else if (TextUtils.equals(transactionName, PAUSE_ITEM_CLASS)) {
                ActivityCostManager.Companion.getInstance().pause();
                return PAUSE_ACTIVITY;
            }
        }
        return msg.what;
    }

    private void postDispatch(int msgType) {
        switch (msgType) {
            case LAUNCH_ACTIVITY:
                ActivityCostManager.Companion.getInstance().launchEnd();
                break;
            case PAUSE_ACTIVITY:
                ActivityCostManager.Companion.getInstance().paused();
                break;
            default:
                break;
        }
    }
}
