package kotlin.test.com.exiftest;

import android.content.Context;
import android.widget.Toast;

/**
 * xmiles
 * Created by lihuange on 2018/10/25.
 */
public class TextBug {
    public static void execute(Context context){
        Toast.makeText(context, "测试一个bug: " + 10 / 0, Toast.LENGTH_LONG).show();
    }
}
