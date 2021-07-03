package top.yokey.shopnc.activity.mine;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import top.yokey.shopnc.R;
import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseConstant;
import top.yokey.shopnc.base.BaseCountTime;
import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseSnackBar;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.model.MemberAccountModel;
import top.yokey.base.model.SeccodeModel;
import top.yokey.base.util.JsonUtil;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class ModifyMobileActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private LinearLayoutCompat mobileLinearLayout;
    private AppCompatTextView mobileTextView;
    private AppCompatEditText codeEditText;
    private AppCompatTextView getTextView;
    private AppCompatTextView submitTextView;
    private AppCompatTextView payPassTextView;
    private LinearLayoutCompat payPassLinearLayout;
    private AppCompatEditText payPassEditText;
    private AppCompatTextView confirmTextView;

    private String codeKeyString;

    @Override
    public void initView() {

        setContentView(R.layout.activity_mine_modify_mobile);
        mainToolbar = findViewById(R.id.mainToolbar);
        mobileLinearLayout = findViewById(R.id.mobileLinearLayout);
        mobileTextView = findViewById(R.id.mobileTextView);
        codeEditText = findViewById(R.id.codeEditText);
        getTextView = findViewById(R.id.getTextView);
        submitTextView = findViewById(R.id.submitTextView);
        payPassTextView = findViewById(R.id.payPassTextView);
        payPassLinearLayout = findViewById(R.id.payPassLinearLayout);
        payPassEditText = findViewById(R.id.payPassEditText);
        confirmTextView = findViewById(R.id.confirmTextView);

    }

    @Override
    public void initData() {

        mobileTextView.setText("您的手机号码为：");
        mobileTextView.append(BaseApplication.get().getMemberBean().getUserMobile());

        setToolbar(mainToolbar, "修改手机验证");

        codeKeyString = "";
        makeCodeKey();

    }

    @Override
    public void initEven() {

        getTextView.setOnClickListener(view -> modifyMobileSetup2());

        submitTextView.setOnClickListener(view -> modifyMobileSetup3());

        payPassTextView.setOnClickListener(view -> checkPayPass());

        confirmTextView.setOnClickListener(view -> checkPayPassword());

    }

    @Override
    public void onReturn() {
        if (payPassLinearLayout.getVisibility() == View.VISIBLE) {
            payPassLinearLayout.setVisibility(View.GONE);
            mobileLinearLayout.setVisibility(View.VISIBLE);
        } else {
            super.onReturn();
        }
    }

    //自定义方法

    private void makeCodeKey() {

        SeccodeModel.get().makeCodeKey(new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                codeKeyString = JsonUtil.getDatasString(baseBean.getDatas(), "codekey");
            }

            @Override
            public void onFailure(String reason) {
                new BaseCountTime(BaseConstant.TIME_COUNT, BaseConstant.TIME_TICK) {
                    @Override
                    public void onFinish() {
                        super.onFinish();
                        makeCodeKey();
                    }
                }.start();
            }
        });

    }

    private void checkPayPass() {

        BaseSnackBar.get().showHandler(mainToolbar);

        MemberAccountModel.get().getPayPwdInfo(new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                boolean state = JsonUtil.getDatasBoolean(baseBean.getDatas(), "state");
                if (state) {
                    mobileLinearLayout.setVisibility(View.GONE);
                    payPassLinearLayout.setVisibility(View.VISIBLE);
                } else {
                    BaseSnackBar.get().showFailure(mainToolbar);
                }
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainToolbar, reason);
            }
        });

    }

    private void checkPayPassword() {

        BaseApplication.get().hideKeyboard(getActivity());

        String password = payPassEditText.getText().toString();

        if (TextUtils.isEmpty(password)) {
            BaseSnackBar.get().show(mainToolbar, "请输入支付密码");
            return;
        }

        confirmTextView.setEnabled(false);
        confirmTextView.setText("处理中...");

        MemberAccountModel.get().checkPayPwd(password, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                confirmTextView.setText("确 认");
                confirmTextView.setEnabled(false);
                BaseApplication.get().getMemberBean().setMobielState(false);
                BaseApplication.get().getMemberBean().setUserMobile("");
                BaseApplication.get().start(getActivity(), BindMobileActivity.class);
                BaseApplication.get().finish(getActivity());
            }

            @Override
            public void onFailure(String reason) {
                confirmTextView.setEnabled(false);
                confirmTextView.setText("确 认");
            }
        });

    }

    private void modifyMobileSetup2() {

        BaseApplication.get().hideKeyboard(getActivity());

        getTextView.setEnabled(false);
        getTextView.setText("获取中...");

        MemberAccountModel.get().modifyMobileStep2(codeKeyString, new BaseHttpListener() {
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

    private void modifyMobileSetup3() {

        BaseApplication.get().hideKeyboard(getActivity());

        String code = codeEditText.getText().toString();

        if (TextUtils.isEmpty(code)) {
            BaseSnackBar.get().show(mainToolbar, "请输入验证码！");
            return;
        }

        submitTextView.setEnabled(false);
        submitTextView.setText("处理中...");

        MemberAccountModel.get().modifyMobileStep3(code, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                if (JsonUtil.isSuccess(baseBean.getDatas())) {
                    BaseApplication.get().getMemberBean().setMobielState(false);
                    BaseApplication.get().getMemberBean().setUserMobile("");
                    BaseApplication.get().start(getActivity(), BindMobileActivity.class);
                    BaseApplication.get().finish(getActivity());
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

}
