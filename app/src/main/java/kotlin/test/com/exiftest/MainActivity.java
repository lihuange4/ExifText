package kotlin.test.com.exiftest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int GET_IMG_CODE = 10001;
    private static final String TAG = "MainActivity";
    private String mPath;
    private ImageView mImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImg = (ImageView) findViewById(R.id.img);

        findViewById(R.id.open_xj).setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mPath = String.format("%s/%tF.png", Environment.getExternalStorageDirectory(), new Date());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mPath)));
            startActivityForResult(intent, GET_IMG_CODE);
        });

        findViewById(R.id.text2).setOnClickListener(v -> {
            TextBug.execute(getApplicationContext());
        });

        findViewById(R.id.text3).setOnClickListener(v -> {
            FixBug.executeFix(this.getApplication());
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == GET_IMG_CODE) {

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    int attributeInt = 0;
                    try {
                        ExifInterface exifInterface = new ExifInterface(mPath);
                        attributeInt = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                        Log.i(TAG, "attributeInt : " + attributeInt);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    File file = new File(mPath);

                    if (file.exists() && file.isFile()) {
                        int imgWidth = 320;
                        Bitmap bitmap = null;
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        options.inSampleSize = options.outWidth / imgWidth;
                        options.inJustDecodeBounds = false;

                        Bitmap bitmap2 = BitmapFactory.decodeFile(file.getPath(), options);
                        if (attributeInt == ExifInterface.ORIENTATION_ROTATE_90) {
                            Matrix matrix = new Matrix();
                            matrix.preRotate(90);
                            bitmap = Bitmap.createBitmap(bitmap2, 0, 0, options.outWidth, options.outHeight,
                                    matrix,
                                    false);
                        } else {
                            bitmap = bitmap2;
                        }

                        final Bitmap finalBitmap = bitmap;

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mImg.setImageBitmap(finalBitmap);
                            }
                        });
                    }
                }
            });
        }
    }

    private void setImgToView(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            mImg.setImageBitmap(bitmap);
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private Executor executor = Executors.newSingleThreadExecutor();
}
