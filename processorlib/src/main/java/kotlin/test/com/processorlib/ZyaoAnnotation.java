package kotlin.test.com.processorlib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * xmiles
 * Created by lihuange on 2018/11/20.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface ZyaoAnnotation {
    String name() default "undefined";

    String text() default "";
}
