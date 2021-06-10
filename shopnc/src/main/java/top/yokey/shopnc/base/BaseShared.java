package top.yokey.shopnc.base;

import android.content.SharedPreferences;

/**
 * 基础SharedPreferences
 *
 * @author MapStory
 * @ qq 1002285057
 * @ project https://gitee.com/MapStory/ShopNc-Android
 */

@SuppressWarnings("ALL")
public class BaseShared {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;
    private static volatile BaseShared instance;

    public static BaseShared get() {
        if (instance == null) {
            synchronized (BaseShared.class) {
                if (instance == null) {
                    instance = new BaseShared();
                }
            }
        }
        return instance;
    }

    public void init(SharedPreferences sharedPreferences) {

        this.sharedPreferences = sharedPreferences;
        this.sharedPreferencesEditor = sharedPreferences.edit();

    }

    public String getString(String name) {

        return sharedPreferences.getString(name, "");

    }

    public boolean getBoolean(String name, boolean defValue) {

        return sharedPreferences.getBoolean(name, defValue);

    }

    public void putString(String name, String value) {

        sharedPreferencesEditor.putString(name, value).apply();

    }

    public void putBoolean(String name, boolean value) {

        sharedPreferencesEditor.putBoolean(name, value).apply();

    }

}
