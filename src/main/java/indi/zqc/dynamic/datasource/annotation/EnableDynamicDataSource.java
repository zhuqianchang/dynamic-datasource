package indi.zqc.dynamic.datasource.annotation;

import indi.zqc.dynamic.datasource.configuration.DynamicDataSourceConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启动态数据源
 *
 * @author Zhu.Qianchang
 * @date 2019/11/7.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DynamicDataSourceConfiguration.class)
public @interface EnableDynamicDataSource {
}
