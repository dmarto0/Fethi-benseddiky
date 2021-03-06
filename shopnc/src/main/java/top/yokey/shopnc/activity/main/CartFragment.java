package top.yokey.shopnc.activity.main;

import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

import io.github.xudaojie.qrcodelib.CaptureActivity;
import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseSnackBar;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.bean.CartBean;
import top.yokey.base.event.MainPositionEvent;
import top.yokey.base.model.MemberCartModel;
import top.yokey.base.util.JsonUtil;
import top.yokey.shopnc.R;
import top.yokey.shopnc.activity.base.LoginActivity;
import top.yokey.shopnc.activity.home.ChatListActivity;
import top.yokey.shopnc.adapter.CartListAdapter;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseBusClient;
import top.yokey.shopnc.base.BaseConstant;
import top.yokey.shopnc.base.BaseFragment;
import top.yokey.shopnc.view.PullRefreshView;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

@ContentView(R.layout.fragment_main_cart)
public class CartFragment extends BaseFragment {

    @ViewInject(R.id.searchEditText)
    private AppCompatEditText searchEditText;
    @ViewInject(R.id.messageImageView)
    private AppCompatImageView messageImageView;
    @ViewInject(R.id.scanImageView)
    private AppCompatImageView scanImageView;

    @ViewInject(R.id.tipsRelativeLayout)
    private RelativeLayout tipsRelativeLayout;
    @ViewInject(R.id.tipsTextView)
    private AppCompatTextView tipsTextView;

    @ViewInject(R.id.mainPullRefreshView)
    private PullRefreshView mainPullRefreshView;

    @ViewInject(R.id.lineView)
    private View lineView;
    @ViewInject(R.id.operaLinearLayout)
    private LinearLayoutCompat operaLinearLayout;
    @ViewInject(R.id.moneyTextView)
    private AppCompatTextView moneyTextView;
    @ViewInject(R.id.mainCheckBox)
    private AppCompatCheckBox mainCheckBox;
    @ViewInject(R.id.balanceTextView)
    private AppCompatTextView balanceTextView;

    private int countInt;
    private float moneyFloat;
    private String cartIdString;
    private CartListAdapter mainAdapter;
    private ArrayList<CartBean> mainArrayList;

    @Override
    public void initData() {

        if (BaseApplication.get().isLogin()) {
            tipsTextView.setText("?????????...");
        }

        countInt = 0;
        moneyFloat = 0f;
        cartIdString = "";
        mainArrayList = new ArrayList<>();
        mainAdapter = new CartListAdapter(mainArrayList);
        mainPullRefreshView.getRecyclerView().setAdapter(mainAdapter);
        mainPullRefreshView.setCanLoadMore(false);
        getCart();

    }

