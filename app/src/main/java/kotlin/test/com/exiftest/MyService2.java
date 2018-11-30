package kotlin.test.com.exiftest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import kotlin.test.com.exiftest.aidl.IMyAidlInterface;

public class MyService2 extends Service {
    public MyService2() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    class MyBinder extends IMyAidlInterface.Stub {

        @Override
        public String getName() throws RemoteException {
            return "MyService2";
        }
    }
}
