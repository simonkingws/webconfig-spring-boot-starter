# webconfig-springboot 快速开始

### 1、webconfig-spring-boot-starter
#### 1.1 依赖引入
```xml
<dependency>
    <groupId>io.github.simonkingws</groupId>
    <artifactId>webconfig-spring-boot-starter</artifactId>
    <version>${latest.version}</version>
</dependency>
```
#### 1.2 接口统一返回
引入项目之后默认提供统一返回结果集：```com.simonkingws.webconfig.common.core.JsonResult```
```json
{
    "success": false,
    "msg": "",
    "data": null,
    "code": null,
    "extInfo": null
}
```
* success (Boolean): 布尔类型，接口返回的成功或失败的标识
* msg (String)：字符串类型，失败的信息提示
* data (T)：泛型，成功的返回的数据
* code (String)：字符串类型，响应码
* extInfo (Map)：Map类型，扩展信息

> 用户也可以自定义自己的统一返回结果集
#### 1.3 异常统一捕获
```com.simonkingws.webconfig.core.handler.GlobalExceptionHandler```

##### 1.3.1 已经处理的异常
* ```org.springframework.validation.BindException```
* ```org.springframework.web.bind.MethodArgumentNotValidException```
* ```org.springframework.web.bind.MissingServletRequestParameterException```
* ```com.simonkingws.webconfig.common.exception.WebConfigException```
* ```java.lang.Exception```
> 其中```WebConfigException```为webconfig项目的自定义异常，```Exception```为兜底异常。

为什么没有处理其他异常？
> 因为其他异常获取异常信息（message）的方式均和```java.lang.Exception```一致，故没有细分异常

##### 1.3.3 异常信息的返回结果集
异常返回的默认结果集是：```com.simonkingws.webconfig.common.core.JsonResult```

##### 1.3.4 自定义异常以及自定义结果集
如果项目有自己的结果集和自定义异常，也可以直接使用。需要实现```com.simonkingws.webconfig.core.resolver.GlobalExceptionResponseResolver```接口。
```java
public interface GlobalExceptionResponseResolver {

    /**
     * 自定义全局异常返回
     * 
     * @author ws
     * @date 2024/1/29 13:51
     * @param e 可以自定义处理异常或者自定义异常
     * @param errorMsg 已经处理好的异常信息
     */
    Object resolveExceptionResponse(Exception e, String errorMsg);

    /**
     * 推送异常通知
     *
     * @author ws
     * @date 2024/1/29 13:52
     * @param e
     * @param errorMsg 已经处理好的异常信息
     */
    default void pushExceptionNotice(Exception e, String errorMsg){
        
    }

}
```
接口方法说明：
* resolveExceptionResponse：实现此方法可以将异常封装到自己的结果集里面，同时也可以通过```Exception```参数，判断异常类型，处理自己的自定义异常。
* pushExceptionNotice：异常信息的推送，用户可以自定义异常的推送，例如企业微信、微信、短信等。

#### 1.4 统一日期的处理
关键处理类：`com.simonkingws.webconfig.core.handler.GlobalWebHandler`
> 对于Form表单提交日期参数，格式各异，如：`yyyy-MM-dd`，`yyyy-MM-dd HH:mm`, `yyyy-MM-dd HH:mm:ss`，`yyyy/MM/dd`等。
> 
> 服务端我们需要处理成日期格式，如`java.util.Date`, `java.time.LocalDateTime`
 
本项目自动处理的日期格式：
* yyyy-MM-dd HH:mm:ss
* yyyy-MM-dd HH:mm
* yyyy-MM-dd
* yyyy/MM/dd
* yyyyMMdd
* yyyyMM

自动映射的日期类：
* java.util.Date
* java.time.LocalDateTime 兼容jdk8

#### 1.5 常用过滤器
内置请求参数的处理的抽象类：`com.simonkingws.webconfig.core.wrapper.AbstractRequestParamsWrapper`
> 该抽象类已经处理好了请求参数，对于请求参数的处理可以在定义，只需要重写抽象方法：`abstract String cleanParams(String params);`即可。

已经实现的过滤器：
* com.simonkingws.webconfig.core.filter.ParameterTrimFilter: 参数去除前后空格
* com.simonkingws.webconfig.core.filter.XssFilter：防止Xss攻击
> 过滤器需要自行配置方可生效。

#### 1.6 全局请求拦截器
拦截器类：`com.simonkingws.webconfig.core.Interceptor.RequestContextInterceptor`

