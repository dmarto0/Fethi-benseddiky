package top.yokey.shopnc.activity.home;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import top.yokey.shopnc.R;
import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseConstant;
import top.yokey.base.base.BaseToast;
import top.yokey.base.bean.ArticleBean;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class NoticeShowActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private AppCompatTextView contentTextView;

    @Override
    public void initView() {

        setContentView(R.layout.activity_home_notice_show);
        mainToolbar = findViewById(R.id.mainToolbar);
        contentTextView = findViewById(R.id.contentTextView);

    }

    @Override
    public void initData() {

        ArticleBean articleBean = (ArticleBean) getIntent().getSerializableExtra(BaseConstant.DATA_BEAN);

        if (articleBean == null) {
            BaseToast.get().showDataError();
            BaseApplication.get().finish(getActivity());
        }

        assert articleBean != null;
        contentTextView.setText(articleBean.getArticleContent());
        setToolbar(mainToolbar, articleBean.getArticleTitle());

    }

    @Override
    public void initEven() {

    }

}
