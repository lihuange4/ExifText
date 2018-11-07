package kotlin.test.com.exiftest.test_ipc;

/**
 * xmiles
 * Created by lihuange on 2018/11/7.
 */
public interface IPC {
    interface Msg {
        int BASE_MSG = 20000;
        int MSG_CLIENT_TO_SERVICE = BASE_MSG + 1;
        int MSG_SERVICE_TO_CLIENT = BASE_MSG + 2;
    }

    interface Key{
        String TAG = "test_ipc";
    }
}