拦截器已经自动注册，并且优先级最高。通过配置类`com.simonkingws.webconfig.common.core.WebconfigProperies`的`requestContextInterceptor`参数控制是否开启。默认开启。

##### 1.6.1 请求的链路信息
全局参数实体类：`com.simonkingws.webconfig.common.context.RequestContextLocal`
> 拦截器将拦截的信息转移到全局类里面保存在本地线程副本中（`com.simonkingws.webconfig.common.util.RequestHolder`），然后使用信息是就可以直接从`RequestHolder`中直接获取。

记录Header的主要信息：
* 整个请求的链路标识：`traceId`
* 整个请求的子链路标识：`spanId`
* 整个请求的链路数：`traceSum`
* 整个请求的链路轨迹：`traceWalking`
* 第一次进入请求的时间：`traceStartMs`
* 第一次进入请求的请求路径：`startPos`
* 结束请求的时间：`traceEndMs`
* 结束请求的路径或者方法：`endPos`
* 请求的路登录信息：`userId`,`userName`,`userType`,`token`
* 其他header信息：`extendContext`
* 是否采集链路信息：`openTraceCollect`
* 远程调用的方法名,次参数传输不传递：`rpcMethodName`

##### 1.6.2 日志的链路信息
拦截器中设置了MDC信息：`MDC.put("traceId", requestContextLocal.getTraceId());`

日志中需要配置可以直接获取`traceId`参数。以logback为例：`[traceId:%X{traceId}]`
```xml
<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
        <!--格式化输出：%d表示日期，%thread表示线程名，traceId标识链路标识，%-5level：级别从左显示5个字符宽度，%logger：日志名，%line：日志打印行号，%msg：日志消息，%n：是换行符-->
        <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [traceId:%X{traceId}] %-5level %logger{100}[%line] - %msg%n</pattern>
    </encoder>
</appender>
```
请求之后的效果：
```text
2024-02-21 17:51:08.534 [http-nio-8080-exec-6] [traceId:] INFO  com.simonkingws.application.RequestContextLocalPostProcessImpl[22] 
2024-02-21 17:51:08.539 [http-nio-8080-exec-6] [traceId:c0aeb885-2010-461a-9958-0900b01a7fda] INFO  com.simonkingws.webconfig.feign.interceptor.FeignRequestInterceptor[59]
```

##### 1.6.3 全局请求参数管理器
类名称：`com.simonkingws.webconfig.common.util.RequestHolder`
主要方法说明：
* add()：将`RequestContextLocal`放到当前线程中，拦截器已经自动做了，无需用户自己设置
* get()：从当前线程中获取`RequestContextLocal`信息
* remove(): 线程结束后清空当前线程的的数据，防止内存溢出，拦截器已经自动设置

##### 1.6.4 请求参数的后置处理
后置处理类：`com.simonkingws.webconfig.common.process.RequestContextLocalPostProcess`
```java
public interface RequestContextLocalPostProcess {

    /**
     * 构建完参数全局请求参数的后置处理
     */
    default void afterRequestContextLocal(RequestContextLocal requestContextLocal){

    }

    /**
     * 全局请求参数被销毁后的处理
     */
    default void destroy(RequestContextLocal finalRequestContextLocal){

    }
}
```
* afterRequestContextLocal(): 将RequestHeader中的数据加载到当前线程后调用，可以自定补充一些信息。
* destroy(): 本地线程销毁后调用，`finalRequestContextLocal` 也是最终的链路数据。根据链路数据可以数据分析，需要自行实现。

#### 1.7 常用注解

##### 1.7.1 频繁请求限制注解
注解类：`@com.simonkingws.webconfig.core.annotation.RequestLimiting`
> 该注解主要用来防止用户恶意刷新接口或者慢请求的重复点击导致的资源消耗，主要用于`Get`请求。第一次请求未完结，后续的请求会被拦截。

拦截策略：
```java
public enum Policy {

    /**
     *  忽略请求
     */
    IGNORE,

    /**
     *  返回JSON数据
     */
    RETURN_JSON,

    /**
     *  返回页面
     */
    RETURN_PAGE


}
```
* IGNORE：直接忽略后续请求，用户无感，项目默认
* RETURN_JSON：返回JSON数据，默认信息（数据正在处理中，请勿重复请求!）
* RETURN_PAGE：重定向指定的页面

