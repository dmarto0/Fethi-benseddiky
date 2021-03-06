package top.yokey.shopnc.activity.main;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.squareup.otto.Subscribe;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import top.yokey.base.base.BaseDialog;
import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseSnackBar;
import top.yokey.base.base.BaseToast;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.event.MainPositionEvent;
import top.yokey.base.model.IndexModel;
import top.yokey.base.util.JsonUtil;
import top.yokey.base.util.TextUtil;
import top.yokey.shopnc.R;
import top.yokey.shopnc.adapter.BaseFragmentAdapter;
import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseConstant;
import top.yokey.shopnc.base.BaseFileClient;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class MainActivity extends BaseActivity {

    private ViewPager mainViewPager;
    private AppCompatTextView[] navigationTextView;

    private String version;
    private long exitTimeLong;
    private ProgressDialog progressDialog;
    private BroadcastReceiver broadcastReceiver;

    private Drawable[] navigationPressDrawable;
    private Drawable[] navigationNormalDrawable;

    @Override
    public void initView() {

        setContentView(R.layout.activity_main_main);
        mainViewPager = findViewById(R.id.mainViewPager);
        navigationTextView = new AppCompatTextView[5];
        navigationTextView[0] = findViewById(R.id.homeTextView);
        navigationTextView[1] = findViewById(R.id.cateTextView);
        navigationTextView[2] = findViewById(R.id.searchTextView);
        navigationTextView[3] = findViewById(R.id.cartTextView);
        navigationTextView[4] = findViewById(R.id.mineTextView);

    }

    @Override
    public void initData() {

        exitTimeLong = 0L;

        navigationNormalDrawable = new Drawable[navigationTextView.length];
        navigationNormalDrawable[0] = BaseApplication.get().getMipmap(R.drawable.ic_navigation_home);
        navigationNormalDrawable[1] = BaseApplication.get().getMipmap(R.drawable.ic_navigation_cate);
        navigationNormalDrawable[2] = BaseApplication.get().getMipmap(R.drawable.ic_navigation_search);
        navigationNormalDrawable[3] = BaseApplication.get().getMipmap(R.drawable.ic_navigation_cart);
        navigationNormalDrawable[4] = BaseApplication.get().getMipmap(R.drawable.ic_navigation_mine);

        navigationPressDrawable = new Drawable[navigationTextView.length];
        navigationPressDrawable[0] = BaseApplication.get().getMipmap(R.drawable.ic_navigation_home_press);
        navigationPressDrawable[1] = BaseApplication.get().getMipmap(R.drawable.ic_navigation_cate_press);
        navigationPressDrawable[2] = BaseApplication.get().getMipmap(R.drawable.ic_navigation_search_press);
        navigationPressDrawable[3] = BaseApplication.get().getMipmap(R.drawable.ic_navigation_cart_press);
        navigationPressDrawable[4] = BaseApplication.get().getMipmap(R.drawable.ic_navigation_mine_press);

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new HomeFragment());
        fragmentList.add(new CateFragment());
        fragmentList.add(new SearchFragment());
        fragmentList.add(new CartFragment());
        fragmentList.add(new MineFragment());

        mainViewPager.setAdapter(new BaseFragmentAdapter(getSupportFragmentManager(), fragmentList));
        mainViewPager.setOffscreenPageLimit(navigationTextView.length);

        version = "";
        checkVersion();
        broadcastReceiver = null;
        updateNavigation(0);

    }

    @Override
    public void initEven() {

        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateNavigation(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        for (int i = 0; i < navigationTextView.length; i++) {
            final int position = i;
            navigationTextView[i].setOnClickListener(view -> updateNavigation(position));
        }

    }

    @Override
    public void onReturn() {

        if (System.currentTimeMillis() - exitTimeLong > BaseConstant.TIME_EXIT) {
            BaseSnackBar.get().show(mainViewPager, "????????????????????????...");
            exitTimeLong = System.currentTimeMillis();
        } else {
            BaseApplication.get().startHome(getActivity());
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        setSlideable(false);
        super.onCreate(bundle);
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
    public void onActivityResult(int req, int res, Intent intent) {
        super.onActivityResult(req, res, intent);
        if (res == RESULT_OK && req == BaseConstant.CODE_QRCODE) {
            handlerQRCode(intent.getStringExtra("result"));
        }
    }

    //???????????????

    private void checkVersion() {

        IndexModel.get().apkVersion(new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                version = JsonUtil.getDatasString(baseBean.getDatas(), "version");
                final String url = JsonUtil.getDatasString(baseBean.getDatas(), "url");
                if (!version.equals(BaseApplication.get().getVersion())) {
                    BaseDialog.get().query(getActivity(), "???????????????", "??????????????????", (dialog, which) -> {
                        BaseToast.get().show("????????????????????????...");
                        downloadApk(url);
                    }, (dialog, which) -> BaseToast.get().show("?????????????????? -> ???????????? ???????????????APP??????"));
                }
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainViewPager, reason);
            }
        });

    }

    private void downloadApk(String url) {

        RequestParams requestParams = new RequestParams(url);
        requestParams.setSaveFilePath(BaseFileClient.get().getDownPath());
        requestParams.setAutoRename(true);
        requestParams.setAutoRename(true);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);

        x.http().get(requestParams, new Callback.ProgressCallback<File>() {

            @Override
            public void onSuccess(File file) {
                BaseApplication.get().startInstallApk(getActivity(), file);
                progressDialog.dismiss();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                progressDialog.dismiss();
            }

            @Override
            public void onFinished() {
                progressDialog.dismiss();
            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {
                BaseToast.get().show("??????????????????...");
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMessage("???????????????...");
                progressDialog.show();
                progressDialog.setMax((int) total);
                progressDialog.setProgress((int) current);
                progressDialog.setProgressNumberFormat(" ");
            }
        });

    }

    private void handlerQRCode(String result) {

        if (result.contains(BaseConstant.URL)) {
            //??????
            if (result.contains("goods_id")) {
                String goodsId = result.substring(result.lastIndexOf("=") + 1, result.length());
                BaseApplication.get().startGoods(getActivity(), goodsId);
                return;
            }
            //??????
            if (result.contains("store_id")) {
                String storeId = result.substring(result.lastIndexOf("=") + 1, result.length());
                BaseApplication.get().startStore(getActivity(), storeId);
                return;
            }
        }

        if (TextUtil.isUrl(result)) {
            BaseApplication.get().startBrowser(getActivity(), result);
            return;
        }

        BaseToast.get().show(result);

    }

    private void updateNavigation(int position) {

        for (int i = 0; i < navigationTextView.length; i++) {
            navigationTextView[i].setTextColor(BaseApplication.get().getColors(R.color.grey));
            navigationTextView[i].setCompoundDrawablesWithIntrinsicBounds(null, navigationNormalDrawable[i], null, null);
        }

        navigationTextView[position].setTextColor(BaseApplication.get().getColors(R.color.primary));
        navigationTextView[position].setCompoundDrawablesWithIntrinsicBounds(null, navigationPressDrawable[position], null, null);
        mainViewPager.setCurrentItem(position);

    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onMainSearchEvent(MainPositionEvent event) {

        mainViewPager.setCurrentItem(event.getPosition());

    }

}
