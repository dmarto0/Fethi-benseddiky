package top.yokey.shopnc.activity.choose;

import android.content.Intent;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseSnackBar;
import top.yokey.base.bean.AddressBean;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.model.MemberAddressModel;
import top.yokey.base.util.JsonUtil;
import top.yokey.shopnc.R;
import top.yokey.shopnc.activity.mine.AddressAddActivity;
import top.yokey.shopnc.activity.mine.AddressEditActivity;
import top.yokey.shopnc.adapter.AddressListAdapter;
import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseConstant;
import top.yokey.shopnc.view.PullRefreshView;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class AddressActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private AppCompatImageView toolbarImageView;
    private PullRefreshView mainPullRefreshView;

    private long exitTimeLong;
    private AddressListAdapter mainAdapter;
    private ArrayList<AddressBean> mainArrayList;

    @Override
    public void initView() {

        setContentView(R.layout.activity_recycler_view);
        mainToolbar = findViewById(R.id.mainToolbar);
        toolbarImageView = findViewById(R.id.toolbarImageView);
        mainPullRefreshView = findViewById(R.id.mainPullRefreshView);

    }

    @Override
    public void initData() {

        setToolbar(mainToolbar, "选择地址");
        toolbarImageView.setImageResource(R.drawable.ic_action_add);

        exitTimeLong = 0L;
        mainArrayList = new ArrayList<>();
        mainAdapter = new AddressListAdapter(mainArrayList);
        mainPullRefreshView.getRecyclerView().setAdapter(mainAdapter);

    }

    @Override
    public void initEven() {

        toolbarImageView.setOnClickListener(view -> BaseApplication.get().start(getActivity(), AddressAddActivity.class));

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

        mainAdapter.setOnItemClickListener(new AddressListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, AddressBean addressBean) {
                Intent intent = new Intent();
                intent.putExtra(BaseConstant.DATA_ID, addressBean.getAddressId());
                BaseApplication.get().finishOk(getActivity(), intent);
            }

            @Override
            public void onEdit(int position, AddressBean addressBean) {
                Intent intent = new Intent(getActivity(), AddressEditActivity.class);
                intent.putExtra(BaseConstant.DATA_BEAN, addressBean);
                BaseApplication.get().start(getActivity(), intent);
            }

            @Override
            public void onDelete(int position, AddressBean addressBean) {
                delAddress(addressBean.getAddressId());
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        getAddress();
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

    //自定义方法

    private void getAddress() {

        mainPullRefreshView.setLoading();

        MemberAddressModel.get().addressList(new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                mainArrayList.clear();
                String data = JsonUtil.getDatasString(baseBean.getDatas(), "address_list");
                mainArrayList.addAll(JsonUtil.json2ArrayList(data, AddressBean.class));
                mainPullRefreshView.setComplete();
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainToolbar, reason);
            }
        });

    }

    private void delAddress(String addressId) {

        BaseSnackBar.get().showHandler(mainToolbar);

        MemberAddressModel.get().addressDel(addressId, new BaseHttpListener() {
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