缓存解析器：
> 这里的缓存解析器，是针对`@RequestLimiting`注解的缓存模式。默认`redis`模式。本项目共实现2中模式：
* redis：`com.simonkingws.webconfig.core.resolver.impl.RedisResolver`
* local: `com.simonkingws.webconfig.core.resolver.impl.LocalCacheResolver`

用户也可以自定义缓存模式，需要实现：`com.simonkingws.webconfig.core.resolver.CacheResolver`
实现类的BeanName需要和`WebconfigProperies`中的`requestLimitCacheMode`属性保持一致。
```java
public interface CacheResolver {

    /**
     * key 不存在时设置
     *
     * @author ws
     * @date 2024/2/5 10:26
     */
    Boolean setIfAbsent(String key, String value, long timeout, TimeUnit unit);

    /**
     * 删除Key
     *
     * @author ws
     * @date 2024/2/5 10:26
     */
    void remove(String key);
}
```

##### 1.7.2 内部链路追踪的注解
`com.simonkingws.webconfig.core.annotation.InnerTrace`
> 将`@InnerTrace`注解的方法，加入到链路信息中，主要用户服务内部方法调用。如果PRC的方法也被次注解修饰，则会忽略此注解

#### 1.8 配置类
`com.simonkingws.webconfig.common.core.WebconfigProperies`
```java
public class WebconfigProperies {

    /**
     * 重复请求的缓存模式
     */
    private String requestLimitCacheMode = "redis";

    /**
     * 是否开启检验参数完成后返回所有错误信息，默认一个参数异常就返回
     */
    private Boolean validFailFast = true;

    /**
     * 是否开启全局请求参数拦截
     */
    private Boolean requestContextInterceptor = true;

    /**
     * 全局请求参数拦截路径
     */
    private String interceptorPathPatterns = "/**";

    /**
     * 全局请求参数忽略路径
     */
    private String excludePathPatterns;

    /**
     * 是否开启dubbo全局请求参数拦截
     */
    private Boolean dubboInterceptor = true;

    /**
     * 是否开启Feign全局请求头参数拦截
     */
    private Boolean feignInterceptor = true;

    /**
     * 是否开启链路信息采集
     */
    private Boolean openTraceCollect = false;

    /**
     * 链路信息采集的地址：IP/域名:PORT
     */
    private String traceCollectAddress;

    /**
     * 请求的协议，默认http
     */
    private String protocol = "http";

    /**
     * arthas 是否开启,默认关闭。关闭状态可以使用arthas的原生配置
     */
    private Boolean arthasOpen = false;    /**
     
     * arthas 是否开启,默认关闭。关闭状态可以使用arthas的原生配置
     */
    private Boolean arthasIsSelfConfig = false;

    /**
     * arthas ip
     */
    private String arthasIp = "0.0.0.0";
    
    /**
     * arthas 服务唯一标识：默认是为空，系统自动生成。
     * <pre>
     *     通过 http://localhost:8080/apps.html 可以查看
     * </pre>
     */
    private String arthasAgentId;

    /**
     * arthas 服务地址
     */
    private String arthasTunnelServer;

}
```
> 配置类的前缀为`springboot.webconfig`,可以在配置文件中自行配置。大部分配置类有默认值，用户可以通过配置自行修改。
##### 1.8.1 arthas相关说明
* arthasOpen 默认关闭。关闭状态可以自行使用arthas的配置
* arthasAgentId 默认为空，系统自动生成。客户端（`Arthas Tunnel`）统一管理，启动后可以通过`ip:port/apps.html`统一管理查看。
* arthasTunnelServer 客户端服务地址，例：`ws://127.0.0.1:7777/ws`。其中端口7777是客户端的默认端口
##### 1.8.2 远程管理客户端使用注意（`Arthas Tunnel`）
> 下载资源：https://github.com/alibaba/arthas/releases
> 使用文档：https://arthas.aliyun.com/doc/tunnel.html

客户端提供管理页面，但是没有权限控制。默认是关闭页面的，使用时需要开启，并注意权限控制，最好外网不能访问。
客户端启动(开启页面管理)：
> 开启页面管理参数：arthas.enable-detail-pages=true

> java -jar arthas-tunnel-server.jar --arthas.enable-detail-pages=true

