package top.yokey.shopnc.activity.mine;

import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseSnackBar;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.bean.VoucherBean;
import top.yokey.base.model.MemberVoucherModel;
import top.yokey.base.model.SeccodeModel;
import top.yokey.base.util.JsonUtil;
import top.yokey.shopnc.R;
import top.yokey.shopnc.adapter.BaseViewPagerAdapter;
import top.yokey.shopnc.adapter.VoucherListAdapter;
import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseConstant;
import top.yokey.shopnc.base.BaseCountTime;
import top.yokey.shopnc.base.BaseImageLoader;
import top.yokey.shopnc.view.PullRefreshView;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class VoucherActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private TabLayout mainTabLayout;
    private ViewPager mainViewPager;

    private int pageInt;
    private PullRefreshView mainPullRefreshView;
    private VoucherListAdapter mainAdapter;
    private ArrayList<VoucherBean> mainArrayList;
    private String codeKeyString;
    private AppCompatEditText codeEditText;
    private AppCompatEditText captchaEditText;
    private AppCompatImageView captchaImageView;
    private AppCompatTextView submitTextView;

    @Override
    public void initView() {

        setContentView(R.layout.activity_mine_voucher);
        mainToolbar = findViewById(R.id.mainToolbar);
        mainTabLayout = findViewById(R.id.mainTabLayout);
        mainViewPager = findViewById(R.id.mainViewPager);

    }

    @Override
    public void initData() {

        setToolbar(mainToolbar, "???????????????");

        List<String> titleList = new ArrayList<>();
        titleList.add("???????????????");
        titleList.add("???????????????");

        List<View> viewList = new ArrayList<>();
        viewList.add(getLayoutInflater().inflate(R.layout.include_recycler_view, null));
        viewList.add(getLayoutInflater().inflate(R.layout.include_mine_voucher, null));

        //???????????????
        pageInt = 1;
        mainArrayList = new ArrayList<>();
        mainAdapter = new VoucherListAdapter(mainArrayList);
        mainPullRefreshView = viewList.get(0).findViewById(R.id.mainPullRefreshView);
        mainPullRefreshView.getRecyclerView().setPadding(0, BaseApplication.get().dipToPx(8), 0, BaseApplication.get().dipToPx(8));
        mainPullRefreshView.getRecyclerView().setAdapter(mainAdapter);

        //???????????????
        codeKeyString = "";
        codeEditText = viewList.get(1).findViewById(R.id.codeEditText);
        captchaEditText = viewList.get(1).findViewById(R.id.captchaEditText);
        captchaImageView = viewList.get(1).findViewById(R.id.captchaImageView);
        submitTextView = viewList.get(1).findViewById(R.id.submitTextView);

        BaseApplication.get().setTabLayout(mainTabLayout, new BaseViewPagerAdapter(viewList, titleList), mainViewPager);
        mainTabLayout.setTabMode(TabLayout.MODE_FIXED);

        getVoucherList();
        makeCodeKey();

    }

    @Override
    public void initEven() {

        mainPullRefreshView.setOnClickListener(view -> {
            if (mainPullRefreshView.isFailure()) {
                pageInt = 1;
                getVoucherList();
            }
        });

        mainPullRefreshView.setOnRefreshListener(new PullRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageInt = 1;
                getVoucherList();
            }

            @Override
            public void onLoadMore() {
                getVoucherList();
            }
        });

        mainAdapter.setOnItemClickListener((position, voucherBean) -> {

        });

        captchaImageView.setOnClickListener(view -> makeCodeKey());

        submitTextView.setOnClickListener(view -> submit());

    }

    //???????????????

    private void getVoucherList() {

        mainPullRefreshView.setLoading();

        MemberVoucherModel.get().voucherList(pageInt + "", new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                if (pageInt == 1) {
                    mainArrayList.clear();
                }
                if (baseBean.isHasmore()) {
                    pageInt++;
                }
                String data = JsonUtil.getDatasString(baseBean.getDatas(), "voucher_list");
                mainArrayList.addAll(JsonUtil.json2ArrayList(data, VoucherBean.class));
                mainPullRefreshView.setComplete();
            }

            @Override
            public void onFailure(String reason) {
                mainPullRefreshView.setFailure();
            }
        });

    }

    private void makeCodeKey() {

        SeccodeModel.get().makeCodeKey(new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                codeKeyString = JsonUtil.getDatasString(baseBean.getDatas(), "codekey");
                BaseImageLoader.get().display(SeccodeModel.get().makeCode(codeKeyString), captchaImageView);
            }

            @Override
            public void onFailure(String reason) {
                new BaseCountTime(BaseConstant.TIME_COUNT, BaseConstant.TIME_TICK) {
                    @Override
                    public void onFinish() {
                        super.onFinish();
                        makeCodeKey();
                    }
                }.start();
            }
        });

    }

    private void submit() {

        String sn = codeEditText.getText().toString();
        String captcha = captchaEditText.getText().toString();

        if (TextUtils.isEmpty(sn) || TextUtils.isEmpty(captcha)) {
            BaseSnackBar.get().show(mainToolbar, "???????????????????????????");
            return;
        }

        submitTextView.setEnabled(false);
        submitTextView.setText("?????????...");

        MemberVoucherModel.get().voucherPwex(sn, captcha, codeKeyString, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                pageInt = 1;
                makeCodeKey();
                getVoucherList();
                codeEditText.setText("");
                captchaEditText.setText("");
                submitTextView.setEnabled(true);
                submitTextView.setText("????????????");
                BaseSnackBar.get().show(mainToolbar, "?????????????????????");
            }

            @Override
            public void onFailure(String reason) {
                makeCodeKey();
                captchaEditText.setText("");
                submitTextView.setEnabled(true);
                submitTextView.setText("????????????");
                BaseSnackBar.get().show(mainToolbar, reason);
            }
        });

    }

}
