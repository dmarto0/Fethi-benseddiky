package top.yokey.shopnc.activity.refund;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.zhihu.matisse.Matisse;

import java.util.ArrayList;
import java.util.Vector;

import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseSnackBar;
import top.yokey.base.base.BaseToast;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.bean.FileUploadBean;
import top.yokey.base.bean.RefundApplyBean;
import top.yokey.base.model.MemberRefundModel;
import top.yokey.base.util.ImageUtil;
import top.yokey.base.util.JsonUtil;
import top.yokey.shopnc.R;
import top.yokey.shopnc.adapter.GoodsRefundApplyListAdapter;
import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseConstant;
import top.yokey.shopnc.base.BaseCountTime;
import top.yokey.shopnc.base.BaseFileClient;
import top.yokey.shopnc.base.BaseImageLoader;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class RefundApplyActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private AppCompatTextView storeNameTextView;
    private RecyclerView mainRecyclerView;
    private LinearLayoutCompat zengPinLinearLayout;
    private AppCompatTextView zengPinDescTextView;
    private AppCompatImageView zengPinGoodsImageView;
    private RelativeLayout goodsRelativeLayout;
    private AppCompatImageView goodsImageView;
    private AppCompatTextView goodsNameTextView;
    private AppCompatTextView goodsMoneyTextView;
    private AppCompatTextView goodsSpecTextView;
    private AppCompatTextView goodsNumberTextView;
    private AppCompatTextView reasonTextView;
    private AppCompatSpinner reasonSpinner;
    private AppCompatTextView moneyTextView;
    private AppCompatEditText remarkEditText;
    private AppCompatImageView zeroImageView;
    private AppCompatImageView oneImageView;
    private AppCompatImageView twoImageView;
    private AppCompatTextView submitTextView;

    private int positionInt;
    private String orderIdString;
    private String goodsIdString;
    private String reasonIdString;
    private String refundPic0String;
    private String refundPic1String;
    private String refundPic2String;
    private Vector<String> reasonIdVector;
    private RefundApplyBean orderRefundBean;
    private Vector<String> reasonContentVector;

    private GoodsRefundApplyListAdapter mainAdapter;
    private ArrayList<RefundApplyBean.GoodsListBean> mainArrayList;

    @Override
    public void initView() {

        setContentView(R.layout.activity_refund_apply);
        mainToolbar = findViewById(R.id.mainToolbar);
        storeNameTextView = findViewById(R.id.storeNameTextView);
        mainRecyclerView = findViewById(R.id.mainRecyclerView);
        zengPinLinearLayout = findViewById(R.id.zengPinLinearLayout);
        zengPinDescTextView = findViewById(R.id.zengPinDescTextView);
        zengPinGoodsImageView = findViewById(R.id.zengPinGoodsImageView);
        goodsRelativeLayout = findViewById(R.id.goodsRelativeLayout);
        goodsImageView = findViewById(R.id.goodsImageView);
        goodsNameTextView = findViewById(R.id.goodsNameTextView);
        goodsMoneyTextView = findViewById(R.id.goodsMoneyTextView);
        goodsSpecTextView = findViewById(R.id.goodsSpecTextView);
        goodsNumberTextView = findViewById(R.id.goodsNumberTextView);
        reasonTextView = findViewById(R.id.reasonTextView);
        reasonSpinner = findViewById(R.id.reasonSpinner);
        moneyTextView = findViewById(R.id.moneyTextView);
        remarkEditText = findViewById(R.id.remarkEditText);
        zeroImageView = findViewById(R.id.zeroImageView);
        oneImageView = findViewById(R.id.oneImageView);
        twoImageView = findViewById(R.id.twoImageView);
        submitTextView = findViewById(R.id.submitTextView);

    }

    @Override
    public void initData() {

        orderIdString = getIntent().getStringExtra(BaseConstant.DATA_ID);
        goodsIdString = getIntent().getStringExtra(BaseConstant.DATA_GOODSID);
        if (TextUtils.isEmpty(orderIdString)) {
            BaseToast.get().showDataError();
            BaseApplication.get().finish(getActivity());
        }

        orderRefundBean = new RefundApplyBean();
        reasonIdVector = new Vector<>();
        reasonContentVector = new Vector<>();

        positionInt = 0;
        reasonIdString = "0";
        refundPic0String = "";
        refundPic1String = "";
        refundPic2String = "";
        mainArrayList = new ArrayList<>();
        mainAdapter = new GoodsRefundApplyListAdapter(mainArrayList);
        BaseApplication.get().setRecyclerView(getActivity(), mainRecyclerView, mainAdapter);

        if (TextUtils.isEmpty(goodsIdString)) {
            setToolbar(mainToolbar, "????????????");
            getDataAll();
        } else {
            setToolbar(mainToolbar, "????????????");
            getData();
        }

    }

    @Override
    public void initEven() {

        storeNameTextView.setOnClickListener(view -> BaseApplication.get().startStore(getActivity(), orderRefundBean.getOrder().getStoreId()));

        mainAdapter.setOnItemClickListener((position, goodsListBean) -> BaseApplication.get().startGoods(getActivity(), goodsListBean.getGoodsId()));

        reasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reasonIdString = reasonIdVector.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        zeroImageView.setOnClickListener(view -> {
            positionInt = 0;
            BaseApplication.get().startMatisse(getActivity(), 1, BaseConstant.CODE_ALBUM);
        });

        oneImageView.setOnClickListener(view -> {
            positionInt = 1;
            BaseApplication.get().startMatisse(getActivity(), 1, BaseConstant.CODE_ALBUM);
        });

        twoImageView.setOnClickListener(view -> {
            positionInt = 2;
            BaseApplication.get().startMatisse(getActivity(), 1, BaseConstant.CODE_ALBUM);
        });

        submitTextView.setOnClickListener(view -> {
            if (TextUtils.isEmpty(goodsIdString)) {
                submitAll();
            } else {
                submit();
            }
        });

    }

    @Override
    public void onActivityResult(int req, int res, Intent intent) {
        super.onActivityResult(req, res, intent);
        if (res == RESULT_OK && req == BaseConstant.CODE_ALBUM) {
            updateImage(Matisse.obtainPathResult(intent).get(0));
        }
    }

    //???????????????

    private void submit() {

        String remark = remarkEditText.getText().toString();

        if (TextUtils.isEmpty(remark)) {
            BaseSnackBar.get().show(mainToolbar, "??????????????????");
            return;
        }

        submitTextView.setEnabled(false);
        submitTextView.setText("?????????...");

        MemberRefundModel.get().refundPost(orderIdString, goodsIdString, "1", reasonIdString, orderRefundBean.getOrder().getOrderAmount(), remark, refundPic0String, refundPic1String, refundPic2String, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                if (JsonUtil.isSuccess(baseBean.getDatas())) {
                    BaseToast.get().show("???????????????");
                    BaseApplication.get().finish(getActivity());
                } else {
                    BaseToast.get().showFailure();
                    submitTextView.setEnabled(true);
                    submitTextView.setText("??? ???");
                }
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainToolbar, reason);
                submitTextView.setEnabled(true);
                submitTextView.setText("??? ???");
            }
        });

    }

    private void getData() {

        BaseSnackBar.get().showHandler(mainToolbar);

        MemberRefundModel.get().refundForm(orderIdString, goodsIdString, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                orderRefundBean = JsonUtil.json2Bean(baseBean.getDatas(), RefundApplyBean.class);
                storeNameTextView.setText(orderRefundBean.getOrder().getStoreName());
                moneyTextView.setText("???");
                moneyTextView.append(orderRefundBean.getGoods().getGoodsPayPrice());
                zengPinLinearLayout.setVisibility(View.GONE);
                goodsRelativeLayout.setVisibility(View.VISIBLE);
                BaseImageLoader.get().display(orderRefundBean.getGoods().getGoodsImg360(), goodsImageView);
                goodsNameTextView.setText(orderRefundBean.getGoods().getGoodsName());
                goodsMoneyTextView.setText("???");
                goodsMoneyTextView.append(orderRefundBean.getGoods().getGoodsPrice());
                goodsSpecTextView.setText(orderRefundBean.getGoods().getGoodsSpec());
                goodsNumberTextView.setText("x");
                goodsNumberTextView.append(orderRefundBean.getGoods().getGoodsNum());
                reasonSpinner.setVisibility(View.VISIBLE);
                reasonTextView.setVisibility(View.GONE);
                reasonIdVector.clear();
                reasonContentVector.clear();
                for (int i = 0; i < orderRefundBean.getReasonList().size(); i++) {
                    if (i == 0) {
                        reasonIdString = orderRefundBean.getReasonList().get(i).getReasonId();
                    }
                    reasonIdVector.add(orderRefundBean.getReasonList().get(i).getReasonId());
                    reasonContentVector.add(orderRefundBean.getReasonList().get(i).getReasonInfo());
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.simple_spinner_item, reasonContentVector);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                reasonSpinner.setAdapter(arrayAdapter);
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

    private void submitAll() {

        submitTextView.setEnabled(false);
        submitTextView.setText("?????????...");

        MemberRefundModel.get().refundAllPost(orderIdString, remarkEditText.getText().toString(), refundPic0String, refundPic1String, refundPic2String, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                if (JsonUtil.isSuccess(baseBean.getDatas())) {
                    BaseToast.get().show("???????????????");
                    BaseApplication.get().finish(getActivity());
                } else {
                    BaseToast.get().showFailure();
                    submitTextView.setEnabled(true);
                    submitTextView.setText("??? ???");
                }
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainToolbar, reason);
                submitTextView.setEnabled(true);
                submitTextView.setText("??? ???");
            }
        });

    }

    private void getDataAll() {

        BaseSnackBar.get().showHandler(mainToolbar);

        MemberRefundModel.get().refundAllFrom(orderIdString, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                orderRefundBean = JsonUtil.json2Bean(baseBean.getDatas(), RefundApplyBean.class);
                storeNameTextView.setText(orderRefundBean.getOrder().getStoreName());
                mainArrayList.clear();
                mainArrayList.addAll(orderRefundBean.getGoodsList());
                mainAdapter.notifyDataSetChanged();
                moneyTextView.setText("???");
                moneyTextView.append(orderRefundBean.getOrder().getAllowRefundAmount());
                goodsRelativeLayout.setVisibility(View.GONE);
                reasonSpinner.setVisibility(View.GONE);
                reasonTextView.setVisibility(View.VISIBLE);
                if (orderRefundBean.getGiftList().size() == 0) {
                    zengPinLinearLayout.setVisibility(View.GONE);
                } else {
                    zengPinLinearLayout.setVisibility(View.VISIBLE);
                    zengPinDescTextView.setText(orderRefundBean.getGiftList().get(0).getGoodsName());
                    zengPinDescTextView.append(" x" + orderRefundBean.getGiftList().get(0).getGoodsNum());
                    BaseImageLoader.get().display(orderRefundBean.getGiftList().get(0).getGoodsImg360(), zengPinGoodsImageView);
                }
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainToolbar, reason);
                new BaseCountTime(BaseConstant.TIME_COUNT, BaseConstant.TIME_TICK) {
                    @Override
                    public void onFinish() {
                        super.onFinish();
                        getDataAll();
                    }
                }.start();
            }
        });

    }

    private void updateImage(String path) {

        BaseSnackBar.get().showHandler(mainToolbar);

        MemberRefundModel.get().uploadPic(BaseFileClient.get().createImage("evaluate" + positionInt, ImageUtil.getLocal(path)), new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                FileUploadBean fileUploadBean = JsonUtil.json2Bean(baseBean.getDatas(), FileUploadBean.class);
                switch (positionInt) {
                    case 0:
                        refundPic0String = fileUploadBean.getFileName();
                        BaseImageLoader.get().display(fileUploadBean.getPic(), zeroImageView);
                        break;
                    case 1:
                        refundPic1String = fileUploadBean.getFileName();
                        BaseImageLoader.get().display(fileUploadBean.getPic(), oneImageView);
                        break;
                    case 2:
                        refundPic2String = fileUploadBean.getFileName();
                        BaseImageLoader.get().display(fileUploadBean.getPic(), twoImageView);
                        break;
                }
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainToolbar, reason);
            }
        });

    }

}