如果需要修改其他参数，可以`--参数`修改。
![arthas-manage.png](https://s2.loli.net/2024/03/18/Xdcm2fTW6tuOZae.png)
页面通过AgentId来建立连接。自定义AgentId需要记住AgentId名称，方便连接。系统自动生成可以使用管理页面。

管理页面：http://ip:port/apps.html
![apps.png](https://s2.loli.net/2024/03/18/8NzWswuchrMOlUt.png)
![detail.png](https://s2.loli.net/2024/03/18/r8tqBpoCxXIevjK.png)

`自定义的AgentId管理页面获取不到，是因为管理页面通过下划线（_）分割的，系统生成的时会默认通过下划线连接`。

无法查看Agent管理时，可以通过`http://localhost:8080/actuator/arthas` 查看。


### 2、webconfig-dubbo3
主要通过dubbo3传递全局参数。
#### 2.1 引入依赖
```xml
<dependency>
    <groupId>io.github.simonkingws</groupId>
    <artifactId>webconfig-dubbo3</artifactId>
    <version>${latest.version}</version>
</dependency>
```
#### 2.2 dubbo扩展Filter
Filter类：`com.simonkingws.webconfig.dubbo3.filter.DubboRpcFilter`
* 通过消费端将全局参数设置到Dubbo中
* 通过服务端记录链路信息
* 链路信息保存在Redis中，方面消费端获取全局链路

### 3、webconfig-feign
#### 3.1 引入依赖
```xml
<dependency>
    <groupId>io.github.simonkingws</groupId>
    <artifactId>webconfig-feign</artifactId>
    <version>${latest.version}</version>
</dependency>
```
#### 3.2 全局参数拦截器
拦截器：`com.simonkingws.webconfig.feign.interceptor.FeignRequestInterceptor`
* 通过`feign.RequestTemplate`将`RequestHeader`参数和上下文参数`RequestContextLocal`交互
* 所有的数据都是放在请求头里面的

#### 3.3 日期参数传递的时差问题处理
自定义Formatter，将日期转化成字符串`yyyy-MM-dd HH:mm:ss`，这和`1.4 统一日期的处理`对应，否则会出问题。

支持的日期类型：
* java.util.Date
* java.time.LocalDateTime 兼容jdk8

### 4、webconfig-trace-admin
* 提供webUI的能力
* 默认登录账号：trace/trace
* 访问地址：ip/域名+端口


### 5、更新日志
### v1.1.0 @2024-03-29
* 发布分别startter、dubbo3、feign中央仓库版本的1.1.0版本

### v1.0.1 @2024-03-18
* [优化]`webconfig-trace-admin` 完整链路增加用户的信息以及索引
* [新增]`webconfig-spring-boot-starter` 增加`@PrintParams` 打印请求参数的注解
* [新增]`webconfig-spring-boot-starter` 增加`arthas` 在线运维的功能
* [优化]`webconfig-demo` 增加测试case 
* [优化]更新文档


### v1.0.1 @2024-03-15
* [新增]`webconfig-trace-admin`链路追踪的webui
* [新增]新增`@SubmitLimiting` 注解
* [优化]优化链路追踪的字段
* [优化]更新文档

### v1.0.1 @2024-02-29
* [新增]新增链路信息的采集接口的配置
* [优化]链路收集过程的方式
* [优化]更新文档

### v1.0.1 @2024-02-28
* [新增]`@InnerTrace`注解
* [新增]全链路的信息采集的开关，默认false
* [新增]链路异常信息采集
* [优化]优化`webconfig-spring-boot-starter`的Threadlocal，并升级版本为1.0.1
* [优化]dubbo3链路采集的方式，优化Threadlocal，并升级版本为1.0.1
* [优化]feign链路采集的方式，并升级版本为1.0.1
* [优化]更新文档

#### v1.0.0 @2024-02-22
* [优化]使用Validator校验参数时，配置快速失败。只要有一个字段校验不通过就返回，否则校验全部字段。
* [优化]更新文档 

#### v1.0.0 @2024-02-21
* 第一版上传至github
* [新增]webconfig-spring-boot-starter项目
* [新增]webconfig-dubbo3项目
* [新增]webconfig-feign项目
* [新增]webconfig-demo项目
* 统一接口返回。可配置自定义所需要的统一返回实体。
* 统一异常捕获。所有的异常统一处理，也可以自定义异常。并针对需要异常发送通知。
* 统一日期参数的处理。form表单类的日期统一处理，包括Date、LocalDateTime。
* 常用过滤器。xss攻击Filter、请求参数去除空格Filter等。
* 常用拦截器。全局参数拦截器。
* 常用注解。重复请求拦截注解。
* 全链路追踪。
* duubo3下全局参数
* feign调用下的全局参数
* Date参数传递时差问题统一处理
* 应用实例以及case