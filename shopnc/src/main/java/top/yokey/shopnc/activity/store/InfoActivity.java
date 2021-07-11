package top.yokey.shopnc.activity.store;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.ContentLoadingProgressBar;

import top.yokey.base.base.BaseToast;
import top.yokey.shopnc.R;
import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseConstant;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class InfoActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private WebView mainWebView;
    private ContentLoadingProgressBar mainProgressBar;

    private String storeId;

    @Override
    public void initView() {

        setContentView(R.layout.activity_base_browser);
        mainToolbar = findViewById(R.id.mainToolbar);
        mainWebView = findViewById(R.id.mainWebView);
        mainProgressBar = findViewById(R.id.mainProgressBar);

    }

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public void initData() {

        storeId = getIntent().getStringExtra(BaseConstant.DATA_ID);

        if (TextUtils.isEmpty(storeId)) {
            BaseToast.get().showDataError();
            BaseApplication.get().finish(getActivity());
        }

        setToolbar(mainToolbar, "店铺信息");
        BaseApplication.get().setWebView(mainWebView);
        mainWebView.loadUrl(BaseConstant.URL_STORE_INFO + storeId);

    }

    @Override
    public void initEven() {

        mainWebView.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (url.contains("tel:")) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
                return null;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mainWebView.loadUrl(url.contains(BaseConstant.URL_STORE_INFO) ? url : BaseConstant.URL_STORE_INFO + storeId);
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

}
