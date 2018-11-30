package kotlin.test.com.exiftest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Main2Activity extends AppCompatActivity {

    private static final int TEST_TOAST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        this.bindService(new Intent(getApplicationContext(), MyService.class), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);


        HandlerThread handlerThread = new HandlerThread("lee");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case TEST_TOAST:{
                        Class<Toast> toastClass = Toast.class;
                        Toast toast = Toast.makeText(Main2Activity.this, Looper.myLooper().getThread().getName(),
                                Toast.LENGTH_LONG);
                        try {
                            Field mTN = toastClass.getDeclaredField("mTN");
                            mTN.setAccessible(true);
                            Class<?> tnClass = mTN.get(toast).getClass();
                            Field long_duration_timeout = tnClass.getDeclaredField("LONG_DURATION_TIMEOUT");
                            long_duration_timeout.setAccessible(true);
                            Field modifiersField = Field.class.getDeclaredField("accessFlags");
                            modifiersField.setAccessible(true);
                            modifiersField.setInt(long_duration_timeout, long_duration_timeout.getModifiers() & ~Modifier.FINAL);
                            long_duration_timeout.set(null, 10000);
                            long aLong = long_duration_timeout.getLong(null);
                            Log.i("AAA", " aLong ： " + aLong);

                            Field mDuration = tnClass.getDeclaredField("mDuration");
                            mDuration.setAccessible(true);
                            mDuration.setInt(mTN.get(toast), 0);
                            ;
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        toast.show();
                    }
                    break;
                }

            }


        };

        findViewById(R.id.toast).setOnClickListener(v -> {
            handler.sendEmptyMessage(TEST_TOAST);
        });
    }
}
