package top.yokey.shopnc.activity.seller;

import android.content.Intent;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseSnackBar;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.util.JsonUtil;
import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.view.PullRefreshView;
import top.yokey.shopnc.R;
import top.yokey.shopnc.adapter.AddressSellerListAdapter;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseConstant;
import top.yokey.base.bean.AddressSellerBean;
import top.yokey.base.model.SellerAddressModel;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class AddressActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private AppCompatImageView toolbarImageView;
    private PullRefreshView mainPullRefreshView;

    private AddressSellerListAdapter mainAdapter;
    private ArrayList<AddressSellerBean> mainArrayList;

    @Override
    public void initView() {

        setContentView(R.layout.activity_recycler_view);
        mainToolbar = findViewById(R.id.mainToolbar);
        toolbarImageView = findViewById(R.id.toolbarImageView);
        mainPullRefreshView = findViewById(R.id.mainPullRefreshView);

    }

    @Override
    public void initData() {

        setToolbar(mainToolbar, "发货地址管理");
        toolbarImageView.setImageResource(R.drawable.ic_action_add);

        mainArrayList = new ArrayList<>();
        mainAdapter = new AddressSellerListAdapter(mainArrayList);
        mainPullRefreshView.getRecyclerView().setAdapter(mainAdapter);

    }

    @Override
    public void initEven() {

        toolbarImageView.setOnClickListener(view -> BaseApplication.get().startCheckSellerLogin(getActivity(), AddressAddActivity.class));

        mainPullRefreshView.setOnRefreshListener(new PullRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAddress();
            }

            @Override
            public void onLoadMore() {
                getAddress();
            }
        });

        mainAdapter.setOnItemClickListener(new AddressSellerListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, AddressSellerBean bean) {
                Intent intent = new Intent(getActivity(), AddressEditActivity.class);
                intent.putExtra(BaseConstant.DATA_BEAN, bean);
                BaseApplication.get().startCheckSellerLogin(getActivity(), intent);
            }

            @Override
            public void onEdit(int position, AddressSellerBean bean) {
                Intent intent = new Intent(getActivity(), AddressEditActivity.class);
                intent.putExtra(BaseConstant.DATA_BEAN, bean);
                BaseApplication.get().startCheckSellerLogin(getActivity(), intent);
            }

            @Override
            public void onDelete(int position, AddressSellerBean bean) {
                deleteAddress(bean.getAddressId());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getAddress();
    }

    //自定义方法

    private void getAddress() {

        mainPullRefreshView.setLoading();

        SellerAddressModel.get().addressList(new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                mainArrayList.clear();
                String data = JsonUtil.getDatasString(baseBean.getDatas(), "address_list");
                mainArrayList.addAll(JsonUtil.json2ArrayList(data, AddressSellerBean.class));
                mainPullRefreshView.setComplete();
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainToolbar, reason);
            }
        });

    }

    private void deleteAddress(String addressId) {

        BaseSnackBar.get().showHandler(mainToolbar);

        SellerAddressModel.get().addressDel(addressId, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                getAddress();
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainToolbar, reason);
            }
        });

    }

}
