package indi.zqc.dynamic.datasource;

import indi.zqc.dynamic.datasource.annotation.EnableDynamicDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * EnableDynamicDataSource 开启动态数据源
 *
 * @author Zhu.Qianchang
 * @date 2019/11/8.
 */
@EnableSwagger2
@EnableDynamicDataSource
@SpringBootApplication
public class DynamicDataSourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DynamicDataSourceApplication.class, args);
    }
}
