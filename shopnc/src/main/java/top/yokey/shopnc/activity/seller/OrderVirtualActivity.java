package top.yokey.shopnc.activity.seller;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseSnackBar;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.bean.OrderSellerBean;
import top.yokey.base.model.SellerOrderModel;
import top.yokey.base.util.JsonUtil;
import top.yokey.shopnc.R;
import top.yokey.shopnc.adapter.BaseViewPagerAdapter;
import top.yokey.shopnc.adapter.OrderSellerListAdapter;
import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseConstant;
import top.yokey.shopnc.view.PullRefreshView;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class OrderVirtualActivity extends BaseActivity {


    private Toolbar mainToolbar;
    private AppCompatEditText searchEditText;
    private AppCompatImageView toolbarImageView;
    private TabLayout mainTabLayout;
    private ViewPager mainViewPager;

    private OrderSellerListAdapter[] mainAdapter;
    private PullRefreshView[] mainPullRefreshView;
    private ArrayList<OrderSellerBean>[] mainArrayList;

    private int[] pageInt;
    private String keyword;
    private int positionInt;
    private boolean refreshBoolean;
    private String[] stateTypeString;

    @Override
    public void initView() {

        setContentView(R.layout.activity_seller_order);
        mainToolbar = findViewById(R.id.mainToolbar);
        searchEditText = findViewById(R.id.searchEditText);
        toolbarImageView = findViewById(R.id.toolbarImageView);
        mainTabLayout = findViewById(R.id.mainTabLayout);
        mainViewPager = findViewById(R.id.mainViewPager);

    }

    @Override
    public void initData() {

        positionInt = getIntent().getIntExtra(BaseConstant.DATA_POSITION, 0);

        setToolbar(mainToolbar, "");

        toolbarImageView.setImageResource(R.drawable.ic_action_search);

        stateTypeString = new String[3];
        stateTypeString[0] = "state_new";
        stateTypeString[1] = "state_pay";
        stateTypeString[2] = "state_success";

        List<String> titleList = new ArrayList<>();
        titleList.add("?????????");
        titleList.add("?????????");
        titleList.add("?????????");

        List<View> viewList = new ArrayList<>();
        viewList.add(getLayoutInflater().inflate(R.layout.include_recycler_view, null));
        viewList.add(getLayoutInflater().inflate(R.layout.include_recycler_view, null));
        viewList.add(getLayoutInflater().inflate(R.layout.include_recycler_view, null));

        keyword = "";
        refreshBoolean = false;
        pageInt = new int[viewList.size()];
        //noinspection unchecked
        mainArrayList = new ArrayList[viewList.size()];
        mainAdapter = new OrderSellerListAdapter[viewList.size()];
        mainPullRefreshView = new PullRefreshView[viewList.size()];

        for (int i = 0; i < viewList.size(); i++) {
            pageInt[i] = 1;
            mainArrayList[i] = new ArrayList<>();
            mainAdapter[i] = new OrderSellerListAdapter(mainArrayList[i]);
            mainPullRefreshView[i] = viewList.get(i).findViewById(R.id.mainPullRefreshView);
            mainTabLayout.addTab(mainTabLayout.newTab().setText(titleList.get(i)));
            mainPullRefreshView[i].getRecyclerView().setAdapter(mainAdapter[i]);
        }

        BaseApplication.get().setTabLayout(mainTabLayout, new BaseViewPagerAdapter(viewList, titleList), mainViewPager);
        mainTabLayout.setTabMode(TabLayout.MODE_FIXED);

        mainViewPager.setCurrentItem(positionInt);
        getOrder();

    }

    @Override
    public void initEven() {

        toolbarImageView.setOnClickListener(view -> {
            BaseApplication.get().hideKeyboard(getActivity());
            keyword = searchEditText.getText().toString();
            pageInt[positionInt] = 1;
            getOrder();
        });

        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                positionInt = position;
                if (mainArrayList[positionInt].size() == 0) {
                    getOrder();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        for (PullRefreshView pullRefreshView : mainPullRefreshView) {
            pullRefreshView.setOnRefreshListener(new PullRefreshView.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    pageInt[positionInt] = 1;
                    getOrder();
                }

                @Override
                public void onLoadMore() {
                    getOrder();
                }
            });
        }

        for (OrderSellerListAdapter orderSellerListAdapter : mainAdapter) {
            orderSellerListAdapter.setOnItemClickListener(new OrderSellerListAdapter.OnItemClickListener() {
                @Override
                public void onOption(final int position, final OrderSellerBean bean) {
                    switch (bean.getOrderState()) {
                        case "10":
                            Intent intent = new Intent(getActivity(), OrderCancelActivity.class);
                            intent.putExtra(BaseConstant.DATA_ID, bean.getOrderId());
                            intent.putExtra(BaseConstant.DATA_SN, bean.getOrderSn());
                            intent.putExtra(BaseConstant.DATA_CONTENT, bean.getOrderAmount());
                            BaseApplication.get().startCheckSellerLogin(getActivity(), intent);
                            refreshBoolean = true;
                            break;
                    }
                }

                @Override
                public void onOpera(final int position, final OrderSellerBean bean) {
                    switch (bean.getOrderState()) {
                        case "10":
                            int paddingTop = BaseApplication.get().dipToPx(16);
                            int paddingLeft = BaseApplication.get().dipToPx(28);
                            final AppCompatEditText appCompatEditText = new AppCompatEditText(getActivity());
                            appCompatEditText.setTextColor(BaseApplication.get().getColors(R.color.primary));
                            appCompatEditText.setPadding(paddingLeft, paddingTop, paddingLeft, 0);
                            appCompatEditText.setHint("???????????????" + bean.getOrderAmount());
                            appCompatEditText.setBackgroundColor(Color.TRANSPARENT);
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("???????????????~")
                                    .setView(appCompatEditText)
                                    .setPositiveButton("??????", (dialog, which) -> orderSpayPrice(position, bean.getOrderId(), appCompatEditText.getText().toString()))
                                    .setNegativeButton("??????", null)
                                    .show();
                            break;
                        case "20":
                            Intent intent = new Intent(getActivity(), OrderSendActivity.class);
                            intent.putExtra(BaseConstant.DATA_ID, bean.getOrderId());
                            BaseApplication.get().startCheckSellerLogin(getActivity(), intent);
                            refreshBoolean = true;
                            break;
                    }
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (refreshBoolean) {
            refreshBoolean = false;
            pageInt[positionInt] = 1;
            getOrder();
        }
    }

    //???????????????

    private void getOrder() {

        mainPullRefreshView[positionInt].setLoading();

        SellerOrderModel.get().orderList(stateTypeString[positionInt], keyword, pageInt[positionInt] + "", new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                if (pageInt[positionInt] == 1) {
                    mainArrayList[positionInt].clear();
                }
                if (pageInt[positionInt] <= baseBean.getPageTotal()) {
                    String data = JsonUtil.getDatasString(baseBean.getDatas(), "order_list");
                    mainArrayList[positionInt].addAll(JsonUtil.json2ArrayList(data, OrderSellerBean.class));
                    pageInt[positionInt]++;
                }
                mainPullRefreshView[positionInt].setComplete();
            }

            @Override
            public void onFailure(String reason) {
                mainPullRefreshView[positionInt].setFailure();
            }
        });

    }

    private void orderSpayPrice(final int position, final String orderId, final String orderFee) {

        SellerOrderModel.get().orderSpayPrice(orderId, orderFee, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                BaseSnackBar.get().show(mainToolbar, baseBean.getDatas());
                mainArrayList[positionInt].get(position).setOrderAmount(orderFee);
                mainAdapter[positionInt].notifyDataSetChanged();
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainToolbar, reason);
            }
        });

    }

}
