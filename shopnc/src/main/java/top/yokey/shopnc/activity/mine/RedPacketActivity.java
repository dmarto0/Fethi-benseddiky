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
import top.yokey.base.bean.RedPacketBean;
import top.yokey.base.model.MemberRedPackerModel;
import top.yokey.base.model.SeccodeModel;
import top.yokey.base.util.JsonUtil;
import top.yokey.shopnc.R;
import top.yokey.shopnc.adapter.BaseViewPagerAdapter;
import top.yokey.shopnc.adapter.RedPacketListAdapter;
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

public class RedPacketActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private TabLayout mainTabLayout;
    private ViewPager mainViewPager;

    private int pageInt;
    private PullRefreshView mainPullRefreshView;
    private RedPacketListAdapter mainAdapter;
    private ArrayList<RedPacketBean> mainArrayList;

    private String codeKeyString;
    private AppCompatEditText codeEditText;
    private AppCompatEditText captchaEditText;
    private AppCompatImageView captchaImageView;
    private AppCompatTextView submitTextView;

    @Override
    public void initView() {

        setContentView(R.layout.activity_mine_red_packet);
        mainToolbar = findViewById(R.id.mainToolbar);
        mainTabLayout = findViewById(R.id.mainTabLayout);
        mainViewPager = findViewById(R.id.mainViewPager);

    }

    @Override
    public void initData() {

        setToolbar(mainToolbar, "????????????");

        List<String> titleList = new ArrayList<>();
        titleList.add("????????????");
        titleList.add("????????????");

        List<View> viewList = new ArrayList<>();
        viewList.add(getLayoutInflater().inflate(R.layout.include_recycler_view, null));
        viewList.add(getLayoutInflater().inflate(R.layout.include_mine_red_packet, null));

        //???????????????
        pageInt = 1;
        mainArrayList = new ArrayList<>();
        mainAdapter = new RedPacketListAdapter(mainArrayList);
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

        getRedPack();
        makeCodeKey();

    }

    @Override
    public void initEven() {

        mainPullRefreshView.setOnClickListener(view -> {
            if (mainPullRefreshView.isFailure()) {
                pageInt = 1;
                getRedPack();
            }
        });

        mainPullRefreshView.setOnRefreshListener(new PullRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageInt = 1;
                getRedPack();
            }

            @Override
            public void onLoadMore() {
                getRedPack();
            }
        });

        mainAdapter.setOnItemClickListener((position, redPacketBean) -> {

        });

        captchaImageView.setOnClickListener(view -> makeCodeKey());

        submitTextView.setOnClickListener(view -> submit());

    }

    //???????????????

    private void getRedPack() {

        mainPullRefreshView.setLoading();

        MemberRedPackerModel.get().redPacketList(pageInt + "", new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                if (pageInt == 1) {
                    mainArrayList.clear();
                }
                if (baseBean.isHasmore()) {
                    pageInt++;
                }
                String data = JsonUtil.getDatasString(baseBean.getDatas(), "redpacket_list");
                mainArrayList.addAll(JsonUtil.json2ArrayList(data, RedPacketBean.class));
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

        MemberRedPackerModel.get().redPacketPwex(sn, captcha, codeKeyString, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                pageInt = 1;
                getRedPack();
                makeCodeKey();
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
