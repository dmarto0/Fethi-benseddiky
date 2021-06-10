package top.yokey.shopnc.activity.goods;

import android.annotation.SuppressLint;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.squareup.otto.Subscribe;

import top.yokey.shopnc.R;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseFragment;
import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseSnackBar;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.event.GoodsBeanEvent;
import top.yokey.base.model.GoodsModel;
import top.yokey.base.util.TextUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

@ContentView(R.layout.fragment_goods_detailed)
public class DetailedFragment extends BaseFragment {

    @ViewInject(R.id.mainProgressBar)
    private ContentLoadingProgressBar mainProgressBar;
    @ViewInject(R.id.mainWebView)
    private WebView mainWebView;

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public void initData() {

        BaseApplication.get().setWebView(mainWebView);

    }

    @Override
    public void initEven() {

        mainWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mainWebView.loadUrl(url);
                return false;
            }
        });

        mainWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mainProgressBar.setVisibility(View.GONE);
                } else {
                    if (mainProgressBar.getVisibility() == View.GONE) {
                        mainProgressBar.setVisibility(View.VISIBLE);
                    }
                    mainProgressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });

    }

    //自定义方法

    @Subscribe
    @SuppressWarnings("unused")
    public void onGoodsBeanEvent(GoodsBeanEvent event) {
        try {
            String temp = "";
            JSONObject jsonObject = new JSONObject(event.getBaseBean().getDatas());
            jsonObject = new JSONObject(jsonObject.getString("goods_info"));
            String goodsId = jsonObject.getString("goods_id");
            GoodsModel.get().goodsBody(goodsId, new BaseHttpListener() {
                @Override
                public void onSuccess(BaseBean baseBean) {
                    BaseSnackBar.get().show(mainWebView, "数据解析错误！");
                }

                @Override
                public void onFailure(String reason) {
                    mainWebView.loadDataWithBaseURL(
                            null,
                            TextUtil.encodeHtml(getActivity(), reason),
                            "text/html",
                            "UTF-8",
                            null
                    );
                }
            });
        } catch (JSONException e) {
            BaseSnackBar.get().show(mainWebView, "数据解析错误！");
            e.printStackTrace();
        }
    }

}
