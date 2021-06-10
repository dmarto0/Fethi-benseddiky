package top.yokey.shopnc.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseConstant;

@SuppressWarnings("ALL")
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseApplication.get().setIwxapi(WXAPIFactory.createWXAPI(this, BaseConstant.WX_APP_ID));
        BaseApplication.get().getIwxapi().handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        BaseApplication.get().getIwxapi().handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            switch (resp.errCode) {
                case 0:
                    BaseApplication.get().setSuccess(true);
                    break;
                case -1:
                    BaseApplication.get().setSuccess(false);
                    break;
                case -2:
                    BaseApplication.get().setSuccess(false);
                    break;
            }
            finish();
        }
    }

}
