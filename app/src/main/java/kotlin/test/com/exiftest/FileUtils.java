package kotlin.test.com.exiftest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * xmiles
 * Created by lihuange on 2018/10/26.
 */
public class FileUtils {
    public static boolean copyFileTo(String originPath, String copyTo) {
        boolean result = false;
        File dexFile = new File(originPath);
        if (dexFile.exists() && dexFile.isFile()) {
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                bis = new BufferedInputStream(new FileInputStream(dexFile));

                File copyToFile = new File(copyTo + File.separator + dexFile.getName());

                if (copyToFile.exists() && copyToFile.isFile()) {
                    copyToFile.delete();
                }

                if (copyToFile.createNewFile()) {
                    bos = new BufferedOutputStream(new FileOutputStream(copyToFile));

                    byte[] bytes = new byte[1024 * 10];
                    int len = -1;
                    while ((len = bis.read(bytes)) > 0) {
                        bos.write(bytes, 0, len);
                        bos.flush();
                    }
                }

                result = true;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (bos != null) {
                    try {
                        bos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        return result;
    }
}
