package indi.zqc.dynamic.datasource.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import indi.zqc.dynamic.datasource.aspect.DynamicDataSourceAspect;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * @author Zhu.Qianchang
 * @date 2019/11/7.
 */
public class DynamicDataSourceConfiguration {

    @Bean(name = "db1")
    @ConfigurationProperties(prefix = DynamicDataSource.PREFIX + ".db1")
    public DataSource dataSource1() {
        return DataSourceBuilder.create().type(DruidDataSource.class).build();
    }

    @Bean(name = "db2")
    @ConfigurationProperties(prefix = DynamicDataSource.PREFIX + ".db2")
    public DataSource dataSource2() {
        return DataSourceBuilder.create().type(DruidDataSource.class).build();
    }

    @Bean
    @Primary
    public DynamicDataSource dynamicDataSource() {
        return new DynamicDataSource();
    }

    @Bean
    public DynamicDataSourceAspect dynamicDataSourceAspect() {
        return new DynamicDataSourceAspect();
    }

    @Bean
    public PlatformTransactionManager transactionManager(DynamicDataSource dynamicDataSource) {
        return new DataSourceTransactionManager(dynamicDataSource);
    }
}
