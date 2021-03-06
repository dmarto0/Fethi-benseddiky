package top.yokey.shopnc.activity.order;

import android.content.Intent;
import android.view.View;

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
import top.yokey.base.bean.OrderBean;
import top.yokey.base.model.MemberOrderModel;
import top.yokey.base.util.JsonUtil;
import top.yokey.shopnc.R;
import top.yokey.shopnc.activity.refund.RefundApplyActivity;
import top.yokey.shopnc.adapter.BaseViewPagerAdapter;
import top.yokey.shopnc.adapter.OrderListAdapter;
import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseConstant;
import top.yokey.shopnc.view.PullRefreshView;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class OrderActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private AppCompatEditText searchEditText;
    private AppCompatImageView toolbarImageView;
    private TabLayout mainTabLayout;
    private ViewPager mainViewPager;

    private OrderListAdapter[] mainAdapter;
    private PullRefreshView[] mainPullRefreshView;
    private ArrayList<OrderBean>[] mainArrayList;

    private int[] pageInt;
    private String keyword;
    private int positionInt;
    private boolean refreshBoolean;
    private String[] stateTypeString;

    @Override
    public void initView() {

        setContentView(R.layout.activity_order_order);
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

        stateTypeString = new String[5];
        stateTypeString[0] = "";
        stateTypeString[1] = "state_new";
        stateTypeString[2] = "state_send";
        stateTypeString[3] = "state_notakes";
        stateTypeString[4] = "state_noeval";

        List<String> titleList = new ArrayList<>();
        titleList.add("??????");
        titleList.add("?????????");
        titleList.add("?????????");
        titleList.add("?????????");
        titleList.add("?????????");

        List<View> viewList = new ArrayList<>();
        viewList.add(getLayoutInflater().inflate(R.layout.include_recycler_view, null));
        viewList.add(getLayoutInflater().inflate(R.layout.include_recycler_view, null));
        viewList.add(getLayoutInflater().inflate(R.layout.include_recycler_view, null));
        viewList.add(getLayoutInflater().inflate(R.layout.include_recycler_view, null));
        viewList.add(getLayoutInflater().inflate(R.layout.include_recycler_view, null));

        keyword = "";
        refreshBoolean = false;
        pageInt = new int[viewList.size()];
        //noinspection unchecked
        mainArrayList = new ArrayList[viewList.size()];
        mainAdapter = new OrderListAdapter[viewList.size()];
        mainPullRefreshView = new PullRefreshView[viewList.size()];

        for (int i = 0; i < viewList.size(); i++) {
            pageInt[i] = 1;
            mainArrayList[i] = new ArrayList<>();
            mainAdapter[i] = new OrderListAdapter(mainArrayList[i]);
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

        for (OrderListAdapter orderListAdapter : mainAdapter) {
            orderListAdapter.setOnItemClickListener(new OrderListAdapter.OnItemClickListener() {
                @Override
                public void onPay(int position, OrderBean orderBean) {
                    BaseApplication.get().startOrderPay(getActivity(), orderBean.getPaySn());
                    refreshBoolean = true;
                }

                @Override
                public void onClick(int position, OrderBean orderBean) {

                }

                @Override
                public void onItemClick(int position, int itemPosition, OrderBean.OrderListBean orderListBean) {
                    orderDetailed(orderListBean.getOrderId());
                }

                @Override
                public void onItemGoodsClick(int position, int itemPosition, OrderBean.OrderListBean orderListBean) {
                    orderDetailed(orderListBean.getOrderId());
                }

                @Override
                public void onOption(int position, int itemPosition, OrderBean.OrderListBean orderListBean) {
                    switch (orderListBean.getOrderState()) {
                        case "0":
                            //????????????
                            orderDetailed(orderListBean.getOrderId());
                            break;
                        case "10":
                            //????????????
                            orderDetailed(orderListBean.getOrderId());
                            break;
                        case "20":
                            if (orderListBean.getLockState().equals("0")) {
                                //????????????
                                orderDetailed(orderListBean.getOrderId());
                            }
                        case "30":
                            if (orderListBean.getLockState().equals("0")) {
                                //????????????
                                orderLogistics(orderListBean.getShippingCode());
                            } else {
                                //????????????
                                orderLogistics(orderListBean.getShippingCode());
                            }
                            break;
                        case "40":
                            if (orderListBean.getEvaluationState().equals("1")) {
                                if (orderListBean.getEvaluationAgainState().equals("1")) {
                                    //????????????
                                    orderDelete(orderListBean.getOrderId());
                                } else {
                                    //????????????
                                    orderDelete(orderListBean.getOrderId());
                                }
                            } else {
                                //????????????
                                orderDetailed(orderListBean.getOrderId());
                            }
                            break;
                    }
                }

                @Override
                public void onOpera(int position, int itemPosition, OrderBean.OrderListBean orderListBean) {
                    switch (orderListBean.getOrderState()) {
                        case "0":
                            //????????????
                            orderDelete(orderListBean.getOrderId());
                            break;
                        case "10":
                            //????????????
                            orderCancel(orderListBean.getOrderId());
                            break;
                        case "20":
                            if (orderListBean.getLockState().equals("0")) {
                                //????????????
                                orderRefund(orderListBean.getOrderId());
                            } else {
                                //????????????
                                orderDetailed(orderListBean.getOrderId());
                            }
                            break;
                        case "30":
                            if (orderListBean.getLockState().equals("0")) {
                                //????????????
                                orderReceive(orderListBean.getOrderId());
                            } else {
                                //????????????
                                orderDetailed(orderListBean.getOrderId());
                            }
                            break;
                        case "40":
                            if (orderListBean.getEvaluationState().equals("1")) {
                                if (orderListBean.getEvaluationAgainState().equals("1")) {
                                    //????????????
                                    orderDetailed(orderListBean.getOrderId());
                                } else {
                                    //????????????
                                    orderEvaluateAgain(orderListBean.getOrderId());
                                }
                            } else {
                                //????????????
                                orderEvaluate(orderListBean.getOrderId());
                            }
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

        MemberOrderModel.get().orderList(stateTypeString[positionInt], keyword, pageInt[positionInt] + "", new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                if (pageInt[positionInt] == 1) {
                    mainArrayList[positionInt].clear();
                }
                if (pageInt[positionInt] <= baseBean.getPageTotal()) {
                    String data = JsonUtil.getDatasString(baseBean.getDatas(), "order_group_list");
                    mainArrayList[positionInt].addAll(JsonUtil.json2ArrayList(data, OrderBean.class));
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

    private void orderDelete(String orderId) {

        BaseSnackBar.get().showHandler(mainToolbar);

        MemberOrderModel.get().orderDelete(orderId, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                pageInt[positionInt] = 1;
                getOrder();
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainToolbar, reason);
            }
        });

    }

    private void orderCancel(String orderId) {

        BaseSnackBar.get().showHandler(mainToolbar);

        MemberOrderModel.get().orderCancel(orderId, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                pageInt[positionInt] = 1;
                getOrder();
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainToolbar, reason);
            }
        });

    }

    private void orderReceive(String orderId) {

        BaseSnackBar.get().showHandler(mainToolbar);

        MemberOrderModel.get().orderReceive(orderId, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                pageInt[positionInt] = 1;
                getOrder();
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainToolbar, reason);
            }
        });

    }

    private void orderLogistics(String number) {

        Intent intent = new Intent(getActivity(), LogisticsActivity.class);
        intent.putExtra(BaseConstant.DATA_ID, number);
        BaseApplication.get().start(getActivity(), intent);

    }

    private void orderRefund(String orderId) {

        Intent intent = new Intent(getActivity(), RefundApplyActivity.class);
        intent.putExtra(BaseConstant.DATA_ID, orderId);
        intent.putExtra(BaseConstant.DATA_GOODSID, "");
        BaseApplication.get().start(getActivity(), intent);
        refreshBoolean = true;

    }

    private void orderDetailed(String orderId) {

        Intent intent = new Intent(getActivity(), DetailedActivity.class);
        intent.putExtra(BaseConstant.DATA_ID, orderId);
        BaseApplication.get().start(getActivity(), intent);
        refreshBoolean = true;

    }

    private void orderEvaluate(String orderId) {

        Intent intent = new Intent(getActivity(), EvaluateActivity.class);
        intent.putExtra(BaseConstant.DATA_ID, orderId);
        BaseApplication.get().start(getActivity(), intent);
        refreshBoolean = true;

    }

    private void orderEvaluateAgain(String orderId) {

        Intent intent = new Intent(getActivity(), EvaluateAgainActivity.class);
        intent.putExtra(BaseConstant.DATA_ID, orderId);
        BaseApplication.get().start(getActivity(), intent);
        refreshBoolean = true;

    }

}
