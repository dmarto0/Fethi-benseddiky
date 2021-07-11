package top.yokey.shopnc.adapter;

import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

import top.yokey.base.bean.StoreStreetBean;
import top.yokey.shopnc.R;
import top.yokey.shopnc.base.BaseImageLoader;
import top.yokey.shopnc.base.BaseViewHolder;

/**
 * 适配器
 *
 * @author MapStory
 * @ qq 1002285057
 * @ project https://gitee.com/MapStory/ShopNc-Android
 */

public class StoreStreetListAdapter extends RecyclerView.Adapter<StoreStreetListAdapter.ViewHolder> {

    private final ArrayList<StoreStreetBean> arrayList;
    private OnItemClickListener onItemClickListener;

    public StoreStreetListAdapter(ArrayList<StoreStreetBean> arrayList) {
        this.arrayList = arrayList;
        this.onItemClickListener = null;
    }

    @Override
    public int getItemCount() {

        return arrayList.size();

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final int positionInt = position;
        final StoreStreetBean bean = arrayList.get(position);

        BaseImageLoader.get().displayRadius(bean.getStoreAvatarUrl(), holder.mainImageView);
        holder.nameTextView.setText(bean.getStoreName());
        String temp = "粉丝： " + "<font color='#FF0000'>" + bean.getStoreCollect() + "</font> 人";
        holder.favoritesTextView.setText(Html.fromHtml(temp));
        temp = "地址：";
        temp += TextUtils.isEmpty(bean.getStoreAddress()) ? "未填写" : bean.getStoreAddress();
        holder.addressTextView.setText(temp);

        holder.mainRelativeLayout.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onClick(positionInt, bean);
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup group, int viewType) {
        View view = LayoutInflater.from(group.getContext()).inflate(R.layout.item_list_store_street, group, false);
        return new ViewHolder(view);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {

        this.onItemClickListener = listener;

    }

    public interface OnItemClickListener {

        void onClick(int position, StoreStreetBean bean);

    }

    class ViewHolder extends BaseViewHolder {

        @ViewInject(R.id.mainRelativeLayout)
        private RelativeLayout mainRelativeLayout;
        @ViewInject(R.id.mainImageView)
        private AppCompatImageView mainImageView;
        @ViewInject(R.id.nameTextView)
        private AppCompatTextView nameTextView;
        @ViewInject(R.id.favoritesTextView)
        private AppCompatTextView favoritesTextView;
        @ViewInject(R.id.addressTextView)
        private AppCompatTextView addressTextView;

        private ViewHolder(View view) {
            super(view);
        }

    }

}
