package top.yokey.shopnc.adapter;

import android.app.Activity;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import top.yokey.shopnc.R;
import top.yokey.shopnc.base.BaseImageLoader;
import top.yokey.shopnc.base.BaseApplication;
import top.yokey.shopnc.base.BaseViewHolder;
import top.yokey.base.bean.HomeBean;

import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

/**
 * 适配器
 *
 * @author MapStory
 * @ qq 1002285057
 * @ project https://gitee.com/MapStory/ShopNc-Android
 */

public class Home3ListAdapter extends RecyclerView.Adapter<Home3ListAdapter.ViewHolder> {

    private final Activity activity;
    private final ArrayList<HomeBean.Home3Bean.ItemBean> arrayList;

    Home3ListAdapter(Activity activity, ArrayList<HomeBean.Home3Bean.ItemBean> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
    }

    @Override
    public int getItemCount() {

        return arrayList.size();

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final HomeBean.Home3Bean.ItemBean bean = arrayList.get(position);

        BaseImageLoader.get().display(bean.getImage(), holder.mainImageView);

        holder.mainLinearLayout.setOnClickListener(view -> BaseApplication.get().startTypeValue(activity, bean.getType(), bean.getData()));

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_home_3, group, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends BaseViewHolder {

        @ViewInject(R.id.mainLinearLayout)
        private LinearLayoutCompat mainLinearLayout;
        @ViewInject(R.id.mainImageView)
        private AppCompatImageView mainImageView;

        private ViewHolder(View view) {
            super(view);
        }

    }

}
