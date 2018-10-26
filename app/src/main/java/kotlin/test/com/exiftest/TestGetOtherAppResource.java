package kotlin.test.com.exiftest;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

/**
 * xmiles
 * Created by lihuange on 2018/10/26.
 */
public class TestGetOtherAppResource {
    private static final String PKG_NAME = "com.moneyfanli.fanli";
    private static final String APP_FILE_NAME = "caimizhijia.apk";

    public static Drawable getInnerImg(Context context) {
        try {
            Context packageContext = context.createPackageContext(PKG_NAME, Context.CONTEXT_INCLUDE_CODE | Context
                    .CONTEXT_IGNORE_SECURITY);

            ClassLoader classLoader = packageContext.getClassLoader();
            Resources resources = packageContext.getResources();

            try {
                Class<?> aClass = classLoader.loadClass(PKG_NAME + ".R$drawable");
                //public static final int guide_new_page0_bg = 0x333333;
                Field field = aClass.getField("guide_new_page0_bg");
                int id = (int) field.get(null);

                return resources.getDrawable(id);

            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Drawable getUninstallResource(Context context) {
        try {
            String absolutePath = context.getFilesDir().getAbsolutePath();
            String appFileName = "caimizhijia.apk";
            boolean isCopySuccess = FileUtils.copyFileTo(Environment.getExternalStorageDirectory() + "/" + appFileName, absolutePath);

            if (isCopySuccess) {
                DexClassLoader dexClassLoader = new DexClassLoader(absolutePath + "/caimizhijia.apk", absolutePath, null, context.getClassLoader());

                AssetManager assetManager = AssetManager.class.newInstance();
                Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
                addAssetPath.invoke(assetManager, absolutePath + "/" + appFileName);

                Resources resources = new Resources(assetManager, context.getResources().getDisplayMetrics(), context.getResources().getConfiguration());

                Class<?> aClass = dexClassLoader.loadClass(PKG_NAME + ".R$drawable");
                //public static final int guide_new_page0_bg = 0x333333;
                Field field = aClass.getField("guide_new_page0_bg");
                int id = (int) field.get(null);

                return resources.getDrawable(id);
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (Exception e){

        }

        return null;
    }
}
