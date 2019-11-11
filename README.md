# SpringBoot配置多数据源

## 背景
随着数据量和并发量的增长，数据库会到达瓶颈。这时就需要根据实际情况，对数据库进行读写分离或分库分表，并在运行时能够根据请求动态的切换数据源。

## 使用技术
+ SpringBoot 2.0.3.RELEASE
+ MyBatis 
+ Druid 1.0.16
+ AOP
+ Lombok

## 具体实现

### 1.Maven依赖
```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.3.RELEASE</version>
    </parent>
```
```xml
    <dependencies>
       <!-- web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- jdbc -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>

        <!-- mysql -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>

        <!-- druid -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>1.0.16</version>
        </dependency>

        <!-- aop -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aspects</artifactId>
        </dependency>

        <!-- mybatis -->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>1.3.2</version>
        </dependency>

        <!-- lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>

        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
        </dependency>
    </dependency>
```

### 2.AbstractRoutingDataSource 和 DataSourceHolder
AbstractRoutingDataSource类是Spring提供的抽象类，通过和类DataSourceHolder配合实现动态的切换数据源。

AbstractRoutingDataSource类的成员变量：
```java
    private Map<Object, Object> targetDataSources;
    private Object defaultTargetDataSource;
```
+ targetDataSources 保存key和数据源的映射关系
+ defaultTargetDataSource 表示默认的连接

下面继承AbstractRoutingDataSource类，实现自己的选择数据库逻辑
```java
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

    /**
     * 初始化所有数据源和并指定默认数据源
     */
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
                        // routes的第一个为默认数据源
                        defaultTargetDataSource = dataSource;
                    }
                }
            }
        }

        setTargetDataSources(targetDataSources);
        setDefaultTargetDataSource(defaultTargetDataSource);
    }

    /**
     * 在访问数据源时，会调用当前方法来判断访问哪个数据源
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceHolder.get();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
```

DataSourceHolder的实现
```java
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
```

### 3.增加切面
如果每次在数据库操作前后分别调用DataSourceHolder.set(dataSource)和DataSourceHolder.get()方法也能够实现切换数据源，但这就很麻烦。所以这里借助注解和Spring的切面解决这个问题。

自定义注解DataSource
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSource {

    String value();
}
```

增加切面，定义切面时需要指定Order，使该切面在事务@Transactional前执行
```java
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
        // 设置数据源
        DataSourceHolder.set(dataSource);
    }

    @After("pointcut()")
    public void after(JoinPoint point) {
        DataSourceHolder.clear();
    }
}
```

### 4.数据源配置
```java
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
```

## 使用方式

### 1.参数配置
```properties
indi.zqc.dynamic.datasource.routes=db1,db2
indi.zqc.dynamic.datasource.db1.db-type=mysql
indi.zqc.dynamic.datasource.db1.driver-class-name=com.mysql.jdbc.Driver
indi.zqc.dynamic.datasource.db1.url=jdbc:mysql://******/db1?useSSL=true
indi.zqc.dynamic.datasource.db1.username=******
indi.zqc.dynamic.datasource.db1.password=******
indi.zqc.dynamic.datasource.db2.db-type=mysql
indi.zqc.dynamic.datasource.db2.driver-class-name=com.mysql.jdbc.Driver
indi.zqc.dynamic.datasource.db2.url=jdbc:mysql://******/db2?useSSL=true
indi.zqc.dynamic.datasource.db2.username=******
indi.zqc.dynamic.datasource.db2.password=******
```
+ indi.zqc.dynamic.datasource.routes指定生效的数据源，第一个为默认数据源

### 2.Service层使用
```java
@Transactional
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    /**
     * 注解方式访问数据源db1
     */
    @DataSource("db1")
    @Override
    public User getUser(int id) {
        return userDao.getUser(id);
    }

    /**
     * 注解方式访问数据源db2
     */
    @DataSource("db1")
    @Override
    public void insertUser(User user) {
        userDao.insertUser(user);
    }

    /**
     * 注解方式访问数据源db2
     */
    @DataSource("db2")
    @Override
    public void updateUser(User user) {
        userDao.updateUser(user);
    }
}
```

## Github地址
[https://github.com/zhuqianchang/dynamic-datasource](https://github.com/zhuqianchang/dynamic-datasource)