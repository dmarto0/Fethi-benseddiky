package top.yokey.shopnc.base;

import android.content.Context;
import android.widget.ImageView;

import com.youth.banner.loader.ImageLoader;

/**
 * Banner图片加载器
 *
 * @author MapStory
 * @ qq 1002285057
 * @ project https://gitee.com/MapStory/ShopNc-Android
 */

@SuppressWarnings("ALL")
public class UBLImageLoader extends ImageLoader {

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {

        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        BaseImageLoader.get().display(path.toString(), imageView);

    }

}
