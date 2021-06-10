package top.yokey.shopnc.activity.store;

import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.view.PullRefreshView;
import top.yokey.shopnc.R;
import top.yokey.shopnc.adapter.StoreStreetClassListAdapter;
import top.yokey.shopnc.adapter.StoreStreetListAdapter;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseSnackBar;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.bean.StoreStreetBean;
import top.yokey.base.bean.StoreStreetClassBean;
import top.yokey.base.model.StoreModel;
import top.yokey.base.util.JsonUtil;

import java.util.ArrayList;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class StreetActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private AppCompatImageView toolbarImageView;
    private AppCompatEditText searchEditText;
    private AppCompatImageView searchImageView;
    private PullRefreshView mainPullRefreshView;
    private PullRefreshView classPullRefreshView;

    private int pageInt;
    private String gcIdString;
    private String keywordString;
    private StoreStreetListAdapter mainAdapter;
    private ArrayList<StoreStreetBean> mainArrayList;

    private StoreStreetClassListAdapter classAdapter;
    private ArrayList<StoreStreetClassBean> classArrayList;

    @Override
    public void initView() {

        setContentView(R.layout.activity_store_street);
        mainToolbar = findViewById(R.id.mainToolbar);
        toolbarImageView = findViewById(R.id.toolbarImageView);
        searchEditText = findViewById(R.id.searchEditText);
        searchImageView = findViewById(R.id.searchImageView);
        mainPullRefreshView = findViewById(R.id.mainPullRefreshView);
        classPullRefreshView = findViewById(R.id.classPullRefreshView);

    }

    @Override
    public void initData() {

        setToolbar(mainToolbar, "店铺街");
        toolbarImageView.setImageResource(R.drawable.ic_navigation_cate);

        pageInt = 1;
        gcIdString = "";
        keywordString = "";
        mainArrayList = new ArrayList<>();
        mainAdapter = new StoreStreetListAdapter(mainArrayList);
        mainPullRefreshView.getRecyclerView().setAdapter(mainAdapter);

        classArrayList = new ArrayList<>();
        classAdapter = new StoreStreetClassListAdapter(classArrayList);
        classPullRefreshView.getRecyclerView().setAdapter(classAdapter);

        streetList();
        streetClass();

    }

    @Override
    public void initEven() {

        searchImageView.setOnClickListener(view -> {
            gcIdString = "";
            keywordString = searchEditText.getText().toString();
            BaseSnackBar.get().showHandler(mainToolbar);
            pageInt = 1;
            streetList();
        });

        toolbarImageView.setOnClickListener(view -> {
            if (classPullRefreshView.getVisibility() == View.VISIBLE) {
                mainPullRefreshView.setVisibility(View.VISIBLE);
                classPullRefreshView.setVisibility(View.GONE);
            } else {
                mainPullRefreshView.setVisibility(View.GONE);
                classPullRefreshView.setVisibility(View.VISIBLE);
            }
        });

        mainPullRefreshView.setOnClickListener(view -> {
            if (mainPullRefreshView.isFailure()) {
                pageInt = 1;
                streetList();
            }
        });

        mainPullRefreshView.setOnRefreshListener(new PullRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageInt = 1;
                streetList();
            }

            @Override
            public void onLoadMore() {
                streetList();
            }
        });

        mainAdapter.setOnItemClickListener((position, storeStreetBean) -> BaseApplication.get().startStore(getActivity(), storeStreetBean.getStoreId()));

        classPullRefreshView.setOnClickListener(view -> {
            if (classPullRefreshView.isFailure()) {
                streetClass();
            }
        });

        classPullRefreshView.setOnRefreshListener(new PullRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                streetClass();
            }

            @Override
            public void onLoadMore() {
                streetClass();
            }
        });

        classAdapter.setOnItemClickListener((position, storeStreetClassBean) -> {
            BaseSnackBar.get().showHandler(mainToolbar);
            mainPullRefreshView.setVisibility(View.VISIBLE);
            classPullRefreshView.setVisibility(View.GONE);
            gcIdString = storeStreetClassBean.getScId();
            keywordString = "";
            pageInt = 1;
            streetList();
        });

    }

    @Override
    public void onReturn() {

        if (classPullRefreshView.getVisibility() == View.VISIBLE) {
            mainPullRefreshView.setVisibility(View.VISIBLE);
            classPullRefreshView.setVisibility(View.GONE);
            return;
        }

        if (searchEditText.getText().length() != 0) {
            searchEditText.setText("");
            keywordString = "";
            pageInt = 1;
            streetList();
        }

        super.onReturn();

    }

    //自定义方法

    private void streetList() {

        mainPullRefreshView.setLoading();

        StoreModel.get().streetList(keywordString, gcIdString, pageInt + "", new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                if (pageInt == 1) {
                    mainArrayList.clear();
                }
                if (pageInt <= baseBean.getPageTotal()) {
                    String data = JsonUtil.getDatasString(baseBean.getDatas(), "store_list");
                    mainArrayList.addAll(JsonUtil.json2ArrayList(data, StoreStreetBean.class));
                    pageInt++;
                }
                mainPullRefreshView.setComplete();
            }

            @Override
            public void onFailure(String reason) {
                mainPullRefreshView.setFailure();
            }
        });

    }

    private void streetClass() {

        classPullRefreshView.setLoading();

        StoreModel.get().streetClass(new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                classArrayList.clear();
                String data = JsonUtil.getDatasString(baseBean.getDatas(), "class_list");
                classArrayList.addAll(JsonUtil.json2ArrayList(data, StoreStreetClassBean.class));
                classPullRefreshView.setComplete();
            }

            @Override
            public void onFailure(String reason) {
                classPullRefreshView.setFailure();
            }
        });

    }

}
