package top.yokey.shopnc.activity.seller;

import android.text.TextUtils;

import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseSnackBar;
import top.yokey.base.base.BaseToast;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.model.SellerOrderModel;
import top.yokey.shopnc.R;
import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseConstant;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class OrderCancelActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private AppCompatTextView moneyTextView;
    private AppCompatTextView snTextView;
    private AppCompatRadioButton oneRadioButton;
    private AppCompatRadioButton twoRadioButton;
    private AppCompatRadioButton thrRadioButton;
    private AppCompatRadioButton fouRadioButton;
    private AppCompatTextView submitTextView;

    private String orderId;
    private String reason;

    @Override
    public void initView() {

        setContentView(R.layout.activity_seller_order_cancel);
        mainToolbar = findViewById(R.id.mainToolbar);
        moneyTextView = findViewById(R.id.moneyTextView);
        snTextView = findViewById(R.id.snTextView);
        oneRadioButton = findViewById(R.id.oneRadioButton);
        twoRadioButton = findViewById(R.id.twoRadioButton);
        thrRadioButton = findViewById(R.id.thrRadioButton);
        fouRadioButton = findViewById(R.id.fouRadioButton);
        submitTextView = findViewById(R.id.submitTextView);

    }

    @Override
    public void initData() {

        orderId = getIntent().getStringExtra(BaseConstant.DATA_ID);
        if (TextUtils.isEmpty(orderId)) {
            BaseToast.get().showDataError();
            BaseApplication.get().finish(getActivity());
        }

        setToolbar(mainToolbar, "????????????");

        reason = "??????????????????";
        moneyTextView.setText("???");
        moneyTextView.append(getIntent().getStringExtra(BaseConstant.DATA_CONTENT));
        snTextView.setText("???????????????");
        snTextView.append(getIntent().getStringExtra(BaseConstant.DATA_SN));

    }

    @Override
    public void initEven() {

        oneRadioButton.setOnClickListener(view -> {
            reason = "??????????????????";
            oneRadioButton.setChecked(true);
            twoRadioButton.setChecked(false);
            thrRadioButton.setChecked(false);
            fouRadioButton.setChecked(false);
        });

        twoRadioButton.setOnClickListener(view -> {
            reason = "?????????????????????";
            oneRadioButton.setChecked(false);
            twoRadioButton.setChecked(true);
            thrRadioButton.setChecked(false);
            fouRadioButton.setChecked(false);
        });

        thrRadioButton.setOnClickListener(view -> {
            reason = "??????????????????";
            oneRadioButton.setChecked(false);
            twoRadioButton.setChecked(false);
            thrRadioButton.setChecked(true);
            fouRadioButton.setChecked(false);
        });

        fouRadioButton.setOnClickListener(view -> {
            reason = "????????????";
            oneRadioButton.setChecked(false);
            twoRadioButton.setChecked(false);
            thrRadioButton.setChecked(false);
            fouRadioButton.setChecked(true);
        });

        submitTextView.setOnClickListener(view -> cancel());

    }

    //???????????????

    private void cancel() {

        submitTextView.setEnabled(false);
        submitTextView.setText("?????????...");

        SellerOrderModel.get().orderCancel(orderId, reason, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                BaseToast.get().show("????????????");
                BaseApplication.get().finish(getActivity());
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainToolbar, reason);
                submitTextView.setEnabled(true);
                submitTextView.setText("??????");
            }
        });

    }

}
