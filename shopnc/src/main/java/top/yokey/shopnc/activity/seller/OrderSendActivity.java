package top.yokey.shopnc.activity.seller;

import android.text.Html;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseSnackBar;
import top.yokey.base.base.BaseToast;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.bean.ExpressSellerSendBean;
import top.yokey.base.bean.OrderSellerBean;
import top.yokey.base.model.SellerExpressModel;
import top.yokey.base.model.SellerOrderModel;
import top.yokey.base.util.JsonUtil;
import top.yokey.shopnc.R;
import top.yokey.shopnc.adapter.ExpressSellerSendListAdapter;
import top.yokey.shopnc.adapter.GoodsOrderSellerListAdapter;
import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseConstant;
import top.yokey.shopnc.base.BaseCountTime;
import top.yokey.shopnc.base.BaseImageLoader;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class OrderSendActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private AppCompatTextView snTextView;
    private AppCompatTextView timeTextView;
    private RecyclerView mainRecyclerView;
    private View zengPinView;
    private LinearLayoutCompat zengPinLinearLayout;
    private AppCompatTextView zengPinDescTextView;
    private AppCompatImageView zengPinGoodsImageView;
    private AppCompatTextView totalTextView;
    private AppCompatTextView addressNameTextView;
    private AppCompatTextView addressMobileTextView;
    private AppCompatTextView addressAreaTextView;
    private AppCompatTextView daddressNameTextView;
    private AppCompatTextView daddressMobileTextView;
    private AppCompatTextView daddressAreaTextView;
    private AppCompatRadioButton needRadioButton;
    private AppCompatRadioButton noNeedRadioButton;
    private RecyclerView expressRecyclerView;
    private AppCompatTextView noNeedTextView;
    private AppCompatTextView confirmTextView;

    private String orderId;
    private OrderSellerBean orderSellerBean;
    private ExpressSellerSendListAdapter expressAdapter;
    private ArrayList<ExpressSellerSendBean> expressArrayList;

    @Override
    public void initView() {

        setContentView(R.layout.activity_seller_order_send);
        mainToolbar = findViewById(R.id.mainToolbar);
        snTextView = findViewById(R.id.snTextView);
        timeTextView = findViewById(R.id.timeTextView);
        mainRecyclerView = findViewById(R.id.mainRecyclerView);
        zengPinView = findViewById(R.id.zengPinView);
        zengPinLinearLayout = findViewById(R.id.zengPinLinearLayout);
        zengPinDescTextView = findViewById(R.id.zengPinDescTextView);
        zengPinGoodsImageView = findViewById(R.id.zengPinGoodsImageView);
        totalTextView = findViewById(R.id.totalTextView);
        addressNameTextView = findViewById(R.id.addressNameTextView);
        addressMobileTextView = findViewById(R.id.addressMobileTextView);
        addressAreaTextView = findViewById(R.id.addressAreaTextView);
        daddressNameTextView = findViewById(R.id.daddressNameTextView);
        daddressMobileTextView = findViewById(R.id.daddressMobileTextView);
        daddressAreaTextView = findViewById(R.id.daddressAreaTextView);
        needRadioButton = findViewById(R.id.needRadioButton);
        noNeedRadioButton = findViewById(R.id.noNeedRadioButton);
        expressRecyclerView = findViewById(R.id.expressRecyclerView);
        noNeedTextView = findViewById(R.id.noNeedTextView);
        confirmTextView = findViewById(R.id.confirmTextView);

    }

    @Override
    public void initData() {

        orderId = getIntent().getStringExtra(BaseConstant.DATA_ID);
        if (TextUtils.isEmpty(orderId)) {
            BaseToast.get().showDataError();
            BaseApplication.get().finish(getActivity());
        }

        expressArrayList = new ArrayList<>();
        expressAdapter = new ExpressSellerSendListAdapter(expressArrayList);
        BaseApplication.get().setRecyclerView(getActivity(), expressRecyclerView, expressAdapter);

        orderSellerBean = new OrderSellerBean();
        setToolbar(mainToolbar, "订单发货");

        getData();

    }

    @Override
    public void initEven() {

        needRadioButton.setOnClickListener(view -> {
            needRadioButton.setChecked(true);
            noNeedRadioButton.setChecked(false);
            confirmTextView.setVisibility(View.GONE);
            noNeedTextView.setVisibility(View.GONE);
            expressRecyclerView.setVisibility(View.VISIBLE);
        });

        noNeedRadioButton.setOnClickListener(view -> {
            needRadioButton.setChecked(false);
            noNeedRadioButton.setChecked(true);
            confirmTextView.setVisibility(View.VISIBLE);
            noNeedTextView.setVisibility(View.VISIBLE);
            expressRecyclerView.setVisibility(View.GONE);
        });

        expressAdapter.setOnItemClickListener(new ExpressSellerSendListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, ExpressSellerSendBean bean) {

            }

            @Override
            public void onConfirm(int position, ExpressSellerSendBean bean) {
                if (TextUtils.isEmpty(bean.getCode())) {
                    BaseSnackBar.get().show(mainToolbar, "请输入单号！");
                    return;
                }
                SellerOrderModel.get().orderDeliverSend(orderId, bean.getCode(), bean.getId(), new BaseHttpListener() {
                    @Override
                    public void onSuccess(BaseBean baseBean) {
                        BaseToast.get().show("发货成功！");
                        BaseApplication.get().finish(getActivity());
                    }

                    @Override
                    public void onFailure(String reason) {
                        BaseSnackBar.get().show(mainToolbar, reason);
                        confirmTextView.setEnabled(true);
                        confirmTextView.setText("确认");
                    }
                });
            }
        });

        confirmTextView.setOnClickListener(view -> send());

    }

    //自定义方法

    private void send() {

        confirmTextView.setEnabled(false);
        confirmTextView.setText("发货中...");

        SellerOrderModel.get().orderDeliverSend(orderId, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                BaseToast.get().show("发货成功！");
                BaseApplication.get().finish(getActivity());
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainToolbar, reason);
                confirmTextView.setEnabled(true);
                confirmTextView.setText("确认");
            }
        });

    }

    private void getData() {

        SellerExpressModel.get().getMyList(orderId, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                try {
                    String data = JsonUtil.getDatasString(baseBean.getDatas(), "orderinfo");
                    data = data.replace("null", "\"\"");
                    orderSellerBean = JsonUtil.json2Bean(data, OrderSellerBean.class);
                    JSONObject jsonObject = new JSONObject(data);
                    jsonObject = new JSONObject(jsonObject.getString("extend_order_common"));
                    addressNameTextView.setText("收货人：");
                    addressNameTextView.append(jsonObject.getString("reciver_name"));
                    jsonObject = new JSONObject(jsonObject.getString("reciver_info"));
                    addressMobileTextView.setText(jsonObject.getString("mob_phone"));
                    addressAreaTextView.setText("收货地址：");
                    addressAreaTextView.append(jsonObject.getString("address"));
                    expressArrayList.clear();
                    data = JsonUtil.getDatasString(baseBean.getDatas(), "express_array");
                    jsonObject = new JSONObject(data);
                    Iterator iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next().toString();
                        String value = jsonObject.getString(key);
                        expressArrayList.add(JsonUtil.json2Bean(value, ExpressSellerSendBean.class));
                    }
                    expressAdapter.notifyDataSetChanged();
                    getDefaultExpress();
                    setData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    private void setData() {

        GoodsOrderSellerListAdapter goodsOrderSellerListAdapter;

        goodsOrderSellerListAdapter = new GoodsOrderSellerListAdapter(orderSellerBean.getGoodsList());
        BaseApplication.get().setRecyclerView(BaseApplication.get(), mainRecyclerView, goodsOrderSellerListAdapter);

        if (orderSellerBean.getZengpinList().size() == 0) {
            zengPinView.setVisibility(View.GONE);
            zengPinLinearLayout.setVisibility(View.GONE);
        } else {
            zengPinView.setVisibility(View.VISIBLE);
            zengPinLinearLayout.setVisibility(View.VISIBLE);
            zengPinDescTextView.setText(orderSellerBean.getZengpinList().get(0).getGoodsName());
            BaseImageLoader.get().display(orderSellerBean.getZengpinList().get(0).getImage240Url(), zengPinGoodsImageView);
        }

        snTextView.setText("单号：");
        snTextView.append(orderSellerBean.getOrderSn());
        timeTextView.setText(orderSellerBean.getBuyerName());

        String temp = "共 <font color='#FF0000'>" + orderSellerBean.getGoodsCount() + " </font>件商品，合计<font color='#FF0000'>￥" + orderSellerBean.getOrderAmount() + "</font>（含运费：<font color='#FF0000'>￥" + orderSellerBean.getShippingFee() + "</font>）";
        totalTextView.setText(Html.fromHtml(temp));

    }

    private void getDefaultExpress() {

        SellerExpressModel.get().getDefaultExpress(orderId, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                try {
                    String data = JsonUtil.getDatasString(baseBean.getDatas(), "daddress_info");
                    JSONObject jsonObject = new JSONObject(data);
                    daddressNameTextView.setText("发货人：");
                    daddressNameTextView.append(jsonObject.getString("seller_name"));
                    daddressMobileTextView.setText(jsonObject.getString("telphone"));
                    daddressAreaTextView.setText("发货地址：");
                    daddressAreaTextView.append(jsonObject.getString("area_info"));
                    daddressAreaTextView.append(" " + jsonObject.getString("address"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainToolbar, reason);
                new BaseCountTime(BaseConstant.TIME_COUNT, BaseConstant.TIME_TICK) {
                    @Override
                    public void onFinish() {
                        super.onFinish();
                        getDefaultExpress();
                    }
                }.start();
            }
        });

    }

}
