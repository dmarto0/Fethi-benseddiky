package top.yokey.shopnc.activity.base;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import top.yokey.base.base.MemberHttpClient;
import top.yokey.shopnc.R;
import top.yokey.shopnc.activity.main.MainActivity;
import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseConstant;
import top.yokey.shopnc.base.BaseCountTime;
import top.yokey.base.base.BaseHttpListener;
import top.yokey.shopnc.base.BaseShared;
import top.yokey.base.base.BaseSnackBar;
import top.yokey.base.base.BaseToast;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.model.ConnectModel;
import top.yokey.base.model.LoginModel;
import top.yokey.base.util.JsonUtil;
import top.yokey.base.util.TextUtil;
import top.yokey.shopnc.view.CenterTextView;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class RegisterActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private LinearLayoutCompat typeLinearLayout;
    private CenterTextView normalTextView;
    private CenterTextView mobileTextView;
    private LinearLayoutCompat normalLinearLayout;
    private AppCompatEditText usernameEditText;
    private AppCompatEditText passwordEditText;
    private AppCompatEditText confirmEditText;
    private AppCompatEditText emailEditText;
    private AppCompatTextView registerTextView;
    private LinearLayoutCompat mobileLinearLayout;
    private AppCompatEditText mobileEditText;
    private AppCompatTextView getTextView;
    private AppCompatEditText codeEditText;
    private AppCompatEditText passwordSmsEditText;
    private AppCompatTextView completeTextView;

    private long exitTimeLong;
    private Drawable normalDrawable, normalPressDrawable;
    private Drawable mobileDrawable, mobilePressDrawable;

    @Override
    public void initView() {

        setContentView(R.layout.activity_base_register);
        mainToolbar = findViewById(R.id.mainToolbar);
        typeLinearLayout = findViewById(R.id.typeLinearLayout);
        normalTextView = findViewById(R.id.normalTextView);
        mobileTextView = findViewById(R.id.mobileTextView);
        normalLinearLayout = findViewById(R.id.normalLinearLayout);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmEditText = findViewById(R.id.confirmEditText);
        emailEditText = findViewById(R.id.emailEditText);
        registerTextView = findViewById(R.id.registerTextView);
        mobileLinearLayout = findViewById(R.id.mobileLinearLayout);
        mobileEditText = findViewById(R.id.mobileEditText);
        getTextView = findViewById(R.id.getTextView);
        codeEditText = findViewById(R.id.codeEditText);
        passwordSmsEditText = findViewById(R.id.passwordSmsEditText);
        completeTextView = findViewById(R.id.completeTextView);

    }

    @Override
    public void initData() {

        exitTimeLong = 0L;
        normalDrawable = BaseApplication.get().getMipmap(R.mipmap.ic_register_normal, R.color.greyAdd);
        mobileDrawable = BaseApplication.get().getMipmap(R.mipmap.ic_register_mobile, R.color.greyAdd);
        normalPressDrawable = BaseApplication.get().getMipmap(R.mipmap.ic_register_normal_press);
        mobilePressDrawable = BaseApplication.get().getMipmap(R.mipmap.ic_register_mobile_press);

        setToolbar(mainToolbar, "会员注册");
        getState();

    }

    @Override
    public void initEven() {

        normalTextView.setOnClickListener(view -> {
            normalTextView.setTextColor(BaseApplication.get().getColors(R.color.primary));
            normalTextView.setCompoundDrawablesWithIntrinsicBounds(normalPressDrawable, null, null, null);
            mobileTextView.setTextColor(BaseApplication.get().getColors(R.color.greyAdd));
            mobileTextView.setCompoundDrawablesWithIntrinsicBounds(mobileDrawable, null, null, null);
            normalLinearLayout.setVisibility(View.VISIBLE);
            mobileLinearLayout.setVisibility(View.GONE);
        });

        mobileTextView.setOnClickListener(view -> {
            normalTextView.setTextColor(BaseApplication.get().getColors(R.color.greyAdd));
            normalTextView.setCompoundDrawablesWithIntrinsicBounds(normalDrawable, null, null, null);
            mobileTextView.setTextColor(BaseApplication.get().getColors(R.color.primary));
            mobileTextView.setCompoundDrawablesWithIntrinsicBounds(mobilePressDrawable, null, null, null);
            normalLinearLayout.setVisibility(View.GONE);
            mobileLinearLayout.setVisibility(View.VISIBLE);
        });

        registerTextView.setOnClickListener(view -> register());

        getTextView.setOnClickListener(view -> getCode());

        completeTextView.setOnClickListener(view -> checkCode());

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

    //自定义方法

    private void getState() {

        BaseSnackBar.get().showHandler(mainToolbar);

        ConnectModel.get().getState("connect_sms_reg", new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                if (JsonUtil.isSuccess(baseBean.getDatas())) {
                    normalTextView.setTextColor(BaseApplication.get().getColors(R.color.primary));
                    normalTextView.setCompoundDrawablesWithIntrinsicBounds(normalPressDrawable, null, null, null);
                    mobileTextView.setTextColor(BaseApplication.get().getColors(R.color.greyAdd));
                    mobileTextView.setCompoundDrawablesWithIntrinsicBounds(mobileDrawable, null, null, null);
                    typeLinearLayout.setVisibility(View.VISIBLE);
                    normalLinearLayout.setVisibility(View.VISIBLE);
                    mobileLinearLayout.setVisibility(View.GONE);
                } else {
                    typeLinearLayout.setVisibility(View.GONE);
                    normalLinearLayout.setVisibility(View.VISIBLE);
                    mobileLinearLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainToolbar, reason);
            }
        });

    }

    private void register() {

        BaseApplication.get().hideKeyboard(getActivity());

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirm = confirmEditText.getText().toString();
        String email = emailEditText.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirm) || TextUtils.isEmpty(email)) {
            BaseSnackBar.get().show(mainToolbar, "请填写完所有的信息...");
            return;
        }

        if (!password.equals(confirm)) {
            BaseSnackBar.get().show(mainToolbar, "两次输入的密码不一致...");
            return;
        }

        if (!TextUtil.isEmail(email)) {
            BaseSnackBar.get().show(mainToolbar, "邮箱地址格式不正确...");
            return;
        }

        registerTextView.setEnabled(false);
        registerTextView.setText("注册中...");

        LoginModel.get().register(username, password, email, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                registerTextView.setEnabled(true);
                registerTextView.setText("注册账号");
                BaseToast.get().show("注册成功！");
                MemberHttpClient.get().updateKey(JsonUtil.getDatasString(baseBean.getDatas(), "key"));
                BaseShared.get().putString(BaseConstant.SHARED_KEY, JsonUtil.getDatasString(baseBean.getDatas(), "key"));
                BaseApplication.get().start(getActivity(), MainActivity.class);
                BaseApplication.get().finish(getActivity());
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainToolbar, reason);
                registerTextView.setText("注册账号");
                registerTextView.setEnabled(true);
            }
        });

    }

    private void getCode() {

        BaseApplication.get().hideKeyboard(getActivity());

        String mobile = mobileEditText.getText().toString();

        if (!TextUtil.isMobile(mobile)) {
            BaseSnackBar.get().show(mainToolbar, "手机号码格式不正确！");
            return;
        }

        getTextView.setEnabled(false);
        getTextView.setText("获取中...");

        ConnectModel.get().getSmsCaptcha("1", mobile, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                final String smsTime = JsonUtil.getDatasString(baseBean.getDatas(), "sms_time");
                final int time = Integer.parseInt(smsTime);
                //倒计时
                new BaseCountTime(time * 1000, BaseConstant.TIME_TICK) {

                    int totalTime = time;

                    @Override
                    public void onTick(long millis) {
                        super.onTick(millis);
                        String temp = "再次获取（" + totalTime-- + " S ）";
                        getTextView.setText(temp);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        getTextView.setEnabled(true);
                        getTextView.setText("获取验证码");
                    }

                }.start();

            }

            @Override
            public void onFailure(String reason) {
                getTextView.setEnabled(true);
                getTextView.setText("获取验证码");
                BaseSnackBar.get().show(mainToolbar, reason);
            }
        });

    }

    private void checkCode() {

        BaseApplication.get().hideKeyboard(getActivity());

        String captcha = codeEditText.getText().toString();
        String mobile = mobileEditText.getText().toString();

        if (TextUtils.isEmpty(captcha)) {
            BaseSnackBar.get().show(mainToolbar, "请输入验证码！");
            return;
        }

        completeTextView.setEnabled(false);
        completeTextView.setText("处理中...");

        ConnectModel.get().checkSmsCaptcha("1", mobile, captcha, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                if (JsonUtil.isSuccess(baseBean.getDatas())) {
                    smsRegister();
                } else {
                    completeTextView.setEnabled(true);
                    completeTextView.setText("完成注册");
                    BaseSnackBar.get().showFailure(mainToolbar);
                }
            }

            @Override
            public void onFailure(String reason) {
                completeTextView.setEnabled(true);
                completeTextView.setText("完成注册");
                BaseSnackBar.get().show(mainToolbar, reason);
            }
        });

    }

    private void smsRegister() {

        String captcha = codeEditText.getText().toString();
        String mobile = mobileEditText.getText().toString();
        String password = passwordSmsEditText.getText().toString();

        if (TextUtils.isEmpty(password)) {
            BaseSnackBar.get().show(mainToolbar, "请输入账户密码！");
            return;
        }

        ConnectModel.get().smsRegister(mobile, captcha, password, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                completeTextView.setEnabled(true);
                completeTextView.setText("完成注册");
                BaseToast.get().show("注册成功！");
                MemberHttpClient.get().updateKey(JsonUtil.getDatasString(baseBean.getDatas(), "key"));
                BaseShared.get().putString(BaseConstant.SHARED_KEY, JsonUtil.getDatasString(baseBean.getDatas(), "key"));
                BaseApplication.get().start(getActivity(), MainActivity.class);
                BaseApplication.get().finish(getActivity());
            }

            @Override
            public void onFailure(String reason) {
                completeTextView.setEnabled(true);
                completeTextView.setText("完成注册");
                BaseSnackBar.get().show(mainToolbar, reason);
            }
        });

    }

}
