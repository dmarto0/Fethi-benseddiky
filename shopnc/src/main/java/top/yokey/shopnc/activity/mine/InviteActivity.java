package top.yokey.shopnc.activity.mine;

import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;

import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.base.BaseCountTime;
import top.yokey.base.base.BaseSnackBar;
import top.yokey.base.util.JsonUtil;
import top.yokey.shopnc.R;
import top.yokey.shopnc.base.BaseConstant;
import top.yokey.base.base.BaseHttpListener;
import top.yokey.shopnc.base.BaseImageLoader;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.bean.InviteIndexBean;
import top.yokey.base.model.MemberInviteModel;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class InviteActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private AppCompatImageView qrCodeImageView;
    private AppCompatEditText linkEditText;

    private InviteIndexBean inviteIndexBean;

    @Override
    public void initView() {

        setContentView(R.layout.activity_mine_invite);
        mainToolbar = findViewById(R.id.mainToolbar);
        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        linkEditText = findViewById(R.id.linkEditText);

    }

    @Override
    public void initData() {

        setToolbar(mainToolbar, "邀请返利");
        getData();

    }

    @Override
    public void initEven() {

    }

    //自定义方法

    private void getData() {

        MemberInviteModel.get().index(new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                String data = JsonUtil.getDatasString(baseBean.getDatas(), "member_info");
                inviteIndexBean = JsonUtil.json2Bean(data, InviteIndexBean.class);
                BaseImageLoader.get().display(inviteIndexBean.getMyurlSrc(), qrCodeImageView);
                linkEditText.setText(inviteIndexBean.getMyurl());
                linkEditText.setSelection(linkEditText.getText().length());
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainToolbar, reason);
                new BaseCountTime(BaseConstant.TIME_COUNT, BaseConstant.TIME_TICK) {
                    @Override
                    public void onFinish() {
                        super.onFinish();
                        getData();
                    }
                }.start();
            }
        });

    }

}