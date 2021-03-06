package top.yokey.shopnc.activity.base;

import android.content.Intent;
import android.text.TextUtils;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseSnackBar;
import top.yokey.base.base.BaseToast;
import top.yokey.base.base.MemberHttpClient;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.model.LoginModel;
import top.yokey.base.util.JsonUtil;
import top.yokey.base.util.TextUtil;
import top.yokey.shopnc.R;
import top.yokey.shopnc.activity.main.MainActivity;
import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseConstant;
import top.yokey.shopnc.base.BaseShared;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class LoginActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private AppCompatEditText usernameEditText;
    private AppCompatEditText passwordEditText;
    private AppCompatTextView registerTextView;
    private AppCompatTextView findPassTextView;
    private AppCompatTextView loginTextView;
    private AppCompatImageView qqImageView;
    private AppCompatImageView wxImageView;
    private AppCompatImageView wbImageView;

    private long exitTimeLong;

    @Override
    public void initView() {

        setContentView(R.layout.activity_base_login);
        mainToolbar = findViewById(R.id.mainToolbar);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerTextView = findViewById(R.id.registerTextView);
        findPassTextView = findViewById(R.id.findPassTextView);
        loginTextView = findViewById(R.id.loginTextView);
        qqImageView = findViewById(R.id.qqImageView);
        wxImageView = findViewById(R.id.wxImageView);
        wbImageView = findViewById(R.id.wbImageView);

    }

    @Override
    public void initData() {

        exitTimeLong = 0L;

        setToolbar(mainToolbar, "??????");

    }

    @Override
    public void initEven() {

        registerTextView.setOnClickListener(view -> BaseApplication.get().start(getActivity(), RegisterActivity.class));

        findPassTextView.setOnClickListener(view -> BaseApplication.get().start(getActivity(), FindPassActivity.class));

        loginTextView.setOnClickListener(view -> login());

        qqImageView.setOnClickListener(view -> {

        });

        wxImageView.setOnClickListener(view -> {

        });

        wbImageView.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), LoginWebActivity.class);
            intent.putExtra(BaseConstant.DATA_URL, BaseConstant.URL_LOGIN_WB);
            BaseApplication.get().start(getActivity(), intent, BaseConstant.CODE_LOGIN);
        });

    }

    @Override
    public void onReturn() {

        if (System.currentTimeMillis() - exitTimeLong > BaseConstant.TIME_EXIT) {
            BaseSnackBar.get().showClickReturn(mainToolbar);
            exitTimeLong = System.currentTimeMillis();
        } else {
            super.onReturn();
        }

    }

    @Override
    public void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (res == RESULT_OK) {
            switch (req) {
                case BaseConstant.CODE_LOGIN:
                    BaseToast.get().show("???????????????");
                    String key = data.getStringExtra(BaseConstant.DATA_KEY);
                    MemberHttpClient.get().updateKey(key);
                    BaseShared.get().putString(BaseConstant.SHARED_KEY, key);
                    BaseApplication.get().start(getActivity(), MainActivity.class);
                    BaseApplication.get().finish(getActivity());
                    break;
                default:
                    break;
            }
        }
    }

    //???????????????

    private void login() {

        BaseApplication.get().hideKeyboard(getActivity());

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtil.isEmail(password)) {
            BaseSnackBar.get().show(mainToolbar, "???????????????????????????");
            return;
        }

        loginTextView.setEnabled(false);
        loginTextView.setText("?????????...");

        LoginModel.get().index(username, password, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                loginTextView.setEnabled(true);
                loginTextView.setText("??? ???");
                BaseToast.get().show("???????????????");
                MemberHttpClient.get().updateKey(JsonUtil.getDatasString(baseBean.getDatas(), "key"));
                BaseShared.get().putString(BaseConstant.SHARED_KEY, JsonUtil.getDatasString(baseBean.getDatas(), "key"));
                BaseApplication.get().start(getActivity(), MainActivity.class);
                BaseApplication.get().finish(getActivity());
            }

            @Override
            public void onFailure(String reason) {
                loginTextView.setEnabled(true);
                loginTextView.setText("??? ???");
                BaseSnackBar.get().show(mainToolbar, reason);
            }
        });

    }

}
