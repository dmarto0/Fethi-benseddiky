package top.yokey.shopnc.activity.base;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;

import top.yokey.shopnc.R;
import top.yokey.shopnc.activity.goods.GoodsActivity;
import top.yokey.shopnc.activity.main.MainActivity;
import top.yokey.shopnc.activity.store.StoreActivity;
import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseConstant;
import top.yokey.shopnc.base.BaseCountTime;
import top.yokey.base.base.BaseSnackBar;
import top.yokey.base.base.BaseToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class LoadActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private LinearLayoutCompat mainLinearLayout;

    private long exitTimeLong;
    private String[] permissions;
    private boolean checkBoolean;

    @Override
    public void initView() {

        setContentView(R.layout.activity_base_load);
        mainLinearLayout = findViewById(R.id.mainLinearLayout);

    }

    @Override
    public void initData() {

        exitTimeLong = 0L;
        checkBoolean = true;

        permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };

        BaseApplication.get().setFullScreen(getActivity());

        try {
            Intent intent = getIntent();
            Uri uri = intent.getData();
            String goodsId = Objects.requireNonNull(uri).getQueryParameter("goods_id");
            String storeId = uri.getQueryParameter("store_id");
            if (!TextUtils.isEmpty(goodsId)) {
                intent = new Intent(getActivity(), GoodsActivity.class);
                intent.putExtra(BaseConstant.DATA_ID, goodsId);
                if (BaseApplication.get().isLaunchedActivity(this, GoodsActivity.class)) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    TaskStackBuilder.create(this).addParentStack(intent.getComponent())
                            .addNextIntent(intent)
                            .startActivities();
                }
                BaseApplication.get().finish(getActivity());
            }
            if (!TextUtils.isEmpty(storeId)) {
                intent = new Intent(getActivity(), StoreActivity.class);
                intent.putExtra(BaseConstant.DATA_ID, storeId);
                if (BaseApplication.get().isLaunchedActivity(this, GoodsActivity.class)) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    TaskStackBuilder.create(this).addParentStack(intent.getComponent())
                            .addNextIntent(intent)
                            .startActivities();
                }
                BaseApplication.get().finish(getActivity());
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initEven() {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkBoolean) {
            checkPermissions(permissions);
        }
    }

    @Override
    public void onReturn() {

        if (System.currentTimeMillis() - exitTimeLong > BaseConstant.TIME_EXIT) {
            BaseSnackBar.get().showClickReturn(mainLinearLayout);
            exitTimeLong = System.currentTimeMillis();
        } else {
            BaseApplication.get().startHome(getActivity());
        }

    }

    @Override
    public void onCreate(Bundle bundle) {
        setSlideable(false);
        super.onCreate(bundle);
    }

    //权限检查

    private void checkPermissions(String... strings) {

        List<String> list = findPermissions(strings);

        if (null != list && list.size() > 0) {
            ActivityCompat.requestPermissions(this, list.toArray(new String[list.size()]), 0);
        } else {
            startMain();
        }

    }

    private boolean verifyPermissions(int[] results) {

        for (int result : results) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;

    }

    private List<String> findPermissions(String[] strings) {

        List<String> list = new ArrayList<>();

        for (String perm : strings) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                list.add(perm);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, perm)) {
                    list.add(perm);
                }
            }
        }

        return list;

    }

    @Override
    public void onRequestPermissionsResult(int code, @NonNull String[] per, @NonNull int[] param) {

        if (code == RESULT_OK || verifyPermissions(param)) {
            checkBoolean = false;
            startMain();
            return;
        }

        checkBoolean = true;
        BaseToast.get().show("缺少运行时权限");
        BaseApplication.get().startApplicationSetting(getActivity(), getPackageName());

    }

    //自定义方法

    private void startMain() {

        new BaseCountTime(BaseConstant.TIME_TICK, BaseConstant.TIME_TICK) {
            @Override
            public void onFinish() {
                super.onFinish();
                BaseApplication.get().start(getActivity(), MainActivity.class);
                BaseApplication.get().finish(getActivity());
            }
        }.start();

    }

}
