package top.yokey.shopnc.activity.mine;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.bean.PreDepositCashLogBean;
import top.yokey.base.bean.PreDepositLogBean;
import top.yokey.base.bean.PreDepositRechargeLogBean;
import top.yokey.base.model.MemberFundModel;
import top.yokey.base.util.JsonUtil;
import top.yokey.shopnc.R;
import top.yokey.shopnc.adapter.BaseViewPagerAdapter;
import top.yokey.shopnc.adapter.PreDepositCashLogListAdapter;
import top.yokey.shopnc.adapter.PreDepositLogListAdapter;
import top.yokey.shopnc.adapter.PreDepositRechargeLogListAdapter;
import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseConstant;
import top.yokey.shopnc.view.PullRefreshView;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class PreDepositActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private TabLayout mainTabLayout;
    private ViewPager mainViewPager;
    private AppCompatTextView preDepositValueTextView;

    private int preDepositPageInt;
    private PullRefreshView preDepositPullRefreshView;
    private PreDepositLogListAdapter preDepositAdapter;
    private ArrayList<PreDepositLogBean> preDepositArrayList;
    private int pdRechargePageInt;
    private PullRefreshView pdRechargePullRefreshView;
    private PreDepositRechargeLogListAdapter pdRechargeAdapter;
    private ArrayList<PreDepositRechargeLogBean> pdRechargeArrayList;
    private int pdCashPageInt;
    private PullRefreshView pdCashPullRefreshView;
    private PreDepositCashLogListAdapter pdCashAdapter;
    private ArrayList<PreDepositCashLogBean> pdCashArrayList;

    @Override
    public void initView() {

        setContentView(R.layout.activity_mine_pre_deposit);
        mainToolbar = findViewById(R.id.mainToolbar);
        mainTabLayout = findViewById(R.id.mainTabLayout);
        mainViewPager = findViewById(R.id.mainViewPager);
        preDepositValueTextView = findViewById(R.id.preDepositValueTextView);

    }

    @Override
    public void initData() {

        setToolbar(mainToolbar, "???????????????");

        preDepositValueTextView.setText(BaseApplication.get().getMemberAssetBean().getPredepoit());
        preDepositValueTextView.append("???");

        List<String> titleList = new ArrayList<>();
        titleList.add("????????????");
        titleList.add("????????????");
        titleList.add("????????????");

        List<View> viewList = new ArrayList<>();
        viewList.add(getLayoutInflater().inflate(R.layout.include_recycler_view, null));
        viewList.add(getLayoutInflater().inflate(R.layout.include_recycler_view, null));
        viewList.add(getLayoutInflater().inflate(R.layout.include_recycler_view, null));

        //????????????
        preDepositPageInt = 1;
        preDepositArrayList = new ArrayList<>();
        preDepositAdapter = new PreDepositLogListAdapter(preDepositArrayList);
        preDepositPullRefreshView = viewList.get(0).findViewById(R.id.mainPullRefreshView);
        preDepositPullRefreshView.getRecyclerView().setAdapter(preDepositAdapter);

        //????????????
        pdRechargePageInt = 1;
        pdRechargeArrayList = new ArrayList<>();
        pdRechargeAdapter = new PreDepositRechargeLogListAdapter(pdRechargeArrayList);
        pdRechargePullRefreshView = viewList.get(1).findViewById(R.id.mainPullRefreshView);
        pdRechargePullRefreshView.getRecyclerView().setAdapter(pdRechargeAdapter);

        //????????????
        pdCashPageInt = 1;
        pdCashArrayList = new ArrayList<>();
        pdCashAdapter = new PreDepositCashLogListAdapter(pdCashArrayList);
        pdCashPullRefreshView = viewList.get(2).findViewById(R.id.mainPullRefreshView);
        pdCashPullRefreshView.getRecyclerView().setAdapter(pdCashAdapter);

        BaseApplication.get().setTabLayout(mainTabLayout, new BaseViewPagerAdapter(viewList, titleList), mainViewPager);
        mainTabLayout.setTabMode(TabLayout.MODE_FIXED);

        getPreDepositLog();
        getPdRechargeLog();
        getPdCashLog();

    }

    @Override
    public void initEven() {

        preDepositPullRefreshView.setOnClickListener(view -> {
            if (preDepositPullRefreshView.isFailure()) {
                preDepositPageInt = 1;
                getPreDepositLog();
            }
        });

        preDepositPullRefreshView.setOnRefreshListener(new PullRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                preDepositPageInt = 1;
                getPreDepositLog();
            }

            @Override
            public void onLoadMore() {
                getPreDepositLog();
            }
        });

        preDepositAdapter.setOnItemClickListener((position, preDepositLogBean) -> {

        });

        pdRechargePullRefreshView.setOnClickListener(view -> {
            if (pdRechargePullRefreshView.isFailure()) {
                pdRechargePageInt = 1;
                getPdRechargeLog();
            }
        });

        pdRechargePullRefreshView.setOnRefreshListener(new PullRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pdRechargePageInt = 1;
                getPdRechargeLog();
            }

            @Override
            public void onLoadMore() {
                getPdRechargeLog();
            }
        });

        pdRechargeAdapter.setOnItemClickListener((position, pdRechargeLogBean) -> {

        });

        pdCashPullRefreshView.setOnClickListener(view -> {
            if (pdCashPullRefreshView.isFailure()) {
                pdCashPageInt = 1;
                getPdCashLog();
            }
        });

        pdCashPullRefreshView.setOnRefreshListener(new PullRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pdCashPageInt = 1;
                getPdCashLog();
            }

            @Override
            public void onLoadMore() {
                getPdCashLog();
            }
        });

        pdCashAdapter.setOnItemClickListener((position, pdCashLogBean) -> {
            Intent intent = new Intent(getActivity(), PreDepositCashActivity.class);
            intent.putExtra(BaseConstant.DATA_BEAN, pdCashLogBean);
            BaseApplication.get().start(getActivity(), intent);
        });

    }

    //???????????????

    private void getPreDepositLog() {

        preDepositPullRefreshView.setLoading();

        MemberFundModel.get().preDepositLog(preDepositPageInt + "", new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                if (preDepositPageInt == 1) {
                    preDepositArrayList.clear();
                }
                if (baseBean.isHasmore()) {
                    preDepositPageInt++;
                }
                String data = JsonUtil.getDatasString(baseBean.getDatas(), "list");
                preDepositArrayList.addAll(JsonUtil.json2ArrayList(data, PreDepositLogBean.class));
                preDepositPullRefreshView.setComplete();
            }

            @Override
            public void onFailure(String reason) {
                preDepositPullRefreshView.setFailure();
            }
        });

    }

    private void getPdRechargeLog() {

        pdRechargePullRefreshView.setLoading();

        MemberFundModel.get().pdRechargeList(pdRechargePageInt + "", new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                if (pdRechargePageInt == 1) {
                    pdRechargeArrayList.clear();
                }
                if (baseBean.isHasmore()) {
                    pdRechargePageInt++;
                }
                String data = JsonUtil.getDatasString(baseBean.getDatas(), "list");
                pdRechargeArrayList.addAll(JsonUtil.json2ArrayList(data, PreDepositRechargeLogBean.class));
                pdRechargePullRefreshView.setComplete();
            }

            @Override
            public void onFailure(String reason) {
                pdRechargePullRefreshView.setFailure();
            }
        });

    }

    private void getPdCashLog() {

        pdCashPullRefreshView.setLoading();

        MemberFundModel.get().pdCashList(pdCashPageInt + "", new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                if (pdCashPageInt == 1) {
                    pdCashArrayList.clear();
                }
                if (baseBean.isHasmore()) {
                    pdCashPageInt++;
                }
                String data = JsonUtil.getDatasString(baseBean.getDatas(), "list");
                pdCashArrayList.addAll(JsonUtil.json2ArrayList(data, PreDepositCashLogBean.class));
                pdCashPullRefreshView.setComplete();
            }

            @Override
            public void onFailure(String reason) {
                pdCashPullRefreshView.setFailure();
            }
        });

    }

}
