package kotlin.test.com.exiftest.test_java;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * xmiles
 * Created by lihuange on 2018/11/12.
 */
public class TestJava {
    private static Object sLock = new Object();

    public static void main(String[] args) {
//        Integer min = min(5, 1, 2, 3, 4, 5, 6);
//        System.out.println("min: " + min);
//
//        try {
//            Class<?> aClass = TestJava.class.getClassLoader().loadClass("kotlin.test.com.exiftest.test_java
// .TestJava" +
//                    "$Point");
//            System.out.println("aClass : " + aClass);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            Class<?> aClass1 = Class.forName("kotlin.test.com.exiftest.test_java.TestJava$Point");
//            System.out.println("aClass1 : " + aClass1);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }


        test3();
    }

    private static void test3() {
        String s = "a dda ddddsea";
        Pattern pattern = Pattern.compile("a");
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            System.out.println("-------------");
            System.out.println(matcher.group());
        }
    }

    private static void test2() {
        Class<?> aClass2 = PointA.class;
        Type genericSuperclass = aClass2.getGenericSuperclass();
        System.out.println("genericSuperclass: " + genericSuperclass);
        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) genericSuperclass;
            Type[] actualTypeArguments = type.getActualTypeArguments();
            for (Type type1 : actualTypeArguments) {
                if (type1 instanceof TypeVariable) {
                    System.out.println("++++++++++++++++++");
                    TypeVariable typeVariable = (TypeVariable) type1;
                    String name = typeVariable.getName();
                    System.out.println("name : " + name);

                    Type[] bounds = typeVariable.getBounds();
                    for (Type type2 : bounds) {
                        System.out.println("bounds : " + type2.getTypeName());
                    }
                }
                System.out.println(type1.getClass());
            }

            Type rawType = type.getRawType();
            Type ownerType = type.getOwnerType();
            System.out.println("rawType : " + rawType);
            System.out.println("ownerType : " + ownerType);
        }
    }

    static class IPoint<T> {

    }


    public static <T extends Comparable> T min(@NonNull T... t) {
        T min = t[0];
        for (T t1 : t) {
            if (min.compareTo(t1) > 0) {
                min = t1;
            }
        }

        return min;
    }

    static class PointA<T extends Number> extends IPoint<T> {

    }


    public interface PointInterface<T, U> {
    }

    public class PointGenericityImpl<T extends Number & Serializable> implements PointInterface<T, Integer> {
    }

    static class Point<T extends Number> {
        T mX;
        T mY;

        public <L> int get() {
            return 5;
        }

        public T getX() {
            return mX;
        }

        public T getY() {
            return mY;
        }

        public void setX(T mX) {
            this.mX = mX;
        }

        public void setY(T mY) {
            this.mY = mY;
        }
    }
}
