package top.yokey.shopnc.activity.seller;

import android.text.TextUtils;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseSnackBar;
import top.yokey.base.base.BaseToast;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.bean.ExpressSellerBean;
import top.yokey.base.model.SellerExpressModel;
import top.yokey.base.util.JsonUtil;
import top.yokey.shopnc.R;
import top.yokey.shopnc.adapter.ExpressSellerListAdapter;
import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.view.PullRefreshView;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class ExpressActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private PullRefreshView mainPullRefreshView;
    private AppCompatTextView saveTextView;

    private ExpressSellerListAdapter mainAdapter;
    private ArrayList<ExpressSellerBean> mainArrayList;

    @Override
    public void initView() {

        setContentView(R.layout.activity_seller_express);
        mainToolbar = findViewById(R.id.mainToolbar);
        mainPullRefreshView = findViewById(R.id.mainPullRefreshView);
        saveTextView = findViewById(R.id.saveTextView);

    }

    @Override
    public void initData() {

        setToolbar(mainToolbar, "选择默认物流");

        mainArrayList = new ArrayList<>();
        mainAdapter = new ExpressSellerListAdapter(mainArrayList);
        mainPullRefreshView.getRecyclerView().setAdapter(mainAdapter);

        getData();

    }

    @Override
    public void initEven() {

        mainPullRefreshView.setOnClickListener(view -> {
            if (mainPullRefreshView.isFailure()) {
                getData();
            }
        });

        mainPullRefreshView.setOnRefreshListener(new PullRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }

            @Override
            public void onLoadMore() {
                getData();
            }
        });

        mainAdapter.setOnItemClickListener((position, bean) -> {

        });

        saveTextView.setOnClickListener(view -> save());

    }

    //自定义方法

    private void save() {

        String list;
        ExpressSellerBean expressSellerBean;
        StringBuilder listBuilder = new StringBuilder();
        for (int i = 0; i < mainArrayList.size(); i++) {
            expressSellerBean = mainArrayList.get(i);
            if (expressSellerBean.getIsCheck().equals("1")) {
                listBuilder.append(expressSellerBean.getId()).append(",");
            }
        }
        list = listBuilder.toString();
        if (!TextUtils.isEmpty(list)) {
            list = list.substring(0, list.length() - 1);
        }

        saveTextView.setEnabled(false);
        saveTextView.setText("保存中...");

        SellerExpressModel.get().savedefault(list, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                BaseToast.get().show("保存成功");
                BaseApplication.get().finish(getActivity());
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainToolbar, reason);
                saveTextView.setEnabled(true);
                saveTextView.setText("保存");
            }
        });

    }

    private void getData() {

        mainPullRefreshView.setLoading();

        SellerExpressModel.get().getList(new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                try {
                    mainArrayList.clear();
                    String data = JsonUtil.getDatasString(baseBean.getDatas(), "express_array");
                    JSONObject jsonObject = new JSONObject(data);
                    Iterator iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next().toString();
                        String value = jsonObject.getString(key);
                        mainArrayList.add(JsonUtil.json2Bean(value, ExpressSellerBean.class));
                    }
                    mainPullRefreshView.setComplete();
                } catch (JSONException e) {
                    mainPullRefreshView.setComplete();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String reason) {
                mainPullRefreshView.setFailure();
            }
        });

    }

}
