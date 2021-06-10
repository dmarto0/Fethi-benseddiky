package top.yokey.shopnc.activity.base;

import android.annotation.SuppressLint;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import top.yokey.base.base.BaseSnackBar;
import top.yokey.shopnc.R;
import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.base.BaseAnimClient;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseConstant;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class BrowserActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private ContentLoadingProgressBar mainProgressBar;
    private WebView mainWebView;

    @Override
    public void initView() {

        setContentView(R.layout.activity_base_browser);
        mainToolbar = findViewById(R.id.mainToolbar);
        mainProgressBar = findViewById(R.id.mainProgressBar);
        mainWebView = findViewById(R.id.mainWebView);

    }

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public void initData() {

        String url = getIntent().getStringExtra(BaseConstant.DATA_URL);
        if (TextUtils.isEmpty(url)) {
            BaseSnackBar.get().show(mainToolbar, "加载默认URL");
            url = BaseConstant.URL;
        }

        BaseApplication.get().setWebView(mainWebView);

        mainWebView.loadUrl(url);
        setToolbar(mainToolbar, "加载中...");

    }

    @Override
    public void initEven() {

        mainWebView.setWebViewClient(new WebViewClient() {
            @Override
            @SuppressWarnings("EmptyMethod")
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        mainWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                mainProgressBar.setProgress(progress);
                if (progress == 100) {
                    if (mainProgressBar.getVisibility() == View.VISIBLE) {
                        BaseAnimClient.get().goneAlpha(mainProgressBar);
                    }
                    mainProgressBar.setVisibility(View.GONE);
                } else {
                    if (mainProgressBar.getVisibility() == View.GONE) {
                        BaseAnimClient.get().showAlpha(mainProgressBar);
                    }
                    mainProgressBar.setVisibility(View.VISIBLE);
                }
                super.onProgressChanged(view, progress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                setToolbar(mainToolbar, title);
            }
        });

    }

    @Override
    public void onReturn() {

        if (mainWebView.canGoBack()) {
            mainWebView.goBack();
        } else {
            super.onReturn();
        }

    }

}
