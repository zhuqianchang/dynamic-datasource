package indi.zqc.dynamic.datasource.holder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Zhu.Qianchang
 * @date 2019/11/7.
 */
@Slf4j
public class DataSourceHolder {

    private static final ThreadLocal<String> context = new ThreadLocal<>();

    public static void set(String dataSource) {
        log.error("切换到数据源：{}", dataSource);
        context.set(dataSource);
    }

    public static String get() {
        log.error("操作数据源：{}", context.get());
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}
