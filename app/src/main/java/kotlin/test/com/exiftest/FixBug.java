package kotlin.test.com.exiftest;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

import java.io.File;

/**
 * xmiles
 * Created by lihuange on 2018/10/25.
 */
public class FixBug {

    private static final String DEX_NAME = "test.dex";

    public static void executeFix(@NonNull Context context) {
        String externalPath = Environment.getExternalStorageDirectory() + File.separator + DEX_NAME;
        boolean isCopySuccess = FileUtils.copyFileTo(externalPath, context.getFilesDir().getAbsolutePath());
        if (isCopySuccess) {
            DexFixUtils.loadFixedDex(context);
        }
    }
}
