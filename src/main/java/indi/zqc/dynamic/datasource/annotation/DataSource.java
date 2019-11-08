package indi.zqc.dynamic.datasource.annotation;

import java.lang.annotation.*;

/**
 * DataSource注解，用于指定数据源，建议在service层使用
 *
 * @author Zhu.Qianchang
 * @date 2019/11/7.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {

    String value();
}
