package top.yokey.shopnc.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.xutils.x;

/**
 * 基础ViewHolder
 *
 * @author MapStory
 * @ qq 1002285057
 * @ project https://gitee.com/MapStory/ShopNc-Android
 */

@SuppressWarnings("ALL")
public class BaseViewHolder extends RecyclerView.ViewHolder {

    public BaseViewHolder(View view) {
        super(view);
        x.view().inject(this, view);
    }

}
