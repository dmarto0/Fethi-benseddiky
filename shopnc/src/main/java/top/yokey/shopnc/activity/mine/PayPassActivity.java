package top.yokey.shopnc.activity.mine;

import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import top.yokey.shopnc.R;
import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseConstant;
import top.yokey.shopnc.base.BaseCountTime;
import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseSnackBar;
import top.yokey.base.base.BaseToast;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.model.MemberAccountModel;
import top.yokey.base.util.JsonUtil;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class PayPassActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private AppCompatTextView mobileTextView;
    private AppCompatEditText codeEditText;
    private AppCompatTextView getTextView;
    private AppCompatEditText passwordEditText;
    private AppCompatEditText confirmEditText;
    private AppCompatTextView submitTextView;

    @Override
    public void initView() {

        setContentView(R.layout.activity_mine_pay_pass);
        mainToolbar = findViewById(R.id.mainToolbar);
        mobileTextView = findViewById(R.id.mobileTextView);
        codeEditText = findViewById(R.id.codeEditText);
        getTextView = findViewById(R.id.getTextView);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmEditText = findViewById(R.id.confirmEditText);
        submitTextView = findViewById(R.id.submitTextView);

    }

    @Override
    public void initData() {

        mobileTextView.setText("您的手机号码为：");
        mobileTextView.append(BaseApplication.get().getMemberBean().getUserMobile());

        setToolbar(mainToolbar, "修改支付密码");

    }

    @Override
    public void initEven() {

        getTextView.setOnClickListener(view -> modifyPayPassSetup2());

        submitTextView.setOnClickListener(view -> modifyPayPassSetup3());

    }

    //自定义方法

    private void modifyPayPassSetup2() {

        BaseApplication.get().hideKeyboard(getActivity());

        getTextView.setEnabled(false);
        getTextView.setText("获取中...");

        MemberAccountModel.get().modifyPayPassSetup2(new BaseHttpListener() {
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

    private void modifyPayPassSetup3() {

        BaseApplication.get().hideKeyboard(getActivity());

        String code = codeEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirm = confirmEditText.getText().toString();

        if (TextUtils.isEmpty(code)) {
            BaseSnackBar.get().show(mainToolbar, "请输入验证码！");
            return;
        }

        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirm)) {
            BaseSnackBar.get().show(mainToolbar, "请输入密码！");
            return;
        }

        if (!password.equals(confirm)) {
            BaseSnackBar.get().show(mainToolbar, "两次输入的密码不一致！");
            return;
        }

        submitTextView.setEnabled(false);
        submitTextView.setText("处理中...");

        MemberAccountModel.get().modifyPayPassSetup3(code, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                if (JsonUtil.isSuccess(baseBean.getDatas())) {
                    modifyPayPassSetup4();
                } else {
                    submitTextView.setEnabled(true);
                    submitTextView.setText("提 交");
                    BaseSnackBar.get().showFailure(mainToolbar);
                }
            }

            @Override
            public void onFailure(String reason) {
                submitTextView.setEnabled(true);
                submitTextView.setText("提 交");
                BaseSnackBar.get().show(mainToolbar, reason);
            }
        });

    }

    private void modifyPayPassSetup4() {

        MemberAccountModel.get().modifyPayPassSetup4(new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                if (JsonUtil.isSuccess(baseBean.getDatas())) {
                    modifyPayPassSetup5();
                } else {
                    submitTextView.setEnabled(true);
                    submitTextView.setText("提 交");
                    BaseSnackBar.get().showFailure(mainToolbar);
                }
            }

            @Override
            public void onFailure(String reason) {
                submitTextView.setEnabled(true);
                submitTextView.setText("提 交");
                BaseSnackBar.get().show(mainToolbar, reason);
            }
        });

    }

    private void modifyPayPassSetup5() {

        MemberAccountModel.get().modifyPayPassSetup5(passwordEditText.getText().toString(), new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                submitTextView.setEnabled(true);
                submitTextView.setText("提 交");
                if (JsonUtil.isSuccess(baseBean.getDatas())) {
                    BaseToast.get().showSuccess();
                    BaseApplication.get().finish(getActivity());
                } else {
                    BaseSnackBar.get().showFailure(mainToolbar);
                }
            }

            @Override
            public void onFailure(String reason) {
                submitTextView.setEnabled(true);
                submitTextView.setText("提 交");
                BaseSnackBar.get().show(mainToolbar, reason);
            }
        });

    }

}
