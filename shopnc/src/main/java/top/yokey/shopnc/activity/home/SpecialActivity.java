package top.yokey.shopnc.activity.home;

import android.os.Handler;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import top.yokey.base.base.BaseDialog;
import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseToast;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.bean.HomeBean;
import top.yokey.base.model.IndexModel;
import top.yokey.base.util.JsonUtil;
import top.yokey.shopnc.R;
import top.yokey.shopnc.adapter.HomeListAdapter;
import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseConstant;
import top.yokey.shopnc.base.UBLImageLoader;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class SpecialActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private SwipeRefreshLayout mainSwipeRefreshLayout;
    private Banner mainBanner;
    private RecyclerView mainRecyclerView;

    private String specialId;
    private HomeListAdapter mainAdapter;
    private ArrayList<HomeBean> mainArrayList;

    @Override
    public void initView() {

        setContentView(R.layout.activity_home_special);
        mainToolbar = findViewById(R.id.mainToolbar);
        mainSwipeRefreshLayout = findViewById(R.id.mainSwipeRefreshLayout);
        mainBanner = findViewById(R.id.mainBanner);
        mainRecyclerView = findViewById(R.id.mainRecyclerView);

    }

    @Override
    public void initData() {

        specialId = getIntent().getStringExtra(BaseConstant.DATA_ID);
        if (TextUtils.isEmpty(specialId)) {
            BaseToast.get().showDataError();
            BaseApplication.get().finish(getActivity());
        }

        mainBanner.setImageLoader(new UBLImageLoader());
        mainBanner.setDelayTime(BaseConstant.TIME_DELAY);
        mainBanner.setIndicatorGravity(BannerConfig.CENTER);
        mainBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);

        mainArrayList = new ArrayList<>();
        mainAdapter = new HomeListAdapter(getActivity(), mainArrayList);
        BaseApplication.get().setRecyclerView(getActivity(), mainRecyclerView, mainAdapter);
        BaseApplication.get().setSwipeRefreshLayout(mainSwipeRefreshLayout);

        setToolbar(mainToolbar, "加载中...");
        getData();

    }

    @Override
    public void initEven() {

        mainSwipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            mainSwipeRefreshLayout.setRefreshing(false);
            getData();
        }, BaseConstant.TIME_REFRESH));

    }

    //自定义方法

    private void getData() {

        if (mainArrayList.size() == 0) {
            BaseDialog.get().progress(getActivity());
        }

        IndexModel.get().special(specialId, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                if (mainArrayList.size() == 0) {
                    BaseDialog.get().cancel();
                }
                try {
                    String name;
                    HomeBean indexBean;
                    mainArrayList.clear();
                    JSONObject jsonObject = new JSONObject(baseBean.getDatas());
                    if (baseBean.getDatas().equals("{\"list\":[]}")) {
                        BaseDialog.get().query(getActivity(), "数据出错啦~", "此专题无任何数据...", (dialog, which) -> BaseApplication.get().finish(getActivity()), (dialog, which) -> BaseApplication.get().finish(getActivity()));
                        return;
                    }
                    setToolbar(mainToolbar, jsonObject.getString("special_desc"));
                    String data = jsonObject.getString("list");
                    JSONArray jsonArray = new JSONArray(data);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = new JSONObject(jsonArray.getString(i));
                        //广告图
                        name = "adv_list";
                        if (jsonObject.has(name)) {
                            handlerAdvList(jsonObject.getString(name));
                        }
                        //Home1
                        name = "home1";
                        if (jsonObject.has(name)) {
                            indexBean = new HomeBean();
                            indexBean.setType(name);
                            indexBean.setHome1Bean(JsonUtil.json2Bean(jsonObject.getString(name), HomeBean.Home1Bean.class));
                            mainArrayList.add(indexBean);
                        }
                        //Home2
                        name = "home2";
                        if (jsonObject.has(name)) {
                            indexBean = new HomeBean();
                            indexBean.setType(name);
                            indexBean.setHome2Bean(JsonUtil.json2Bean(jsonObject.getString(name), HomeBean.Home2Bean.class));
                            mainArrayList.add(indexBean);
                        }
                        //Home3
                        name = "home3";
                        if (jsonObject.has(name)) {
                            indexBean = new HomeBean();
                            indexBean.setType(name);
                            indexBean.setHome3Bean(JsonUtil.json2Bean(jsonObject.getString(name), HomeBean.Home3Bean.class));
                            mainArrayList.add(indexBean);
                        }
                        //Home4
                        name = "home4";
                        if (jsonObject.has(name)) {
                            indexBean = new HomeBean();
                            indexBean.setType(name);
                            indexBean.setHome4Bean(JsonUtil.json2Bean(jsonObject.getString(name), HomeBean.Home4Bean.class));
                            mainArrayList.add(indexBean);
                        }
                        //Home5
                        name = "home5";
                        if (jsonObject.has(name)) {
                            indexBean = new HomeBean();
                            indexBean.setType(name);
                            indexBean.setHome5Bean(JsonUtil.json2Bean(jsonObject.getString(name), HomeBean.Home5Bean.class));
                            mainArrayList.add(indexBean);
                        }
                        //Goods
                        name = "goods";
                        if (jsonObject.has(name)) {
                            indexBean = new HomeBean();
                            indexBean.setType(name);
                            indexBean.setGoodsBean(JsonUtil.json2Bean(jsonObject.getString(name), HomeBean.GoodsBean.class));
                            mainArrayList.add(indexBean);
                        }
                        //Goods1
                        name = "goods1";
                        if (jsonObject.has(name)) {
                            indexBean = new HomeBean();
                            indexBean.setType(name);
                            indexBean.setGoods1Bean(JsonUtil.json2Bean(jsonObject.getString(name), HomeBean.Goods1Bean.class));
                            mainArrayList.add(indexBean);
                        }
                        //Goods2
                        name = "goods2";
                        if (jsonObject.has(name)) {
                            indexBean = new HomeBean();
                            indexBean.setType(name);
                            indexBean.setGoods2Bean(JsonUtil.json2Bean(jsonObject.getString(name), HomeBean.Goods2Bean.class));
                            mainArrayList.add(indexBean);
                        }
                    }
                    mainAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    getDataFailure(e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String reason) {
                if (mainArrayList.size() == 0) {
                    BaseDialog.get().cancel();
                }
                getDataFailure(reason);
            }
        });

    }

    private void getDataFailure(String message) {

        BaseDialog.get().queryLoadingFailure(getActivity(), message,
                (dialog, which) -> getData(), (dialog, which) -> BaseApplication.get().finish(getActivity()));

    }

    private void handlerAdvList(String json) {

        try {
            JSONObject jsonObject = new JSONObject(json);
            ArrayList<HomeBean.AdvListBean> arrayList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(jsonObject.getString("item"));
            for (int i = 0; i < jsonArray.length(); i++) {
                arrayList.add(JsonUtil.json2Bean(jsonArray.getString(i), HomeBean.AdvListBean.class));
            }
            if (arrayList.size() == 0) {
                mainBanner.setVisibility(View.GONE);
            } else {
                mainBanner.setVisibility(View.VISIBLE);
                List<String> image = new ArrayList<>();
                final List<String> type = new ArrayList<>();
                final List<String> data = new ArrayList<>();
                for (int i = 0; i < arrayList.size(); i++) {
                    image.add(arrayList.get(i).getImage());
                    type.add(arrayList.get(i).getType());
                    data.add(arrayList.get(i).getData());
                }
                mainBanner.setOnBannerListener(position -> BaseApplication.get().startTypeValue(getActivity(), type.get(position), data.get(position)));
                mainBanner.update(image);
                mainBanner.start();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
