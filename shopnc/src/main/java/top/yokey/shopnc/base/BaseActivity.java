package top.yokey.shopnc.base;

import android.app.Activity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import top.yokey.shopnc.R;
import top.yokey.shopnc.view.slide.SlideBackActivity;

/**
 * BaseActivity
 *
 * @author MapStory
 * @ qq 1002285057
 * @ project https://gitee.com/MapStory/ShopNc-Android
 */

@SuppressWarnings("ALL")
public abstract class BaseActivity extends SlideBackActivity {

    private Activity activity;

    @Override
    public void onStop() {
        super.onStop();
        BaseBusClient.get().unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        BaseBusClient.get().register(this);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        activity = this;
        initView();
        initData();
        initEven();
    }

    @Override
    public void setContentView(int resId) {

        setContentView(LayoutInflater.from(this).inflate(resId, null));

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            onReturn();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            onReturn();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //必须重载

    public abstract void initView();

    public abstract void initData();

    public abstract void initEven();

    //公共方法

    public void onReturn() {

        finish();

    }

    public Activity getActivity() {

        return activity;

    }

    public void setToolbar(Toolbar toolbar, String title) {

        toolbar.setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_action_back);

        if (!TextUtils.isEmpty(title)) {
            AppCompatTextView appCompatTextView = (AppCompatTextView) findViewById(R.id.titleTextView);
            appCompatTextView.setText(title);
        }

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onReturn();
            }
        });

    }

}
