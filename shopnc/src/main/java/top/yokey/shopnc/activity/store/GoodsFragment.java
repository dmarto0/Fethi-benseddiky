package top.yokey.shopnc.activity.store;

import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.squareup.otto.Subscribe;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseSnackBar;
import top.yokey.base.base.BaseToast;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.bean.GoodsBean;
import top.yokey.base.bean.StoreInfoBean;
import top.yokey.base.event.StoreBeanEvent;
import top.yokey.base.model.MemberCartModel;
import top.yokey.base.model.StoreModel;
import top.yokey.base.util.JsonUtil;
import top.yokey.shopnc.R;
import top.yokey.shopnc.adapter.GoodsListAdapter;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseFragment;
import top.yokey.shopnc.view.PullRefreshView;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

@ContentView(R.layout.fragment_store_goods)
public class GoodsFragment extends BaseFragment {

    @ViewInject(R.id.orderTextView)
    private AppCompatTextView orderTextView;
    @ViewInject(R.id.saleTextView)
    private AppCompatTextView saleTextView;
    @ViewInject(R.id.screenTextView)
    private AppCompatTextView screenTextView;
    @ViewInject(R.id.orientationImageView)
    private AppCompatImageView orientationImageView;
    @ViewInject(R.id.orderLinearLayout)
    private LinearLayoutCompat orderLinearLayout;
    @ViewInject(R.id.screenRelativeLayout)
    private RelativeLayout screenRelativeLayout;
    @ViewInject(R.id.orderCompTextView)
    private AppCompatTextView orderCompTextView;
    @ViewInject(R.id.orderHighTextView)
    private AppCompatTextView orderHighTextView;
    @ViewInject(R.id.orderLowTextView)
    private AppCompatTextView orderLowTextView;
    @ViewInject(R.id.orderHotTextView)
    private AppCompatTextView orderHotTextView;
    @ViewInject(R.id.priceFromEditText)
    private AppCompatEditText priceFromEditText;
    @ViewInject(R.id.priceToEditText)
    private AppCompatEditText priceToEditText;
    @ViewInject(R.id.confirmTextView)
    private AppCompatTextView confirmTextView;
    @ViewInject(R.id.mainPullRefreshView)
    private PullRefreshView mainPullRefreshView;

    private String keyString;
    private String orderString;
    private boolean isGridModel;
    private String priceToString;
    private String priceFromString;

    private int pageInt;
    private StoreInfoBean storeInfoBean;
    private GoodsListAdapter mainAdapter;
    private ArrayList<GoodsBean> mainArrayList;

    @Override
    public void initData() {

        keyString = "";
        orderString = "";
        isGridModel = true;
        priceToString = "";
        priceFromString = "";
        storeInfoBean = new StoreInfoBean();
        orientationImageView.setImageDrawable(BaseApplication.get().getMipmap(R.mipmap.ic_orientation_grid, R.color.grey));

        pageInt = 1;
        mainArrayList = new ArrayList<>();
        mainAdapter = new GoodsListAdapter(mainArrayList, isGridModel);

        setGirdModel();
        getGoods();

    }

    @Override
    public void initEven() {

        orderTextView.setOnClickListener(view -> {
            screenRelativeLayout.setVisibility(View.GONE);
            screenTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
            if (orderLinearLayout.getVisibility() == View.VISIBLE) {
                orderLinearLayout.setVisibility(View.GONE);
                orderTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
            } else {
                orderLinearLayout.setVisibility(View.VISIBLE);
                orderTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0);
            }
        });

        saleTextView.setOnClickListener(view -> order(2, "3", "2"));

