package kotlin.test.com.exiftest;

import android.app.Application;


/**
 * xmiles
 * Created by lihuange on 2018/10/26.
 */
public class TestApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DexFixUtils.loadFixedDex(this);
    }
}
