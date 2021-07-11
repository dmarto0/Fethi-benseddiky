package top.yokey.shopnc.base;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import top.yokey.shopnc.R;

/**
 * 图片加载器
 *
 * @author MapStory
 * @ qq 1002285057
 * @ project https://gitee.com/MapStory/ShopNc-Android
 */

@SuppressWarnings("ALL")
public class BaseImageLoader {

    private static volatile BaseImageLoader instance;
    private Context context;

    public static BaseImageLoader get() {
        if (instance == null) {
            synchronized (BaseImageLoader.class) {
                if (instance == null) {
                    instance = new BaseImageLoader();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {

        this.context = context;

    }

    public void display(String url, ImageView imageView) {

        if (BaseApplication.get().isImage()) {
            Glide.with(context).load(url).into(imageView);
        } else {
            Glide.with(context).load(R.mipmap.ic_launcher).into(imageView);
        }

    }

    public void displayCircle(String url, ImageView imageView) {

        if (BaseApplication.get().isImage()) {
            Glide.with(context).load(url).apply(new RequestOptions().transform(new CircleTransform(context))).into(imageView);
        } else {
            Glide.with(context).load(R.mipmap.ic_launcher).apply(new RequestOptions().transform(new CircleTransform(context))).into(imageView);
        }

    }

    public void displayRadius(String url, ImageView imageView) {

        if (BaseApplication.get().isImage()) {
            Glide.with(context).load(url).apply(new RequestOptions().transform(new RadiusTransform(context, 2))).into(imageView);
        } else {
            Glide.with(context).load(R.mipmap.ic_launcher).apply(new RequestOptions().transform(new RadiusTransform(context, 2))).into(imageView);
        }

    }

    public void display(String url, int width, int height, ImageView imageView) {

        if (BaseApplication.get().isImage()) {
            Glide.with(context).load(url).apply(new RequestOptions().override(width, height)).into(imageView);
        } else {
            Glide.with(context).load(R.mipmap.ic_launcher).apply(new RequestOptions().override(width, height)).into(imageView);
        }

    }

}