        screenTextView.setOnClickListener(view -> {
            orderLinearLayout.setVisibility(View.GONE);
            orderTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
            if (screenRelativeLayout.getVisibility() == View.VISIBLE) {
                screenRelativeLayout.setVisibility(View.GONE);
                screenTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
            } else {
                screenRelativeLayout.setVisibility(View.VISIBLE);
                screenTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0);
            }
        });

        orientationImageView.setOnClickListener(view -> {
            isGridModel = !isGridModel;
            if (isGridModel) {
                setGirdModel();
                orientationImageView.setImageDrawable(BaseApplication.get().getMipmap(R.mipmap.ic_orientation_grid, R.color.grey));
            } else {
                setVerModel();
                orientationImageView.setImageDrawable(BaseApplication.get().getMipmap(R.mipmap.ic_orientation_ver, R.color.grey));
            }
        });

        orderCompTextView.setOnClickListener(view -> {
            orderTextView.setText("????????????");
            order(1, "0", "0");
        });

        orderHighTextView.setOnClickListener(view -> {
            orderTextView.setText("??????????????????");
            order(1, "2", "2");
        });

        orderLowTextView.setOnClickListener(view -> {
            orderTextView.setText("??????????????????");
            order(1, "2", "1");
        });

        orderHotTextView.setOnClickListener(view -> {
            orderTextView.setText("????????????");
            order(1, "5", "2");
        });

        confirmTextView.setOnClickListener(view -> order(3, keyString, orderString));

        mainPullRefreshView.setOnRefreshListener(new PullRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageInt = 1;
                getGoods();
            }

            @Override
            public void onLoadMore() {
                getGoods();
            }
        });

    }

    //???????????????

    private void getGoods() {

        mainPullRefreshView.setLoading();

        StoreModel.get().storeGoods(storeInfoBean.getStoreId(), "", "", orderString, keyString, priceFromString, priceToString, pageInt + "", new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                if (pageInt == 1) {
                    mainArrayList.clear();
                }
                if (pageInt <= baseBean.getPageTotal()) {
                    String data = JsonUtil.getDatasString(baseBean.getDatas(), "goods_list");
                    mainArrayList.addAll(JsonUtil.json2ArrayList(data, GoodsBean.class));
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

    private void setVerModel() {

        mainAdapter = new GoodsListAdapter(mainArrayList, isGridModel);
        mainPullRefreshView.getRecyclerView().setLayoutManager(new LinearLayoutManager(getActivity()));
        mainPullRefreshView.getRecyclerView().setPadding(BaseApplication.get().dipToPx(0), BaseApplication.get().dipToPx(0), BaseApplication.get().dipToPx(0), BaseApplication.get().dipToPx(0));
        mainPullRefreshView.getRecyclerView().setAdapter(mainAdapter);
        mainPullRefreshView.setItemDecoration();
        mainPullRefreshView.setComplete();

        mainAdapter.setOnItemClickListener(new GoodsListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, GoodsBean goodsBean) {
                BaseApplication.get().startGoods(getActivity(), goodsBean.getGoodsId());
            }

            @Override
            public void onCart(int position, GoodsBean goodsBean) {
                addCart(goodsBean.getGoodsId());
            }
        });

    }

    private void setGirdModel() {

        mainAdapter = new GoodsListAdapter(mainArrayList, isGridModel);
        mainPullRefreshView.getRecyclerView().setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mainPullRefreshView.getRecyclerView().setPadding(BaseApplication.get().dipToPx(2), BaseApplication.get().dipToPx(2), BaseApplication.get().dipToPx(2), BaseApplication.get().dipToPx(2));
        mainPullRefreshView.getRecyclerView().setAdapter(mainAdapter);
        mainPullRefreshView.clearItemDecoration();
        mainPullRefreshView.setComplete();

        mainAdapter.setOnItemClickListener(new GoodsListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, GoodsBean goodsBean) {
                BaseApplication.get().startGoods(getActivity(), goodsBean.getGoodsId());
            }

            @Override
            public void onCart(int position, GoodsBean goodsBean) {
                addCart(goodsBean.getGoodsId());
            }
        });

    }

    private void addCart(String goodsId) {

        BaseSnackBar.get().showHandler(mainPullRefreshView);

        MemberCartModel.get().cartAdd(goodsId, "1", new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                BaseToast.get().showSuccess();
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainPullRefreshView, reason);
            }
        });

    }

    private void order(int type, String key, String order) {

        pageInt = 1;
        keyString = key;
        orderString = order;
        priceToString = priceToEditText.getText().toString();
        priceFromString = priceFromEditText.getText().toString();

        orderLinearLayout.setVisibility(View.GONE);
        screenRelativeLayout.setVisibility(View.GONE);

        orderTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
        screenTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);

        switch (type) {
            case 1:
                orderTextView.setTextColor(BaseApplication.get().getColors(R.color.primary));
                saleTextView.setTextColor(BaseApplication.get().getColors(R.color.greyAdd));
                break;
            case 2:
                orderTextView.setTextColor(BaseApplication.get().getColors(R.color.greyAdd));
                saleTextView.setTextColor(BaseApplication.get().getColors(R.color.primary));
                break;
        }

        if (TextUtils.isEmpty(priceFromString) && TextUtils.isEmpty(priceToString)) {
            screenTextView.setTextColor(BaseApplication.get().getColors(R.color.greyAdd));
        } else {
            screenTextView.setTextColor(BaseApplication.get().getColors(R.color.primary));
        }


        mainArrayList.clear();
        mainAdapter.notifyDataSetChanged();

        getGoods();

    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onStoreBeanEvent(StoreBeanEvent event) {

        String data = JsonUtil.getDatasString(event.getBaseBean().getDatas(), "store_info");
        storeInfoBean = JsonUtil.json2Bean(data, StoreInfoBean.class);
        getGoods();

    }

}
