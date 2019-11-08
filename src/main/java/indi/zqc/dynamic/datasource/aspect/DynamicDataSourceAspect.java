package indi.zqc.dynamic.datasource.aspect;

import indi.zqc.dynamic.datasource.annotation.DataSource;
import indi.zqc.dynamic.datasource.holder.DataSourceHolder;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;

/**
 * 切面，用于根据DataSource注解动态切换数据源
 *
 * @author Zhu.Qianchang
 * @date 2019/11/7.
 */
@Order(2)
@Aspect
@EnableAspectJAutoProxy
public class DynamicDataSourceAspect {

    @Pointcut("@annotation(indi.zqc.dynamic.datasource.annotation.DataSource)")
    private void pointcut() {
    }

    @Before("pointcut()")
    public void before(JoinPoint point) {
        String dataSource = null;
        // 获取方法上的注解信息
        Signature signature = point.getSignature();
        if (signature instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature) signature;
            DataSource annotation = methodSignature.getMethod().getAnnotation(DataSource.class);
            if (annotation != null && StringUtils.isNotBlank(annotation.value())) {
                dataSource = annotation.value();
            }
        }
        DataSourceHolder.set(dataSource);
    }

    @After("pointcut()")
    public void after(JoinPoint point) {
        DataSourceHolder.clear();
    }
}
