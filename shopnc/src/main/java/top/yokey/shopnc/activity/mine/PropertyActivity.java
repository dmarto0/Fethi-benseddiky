package top.yokey.shopnc.activity.mine;

import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import top.yokey.base.base.BaseSnackBar;
import top.yokey.shopnc.R;
import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.base.BaseApplication;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class PropertyActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private RelativeLayout preDepositRelativeLayout;
    private AppCompatTextView preDepositValueTextView;
    private RelativeLayout rechargeCardRelativeLayout;
    private AppCompatTextView rechargeCardValueTextView;
    private RelativeLayout voucherRelativeLayout;
    private AppCompatTextView voucherValueTextView;
    private RelativeLayout redPacketRelativeLayout;
    private AppCompatTextView redPacketValueTextView;
    private RelativeLayout pointsRelativeLayout;
    private AppCompatTextView pointsValueTextView;

    @Override
    public void initView() {

        setContentView(R.layout.activity_mine_property);
        mainToolbar = findViewById(R.id.mainToolbar);
        preDepositRelativeLayout = findViewById(R.id.preDepositRelativeLayout);
        preDepositValueTextView = findViewById(R.id.preDepositValueTextView);
        rechargeCardRelativeLayout = findViewById(R.id.rechargeCardRelativeLayout);
        rechargeCardValueTextView = findViewById(R.id.rechargeCardValueTextView);
        voucherRelativeLayout = findViewById(R.id.voucherRelativeLayout);
        voucherValueTextView = findViewById(R.id.voucherValueTextView);
        redPacketRelativeLayout = findViewById(R.id.redPacketRelativeLayout);
        redPacketValueTextView = findViewById(R.id.redPacketValueTextView);
        pointsRelativeLayout = findViewById(R.id.pointsRelativeLayout);
        pointsValueTextView = findViewById(R.id.pointsValueTextView);

    }

    @Override
    public void initData() {

        setToolbar(mainToolbar, "我的财产");

        preDepositValueTextView.setText(BaseApplication.get().getMemberAssetBean().getPredepoit());
        preDepositValueTextView.append("元");
        rechargeCardValueTextView.setText(BaseApplication.get().getMemberAssetBean().getAvailableRcBalance());
        rechargeCardValueTextView.append("元");
        voucherValueTextView.setText(BaseApplication.get().getMemberAssetBean().getVoucher());
        voucherValueTextView.append("张");
        redPacketValueTextView.setText(BaseApplication.get().getMemberAssetBean().getRedpacket());
        redPacketValueTextView.append("个");
        pointsValueTextView.setText(BaseApplication.get().getMemberAssetBean().getPoint());
        pointsValueTextView.append("分");

    }

    @Override
    public void initEven() {

        preDepositRelativeLayout.setOnClickListener(view -> {
            if (BaseApplication.get().getMemberAssetBean() == null) {
                BaseSnackBar.get().showHandler(mainToolbar);
                return;
            }
            BaseApplication.get().startCheckLogin(getActivity(), PreDepositActivity.class);
        });

        rechargeCardRelativeLayout.setOnClickListener(view -> {
            if (BaseApplication.get().getMemberAssetBean() == null) {
                BaseSnackBar.get().showHandler(mainToolbar);
                return;
            }
            BaseApplication.get().startCheckLogin(getActivity(), RechargeCardActivity.class);
        });

        voucherRelativeLayout.setOnClickListener(view -> {
            if (BaseApplication.get().getMemberAssetBean() == null) {
                BaseSnackBar.get().showHandler(mainToolbar);
                return;
            }
            BaseApplication.get().startCheckLogin(getActivity(), VoucherActivity.class);
        });

        redPacketRelativeLayout.setOnClickListener(view -> {
            if (BaseApplication.get().getMemberAssetBean() == null) {
                BaseSnackBar.get().showHandler(mainToolbar);
                return;
            }
            BaseApplication.get().startCheckLogin(getActivity(), RedPacketActivity.class);
        });

        pointsRelativeLayout.setOnClickListener(view -> {
            if (BaseApplication.get().getMemberAssetBean() == null) {
                BaseSnackBar.get().showHandler(mainToolbar);
                return;
            }
            BaseApplication.get().startCheckLogin(getActivity(), PointsActivity.class);
        });

    }

}
