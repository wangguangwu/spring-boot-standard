# spring-boot-standard

# 前言

现在的项目大部分都是前后端分离。

后端数据返回给前端的时候就涉及到统一格式的问题，并且还需要对异常进行统一处理。

### 文章地址

[spring-boot-standard](https://www.wangguangwu.com/archives/d6c04d38-1437-471b-ab0c-b9e576721be8)

### 参考文献

[设计之道－controller 层的设计](https://www.jianshu.com/p/654f4589eb8e)

[设计之道－controller 层的设计补遗](https://www.jianshu.com/p/aeeb0ab1dcbb)

[SpringBoot 系列（十）统一异常处理与统一结果返回](https://www.jianshu.com/p/698780498e70)

[SpringBoot 通用返回类 Result](https://www.cnblogs.com/CF1314/p/13686123.html)

[封装 ResultVO 实现统一返回结果](https://juejin.cn/post/6995932258662088717)

[统一结果返回、全局异常处理、日志处理](https://juejin.cn/post/6955311400818311181)

[SpringBoot 统一接口返回和全局异常处理，大佬们怎么玩](https://juejin.cn/post/6994629906424397837)

[SpringBoot 如何统一后端返回格式？老鸟们都是这样玩的！](https://blog.51cto.com/u_15323393/3289327)

[Not annotated parameter overrides @NonNullApi parameter](https://blog.csdn.net/u010234516/article/details/121830953?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522165037707016780269815516%2522%252C%2522scm%2522%253A%252220140713.130102334..%2522%257D&request_id=165037707016780269815516&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~sobaiduend~default-1-121830953.142%5Ev9%5Econtrol,157%5Ev4%5Econtrol&utm_term=Not+annotated+parameter+overrides+%40NonNullApi+parameter&spm=1018.2226.3001.4187)

[Java 包注解和 package-info.java 文件的作用和用法](https://blog.csdn.net/wq6ylg08/article/details/120107782)

# 1. 统一结果返回格式

## 1.1 springBoot 项目接口的返回

默认情况下，SpringBoot 项目会有如下 3 种返回情况。

### 1.1.1 直接返回字符串

```
@GetMapping("/testString")
public String testString() {
    return "Hello World";
}
```

调用接口返回结果：

```
Hello World
```

### 1.1.2 返回实体类

```
@GetMapping("/testEntity")
public User testEntity() {
    return new User("wangguangwu", 22, "Java 开发");
}
```

调用接口返回结果：

```
{
    "name": "wangguangwu",
    "age": 24,
    "description": "Java 开发"
}
```

### 1.1.3 异常情况下返回

```
@GetMapping("/testException")
public String testException() {
    List<String> list = new ArrayList<>();
    return list.get(0);
}
```

调用接口返回结果：

```
{
    "timestamp": "2022-04-20T06:06:49.826+00:00",
    "status": 500,
    "error": "Internal Server Error",
    "trace": "java.lang.IndexOutOfBoundsException: Index: 0,...
    "message": "Index: 0, Size: 0",
    "path": "/hello/testException"
}
```

如果整个项目没有定义统一的返回格式，不仅代码臃肿，而且会降低前后端对接效率。

## 1.2 基础解决方式

项目中最常见的是**封装一个工具类**。

在工具类中定义需要返回的字段信息，把需要返回前端的接口信息，通过该类进行封装，这样就可以解决返回格式不统一的现象了。

### 1.2.1 参数说明

1. code：状态码，后台可以维护一套统一的状态码；
2. message：描述信息，接口调用成功/失败的提示信息；
3. data：返回数据。

### 1.2.2 代码

数据返回格式：

```
import com.wangguangwu.springbootstandard.enums.ResponseEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 - 通用响应类型，用于封装API响应数据
 *
 - @param <T> 响应数据的类型
 - @author wangguangwu
 */
@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Response<T> {

    /**
     - 响应状态码
     */
    int code;

    /**
     - 响应信息
     */
    String message;

    /**
     - 响应数据
     */
    T data;

    /**
     - 构造成功的响应
     *
     - @param data 响应数据
     - @param <T>  响应数据的类型
     - @return 包含成功状态码和消息的响应对象
     */
    public static <T> Response<T> success(T data) {
        return new Response<>(ResponseEnum.SUCCESS.getCode(), ResponseEnum.SUCCESS.getMessage(), data);
    }

    /**
     - 构造错误的响应
     *
     - @param code    自定义错误码
     - @param message 错误信息
     - @param <T>     响应数据的类型
     - @return 包含错误状态码和消息的响应对象
     */
    public static <T> Response<T> error(int code, String message) {
        return new Response<>(code, message, null);
    }

    /**
     - 构造错误的响应，附带响应数据
     *
     - @param resultCodeEnum 错误码枚举
     - @param data           附带的响应数据
     - @param <T>            响应数据的类型
     - @return 包含错误状态码、消息和数据的响应对象
     */
    public static <T> Response<T> error(ResponseEnum resultCodeEnum, T data) {
        return new Response<>(resultCodeEnum.getCode(), resultCodeEnum.getMessage(), data);
    }
}
```

状态返回码：

```
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 - 响应结果枚举，用于定义通用的响应状态和信息
 - <p>
 - {@link  com.wangguangwu.springbootstandard.response.Response}
 *
 - @author wangguangwu
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ResponseEnum {

    /**
     - 成功
     */
    SUCCESS(0, "成功"),

    /**
     - 失败
     */
    FAIL(-1, "失败"),

    /**
     - 业务层异常
     */
    SERVICE_UNKNOWN(1000, "业务层异常"),

    /**
     - 系统未知异常
     */
    SYSTEM_UNKNOWN(1001, "系统未知异常");

    /**
     - 状态码
     */
    private final int code;

    /**
     - 状态信息
     */
    private final String message;

}
```

### 1.2.3 使用示例

```
@GetMapping("/testString")
public Response<String> testString() {
    return Response.success("Hello World");
}
```

调用接口返回结果：

```
{
    "code": 0,
    "message": "成功",
    "data": "Hello World"
}

```

**优点**：统一接口返回结果，方便前后端数据传输。

**缺点**：如果有大量的接口，并且在每个接口中都使用 Result 来包装返回信息，会新增很多重复代码。

## 1.3 优化做法

基本用法学会后，接下来我们试着进行优化。

### 1.3.1 相关知识

我们需要用到两个知识点：

1. **ResponseBodyAdvice 接口**：该接口是 SpringMVC 4.1 提供的，它允许在执行 @ResponseBody 注解后自定义返回数据，用来封装统一数据格式返回；
2. **@RestControllerAdvice 注解**：该注解是对 Controller 进行增强的，并且可以全局捕获抛出的异常。

### 1.3.2 代码

1. 新建 ResponeAdvice 类，用于统一封装 controller 中接口的返回结果；
2. 该类需要实现 ResponseBodyAdvice 接口，并且实现其中的 supports、beforeBodyWrite 方法。

```
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wangguangwu.springbootstandard.response.Response;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.json.JsonParseException;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 - 定义一个响应建议，用于拦截响应体并添加成功状态码。
 - 如果原始响应体为null，则返回带有成功状态码的响应；
 - 如果响应体尚未封装为Result对象，则将其封装为Result对象。
 *
 - @author wangguangwu
 */
@RequiredArgsConstructor
@RestControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    // 用于处理 JSON 序列化的 ObjectMapper 实例
    private final ObjectMapper objectMapper;

    /**
     - 确定是否要对给定的响应执行beforeBodyWrite操作
     *
     - @param returnType    方法的返回类型
     - @param converterType 使用的HttpMessageConverter类型
     - @return 是否支持拦截响应
     */
    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        // 这里返回true，表示所有响应都将执行beforeBodyWrite操作
        return true;
    }

    /**
     - 在响应体写入前执行操作
     *
     - @param body                  原始地响应体
     - @param returnType            方法的返回类型
     - @param selectedContentType   响应的内容类型
     - @param selectedConverterType 使用的HttpMessageConverter类型
     - @param request               当前的HTTP请求
     - @param response              当前的HTTP响应
     - @return 修改后的响应体
     */
    @Override
    public Object beforeBodyWrite(Object body, @NonNull MethodParameter returnType, @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
        // 如果响应体为null，则返回带有成功状态码的默认响应
        if (body == null) {
            return Response.success(null);
        }

        // 如果响应体已经是Response对象，则直接返回，不做修改
        if (body instanceof Response) {
            return body;
        }

        // 如果响应体是String类型，则需要手动处理字符串的包装
        if (body instanceof String) {
            try {
                // 使用ObjectMapper将Response对象转换为JSON字符串
                return objectMapper.writeValueAsString(Response.success(body));
            } catch (JsonProcessingException e) {
                // 捕获JSON处理异常并重新抛出为JsonParseException
                throw new JsonParseException(e);
            }
        }

        // 对于其他类型的响应体，封装为Response对象返回
        return Response.success(body);
    }
}
```

创建该类后，我们对接口也进行一下调整：

```
@GetMapping("/testString")
public String testString() {
    return "Hello World";
}
```

接口返回结果：

```
{
    "code":0,
    "message":"成功",
    "data":"Hello World"
}

```

**优点**：切面管理，统一返回格式的同时，代码量更少更优雅。

**缺点**：只对成功响应的请求进行了处理，对于异常之类的结果并没有处理。

# 2. 全局异常处理

一般都是使用 try catch 对代码进行捕获处理，虽然满足要求，不过这种方式会导致大量代码重复，维护困难，逻辑臃肿等问题，不够优雅。

在此基础上，我们可以采用**全局异常处理**的方式，从而减少代码量。

## 2.1 使用讲解

新增一个类，标注 `@RestControllerAdvice` 注解，从而捕获全局异常。

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

}
```

对于想要拦截的异常类型，只需要新增一个方法，使用 `@ExceptionHandler` 注解修饰，注解参数为目标异常类型。

`@ExceptionHandler` 注解：统一处理某一类异常，从而减少代码重复率和复杂度。

## 2.2 自定义异常

```java
import com.wangguangwu.springbootstandard.enums.ResponseEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 - 自定义业务异常。
 *
 - @author wangguangwu
 */
@SuppressWarnings("unused")
@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceException extends RuntimeException {

    protected final Integer data;
    protected final String message;

    public ServiceException() {
        this.data = ResponseEnum.SERVICE_UNKNOWN.getCode();
        this.message = ResponseEnum.SERVICE_UNKNOWN.getMessage();
    }

    public ServiceException(String message) {
        this.data = ResponseEnum.SERVICE_UNKNOWN.getCode();
        this.message = message;
    }
}
```

## 2.3 完整代码

```java
import com.wangguangwu.springbootstandard.enums.ResponseEnum;
import com.wangguangwu.springbootstandard.exception.ServiceException;
import com.wangguangwu.springbootstandard.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 - 定义全局异常处理
 - 该类用于捕获和处理应用程序中的异常，并返回统一的响应格式。
 *
 - @author wangguangwu
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     - 处理自定义的业务异常 ServiceException
     - 当业务逻辑抛出 ServiceException 时，捕获该异常并返回错误响应。
     *
     - @param exception 捕获的 ServiceException
     - @return 包含错误信息的响应对象
     */
    @ExceptionHandler(ServiceException.class)
    private Response<String> handleServiceException(ServiceException exception) {
        log.error("ServiceException occurred: {}", exception.getMessage(), exception);
        return Response.error(ResponseEnum.SERVICE_UNKNOWN.getCode(), exception.getMessage());
    }

    /**
     - 处理所有未捕获的通用异常
     - 捕获应用程序中未处理的其他异常，并返回通用错误响应。
     *
     - @param exception 捕获的通用异常
     - @return 包含错误信息的响应对象
     */
    @ExceptionHandler(Exception.class)
    public Response<String> handleGeneralException(Exception exception) {
        log.error("Unhandled exception occurred: {}", exception.getMessage(), exception);
        return Response.error(ResponseEnum.SYSTEM_UNKNOWN, exception.getMessage());
    }
}
```

# 3. 打印入参出参

上面我们说过，可以通过实现 ResponseBodyAdvice 接口去做统一的返回数据处理。

**beforeBodyWrite 方法的执行时间**：responseBody 被写入之前。

如果 controller 本身就已经报错了，即 responseBody 没有被写入，这个方法是不会被执行的，加在其中的日志也就不会被打印了。

那有没有方式可以修复这个问题呢？

有的，spring 除了提供 ResponseBodyAdvice 之外，还提供了相对应的 requestBodyAdvice 接口。

**方案**：

1. 在  `beforeBodyRead`  中打印请求日志；
2. 在  `beforeBodyWrite`  中打印正常返回日志；
3. 在  `@ExceptionHandler`  中打印异常返回日志。

但这样的方式还是过于繁琐，所以我们采用另外一种方式：

**单独创建一个切面来做统一的日志打印**。

## 3.1 日志切面

```jsx
/**
 - Define an aspect to print logging.
 *
 - @author wangguangwu
 */
@RequiredArgsConstructor
@Slf4j
@Aspect
@Component
public class RequestLoggingAspect {

    final HttpServletRequest httpServletRequest;

    @Around(value = "execution(* com.wangguangwu.springbootstandard.controller..*.*(..))")
    public Object around(final ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("request url: {}", httpServletRequest.getRequestURL().toString());
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        log.info("request params: {}", gson.toJson(joinPoint.getArgs()));
        Object result = joinPoint.proceed();
        log.info("response:{}", gson.toJson(result));
        return result;
    }
}
```

介绍一下代码中的  AOP 表达式：

```
"execution(* com.wangguangwu.springbootstandard.controller..*.*(..))"
```

- `execution()`：是最常用的切点函数，表示**切面作用于方法执行时**。
- 第一个 `*`：表示不限返回类型；
- 第二个 `*`：表示不限类名；
- 第三个 `*`：表示不限方法名；
- `(..)`  表示不限参数；
- `@Around()`  表示该切面的类型是包围类型。

所以这个 AOP 表达式的整体含义是：

在 `com.wangguangwu.springbootstandard.controller` 包下的所有类的所有方法的执行前后进行拦截。

通过定义这样一个切面，我们就可以在 controller 的方法被调用前打印请求日志，被调用后打印响应日志。

当然，在抛出异常的情况，日志还是打印在 @ExceptionHandler 里的。

如果只是想要打印出全部的日志，以上的代码已经完成需求了。

真正在生产中，我们往往会遇到一个问题，就是有**些接口的日志我们并不想打印出来**。

特别是一些批量查询接口的响应结果，一打就一堆，如果调用频繁，就可能会造成大量空间的浪费，也不方便日志的排查。

那我们就需要针对不同的类，甚至方法进行区别对待。

对于**不同的类**，可以通过自定义切面的方式来解决。

但是如果**同一个类中不同的方法**有不同的日志需求，会引入大量的切点。

## 3.2 自定义注解实现更复杂的日志需求

### 3.2.1 定义日志级别

日志级别：

```java
/**
 - 日志类型枚举。
 - 用于指定在请求处理过程中，应该记录哪些类型的日志。
 *
 - @author wangguangwu
 */
 public enum LogType {

    /**
     - 记录请求的URL。
     */
    URL,

    /**
     - 记录请求的参数。
     */
    REQUEST,

    /**
     - 记录响应结果。
     */
    RESPONSE,

    /**
     - 记录所有类型的日志（URL、请求参数和响应结果）。
     */
    ALL,

    /**
     - 不记录任何日志。
     */
    NONE

}
```

日志选项类:

```java
import lombok.*;

/**
 - 日志选项类，用于配置在请求处理过程中应该记录哪些类型的日志。
 - <p>
 - 通过静态方法 {@code all()} 和 {@code none()} 可以方便地创建记录所有日志或不记录日志的选项。
 *
 - @author wangguangwu
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LogOptions {

    /**
     - 是否记录请求的URL
     */    private boolean url;

    /**
     - 是否记录请求参数
     */
    private boolean request;

    /**
     - 是否记录响应结果
     */
    private boolean response;

    /**
     - 创建一个记录所有日志选项的实例。
     *
     - @return 包含所有日志记录选项的 LogOptions 实例
     */
    public static LogOptions all() {
        return LogOptions.builder()
                .url(true)
                .request(true)
                .response(true)
                .build();
    }

    /**
     - 创建一个不记录任何日志选项的实例。
     *
     - @return 不包含任何日志记录选项的 LogOptions 实例
     */
    public static LogOptions none() {
        return LogOptions.builder()
                .url(false)
                .request(false)
                .response(false)
                .build();
    }
}
```

### 3.2.2 自定义注解

```java
import com.wangguangwu.springbootstandard.log.LogType;

import java.lang.annotation.*;

/**
 - 自定义注解，用于控制方法级别的日志打印行为。
 - <p>
 - 通过设置 {@code type()} 属性，可以指定需要打印哪些类型的日志。
 - 如果未设置，默认不打印任何日志（{@code LogType.NONE}）。
 *
 - @author wangguangwu
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LessLog {

    /**
     - 指定要打印的日志类型。
     - 默认值为 {@code LogType.NONE}，表示不打印任何日志。
     - <p>
     - 如果想打印所有日志，可以使用 {@code LogType.ALL}。
     *
     - @return 需要打印的日志类型数组
     */
    LogType[] type() default LogType.NONE;

}
```

### 3.2.3 完整的日志切面代码

```java
import com.alibaba.fastjson2.JSON;
import com.wangguangwu.springbootstandard.anno.LessLog;
import com.wangguangwu.springbootstandard.log.LogOptions;
import com.wangguangwu.springbootstandard.log.LogType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.EnumSet;

/**
 - 定义一个切面用于打印日志。
 - 该切面拦截指定包路径下的控制器方法，在方法执行前后打印请求和响应的日志信息。
 - 日志打印行为可以通过注解 @LessLog 配置。
 *
 - @author wangguangwu
 */
@RequiredArgsConstructor
@Slf4j
@Aspect
@Component
public class RequestLoggingAspect {

    /**
     - 注入 HttpServletRequest 用于获取请求的详细信息
     */
    private final HttpServletRequest httpServletRequest;

    /**
     - 环绕通知，拦截指定包路径下的控制器方法，打印请求和响应日志
     *
     - @param joinPoint 连接点，表示被拦截的方法
     - @return 方法的返回结果
     - @throws Throwable 如果方法执行过程中发生异常，则抛出
     */
    @Around(value = "execution(* com.wangguangwu.springbootstandard.controller..*.*(..))")
    public Object aroundBack(final ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取日志选项，判断是否打印特定日志
        LogOptions logOptions = getLogOptions(joinPoint);

        // 打印请求的 URL        if (logOptions.isUrl()) {
            log.info("request url: {}", httpServletRequest.getRequestURL().toString());
        }

        // 打印请求参数
        if (logOptions.isRequest()) {
            log.info("request params: {}", JSON.toJSON(joinPoint.getArgs()));
        }

        // 执行目标方法
        Object result = joinPoint.proceed();

        // 打印响应结果
        if (logOptions.isResponse()) {
            log.info("response:{}", JSON.toJSON(result));
        }

        return result;
    }

    /**
     - 获取方法的日志选项，根据 @LessLog 注解确定哪些日志需要打印
     *
     - @param joinPoint 连接点，表示被拦截的方法
     - @return 日志选项，确定是否打印 URL、请求参数和响应结果
     */
    private LogOptions getLogOptions(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LessLog lessLog = method.getAnnotation(LessLog.class);

        // 如果方法未标注 @LessLog 注解，则打印所有日志
        if (lessLog == null) {
            return LogOptions.all();
        }

        // 获取注解中的日志类型
        LogType[] typeArray = lessLog.type();
        EnumSet<LogType> logTypes = EnumSet.copyOf(Arrays.asList(typeArray != null && typeArray.length > 0 ? typeArray : new LogType[]{LogType.NONE}));

        // 如果包含 NONE，则打印所有日志
        if (logTypes.contains(LogType.NONE)) {
            return LogOptions.all();
        }

        // 如果包含 ALL，则不打印任何日志
        if (logTypes.contains(LogType.ALL)) {
            return LogOptions.none();
        }

        // 根据注解中的日志类型决定是否打印对应的日志
        return new LogOptions(!logTypes.contains(LogType.URL), !logTypes.contains(LogType.REQUEST), !logTypes.contains(LogType.RESPONSE));
    }
}
```

# 4. 接入 logback

`application.yml`:

```jsx
# logger configuration
logging:
  file:
    level: info
    # logs location
    path: ./logs
```

在 `resources` 目录下创建 `logback-spring.xml`：

```jsx
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- 属性文件:在 properties 文件中找到对应的配置项 -->
    <springProperty scope="context" name="log.path" source="logging.file.path"/>
    <springProperty scope="context" name="log.level" source="logging.file.level"/>

    <!-- 定义日志保留天数 -->
    <property name="MAX_HISTORY" value="10"/>
    <!-- 定义单个日志文件大小 -->
    <property name="MAX_FILE_SIZE" value="100MB"/>
    <!-- 定义日志文件总大小 -->
    <property name="TOTAL_SIZE_CAP" value="1GB"/>

    <!-- 彩色日志 -->
    <property name="CONSOLE_LOG_PATTERN"
              value="%d{yyyy-MM-dd HH:mm:ss.SSS} %green([%thread]) %highlight(%-5level) %cyan(%logger{20}) - [%method,%line] - %msg%n"/>
    <!-- 默认的控制台日志输出，一般生产环境都是后台启动，这个没太大作用 -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <Pattern>${CONSOLE_LOG_PATTERN}</Pattern>
        </encoder>
    </appender>

    <!-- 定义了一个切面用来监听入参出参 -->
    <!-- 打印 controller 层日志 -->
    <appender name="WANG-ASPECT" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 追加写入 -->
        <append>true</append>
        <file>
            ${log.path}/aspect/aspect.log
        </file>
        <!-- 日志过滤 -->
        <!-- 会打印出当前层级及以上层级的日志，如果想要只打印当前层级，可以更换过滤器为 LevelFilter -->
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>${log.level}</level>
        </filter>
        <!-- 不会打印 error 级别的日志 -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <!-- 如果命中就禁止这条日志 -->
            <onMatch>DENY</onMatch>
            <!-- 如果没有命中就使用这条规则 -->
            <onMismatch>ACCEPT</onMismatch>
        </filter>
        <!-- 基于文件大小和时间的滚动策略 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <FileNamePattern>${log.path}/aspect/aspect-%d{yyyy-MM-dd}-%i.log</FileNamePattern>
            <!-- 日志文件保留天数 -->
            <MaxHistory>${MAX_HISTORY}</MaxHistory>
            <!-- 单个日志文件大小 -->
            <maxFileSize>${MAX_FILE_SIZE}</maxFileSize>
            <!-- 日志归档文件总大小 -->
            <totalSizeCap>${TOTAL_SIZE_CAP}</totalSizeCap>
        </rollingPolicy>
        <!-- 日志输出格式 -->
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36}: %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <!-- 定义了一个切面用来监听 exception -->
    <!-- 打印 controller 层日志 -->
    <appender name="WANG-EXCEPTION" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 追加写入 -->
        <append>true</append>
        <file>
            ${log.path}/exception/exception.log
        </file>
        <!-- 过滤器，只记录 error 级别的日志 -->
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>ERROR</level>
        </filter>
        <!-- 基于文件大小和时间的滚动策略 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <FileNamePattern>${log.path}/exception/exception-%d{yyyy-MM-dd}-%i.log</FileNamePattern>
            <!-- 日志文件保留天数 -->
            <MaxHistory>${MAX_HISTORY}</MaxHistory>
            <!-- 单个日志文件大小 -->
            <maxFileSize>${MAX_FILE_SIZE}</maxFileSize>
            <!-- 日志归档文件总大小 -->
            <totalSizeCap>${TOTAL_SIZE_CAP}</totalSizeCap>
        </rollingPolicy>
        <!-- 日志输出格式 -->
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36}: %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <!-- 打印 controller 层日志 -->
    <appender name="WANG-CONTROLLER" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 追加写入 -->
        <append>true</append>
        <file>
            ${log.path}/controller/controller.log
        </file>
        <!-- 日志过滤 -->
        <!-- 会打印出当前层级及以上层级的日志，如果想要只打印当前层级，可以更换过滤器为 LevelFilter -->
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>${log.level}</level>
        </filter>
        <!-- 不会打印 error 级别的日志 -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <!-- 如果命中就禁止这条日志 -->
            <onMatch>DENY</onMatch>
            <!-- 如果没有命中就使用这条规则 -->
            <onMismatch>ACCEPT</onMismatch>
        </filter>
        <!-- 基于文件大小和时间的滚动策略 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <FileNamePattern>${log.path}/controller/controller-%d{yyyy-MM-dd}-%i.log</FileNamePattern>
            <!-- 日志文件保留天数 -->
            <MaxHistory>${MAX_HISTORY}</MaxHistory>
            <!-- 单个日志文件大小 -->
            <maxFileSize>${MAX_FILE_SIZE}</maxFileSize>
            <!-- 日志归档文件总大小 -->
            <totalSizeCap>${TOTAL_SIZE_CAP}</totalSizeCap>
        </rollingPolicy>
        <!-- 日志输出格式 -->
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36}: %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <!-- 打印 service 层日志 -->
    <appender name="WANG-SERVICE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 追加写入 -->
        <append>true</append>
        <file>
            ${log.path}/service/service.log
        </file>
        <!-- 日志过滤 -->
        <!-- 会打印出当前层级及以上层级的日志，如果想要只打印当前层级，可以更换过滤器为 LevelFilter -->
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>${log.level}</level>
        </filter>
        <!-- 不会打印 error 级别的日志 -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <!-- 如果命中就禁止这条日志 -->
            <onMatch>DENY</onMatch>
            <!-- 如果没有命中就使用这条规则 -->
            <onMismatch>ACCEPT</onMismatch>
        </filter>
        <!-- 基于文件大小和时间的滚动策略 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <FileNamePattern>${log.path}/service/service-%d{yyyy-MM-dd}-%i.log</FileNamePattern>
            <!-- 日志文件保留天数 -->
            <MaxHistory>${MAX_HISTORY}</MaxHistory>
            <!-- 单个日志文件大小 -->
            <maxFileSize>${MAX_FILE_SIZE}</maxFileSize>
            <!-- 日志归档文件总大小 -->
            <totalSizeCap>${TOTAL_SIZE_CAP}</totalSizeCap>
        </rollingPolicy>
        <!-- 日志输出格式 -->
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36}: %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <!-- 错误日志 appender：按照每天生成日志文件 -->
    <appender name="ERROR-APPENDER" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <append>true</append>
        <!-- 过滤器，只记录 error 级别的日志 -->
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>ERROR</level>
        </filter>
        <!-- 日志名称 -->
        <file>${log.path}/error/error.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${log.path}/error/error-%d{yyyy-MM-dd}-%i.log</fileNamePattern>
            <!-- 日志文件保留天数 -->
            <maxHistory>${MAX_HISTORY}</maxHistory>
            <!-- 日志归档文件总大小 -->
            <totalSizeCap>${TOTAL_SIZE_CAP}</totalSizeCap>
            <!-- 单个日志文件大小 -->
            <maxFileSize>${MAX_FILE_SIZE}</maxFileSize>
        </rollingPolicy>
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36}: %msg%n</pattern>
            <!-- 编码 -->
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <!-- 定义了一个切面用来监听入参出参 -->
    <!-- additivity 设置为 true，表示只在当前 logger 中的 appender-ref 中生效 -->
    <logger name="com.wangguangwu.standard.aspect.RequestLoggingAspect" level="${log.level}" additivity="false">
        <appender-ref ref="WANG-ASPECT"/>
    </logger>

    <!-- 定义了一个切面用来监听入参出参 -->
    <!-- additivity 设置为 true，表示只在当前 logger 中的 appender-ref 中生效 -->
    <logger name="com.wangguangwu.standard.aspect.ExceptionLoggingAspect" level="${log.level}" additivity="false">
        <appender-ref ref="WANG-EXCEPTION"/>
    </logger>

    <!-- logger 负责打印 com.wangguangwu.standard.controller 下的日志 -->
    <!-- additivity 设置为 true，表示只在当前 logger 中的 appender-ref 中生效 -->
    <logger name="com.wangguangwu.standard.controller" level="${log.level}" additivity="false">
        <appender-ref ref="WANG-CONTROLLER"/>
        <appender-ref ref="ERROR-APPENDER"/>
    </logger>

    <!-- logger 负责打印 com.wangguangwu.standard.service 下的日志 -->
    <!-- additivity 设置为 true，表示只在当前 logger 中的 appender-ref 中生效 -->
    <logger name="com.wangguangwu.standard.service" level="${log.level}" additivity="false">
        <appender-ref ref="WANG-SERVICE"/>
        <appender-ref ref="ERROR-APPENDER"/>
    </logger>

    <!-- root 指向控制台输出 -->
    <root level="INFO">
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```