    @Override
    public void initEven() {

        scanImageView.setOnClickListener(view -> BaseApplication.get().start(getActivity(), CaptureActivity.class, BaseConstant.CODE_QRCODE));

        searchEditText.setOnClickListener(view -> BaseBusClient.get().post(new MainPositionEvent(2)));

        messageImageView.setOnClickListener(view -> BaseApplication.get().startCheckLogin(getActivity(), ChatListActivity.class));

        tipsRelativeLayout.setOnClickListener(view -> {
            if (!BaseApplication.get().isLogin()) {
                BaseApplication.get().start(getActivity(), LoginActivity.class);
            } else {
                getCart();
            }
        });

        mainPullRefreshView.setOnRefreshListener(new PullRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCart();
            }

            @Override
            public void onLoadMore() {

            }
        });

        mainAdapter.setOnItemClickListener(new CartListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, CartBean cartBean) {

            }

            @Override
            public void onStore(int position, CartBean cartBean) {
                BaseApplication.get().startStore(getActivity(), cartBean.getStoreId());
            }

            @Override
            public void onCheck(int position, boolean isCheck, CartBean cartBean) {
                mainArrayList.get(position).setCheck(isCheck);
                for (int i = 0; i < mainArrayList.get(position).getGoods().size(); i++) {
                    mainArrayList.get(position).getGoods().get(i).setCheck(isCheck);
                }
                mainAdapter.notifyItemChanged(position);
                checkAll();
                calc();
            }

            @Override
            public void onGoods(int position, int positionGoods, CartBean.GoodsBean goodsBean) {
                BaseApplication.get().startGoods(getActivity(), goodsBean.getGoodsId());
            }

            @Override
            public void onGoodsDelete(int position, int positionGoods, CartBean.GoodsBean goodsBean) {
                cartDel(position, positionGoods, goodsBean);
            }

            @Override
            public void onGoodsAdd(int position, int positionGoods, CartBean.GoodsBean goodsBean) {
                int number = Integer.parseInt(goodsBean.getGoodsNum()) + 1;
                cartEditQuantity(goodsBean.getCartId(), number);
            }

            @Override
            public void onGoodsSub(int position, int positionGoods, CartBean.GoodsBean goodsBean) {
                int number = Integer.parseInt(goodsBean.getGoodsNum());
                if (number == 1) {
                    BaseSnackBar.get().show(mainPullRefreshView, "?????????????????????...");
                    return;
                }
                number--;
                cartEditQuantity(goodsBean.getCartId(), number);
            }

            @Override
            public void onGoodsCheck(int position, int positionGoods, boolean isCheck, CartBean.GoodsBean goodsBean) {
                boolean check = true;
                mainArrayList.get(position).getGoods().get(positionGoods).setCheck(isCheck);
                for (int i = 0; i < mainArrayList.get(position).getGoods().size(); i++) {
                    if (!mainArrayList.get(position).getGoods().get(i).isCheck()) {
                        check = false;
                    }
                }
                mainArrayList.get(position).setCheck(check);
                mainAdapter.notifyItemChanged(position);
                checkAll();
                calc();
            }
        });

        mainCheckBox.setOnClickListener(view -> {
            for (int i = 0; i < mainArrayList.size(); i++) {
                mainArrayList.get(i).setCheck(mainCheckBox.isChecked());
                for (int j = 0; j < mainArrayList.get(i).getGoods().size(); j++) {
                    mainArrayList.get(i).getGoods().get(j).setCheck(mainCheckBox.isChecked());
                }
            }
            mainAdapter.notifyDataSetChanged();
            calc();
        });

        balanceTextView.setOnClickListener(view -> BaseApplication.get().startGoodsBuy(getActivity(), cartIdString, "1"));

    }

    @Override
    public void onResume() {
        super.onResume();
        getCart();
    }

    //???????????????

    @SuppressWarnings("StringConcatenationInLoop")
    private void calc() {

        countInt = 0;
        moneyFloat = 0.0f;
        cartIdString = "";

        for (int i = 0; i < mainArrayList.size(); i++) {
            for (int j = 0; j < mainArrayList.get(i).getGoods().size(); j++) {
                if (mainArrayList.get(i).getGoods().get(j).isCheck()) {
                    String cartId = mainArrayList.get(i).getGoods().get(j).getCartId();
                    int count = Integer.parseInt(mainArrayList.get(i).getGoods().get(j).getGoodsNum());
                    float money = Float.parseFloat(mainArrayList.get(i).getGoods().get(j).getGoodsPrice()) * count;
                    countInt += count;
                    moneyFloat += money;
                    cartIdString += cartId + "|" + count + ",";
                }
            }
        }

        if (!TextUtils.isEmpty(cartIdString)) {
            balanceTextView.setEnabled(true);
            cartIdString = cartIdString.substring(0, cartIdString.length() - 1);
        } else {
            balanceTextView.setEnabled(false);
        }

        String temp = "??? <font color='#FF0000'>" + countInt + "</font> ??????" + "?????????" + "<font color='#FF0000'>???" + moneyFloat + " ???</font>";
        moneyTextView.setText(Html.fromHtml(temp));

    }

    private void getCart() {

        MemberCartModel.get().cartList(new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                mainArrayList.clear();
                String data = JsonUtil.getDatasString(baseBean.getDatas(), "cart_list");
                moneyFloat = Float.parseFloat(JsonUtil.getDatasString(baseBean.getDatas(), "sum"));
                countInt = Integer.parseInt(JsonUtil.getDatasString(baseBean.getDatas(), "cart_count"));
                mainArrayList.addAll(JsonUtil.json2ArrayList(data, CartBean.class));
                for (int i = 0; i < mainArrayList.size(); i++) {
                    mainArrayList.get(i).setCheck(true);
                    for (int j = 0; j < mainArrayList.get(i).getGoods().size(); j++) {
                        mainArrayList.get(i).getGoods().get(j).setCheck(true);
                    }
                }
                mainPullRefreshView.setComplete();
                mainCheckBox.setChecked(true);
                if (mainArrayList.size() == 0) {
                    tipsEmpty();
                } else {
                    tipsRelativeLayout.setVisibility(View.GONE);
                    operaLinearLayout.setVisibility(View.VISIBLE);
                    mainPullRefreshView.setVisibility(View.VISIBLE);
                    lineView.setVisibility(View.VISIBLE);
                    calc();
                }
            }

            @Override
            public void onFailure(String reason) {
                tipsRelativeLayout.setVisibility(View.VISIBLE);
                operaLinearLayout.setVisibility(View.GONE);
                mainPullRefreshView.setVisibility(View.GONE);
                lineView.setVisibility(View.GONE);
                tipsTextView.setText(reason);
            }
        });

    }

    private void checkAll() {

        boolean check = true;

        for (int i = 0; i < mainArrayList.size(); i++) {
            for (int j = 0; j < mainArrayList.get(i).getGoods().size(); j++) {
                if (!mainArrayList.get(i).getGoods().get(j).isCheck()) {
                    check = false;
                }
            }
        }

        mainCheckBox.setChecked(check);

    }

    private void tipsEmpty() {

        tipsRelativeLayout.setVisibility(View.VISIBLE);
        operaLinearLayout.setVisibility(View.GONE);
        mainPullRefreshView.setVisibility(View.GONE);
        lineView.setVisibility(View.GONE);
        tipsTextView.setText("????????????????????????????????????");

    }

    private void cartEditQuantity(String cartId, int quantity) {

        BaseSnackBar.get().showHandler(mainPullRefreshView);

        MemberCartModel.get().cartEditQuantity(cartId, quantity + "", new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                getCart();
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainPullRefreshView, reason);
            }
        });

    }

    private void cartDel(final int position, final int positionGoods, CartBean.GoodsBean goodsBean) {

        BaseSnackBar.get().showHandler(mainPullRefreshView);

        MemberCartModel.get().cartDel(goodsBean.getCartId(), new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                mainArrayList.get(position).getGoods().remove(positionGoods);
                if (mainArrayList.get(position).getGoods().size() == 0) {
                    mainArrayList.remove(position);
                }
                if (mainArrayList.size() == 0) {
                    tipsEmpty();
                }
                if (position == 0) {
                    mainAdapter.notifyDataSetChanged();
                } else {
                    mainAdapter.notifyItemChanged(position);
                }
                calc();
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainPullRefreshView, reason);
            }
        });

    }

}
