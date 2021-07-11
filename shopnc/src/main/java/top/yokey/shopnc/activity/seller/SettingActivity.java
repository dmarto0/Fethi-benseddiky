package top.yokey.shopnc.activity.seller;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseSnackBar;
import top.yokey.base.base.BaseToast;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.bean.SellerStoreInfoBean;
import top.yokey.base.model.SellerStoreModel;
import top.yokey.base.util.JsonUtil;
import top.yokey.shopnc.R;
import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseConstant;
import top.yokey.shopnc.base.BaseCountTime;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class SettingActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private AppCompatEditText goodsEditText;
    private AppCompatEditText qqEditText;
    private AppCompatEditText wwEditText;
    private AppCompatEditText seoEditText;
    private AppCompatEditText descEditText;
    private AppCompatEditText phoneEditText;
    private AppCompatTextView saveTextView;

    private SellerStoreInfoBean sellerStoreInfoBean;

    @Override
    public void initView() {

        setContentView(R.layout.activity_seller_setting);
        mainToolbar = findViewById(R.id.mainToolbar);
        goodsEditText = findViewById(R.id.goodsEditText);
        qqEditText = findViewById(R.id.qqEditText);
        wwEditText = findViewById(R.id.wwEditText);
        seoEditText = findViewById(R.id.seoEditText);
        descEditText = findViewById(R.id.descEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        saveTextView = findViewById(R.id.saveTextView);

    }

    @Override
    public void initData() {

        setToolbar(mainToolbar, "店铺设置");

        sellerStoreInfoBean = new SellerStoreInfoBean();

        getData();

    }

    @Override
    public void initEven() {

        saveTextView.setOnClickListener(view -> save());

    }

    //自定义方法

    private void save() {

        String zy = goodsEditText.getText().toString();
        String qq = qqEditText.getText().toString();
        String ww = wwEditText.getText().toString();
        String seo = seoEditText.getText().toString();
        String desc = descEditText.getText().toString();
        String phone = phoneEditText.getText().toString();

        saveTextView.setEnabled(false);
        saveTextView.setText("保存中...");

        SellerStoreModel.get().storeEdit(zy, qq, ww, seo, desc, phone, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                if (JsonUtil.isSuccess(baseBean.getDatas())) {
                    BaseToast.get().show("修改成功~");
                    BaseApplication.get().finish(getActivity());
                } else {
                    BaseSnackBar.get().show(mainToolbar, "修改失败！");
                    saveTextView.setText("保存信息");
                    saveTextView.setEnabled(true);
                }
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainToolbar, reason);
                saveTextView.setText("保存信息");
                saveTextView.setEnabled(true);
            }
        });

    }

    private void getData() {

        SellerStoreModel.get().storeInfo(new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                String data = JsonUtil.getDatasString(baseBean.getDatas(), "store_info");
                sellerStoreInfoBean = JsonUtil.json2Bean(data, SellerStoreInfoBean.class);
                goodsEditText.setText(sellerStoreInfoBean.getStoreZy());
                qqEditText.setText(sellerStoreInfoBean.getStoreQq());
                wwEditText.setText(sellerStoreInfoBean.getStoreWw());
                seoEditText.setText(sellerStoreInfoBean.getStoreKeywords());
                descEditText.setText(sellerStoreInfoBean.getStoreDescription());
                phoneEditText.setText(sellerStoreInfoBean.getStorePhone());
                goodsEditText.setSelection(goodsEditText.getText().length());
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainToolbar, reason);
                new BaseCountTime(BaseConstant.TIME_COUNT, BaseConstant.TIME_TICK) {
                    @Override
                    public void onFinish() {
                        super.onFinish();
                        getData();
                    }
                }.start();
            }
        });

    }

}
