package top.yokey.shopnc.activity.main;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sunfusheng.marqueeview.MarqueeView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import io.github.xudaojie.qrcodelib.CaptureActivity;
import top.yokey.shopnc.R;
import top.yokey.shopnc.activity.base.LoginActivity;
import top.yokey.shopnc.activity.home.ChatListActivity;
import top.yokey.shopnc.activity.home.NoticeActivity;
import top.yokey.shopnc.activity.mine.FootprintActivity;
import top.yokey.shopnc.activity.mine.PropertyActivity;
import top.yokey.shopnc.activity.mine.SignActivity;
import top.yokey.shopnc.activity.points.ListActivity;
import top.yokey.shopnc.activity.store.StreetActivity;
import top.yokey.shopnc.adapter.HomeListAdapter;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseBusClient;
import top.yokey.shopnc.base.BaseConstant;
import top.yokey.shopnc.base.BaseFragment;
import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseSnackBar;
import top.yokey.shopnc.base.UBLImageLoader;
import top.yokey.base.bean.ArticleBean;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.bean.HomeBean;
import top.yokey.base.event.MainPositionEvent;
import top.yokey.base.model.IndexModel;
import top.yokey.base.util.JsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

@ContentView(R.layout.fragment_main_home)
public class HomeFragment extends BaseFragment {

    @ViewInject(R.id.mainSwipeRefreshLayout)
    private SwipeRefreshLayout mainSwipeRefreshLayout;
    @ViewInject(R.id.searchEditText)
    private AppCompatEditText searchEditText;
    @ViewInject(R.id.messageImageView)
    private AppCompatImageView messageImageView;
    @ViewInject(R.id.scanImageView)
    private AppCompatImageView scanImageView;

    @ViewInject(R.id.mainBanner)
    private Banner mainBanner;
    @ViewInject(R.id.categoryTextView)
    private AppCompatTextView categoryTextView;
    @ViewInject(R.id.pointsTextView)
    private AppCompatTextView pointsTextView;
    @ViewInject(R.id.storeTextView)
    private AppCompatTextView storeTextView;
    @ViewInject(R.id.signTextView)
    private AppCompatTextView signTextView;
    @ViewInject(R.id.mineTextView)
    private AppCompatTextView mineTextView;
    @ViewInject(R.id.orderTextView)
    private AppCompatTextView orderTextView;
    @ViewInject(R.id.propertyTextView)
    private AppCompatTextView propertyTextView;
    @ViewInject(R.id.footprintTextView)
    private AppCompatTextView footprintTextView;
    @ViewInject(R.id.noticeMarqueeView)
    private MarqueeView noticeMarqueeView;
    @ViewInject(R.id.noticeTextView)
    private AppCompatTextView noticeTextView;

    @ViewInject(R.id.mainRecyclerView)
    private RecyclerView mainRecyclerView;

    private HomeListAdapter mainAdapter;
    private ArrayList<HomeBean> mainArrayList;
    private ArrayList<ArticleBean> articleArrayList;

    @Override
    public void initData() {

        articleArrayList = new ArrayList<>();
        mainBanner.setImageLoader(new UBLImageLoader());
        mainBanner.setDelayTime(BaseConstant.TIME_DELAY);
        mainBanner.setIndicatorGravity(BannerConfig.CENTER);
        mainBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);

        mainArrayList = new ArrayList<>();
        mainAdapter = new HomeListAdapter(getActivity(), mainArrayList);
        BaseApplication.get().setRecyclerView(getActivity(), mainRecyclerView, mainAdapter);
        BaseApplication.get().setSwipeRefreshLayout(mainSwipeRefreshLayout);
        getIndex();

    }

    @Override
    public void initEven() {

        scanImageView.setOnClickListener(view -> BaseApplication.get().start(getActivity(), CaptureActivity.class, BaseConstant.CODE_QRCODE));

        searchEditText.setOnClickListener(view -> BaseBusClient.get().post(new MainPositionEvent(2)));

        messageImageView.setOnClickListener(view -> BaseApplication.get().startCheckLogin(getActivity(), ChatListActivity.class));

        mainSwipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            mainSwipeRefreshLayout.setRefreshing(false);
            getIndex();
            getGG();
        }, BaseConstant.TIME_REFRESH));

        categoryTextView.setOnClickListener(view -> BaseBusClient.get().post(new MainPositionEvent(1)));

        pointsTextView.setOnClickListener(view -> BaseApplication.get().start(getActivity(), ListActivity.class));

        storeTextView.setOnClickListener(view -> BaseApplication.get().start(getActivity(), StreetActivity.class));

        signTextView.setOnClickListener(view -> BaseApplication.get().startCheckLogin(getActivity(), SignActivity.class));

        mineTextView.setOnClickListener(view -> BaseBusClient.get().post(new MainPositionEvent(4)));

        orderTextView.setOnClickListener(view -> BaseApplication.get().startOrder(getActivity(), 0));

        propertyTextView.setOnClickListener(view -> {
            if (!BaseApplication.get().isLogin()) {
                BaseApplication.get().start(getActivity(), LoginActivity.class);
                return;
            }
            if (BaseApplication.get().getMemberAssetBean() == null) {
                BaseSnackBar.get().showHandler(mainSwipeRefreshLayout);
                return;
            }
            BaseApplication.get().start(getActivity(), PropertyActivity.class);
        });

        footprintTextView.setOnClickListener(view -> BaseApplication.get().startCheckLogin(getActivity(), FootprintActivity.class));

        noticeMarqueeView.setOnItemClickListener((position, textView) -> BaseApplication.get().startNoticeShow(getActivity(), articleArrayList.get(position)));

        noticeTextView.setOnClickListener(view -> BaseApplication.get().start(getActivity(), NoticeActivity.class));

    }

    @Override
    public void onStart() {
        super.onStart();
        mainBanner.startAutoPlay();
        noticeMarqueeView.startFlipping();
    }

    @Override
    public void onStop() {
        super.onStop();
        mainBanner.stopAutoPlay();
        noticeMarqueeView.stopFlipping();
    }

    //自定义方法

    private void getIndex() {

        IndexModel.get().index(new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                try {
                    String name;
                    HomeBean indexBean;
                    JSONObject jsonObject;
                    mainArrayList.clear();
                    JSONArray jsonArray = new JSONArray(baseBean.getDatas());
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
                    e.printStackTrace();
                }
                getGG();
            }

            @Override
            public void onFailure(String reason) {

            }
        });

    }

    private void getGG() {

        IndexModel.get().getGG(new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                articleArrayList.clear();
                List<String> list = new ArrayList<>();
                String data = JsonUtil.getDatasString(baseBean.getDatas(), "article_list");
                articleArrayList.addAll(JsonUtil.json2ArrayList(data, ArticleBean.class));
                for (int i = 0; i < articleArrayList.size(); i++) {
                    list.add(articleArrayList.get(i).getArticleTitle());
                }
                noticeMarqueeView.startWithList(list);
            }

            @Override
            public void onFailure(String reason) {

            }
        });

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
