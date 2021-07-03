package top.yokey.shopnc.activity.home;

import androidx.appcompat.widget.Toolbar;

import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.view.PullRefreshView;
import top.yokey.shopnc.R;
import top.yokey.shopnc.adapter.ArticleListAdapter;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.bean.ArticleBean;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.model.ArticleModel;
import top.yokey.base.util.JsonUtil;

import java.util.ArrayList;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class NoticeActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private PullRefreshView mainPullRefreshView;

    private ArticleListAdapter mainAdapter;
    private ArrayList<ArticleBean> mainArrayList;

    @Override
    public void initView() {

        setContentView(R.layout.activity_recycler_view);
        mainToolbar = findViewById(R.id.mainToolbar);
        mainPullRefreshView = findViewById(R.id.mainPullRefreshView);

    }

    @Override
    public void initData() {

        setToolbar(mainToolbar, "商城公告");

        mainArrayList = new ArrayList<>();
        mainAdapter = new ArticleListAdapter(mainArrayList);
        mainPullRefreshView.getRecyclerView().setAdapter(mainAdapter);
        mainPullRefreshView.setCanLoadMore(false);

        getNotice();

    }

    @Override
    public void initEven() {

        mainPullRefreshView.setOnClickListener(view -> {
            if (mainPullRefreshView.isFailure()) {
                getNotice();
            }
        });


        mainPullRefreshView.setOnRefreshListener(new PullRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNotice();
            }

            @Override
            public void onLoadMore() {

            }
        });

        mainAdapter.setOnItemClickListener((position, articleBean) -> BaseApplication.get().startNoticeShow(getActivity(), articleBean));

    }

    //自定义方法

    private void getNotice() {

        mainPullRefreshView.setLoading();

        ArticleModel.get().articleList(new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                mainArrayList.clear();
                String data = JsonUtil.getDatasString(baseBean.getDatas(), "article_list");
                mainArrayList.addAll(JsonUtil.json2ArrayList(data, ArticleBean.class));
                mainPullRefreshView.setComplete();
            }

            @Override
            public void onFailure(String reason) {
                mainPullRefreshView.setFailure();
            }
        });

    }

}
