package indi.zqc.dynamic.datasource.configuration;

import indi.zqc.dynamic.datasource.holder.DataSourceHolder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 动态数据源
 *
 * @author Zhu.Qianchang
 * @date 2019/11/7.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = DynamicDataSource.PREFIX)
public class DynamicDataSource extends AbstractRoutingDataSource implements ApplicationContextAware {

    public static final String PREFIX = "indi.zqc.dynamic.datasource";

    private ApplicationContext applicationContext;

    private List<String> routes;

    @Override
    public void afterPropertiesSet() {
        initDataSource();
        super.afterPropertiesSet();
    }

    private void initDataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        Object defaultTargetDataSource = null;

        if (!CollectionUtils.isEmpty(routes)) {
            Map<String, DataSource> dataSources = applicationContext.getBeansOfType(DataSource.class);
            for (String route : routes) {
                DataSource dataSource = dataSources.get(route);
                if (dataSource != null) {
                    targetDataSources.put(route, dataSource);
                    if (defaultTargetDataSource == null) {
                        defaultTargetDataSource = dataSource;
                    }
                }
            }
        }

        setTargetDataSources(targetDataSources);
        setDefaultTargetDataSource(defaultTargetDataSource);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceHolder.get();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
