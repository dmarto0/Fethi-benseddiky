package top.yokey.shopnc.base;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;

/**
 * 动画
 *
 * @author MapStory
 * @ qq 1002285057
 * @ project https://gitee.com/MapStory/ShopNc-Android
 */

@SuppressWarnings("ALL")
public class BaseAnimClient {

    private int time;
    private static volatile BaseAnimClient instance;

    public static BaseAnimClient get() {
        if (instance == null) {
            synchronized (BaseAnimClient.class) {
                if (instance == null) {
                    instance = new BaseAnimClient();
                }
            }
        }
        return instance;
    }

    public void init(int time) {

        this.time = time;

    }

    public void showAlpha(View view) {

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation.setDuration(time);
        view.startAnimation(alphaAnimation);

    }

    public void showAlpha(View view, int time) {

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        alphaAnimation.setDuration(time);
        view.startAnimation(alphaAnimation);

    }

    public void goneAlpha(View view) {

        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(time);
        view.startAnimation(alphaAnimation);

    }

    public void goneAlpha(View view, int time) {

        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(time);
        view.startAnimation(alphaAnimation);

    }

    public void upTranslate(View view) {

        TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 600f, 0.0f);
        translateAnimation.setDuration(time);
        view.startAnimation(translateAnimation);

    }

    public void upTranslate(View view, int time) {

        TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 600f, 0.0f);
        translateAnimation.setDuration(time);
        view.startAnimation(translateAnimation);

    }

    public void upTranslate(View view, float y) {

        TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, y, 0.0f);
        translateAnimation.setDuration(time);
        view.startAnimation(translateAnimation);

    }

    public void upTranslate(View view, int time, float y) {

        TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, y, 0.0f);
        translateAnimation.setDuration(time);
        view.startAnimation(translateAnimation);

    }

    public void downTranslate(View view) {

        TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 600f);
        translateAnimation.setDuration(time);
        view.startAnimation(translateAnimation);

    }

    public void downTranslate(View view, int time) {

        TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, 600f);
        translateAnimation.setDuration(time);
        view.startAnimation(translateAnimation);

    }

    public void downTranslate(View view, float y) {

        TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, y);
        translateAnimation.setDuration(time);
        view.startAnimation(translateAnimation);

    }

    public void downTranslate(View view, int time, float y) {

        TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, y);
        translateAnimation.setDuration(time);
        view.startAnimation(translateAnimation);

    }

    public void leftTranslate(View view) {

        TranslateAnimation translateAnimation = new TranslateAnimation(600f, 0.0f, 0.0f, 0.0f);
        translateAnimation.setDuration(time);
        view.startAnimation(translateAnimation);

    }

    public void leftTranslate(View view, int time) {

        TranslateAnimation translateAnimation = new TranslateAnimation(600f, 0.0f, 0.0f, 0.0f);
        translateAnimation.setDuration(time);
        view.startAnimation(translateAnimation);

    }

    public void leftTranslate(View view, float x) {

        TranslateAnimation translateAnimation = new TranslateAnimation(x, 0.0f, 0.0f, 0.0f);
        translateAnimation.setDuration(time);
        view.startAnimation(translateAnimation);

    }

    public void leftTranslate(View view, int time, float x) {

        TranslateAnimation translateAnimation = new TranslateAnimation(x, 0.0f, 0.0f, 0.0f);
        translateAnimation.setDuration(time);
        view.startAnimation(translateAnimation);

    }

    public void rightTranslate(View view) {

        TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 600f, 0.0f, 0.0f);
        translateAnimation.setDuration(time);
        view.startAnimation(translateAnimation);

    }

    public void rightTranslate(View view, int time) {

        TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 600f, 0.0f, 0.0f);
        translateAnimation.setDuration(time);
        view.startAnimation(translateAnimation);

    }

    public void rightTranslate(View view, float x) {

        TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, x, 0.0f, 0.0f);
        translateAnimation.setDuration(time);
        view.startAnimation(translateAnimation);

    }

    public void rightTranslate(View view, int time, float x) {

        TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, x, 0.0f, 0.0f);
        translateAnimation.setDuration(time);
        view.startAnimation(translateAnimation);

    }

}
