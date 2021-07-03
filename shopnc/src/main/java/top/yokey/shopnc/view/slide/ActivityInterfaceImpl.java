package top.yokey.shopnc.view.slide;

import android.app.Application;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 滑动返回
 *
 * @author MapStory
 * @ qq 1002285057
 * @ project https://gitee.com/MapStory/ShopNc-Android
 */

@SuppressWarnings("ALL")
class ActivityInterfaceImpl extends AppCompatActivity implements ActivityInterface {

    private Application.ActivityLifecycleCallbacks mActivityLifecycleCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStackManager.addToStack(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityStackManager.removeFromStack(this);
        if (mActivityLifecycleCallbacks != null) {
            mActivityLifecycleCallbacks.onActivityDestroyed(this);
        }
    }

    @Override
    public void setActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callbacks) {
        mActivityLifecycleCallbacks = callbacks;
    }

}
