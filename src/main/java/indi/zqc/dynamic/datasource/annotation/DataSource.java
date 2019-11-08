package indi.zqc.dynamic.datasource.annotation;

import java.lang.annotation.*;

/**
 * @author Zhu.Qianchang
 * @date 2019/11/7.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {

    String value();
}
