package top.yokey.shopnc.activity.mine;

import android.app.ProgressDialog;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

import top.yokey.base.base.BaseDialog;
import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseSnackBar;
import top.yokey.base.base.BaseToast;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.model.IndexModel;
import top.yokey.base.util.JsonUtil;
import top.yokey.shopnc.R;
import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseConstant;
import top.yokey.shopnc.base.BaseFileClient;
import top.yokey.shopnc.base.BaseShared;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class SettingActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private RelativeLayout pushRelativeLayout;
    private SwitchCompat pushSwitch;
    private RelativeLayout imageRelativeLayout;
    private SwitchCompat imageSwitch;
    private RelativeLayout updateRelativeLayout;
    private AppCompatTextView updateTextView;
    private RelativeLayout aboutRelativeLayout;

    private String url;
    private ProgressDialog progressDialog;

    @Override
    public void initView() {

        setContentView(R.layout.activity_mine_setting);
        mainToolbar = findViewById(R.id.mainToolbar);
        pushRelativeLayout = findViewById(R.id.pushRelativeLayout);
        pushSwitch = findViewById(R.id.pushSwitch);
        imageRelativeLayout = findViewById(R.id.imageRelativeLayout);
        imageSwitch = findViewById(R.id.imageSwitch);
        updateRelativeLayout = findViewById(R.id.updateRelativeLayout);
        updateTextView = findViewById(R.id.updateTextView);
        aboutRelativeLayout = findViewById(R.id.aboutRelativeLayout);

    }

    @Override
    public void initData() {

        setToolbar(mainToolbar, "????????????");

        url = "";
        pushSwitch.setChecked(BaseApplication.get().isPush());
        imageSwitch.setChecked(BaseApplication.get().isImage());
        checkVersion();

    }

    @Override
    public void initEven() {

        pushRelativeLayout.setOnClickListener(view -> pushSwitch.setChecked(!pushSwitch.isChecked()));

        imageRelativeLayout.setOnClickListener(view -> imageSwitch.setChecked(!imageSwitch.isChecked()));

        pushSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            BaseApplication.get().setPush(isChecked);
            BaseShared.get().putBoolean(BaseConstant.SHARED_SETTING_PUSH, isChecked);
        });

        imageSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            BaseApplication.get().setImage(isChecked);
            BaseShared.get().putBoolean(BaseConstant.SHARED_SETTING_IMAGE, isChecked);
        });

        updateRelativeLayout.setOnClickListener(view -> {
            if (updateTextView.getText().equals("??????????????????")) {
                BaseSnackBar.get().show(mainToolbar, "??????????????????");
                return;
            }
            BaseToast.get().show("????????????????????????...");
            downloadApk(url);
        });

        aboutRelativeLayout.setOnClickListener(view -> BaseDialog.get().query(getActivity(), "????????????", "??????????????????MapStory?????????QQ???1002285057???????????????492184679?????????????????????????????????????????????????????????????????????", null, null));

    }

    //???????????????

    private void checkVersion() {

        IndexModel.get().apkVersion(new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                url = JsonUtil.getDatasString(baseBean.getDatas(), "url");
                String version = JsonUtil.getDatasString(baseBean.getDatas(), "version");
                if (!version.equals(BaseApplication.get().getVersion())) {
                    updateTextView.setText("????????????");
                    updateTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.primary));
                } else {
                    updateTextView.setText("??????????????????");
                }
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainToolbar, reason);
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

}
