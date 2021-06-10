package top.yokey.shopnc.base;

import com.squareup.otto.Bus;

/**
 * OTTO
 *
 * @author MapStory
 * @ qq 1002285057
 * @ project https://gitee.com/MapStory/ShopNc-Android
 */

@SuppressWarnings("ALL")
public class BaseBusClient extends Bus {

    private static volatile BaseBusClient instance;

    public static BaseBusClient get() {
        if (instance == null) {
            synchronized (BaseBusClient.class) {
                if (instance == null) {
                    instance = new BaseBusClient();
                }
            }
        }
        return instance;
    }

}
