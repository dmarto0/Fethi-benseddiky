package top.yokey.shopnc.activity.seller;

import android.content.Intent;
import android.text.TextUtils;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseSnackBar;
import top.yokey.base.base.BaseToast;
import top.yokey.base.bean.AddressSellerBean;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.model.SellerAddressModel;
import top.yokey.base.util.TextUtil;
import top.yokey.shopnc.R;
import top.yokey.shopnc.activity.choose.AreaActivity;
import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseConstant;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class AddressEditActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private AppCompatEditText nameEditText;
    private AppCompatEditText mobileEditText;
    private AppCompatEditText areaEditText;
    private AppCompatEditText addressEditText;
    private SwitchCompat defaultSwitch;
    private AppCompatTextView saveTextView;

    private String cityId;
    private String areaId;
    private String areaInfo;
    private AddressSellerBean addressBean;

    @Override
    public void initView() {

        setContentView(R.layout.activity_seller_address_edit);
        mainToolbar = findViewById(R.id.mainToolbar);
        nameEditText = findViewById(R.id.nameEditText);
        mobileEditText = findViewById(R.id.mobileEditText);
        areaEditText = findViewById(R.id.areaEditText);
        addressEditText = findViewById(R.id.addressEditText);
        defaultSwitch = findViewById(R.id.defaultSwitch);
        saveTextView = findViewById(R.id.saveTextView);

    }

    @Override
    public void initData() {

        addressBean = (AddressSellerBean) getIntent().getSerializableExtra(BaseConstant.DATA_BEAN);

        if (addressBean == null) {
            BaseToast.get().showDataError();
            BaseApplication.get().finish(getActivity());
        }

        setToolbar(mainToolbar, "??????????????????");

        cityId = addressBean.getCityId();
        areaId = addressBean.getAreaId();
        areaInfo = addressBean.getAreaInfo();

        nameEditText.setText(addressBean.getSellerName());
        mobileEditText.setText(addressBean.getTelphone());
        areaEditText.setText(addressBean.getAreaInfo());
        addressEditText.setText(addressBean.getAddress());
        defaultSwitch.setChecked(addressBean.getIsDefault().equals("1"));
        nameEditText.setSelection(addressBean.getSellerName().length());

    }

    @Override
    public void initEven() {

        areaEditText.setOnClickListener(view -> BaseApplication.get().start(getActivity(), AreaActivity.class, BaseConstant.CODE_AREA));

        saveTextView.setOnClickListener(view -> editAddress());

    }

    @Override
    public void onActivityResult(int req, int res, Intent intent) {
        super.onActivityResult(req, res, intent);
        if (res == RESULT_OK) {
            switch (req) {
                case BaseConstant.CODE_AREA:
                    cityId = intent.getStringExtra("city_id");
                    areaId = intent.getStringExtra("area_id");
                    areaInfo = intent.getStringExtra("area_info");
                    areaEditText.setText(areaInfo);
                    break;
                default:
                    break;
            }
        }
    }

    //???????????????

    private void editAddress() {

        BaseApplication.get().hideKeyboard(getActivity());

        String name = nameEditText.getText().toString();
        String phone = mobileEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String isDefault = defaultSwitch.isChecked() ? "1" : "0";

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
            BaseSnackBar.get().show(mainToolbar, "???????????????????????????");
            return;
        }

        if (!TextUtil.isMobile(phone)) {
            BaseSnackBar.get().show(mainToolbar, "??????????????????????????????");
            return;
        }

        if (TextUtils.isEmpty(cityId)) {
            BaseSnackBar.get().show(mainToolbar, "??????????????????");
            return;
        }

        saveTextView.setEnabled(false);
        saveTextView.setText("?????????...");

        SellerAddressModel.get().addressAdd(addressBean.getAddressId(), name, phone, cityId, areaId, address, areaInfo, isDefault, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                saveTextView.setEnabled(true);
                saveTextView.setText("????????????");
                BaseToast.get().showSuccess();
                BaseApplication.get().finish(getActivity());
            }

            @Override
            public void onFailure(String reason) {
                saveTextView.setEnabled(true);
                saveTextView.setText("????????????");
            }
        });

    }

}
