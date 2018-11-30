package kotlin.test.com.exiftest;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import kotlin.test.com.exiftest.test_ipc.IPC;

public class MyService extends Service {

    private static final String TAG = "MyService";
    private Messenger mMessenger = new Messenger(new Handler(msg -> {
        switch (msg.what) {
            case IPC.Msg.MSG_CLIENT_TO_SERVICE: {
                Bundle data = msg.getData();
                String s = data.toString();
                Log.i(IPC.Key.TAG, "服务端收到客户端：" + s);

                try {

                    Messenger replyTo = msg.replyTo;
                    Message message = Message.obtain(null, IPC.Msg.MSG_SERVICE_TO_CLIENT);
                    Bundle bundle = new Bundle();
                    bundle.putString(IPC.Key.TAG, "服务收到， 服务发送");
                    message.setData(bundle);
                    replyTo.send(message);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            break;
        }
        return true;
    }));

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "MyService   onCreate");
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "MyService   onCreate");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "MyService   onBind");
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "MyService   onUnbind");

        return super.onUnbind(intent);
    }


}
