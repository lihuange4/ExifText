package kotlin.test.com.exiftest;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import kotlin.test.com.exiftest.test_ipc.IPC;
import kotlin.test.com.processorlib.ZyaoAnnotation;

@ZyaoAnnotation(
        name = "Zyao",
        text = "Hello !!! Welcome "
)
public class MainActivity extends AppCompatActivity {

    private static final int GET_IMG_CODE = 10001;
    private static final String TAG = "MainActivity";
    private String mPath;
    private ImageView mImg;
    private Handler mHandler;
    private Messenger mServiceMessenger;
    private Messenger mClientMessenger;
    private long mSendToServiceTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initHandle();

        mImg = (ImageView) findViewById(R.id.img);

        findViewById(R.id.open_xj).setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mPath = String.format("%s/%tF.png", Environment.getExternalStorageDirectory(), new Date());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mPath)));
            startActivityForResult(intent, GET_IMG_CODE);
        });
        TextBug textBug = new TextBug();
        findViewById(R.id.text2).setOnClickListener(v -> {
            textBug.execute(getApplicationContext());
        });

        findViewById(R.id.text3).setOnClickListener(v -> {
            FixBug.executeFix(this.getApplication());
            System.exit(0);
        });

        mImg.setOnClickListener(v -> {
            Drawable innerImg = TestGetOtherAppResource.getInnerImg(this);
            if (innerImg != null) {
                mImg.setImageDrawable(innerImg);
            }
        });

        findViewById(R.id.test_delay_handler).setOnClickListener((v) -> {
            mHandler.sendEmptyMessageDelayed(IMsg.MSG_TEST_BTN_CLICK, 2000);
        });

        findViewById(R.id.test_handler_barrier).setOnClickListener(v -> {

            Looper looper = Looper.myLooper();
            if (methodInQueue()) {
                postSyncBarrier(looper.getQueue());
            } else {
                postSyncBarrier(looper);
            }


        });

        findViewById(R.id.test_messenger).setOnClickListener(v -> {
            mSendToServiceTime = SystemClock.uptimeMillis();
            try {
                Message message = new Message();
                message.what = IPC.Msg.MSG_CLIENT_TO_SERVICE;
                Bundle bundle = new Bundle();
                bundle.putString(IPC.Key.TAG, "客户端向");
                message.setData(bundle);
                message.replyTo = mClientMessenger;
                mServiceMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });

        findViewById(R.id.test_open_activity2).setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Main2Activity.class));
        });


        handleBindService();

    }

    private void handleBindService() {
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected");
            mServiceMessenger = new Messenger(service);
            mClientMessenger = new Messenger(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected");
            mClientMessenger = null;
        }
    };

//    private void postSyncBarrier(Object obj) {
//        try {
//            Class<?> aClass = obj.getClass();
//            Method postSyncBarrier = aClass.getDeclaredMethod("postSyncBarrier", long.class);
//            postSyncBarrier.setAccessible(true);
//            postSyncBarrier.invoke(obj, 5000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private void postSyncBarrier(Object obj) {
        try {
            Class<?> aClass = obj.getClass();
            if (obj instanceof Looper) {
                Field mQueue = aClass.getDeclaredField("mQueue");
                mQueue.setAccessible(true);
                Object o = mQueue.get(obj); //enqueueSyncBarrier
                Method enqueueSyncBarrier = o.getClass().getDeclaredMethod("enqueueSyncBarrier", long.class);
                enqueueSyncBarrier.setAccessible(true);
                int token = (int) enqueueSyncBarrier.invoke(o, SystemClock.uptimeMillis() + 5000);

                //10秒后清除阻塞
                Message msg = Message.obtain();
                msg.setAsynchronous(true);
                msg.what = IMsg.MSG_CLEAR_BARRIER;
                msg.arg1 = token;
                mHandler.sendMessageDelayed(msg, 10000);

            } else {
                Method postSyncBarrier = aClass.getDeclaredMethod("postSyncBarrier", long.class);
                postSyncBarrier.setAccessible(true);
                postSyncBarrier.invoke(obj, 5000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private void removeSyncBarrier(Object obj, int token) {
        try {
            Class<?> aClass = obj.getClass();
            if (obj instanceof Looper) {
                Field mQueue = aClass.getDeclaredField("mQueue");
                mQueue.setAccessible(true);
                Object o = mQueue.get(obj); //enqueueSyncBarrier
                Method removeSyncBarrier = o.getClass().getDeclaredMethod("removeSyncBarrier", int.class);
                removeSyncBarrier.setAccessible(true);
                removeSyncBarrier.invoke(o, token);

            } else {
                Method postSyncBarrier = aClass.getDeclaredMethod("postSyncBarrier", long.class);
                postSyncBarrier.setAccessible(true);
                postSyncBarrier.invoke(obj, 5000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private Method getHideMethod(String name) {
        Method method = null;
        try {
            if (methodInQueue()) {
                method = MessageQueue.class.getMethod(name);
            } else {
                method = Looper.class.getMethod(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return method;
    }

    private static boolean methodInQueue() {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M;
    }


    private void initHandle() {
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case IMsg.MSG_TEST_BTN_CLICK: {
                        Toast.makeText(getApplicationContext(), "点击了", Toast.LENGTH_LONG).show();
                    }
                    break;

                    case IMsg.MSG_CLEAR_BARRIER: {
                        Looper looper = Looper.myLooper();
                        if (methodInQueue()) {
                            removeSyncBarrier(looper.getQueue(), msg.arg1);
                        } else {
                            removeSyncBarrier(looper, msg.arg1);
                        }
                    }
                    break;

                    case IPC.Msg.MSG_SERVICE_TO_CLIENT: {
                        Bundle data = msg.getData();
                        Log.i(TAG, "客户端收到服务端的信息, 来回一次耗时： " + (SystemClock.uptimeMillis() - mSendToServiceTime));
                    }
                    break;
                }
                return true;
            }
        });
    }

    private interface IMsg {
        int MSG_BASE = 1000;
        int MSG_TEST_BTN_CLICK = MSG_BASE + 1;
        int MSG_BARRIER = MSG_BASE + 2;
        int MSG_CLEAR_BARRIER = MSG_BASE + 3;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == GET_IMG_CODE) {

            executor.execute(() -> {
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

                    mHandler.post(() -> mImg.setImageBitmap(finalBitmap));
                }
            });
        }
    }

    private void setImgToView(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            mImg.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServiceConnection != null ){
            this.unbindService(mServiceConnection);
        }
    }

    private Executor executor = Executors.newSingleThreadExecutor();
}
