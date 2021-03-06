package top.yokey.shopnc.activity.refund;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Vector;

import top.yokey.base.base.BaseHttpListener;
import top.yokey.base.base.BaseSnackBar;
import top.yokey.base.base.BaseToast;
import top.yokey.base.bean.BaseBean;
import top.yokey.base.bean.ExpressBean;
import top.yokey.base.model.MemberReturnModel;
import top.yokey.base.util.JsonUtil;
import top.yokey.shopnc.R;
import top.yokey.shopnc.base.BaseActivity;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseConstant;
import top.yokey.shopnc.base.BaseCountTime;

/**
 * @author MapStory
 * @ qq 1002285057
 * @ Project https://gitee.com/MapStory/ShopNc-Android
 */

public class ReturnDeliveryActivity extends BaseActivity {

    private Toolbar mainToolbar;
    private AppCompatSpinner expressSpinner;
    private AppCompatEditText noEditText;
    private AppCompatTextView submitTextView;

    private String returnIdString;
    private String expressIdString;
    private Vector<String> idVector;
    private Vector<String> nameVector;

    @Override
    public void initView() {

        setContentView(R.layout.activity_return_delivery);
        mainToolbar = findViewById(R.id.mainToolbar);
        expressSpinner = findViewById(R.id.expressSpinner);
        noEditText = findViewById(R.id.noEditText);
        submitTextView = findViewById(R.id.submitTextView);

    }

    @Override
    public void initData() {

        returnIdString = getIntent().getStringExtra(BaseConstant.DATA_ID);
        if (TextUtils.isEmpty(returnIdString)) {
            BaseToast.get().showDataError();
            BaseApplication.get().finish(getActivity());
        }

        expressIdString = "";
        idVector = new Vector<>();
        nameVector = new Vector<>();
        setToolbar(mainToolbar, "????????????");

        getData();

    }

    @Override
    public void initEven() {

        expressSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                expressIdString = idVector.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        submitTextView.setOnClickListener(view -> submit());

    }

    //???????????????

    private void submit() {

        String no = noEditText.getText().toString();
        if (TextUtils.isEmpty(no)) {
            BaseSnackBar.get().show(mainToolbar, "?????????????????????");
            return;
        }

        submitTextView.setEnabled(false);
        submitTextView.setText("?????????...");

        MemberReturnModel.get().shipPost(returnIdString, expressIdString, no, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                BaseToast.get().show("???????????????");
                BaseApplication.get().finish(getActivity());
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

        MemberReturnModel.get().shipForm(returnIdString, new BaseHttpListener() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                idVector.clear();
                nameVector.clear();
                String data = JsonUtil.getDatasString(baseBean.getDatas(), "express_list");
                ArrayList<ExpressBean> arrayList = new ArrayList<>(JsonUtil.json2ArrayList(data, ExpressBean.class));
                for (int i = 0; i < arrayList.size(); i++) {
                    idVector.add(arrayList.get(i).getExpressId());
                    nameVector.add(arrayList.get(i).getExpressName());
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.simple_spinner_item, nameVector);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                expressSpinner.setAdapter(arrayAdapter);
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
