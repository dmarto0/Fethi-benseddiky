package top.yokey.shopnc.activity.base;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import top.yokey.base.base.MemberHttpClient;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseSnackBar;
import top.yokey.base.base.BaseToast;
import top.yokey.base.util.JsonUtil;
import top.yokey.base.util.TextUtil;
import top.yokey.shopnc.R;
import top.yokey.shopnc.activity.main.MainActivity;
import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseConstant;
import top.yokey.shopnc.base.BaseCountTime;
import top.yokey.shopnc.base.BaseShared;
import top.yokey.base.model.ConnectModel;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class FindPassActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private AppCompatEditText mobileEditText;
    private AppCompatTextView getTextView;
    private AppCompatEditText codeEditText;
    private AppCompatEditText passwordEditText;
    private AppCompatTextView findPassTextView;

    private long exitTimeLong;

    @Override
    public void initView() {

        setContentView(R.layout.activity_base_find_pass);
        mainToolbar = findViewById(R.id.mainToolbar);
        mobileEditText = findViewById(R.id.mobileEditText);
        getTextView = findViewById(R.id.getTextView);
        codeEditText = findViewById(R.id.codeEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        findPassTextView = findViewById(R.id.findPassTextView);

    }

    @Override
    public void initData() {

        exitTimeLong = 0L;

        setToolbar(mainToolbar, "找回密码");

    }

    @Override
    public void initEven() {

        getTextView.setOnClickListener(view -> getCode());

        findPassTextView.setOnClickListener(view -> checkCode());

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

    private void getCode() {

        BaseApplication.get().hideKeyboard(getActivity());

        String mobile = mobileEditText.getText().toString();

        if (!TextUtil.isMobile(mobile)) {
            BaseSnackBar.get().show(mainToolbar, "手机号码格式不正确！");
            return;
        }

        getTextView.setEnabled(false);
        getTextView.setText("获取中...");

        ConnectModel.get().getSmsCaptcha("3", mobile, new BaseHttpListener() {
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

        findPassTextView.setEnabled(false);
        findPassTextView.setText("处理中...");

        ConnectModel.get().checkSmsCaptcha("3", mobile, captcha, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                if (JsonUtil.isSuccess(baseBean.getDatas())) {
                    findPassword();
                } else {
                    findPassTextView.setEnabled(true);
                    findPassTextView.setText("找回密码");
                    BaseSnackBar.get().showFailure(mainToolbar);
                }
            }

            @Override
            public void onFailure(String reason) {
                findPassTextView.setEnabled(true);
                findPassTextView.setText("找回密码");
                BaseSnackBar.get().show(mainToolbar, reason);
            }
        });

    }

    private void findPassword() {

        String captcha = codeEditText.getText().toString();
        String mobile = mobileEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(password)) {
            BaseSnackBar.get().show(mainToolbar, "请输入账户密码！");
            return;
        }

        ConnectModel.get().findPassword(mobile, captcha, password, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                findPassTextView.setEnabled(true);
                findPassTextView.setText("找回密码");
                BaseToast.get().show("找回成功！");
                MemberHttpClient.get().updateKey(JsonUtil.getDatasString(baseBean.getDatas(), "key"));
                BaseShared.get().putString(BaseConstant.SHARED_KEY, JsonUtil.getDatasString(baseBean.getDatas(), "key"));
                BaseApplication.get().start(getActivity(), MainActivity.class);
                BaseApplication.get().finish(getActivity());
            }

            @Override
            public void onFailure(String reason) {
                findPassTextView.setEnabled(true);
                findPassTextView.setText("找回密码");
                BaseSnackBar.get().show(mainToolbar, reason);
            }
        });

    }

}
