package kotlin.test.com.exiftest.test_class_loader;

/**
 * xmiles
 * Created by lihuange on 2018/11/29.
 */
public class MyClassLoader extends ClassLoader{

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);

    }
}
