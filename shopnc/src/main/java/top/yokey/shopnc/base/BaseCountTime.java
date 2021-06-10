package top.yokey.shopnc.base;

import android.os.CountDownTimer;

/**
 * 倒计时
 *
 * @author MapStory
 * @ qq 1002285057
 * @ project https://gitee.com/MapStory/ShopNc-Android
 */

@SuppressWarnings("All")
public class BaseCountTime extends CountDownTimer {

    public BaseCountTime(long millis, long count) {
        super(millis, count);
    }

    @Override
    public void onTick(long millisUntilFinished) {

    }

    @Override
    public void onFinish() {

    }

}
