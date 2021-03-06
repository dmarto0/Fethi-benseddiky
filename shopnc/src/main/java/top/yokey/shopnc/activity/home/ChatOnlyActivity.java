package top.yokey.shopnc.activity.home;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.otto.Subscribe;
import com.zhihu.matisse.Matisse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseSnackBar;
import top.yokey.base.base.BaseToast;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.bean.ChatBean;
import top.yokey.base.bean.GoodsBean;
import top.yokey.base.event.MessageCountEvent;
import top.yokey.base.model.MemberChatModel;
import top.yokey.base.util.ImageUtil;
import top.yokey.base.util.JsonUtil;
import top.yokey.shopnc.R;
import top.yokey.shopnc.adapter.ChatOnlyListAdapter;
import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.base.BaseAnimClient;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseConstant;
import top.yokey.shopnc.base.BaseCountTime;
import top.yokey.shopnc.base.BaseFileClient;
import top.yokey.shopnc.base.BaseImageLoader;
import top.yokey.shopnc.view.PullRefreshView;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class ChatOnlyActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private PullRefreshView mainPullRefreshView;
    private AppCompatImageView showImageView;
    private RelativeLayout goodsRelativeLayout;
    private AppCompatImageView goodsImageView;
    private AppCompatTextView goodsTextView;
    private AppCompatTextView moneyTextView;
    private AppCompatImageView imageImageView;
    private AppCompatEditText contentEditText;
    private AppCompatTextView sendTextView;

    private boolean bottomBoolean;
    private GoodsBean goodsBean;
    private String memberIdString;
    private String goodsIdString;
    private String memberNameString;
    private String storeAvatarString;

    private ChatOnlyListAdapter mainAdapter;
    private ArrayList<ChatBean> mainArrayList;

    @Override
    public void initView() {

        setContentView(R.layout.activity_home_chat_only);
        mainToolbar = findViewById(R.id.mainToolbar);
        mainPullRefreshView = findViewById(R.id.mainPullRefreshView);
        showImageView = findViewById(R.id.showImageView);
        goodsRelativeLayout = findViewById(R.id.goodsRelativeLayout);
        goodsImageView = findViewById(R.id.goodsImageView);
        goodsTextView = findViewById(R.id.goodsTextView);
        moneyTextView = findViewById(R.id.moneyTextView);
        imageImageView = findViewById(R.id.imageImageView);
        contentEditText = findViewById(R.id.contentEditText);
        sendTextView = findViewById(R.id.sendTextView);

    }

    @Override
    public void initData() {

        memberIdString = getIntent().getStringExtra(BaseConstant.DATA_MEMBERID);
        goodsIdString = getIntent().getStringExtra(BaseConstant.DATA_GOODSID);

        if (TextUtils.isEmpty(memberIdString)) {
            BaseToast.get().showDataError();
            BaseApplication.get().finish(getActivity());
        }

        bottomBoolean = true;
        goodsBean = new GoodsBean();

        setToolbar(mainToolbar, "...");
        mainArrayList = new ArrayList<>();
        mainAdapter = new ChatOnlyListAdapter(mainArrayList, storeAvatarString);
        mainPullRefreshView.getRecyclerView().setAdapter(mainAdapter);
        mainPullRefreshView.setCanLoadMore(false);
        mainPullRefreshView.setCanRefresh(false);

        if (TextUtils.isEmpty(goodsIdString)) {
            showImageView.setVisibility(View.GONE);
            goodsRelativeLayout.setVisibility(View.GONE);
        }

        getInfo();

    }

    @Override
    public void initEven() {

        showImageView.setOnClickListener(view -> {
            if (goodsRelativeLayout.getVisibility() == View.GONE) {
                goodsRelativeLayout.setVisibility(View.VISIBLE);
                BaseAnimClient.get().showAlpha(goodsRelativeLayout);
            } else {
                goodsRelativeLayout.setVisibility(View.GONE);
                BaseAnimClient.get().goneAlpha(goodsRelativeLayout);
            }
        });

        imageImageView.setOnClickListener(view -> BaseApplication.get().startMatisse(getActivity(), 1, BaseConstant.CODE_ALBUM));

        sendTextView.setOnClickListener(view -> sendMessage());

        mainPullRefreshView.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                bottomBoolean = false;
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();
                    if (lastVisibleItem == (totalItemCount - 1)) {
                        bottomBoolean = true;
                    }
                }
            }
        });

        contentEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomBoolean) {
                    new BaseCountTime(200, 50) {
                        @Override
                        public void onFinish() {
                            super.onFinish();
                            setRecyclerView();
                        }
                    }.start();
                }
            }
        });

        mainAdapter.setOnItemClickListener((position, chatBean) -> {

        });

    }

    @Override
    public void onActivityResult(int req, int res, Intent intent) {
        super.onActivityResult(req, res, intent);
        if (res == RESULT_OK) {
            switch (req) {
                case BaseConstant.CODE_ALBUM:
                    updateImage(Matisse.obtainPathResult(intent).get(0));
                    break;
                default:
                    break;
            }
        }
    }

    //???????????????

    private void getInfo() {

        MemberChatModel.get().getNodeInfo(memberIdString, goodsIdString, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                try {
                    JSONObject jsonObject = new JSONObject(baseBean.getDatas());
                    JSONObject storeJsonObject = new JSONObject(jsonObject.getString("user_info"));
                    memberNameString = storeJsonObject.getString("member_name");
                    storeAvatarString = storeJsonObject.getString("store_avatar");
                    if (jsonObject.has("chat_goods")) {
                        goodsBean = JsonUtil.json2Bean(jsonObject.getString("chat_goods"), GoodsBean.class);
                        BaseImageLoader.get().display(goodsBean.getPic36(), goodsImageView);
                        goodsTextView.setText(goodsBean.getGoodsName());
                        moneyTextView.setText("???");
                        moneyTextView.append(goodsBean.getGoodsPrice());
                    }
                    mainAdapter = new ChatOnlyListAdapter(mainArrayList, storeAvatarString);
                    mainPullRefreshView.getRecyclerView().setAdapter(mainAdapter);
                    setToolbar(mainToolbar, storeJsonObject.getString("store_name"));
                    getMessage();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainToolbar, reason);
                new BaseCountTime(BaseConstant.TIME_COUNT, BaseConstant.TIME_TICK) {
                    @Override
                    public void onFinish() {
                        super.onFinish();
                        getInfo();
                    }
                }.start();
            }
        });

    }

    private void getMessage() {

        MemberChatModel.get().getChatLog(memberIdString, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                mainArrayList.clear();
                String data = JsonUtil.getDatasString(baseBean.getDatas(), "list");
                mainArrayList.addAll(JsonUtil.json2ArrayList(data, ChatBean.class));
                Collections.reverse(mainArrayList);
                mainPullRefreshView.setComplete();
                if (bottomBoolean) {
                    setRecyclerView();
                }
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainToolbar, reason);
                new BaseCountTime(BaseConstant.TIME_COUNT, BaseConstant.TIME_TICK) {
                    @Override
                    public void onFinish() {
                        super.onFinish();
                        getMessage();
                    }
                }.start();
            }
        });

    }

    private void sendMessage() {

        String message = contentEditText.getText().toString();

        if (TextUtils.isEmpty(message)) {
            BaseSnackBar.get().show(mainToolbar, "??????????????????");
            return;
        }

        sendTextView.setEnabled(false);
        sendTextView.setText("?????????...");

        MemberChatModel.get().sendMessage(memberIdString, memberNameString, message, goodsIdString, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                contentEditText.setText("");
                String data = JsonUtil.getDatasString(baseBean.getDatas(), "msg");
                ChatBean chatBean = JsonUtil.json2Bean(data, ChatBean.class);
                mainArrayList.add(chatBean);
                mainPullRefreshView.setComplete();
                sendTextView.setEnabled(true);
                sendTextView.setText("??? ???");
                setRecyclerView();
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainToolbar, reason);
                sendTextView.setEnabled(true);
                sendTextView.setText("??? ???");
            }
        });

    }

    private void setRecyclerView() {

        mainPullRefreshView.getRecyclerView().smoothScrollToPosition(mainArrayList.size());

    }

    private void updateImage(String path) {

        BaseSnackBar.get().showHandler(mainToolbar);

        MemberChatModel.get().imageSend(BaseFileClient.get().createImage("chat_only", ImageUtil.getLocal(path)), new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                try {
                    JSONObject jsonObject = new JSONObject(baseBean.getDatas());
                    String url = jsonObject.getString("url");
                    url = "[SIMG:" + url + "]";
                    contentEditText.setText(url);
                    sendMessage();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String reason) {
                BaseSnackBar.get().show(mainToolbar, reason);
            }
        });

    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onMessageCountEvent(MessageCountEvent event) {

        getMessage();

    }

}
