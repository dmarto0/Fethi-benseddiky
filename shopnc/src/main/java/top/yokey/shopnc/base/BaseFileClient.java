package top.yokey.shopnc.base;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 文件管理
 *
 * @author MapStory
 * @ qq 1002285057
 * @ project https://gitee.com/MapStory/ShopNc-Android
 */

@SuppressWarnings("ALL")
public class BaseFileClient {

    private static volatile BaseFileClient instance;
    private String packName;

    public static BaseFileClient get() {
        if (instance == null) {
            synchronized (BaseFileClient.class) {
                if (instance == null) {
                    instance = new BaseFileClient();
                }
            }
        }
        return instance;
    }

    public void init(String packName) {

        this.packName = packName;
        createCachePath();
        createImagePath();
        createDownPath();

    }

    public boolean hasSDCard() {

        String str = Environment.getExternalStorageState();
        return str.equals(Environment.MEDIA_MOUNTED);

    }

    public String getRootPath() {

        if (hasSDCard()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        } else {
            return Environment.getDataDirectory().getAbsolutePath() + "/data/";
        }

    }

    public String getDataPath() {

        return getRootPath() + "/Android/data/" + packName + "/";

    }

    public String getDownPath() {

        return getDataPath() + "down/";

    }

    public String getCachePath() {

        return getDataPath() + "cache/";

    }

    public String getImagePath() {

        return getDataPath() + "image/";

    }

    public void createImagePath() {

        File file = new File(getImagePath());
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public void createCachePath() {

        File file = new File(getCachePath());
        if (!file.exists()) {
            file.mkdirs();
        }

    }

    public void createDownPath() {

        File file = new File(getDownPath());
        if (!file.exists()) {
            file.mkdirs();
        }

    }

    public File createImage(String name, Bitmap bitmap) {

        String path = getImagePath() + name + ".jpg";

        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fileOutputStream;
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

}
