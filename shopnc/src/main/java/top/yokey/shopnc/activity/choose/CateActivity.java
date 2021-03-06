package top.yokey.shopnc.activity.choose;

import android.content.Intent;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;

import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseSnackBar;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.bean.BrandRecommendBean;
import top.yokey.base.bean.ClassBean;
import top.yokey.base.bean.ClassChildBean;
import top.yokey.base.model.BrandModel;
import top.yokey.base.model.ClassModel;
import top.yokey.base.util.JsonUtil;
import top.yokey.shopnc.R;
import top.yokey.shopnc.adapter.BrandRecommendListAdapter;
import top.yokey.shopnc.adapter.ClassChildListAdapter;
import top.yokey.shopnc.adapter.ClassListAdapter;
import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseConstant;
import top.yokey.shopnc.view.PullRefreshView;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class CateActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private PullRefreshView classPullRefreshView;
    private PullRefreshView brandRecommendPullRefreshView;
    private PullRefreshView classChildPullRefreshView;

    private long exitTimeLong;
    private String gcIdString;
    private String one, two, thr;

    private ClassListAdapter classAdapter;
    private ArrayList<ClassBean> classArrayList;
    private ClassChildListAdapter classChildAdapter;
    private ArrayList<ClassChildBean> classChildArrayList;
    private BrandRecommendListAdapter brandRecommendAdapter;
    private ArrayList<BrandRecommendBean> brandRecommendArrayList;

    @Override
    public void initView() {

        setContentView(R.layout.activity_choose_cate);
        mainToolbar = findViewById(R.id.mainToolbar);
        classPullRefreshView = findViewById(R.id.classPullRefreshView);
        brandRecommendPullRefreshView = findViewById(R.id.brandRecommendPullRefreshView);
        classChildPullRefreshView = findViewById(R.id.classChildPullRefreshView);

    }

    @Override
    public void initData() {

        setToolbar(mainToolbar, "????????????");

        one = "";
        two = "";
        thr = "";
        exitTimeLong = 0L;
        gcIdString = "";

        classArrayList = new ArrayList<>();
        classAdapter = new ClassListAdapter(classArrayList);
        classPullRefreshView.getRecyclerView().setAdapter(classAdapter);
        classPullRefreshView.setCanLoadMore(false);
        classPullRefreshView.setCanRefresh(false);

        brandRecommendArrayList = new ArrayList<>();
        brandRecommendAdapter = new BrandRecommendListAdapter(brandRecommendArrayList);
        brandRecommendPullRefreshView.getRecyclerView().setLayoutManager(new GridLayoutManager(getActivity(), 3));
        brandRecommendPullRefreshView.getRecyclerView().setAdapter(brandRecommendAdapter);
        brandRecommendPullRefreshView.clearItemDecoration();
        brandRecommendPullRefreshView.setCanLoadMore(false);
        brandRecommendPullRefreshView.setCanRefresh(false);

        classChildArrayList = new ArrayList<>();
        classChildAdapter = new ClassChildListAdapter(classChildArrayList);
        classChildPullRefreshView.getRecyclerView().setAdapter(classChildAdapter);
        classChildPullRefreshView.setCanLoadMore(false);
        classChildPullRefreshView.setCanRefresh(false);

        getBrandRecommend();
        getCate();

    }

    @Override
    public void initEven() {

        classPullRefreshView.setOnClickListener(view -> {
            if (classPullRefreshView.isFailure()) {
                getCate();
            }
        });

        classAdapter.setOnItemClickListener((position, classBean) -> {
            brandRecommendPullRefreshView.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            classChildPullRefreshView.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
            for (int i = 0; i < classArrayList.size(); i++) {
                classArrayList.get(i).setClick(false);
                classAdapter.notifyItemChanged(i);
            }
            classArrayList.get(position).setClick(true);
            classAdapter.notifyItemChanged(position);
            gcIdString = classBean.getGcId();
            one = classBean.getGcName();
            getChildAll();
        });

        brandRecommendPullRefreshView.setOnClickListener(view -> {
            if (brandRecommendPullRefreshView.isFailure()) {
                getBrandRecommend();
            }
        });

        brandRecommendAdapter.setOnItemClickListener((position, brandRecommendBean) -> {
            Intent intent = new Intent();
            intent.putExtra(BaseConstant.DATA_BID, brandRecommendBean.getBrandId());
            intent.putExtra(BaseConstant.DATA_GCID, "");
            BaseApplication.get().finishOk(getActivity(), intent);
        });

        classChildPullRefreshView.setOnClickListener(view -> {
            if (classChildPullRefreshView.isFailure()) {
                getChildAll();
            }
        });

        classChildAdapter.setOnItemClickListener(new ClassChildListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, ClassChildBean classChildBean) {
                Intent intent = new Intent();
                intent.putExtra(BaseConstant.DATA_BID, "");
                intent.putExtra(BaseConstant.DATA_GCID, classChildBean.getGcId());
                BaseApplication.get().finishOk(getActivity(), intent);
            }

            @Override
            public void onItemClick(int position, ClassChildBean classChildBean, ClassChildBean.ChildBean childBean) {
                two = classChildBean.getGcName();
                thr = childBean.getGcName();
                Intent intent = new Intent();
                intent.putExtra(BaseConstant.DATA_BID, "");
                intent.putExtra(BaseConstant.DATA_GCID, childBean.getGcId());
                intent.putExtra(BaseConstant.DATA_CONTENT, one + "->" + two + "->" + thr);
                BaseApplication.get().finishOk(getActivity(), intent);
            }
        });

    }

    @Override
    public void onReturn() {

        if (System.currentTimeMillis() - exitTimeLong > BaseConstant.TIME_EXIT) {
            BaseSnackBar.get().showClickReturn(mainToolbar);
            exitTimeLong = System.currentTimeMillis();
        } else {
            super.onReturn();
        }

    }

    //???????????????

    private void getCate() {

        classPullRefreshView.setLoading();

        ClassModel.get().index(new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                classArrayList.clear();
                //????????????
                ClassBean classBean = new ClassBean();
                classBean.setGcName("????????????");
                classBean.setClick(true);
                classArrayList.add(classBean);
                //????????????
                String data = JsonUtil.getDatasString(baseBean.getDatas(), "class_list");
                classArrayList.addAll(JsonUtil.json2ArrayList(data, ClassBean.class));
                classPullRefreshView.setComplete();
            }

            @Override
            public void onFailure(String reason) {
                classPullRefreshView.setFailure();
            }
        });

    }

    private void getChildAll() {

        classChildPullRefreshView.setLoading();

        ClassModel.get().getChildAll(gcIdString, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                classChildArrayList.clear();
                String data = JsonUtil.getDatasString(baseBean.getDatas(), "class_list");
                classChildArrayList.addAll(JsonUtil.json2ArrayList(data, ClassChildBean.class));
                classChildPullRefreshView.setComplete();
            }

            @Override
            public void onFailure(String reason) {
                classChildPullRefreshView.setFailure();
            }
        });

    }

    private void getBrandRecommend() {

        brandRecommendPullRefreshView.setLoading();

        BrandModel.get().recommendList(new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                brandRecommendArrayList.clear();
                String data = JsonUtil.getDatasString(baseBean.getDatas(), "brand_list");
                brandRecommendArrayList.addAll(JsonUtil.json2ArrayList(data, BrandRecommendBean.class));
                brandRecommendPullRefreshView.setComplete();
            }

            @Override
            public void onFailure(String reason) {
                brandRecommendPullRefreshView.setFailure();
            }
        });

    }

}
