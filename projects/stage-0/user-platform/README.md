# Java 实战训练营

## HomeWork

- [Week-03(jolokia microprofile-config)](homework/week-03.md)

- [Week-04(my dependency-injection my-configuration)](homework/week-04.md)


## UserPlatform 说明

当前项目起始于 **Java项目实战营**，在第一次课程之后，从 `https://github.com/mercyblitz/geekbang-lessons` 进行了代码克隆，并拉出新分支 `dev`进行开发。
而后的代码更新皆采用手动合并的方式，一是可以更加熟悉代码编写原理，二是可以加入自己的理解和新的想法。

### 结构说明

当下一共分为了 `6` 个模块，分别是

- `user-boot`: 主要用于定义程序**引导方式**、**时机**以及**顺序**，核心接口: `org.geektimes.boot.ApplicationBootstrapInitializer`
- `user-configuration`: 配置模块，现进行 `microprofile-config` 的支持，用于注册并暴露 `ConfigSource`，并支持拓展
- `user-core`: `UserPlatform` 核心模块，主要进行 `Tomcat` 服务的启动，目前使用嵌入式 `Tomcat`
- `user-manager`: `Java Manager` 模块，用于支持 `MBean` 的代理
- `user-orm`: 数据访问模块，主要用于数据库的访问
- `user-web`: 项目启动入口，各模块集成地

```text
|-- user-platform
    |-- NOTE.md
    |-- pom.xml
    |-- README.md
    |-- homework
    |   |-- week-03.md
    |   |-- week-04.md
    |-- user-boot
    |   |-- pom.xml
    |   |-- src
    |   |   |-- main
    |   |   |   |-- java
    |   |   |   |   |-- org
    |   |   |   |       |-- geektimes
    |   |   |   |           |-- boot
    |   |   |   |               |-- ApplicationBootstrapInitializer.java
    |   |   |   |               |-- UserPlatformServletContainerInitializer.java
    |   |   |   |               |-- wrapper
    |   |   |   |                   |-- PrioritizedWrapper.java
    |   |   |   |-- resources
    |   |   |       |-- META-INF
    |   |   |           |-- services
    |   |   |               |-- javax.servlet.ServletContainerInitializer
    |   |   |-- test
    |   |       |-- java
    |-- user-configuration
    |   |-- pom.xml
    |   |-- src
    |   |   |-- main
    |   |   |   |-- java
    |   |   |   |   |-- org
    |   |   |   |       |-- geektimes
    |   |   |   |           |-- configuration
    |   |   |   |               |-- ConfigurationBootstrapInitializer.java
    |   |   |   |               |-- spi
    |   |   |   |                   |-- UserPlatformConfig.java
    |   |   |   |                   |-- UserPlatformConfigBuilder.java
    |   |   |   |                   |-- UserPlatformConfigProviderResolver.java
    |   |   |   |                   |-- converter
    |   |   |   |                   |   |-- AbstractConverter.java
    |   |   |   |                   |   |-- BooleanConverter.java
    |   |   |   |                   |   |-- ByteConverter.java
    |   |   |   |                   |   |-- ConverterFactory.java
    |   |   |   |                   |   |-- DoubleConverter.java
    |   |   |   |                   |   |-- FloatConverter.java
    |   |   |   |                   |   |-- IntegerConverter.java
    |   |   |   |                   |   |-- LongConverter.java
    |   |   |   |                   |   |-- PrioritizedConverter.java
    |   |   |   |                   |   |-- ShortConverter.java
    |   |   |   |                   |   |-- StringConverter.java
    |   |   |   |                   |-- source
    |   |   |   |                       |-- ConfigSourceFactory.java
    |   |   |   |                       |-- ConfigSourceOrdinalComparator.java
    |   |   |   |                       |-- DynamicConfigSource.java
    |   |   |   |                       |-- MapBasedConfigSource.java
    |   |   |   |                       |-- MicroprofileResourceConfigSource.java
    |   |   |   |                       |-- OperationSystemEnvironmentVariablesConfigSource.java
    |   |   |   |                       |-- SystemPropertiesConfigSource.java
    |   |   |   |                       |-- servlet
    |   |   |   |                           |-- ServletContextConfigSource.java
    |   |   |   |-- resources
    |   |   |       |-- META-INF
    |   |   |           |-- microprofile-config.properties
    |   |   |           |-- services
    |   |   |               |-- org.eclipse.microprofile.config.spi.ConfigProviderResolver
    |   |   |               |-- org.geektimes.boot.ApplicationBootstrapInitializer
    |   |   |               |-- org.geektimes.configuration.ConfigurationInitializer
    |   |   |-- test
    |   |       |-- java
    |-- user-core
    |   |-- pom.xml
    |   |-- src
    |   |   |-- main
    |   |   |   |-- java
    |   |   |   |   |-- org
    |   |   |   |       |-- geektimes
    |   |   |   |           |-- web
    |   |   |   |               |-- Configuration.java
    |   |   |   |               |-- FuYi.java
    |   |   |   |               |-- aop
    |   |   |   |               |   |-- AdviceChain.java
    |   |   |   |               |   |-- Aop.java
    |   |   |   |               |   |-- ProxyAdvisor.java
    |   |   |   |               |   |-- ProxyCreator.java
    |   |   |   |               |   |-- ProxyPointcut.java
    |   |   |   |               |   |-- advice
    |   |   |   |               |   |   |-- Advice.java
    |   |   |   |               |   |   |-- AfterReturningAdvice.java
    |   |   |   |               |   |   |-- AroundAdvice.java
    |   |   |   |               |   |   |-- MethodBeforeAdvice.java
    |   |   |   |               |   |   |-- ThrowsAdvice.java
    |   |   |   |               |   |-- annotation
    |   |   |   |               |       |-- Aspect.java
    |   |   |   |               |       |-- Order.java
    |   |   |   |               |-- core
    |   |   |   |               |   |-- AbstractComponentContext.java
    |   |   |   |               |   |-- BeanContainer.java
    |   |   |   |               |   |-- ComponentContext.java
    |   |   |   |               |   |-- ComponentContextFactory.java
    |   |   |   |               |   |-- annotation
    |   |   |   |               |   |   |-- Autowired.java
    |   |   |   |               |   |   |-- Component.java
    |   |   |   |               |   |   |-- Controller.java
    |   |   |   |               |   |   |-- Repository.java
    |   |   |   |               |   |   |-- Service.java
    |   |   |   |               |   |-- context
    |   |   |   |               |       |-- DefaultComponentContext.java
    |   |   |   |               |       |-- provider
    |   |   |   |               |           |-- AbstractComponentContextProvider.java
    |   |   |   |               |           |-- ComponentContextProvider.java
    |   |   |   |               |           |-- JndiComponentContextProvider.java
    |   |   |   |               |-- function
    |   |   |   |               |   |-- ThrowableAction.java
    |   |   |   |               |   |-- ThrowableFunction.java
    |   |   |   |               |-- ioc
    |   |   |   |               |   |-- Ioc.java
    |   |   |   |               |-- mvc
    |   |   |   |               |   |-- ControllerInfo.java
    |   |   |   |               |   |-- DispatcherServlet.java
    |   |   |   |               |   |-- FrontControllerServlet.java
    |   |   |   |               |   |-- HandlerMethodInfo.java
    |   |   |   |               |   |-- PathInfo.java
    |   |   |   |               |   |-- annotation
    |   |   |   |               |   |   |-- RequestMapping.java
    |   |   |   |               |   |   |-- RequestMethod.java
    |   |   |   |               |   |   |-- RequestParam.java
    |   |   |   |               |   |   |-- ResponseBody.java
    |   |   |   |               |   |-- bean
    |   |   |   |               |   |   |-- ModelAndView.java
    |   |   |   |               |   |-- controller
    |   |   |   |               |   |   |-- Controller.java
    |   |   |   |               |   |   |-- PageController.java
    |   |   |   |               |   |   |-- RestController.java
    |   |   |   |               |   |-- handler
    |   |   |   |               |   |   |-- ControllerHandler.java
    |   |   |   |               |   |   |-- Handler.java
    |   |   |   |               |   |   |-- JspHandler.java
    |   |   |   |               |   |   |-- PreRequestHandler.java
    |   |   |   |               |   |   |-- RequestHandlerChain.java
    |   |   |   |               |   |   |-- SimpleUrlHandler.java
    |   |   |   |               |   |-- header
    |   |   |   |               |   |   |-- CacheControlHeaderWriter.java
    |   |   |   |               |   |   |-- HeaderWriter.java
    |   |   |   |               |   |   |-- annotation
    |   |   |   |               |   |       |-- CacheControl.java
    |   |   |   |               |   |-- render
    |   |   |   |               |       |-- DefaultRender.java
    |   |   |   |               |       |-- InternalErrorRender.java
    |   |   |   |               |       |-- JsonRender.java
    |   |   |   |               |       |-- NotFoundRender.java
    |   |   |   |               |       |-- Render.java
    |   |   |   |               |       |-- ViewRender.java
    |   |   |   |               |-- server
    |   |   |   |               |   |-- Server.java
    |   |   |   |               |   |-- TomcatServer.java
    |   |   |   |               |-- util
    |   |   |   |                   |-- CastUtil.java
    |   |   |   |                   |-- ClassUtil.java
    |   |   |   |                   |-- ValidateUtil.java
    |   |   |   |-- resources
    |   |   |       |-- META-INF
    |   |   |           |-- services
    |   |   |               |-- org.geektimes.web.core.context.provider.ComponentContextProvider
    |   |   |-- test
    |   |       |-- java
    |   |           |-- com
    |   |               |-- geektimes
    |   |                   |-- web
    |   |                       |-- aop
    |   |                       |   |-- AopTest.java
    |   |                       |-- bean
    |   |                       |   |-- FuYiAspect.java
    |   |                       |   |-- FuYiAspect1.java
    |   |                       |   |-- FuYiAspect2.java
    |   |                       |   |-- FuYiController.java
    |   |                       |   |-- FuYiService.java
    |   |                       |   |-- FuYiServiceImpl.java
    |   |                       |-- ioc
    |   |                           |-- IocTest.java
    |-- user-manager
    |   |-- pom.xml
    |   |-- src
    |   |   |-- main
    |   |   |   |-- java
    |   |   |   |   |-- org
    |   |   |   |       |-- geektimes
    |   |   |   |           |-- manager
    |   |   |   |               |-- ManagerBootstrapInitializer.java
    |   |   |   |               |-- listener
    |   |   |   |               |   |-- MBeanRegisterListener.java
    |   |   |   |               |-- mbean
    |   |   |   |                   |-- context
    |   |   |   |                       |-- ComponentContextMBean.java
    |   |   |   |-- resources
    |   |   |       |-- META-INF
    |   |   |           |-- services
    |   |   |               |-- org.geektimes.boot.ApplicationBootstrapInitializer
    |   |   |-- test
    |   |       |-- java
    |-- user-orm
    |   |-- pom.xml
    |   |-- src
    |   |   |-- main
    |   |   |   |-- java
    |   |   |   |   |-- org
    |   |   |   |       |-- geektimes
    |   |   |   |           |-- orm
    |   |   |   |               |-- jpa
    |   |   |   |                   |-- DelegatingEntityManager.java
    |   |   |   |                   |-- annotation
    |   |   |   |                       |-- LocalTransactional.java
    |   |   |   |-- resources
    |   |   |-- test
    |   |       |-- java
    |-- user-web
        |-- pom.xml
        |-- src
        |   |-- main
        |       |-- java
        |       |   |-- org
        |       |       |-- geektimes
        |       |           |-- App.java
        |       |           |-- function
        |       |           |   |-- ThrowableFunction.java
        |       |           |-- projects
        |       |               |-- user
        |       |                   |-- domain
        |       |                   |   |-- User.java
        |       |                   |-- orm
        |       |                   |   |-- jpa
        |       |                   |       |-- JpaDemo.java
        |       |                   |-- repository
        |       |                   |   |-- DatabaseUserRepository.java
        |       |                   |   |-- InMemoryUserRepository.java
        |       |                   |   |-- UserRepository.java
        |       |                   |-- service
        |       |                   |   |-- UserService.java
        |       |                   |   |-- impl
        |       |                   |       |-- UserServiceImpl.java
        |       |                   |-- spi
        |       |                   |   |-- configuration
        |       |                   |       |-- source
        |       |                   |           |-- JndiConfigSource.java
        |       |                   |-- sql
        |       |                   |   |-- DBConnectionManager.java
        |       |                   |-- validator
        |       |                   |   |-- bean
        |       |                   |       |-- validation
        |       |                   |           |-- BeanValidationDemo.java
        |       |                   |           |-- DelegatingValidator.java
        |       |                   |           |-- UserValidAnnotationValidator.java
        |       |                   |           |-- annotation
        |       |                   |               |-- UserValid.java
        |       |                   |-- web
        |       |                       |-- ClientBootstrapInitializer.java
        |       |                       |-- controller
        |       |                       |   |-- FuYiController.java
        |       |                       |   |-- HelloWorldController.java
        |       |                       |   |-- UserController.java
        |       |                       |-- filter
        |       |                       |   |-- CharsetEncodingFilter.java
        |       |                       |-- listener
        |       |                       |   |-- ComponentContextBootstrapInitializer.java
        |       |                       |   |-- TestInitializerListener.java
        |       |                       |-- servlet
        |       |                           |-- HelloServlet.java
        |       |-- resources
        |           |-- index.jsp
        |           |-- log4j.properties
        |           |-- login-form.jsp
        |           |-- success.jsp
        |           |-- META-INF
        |           |   |-- context.xml
        |           |   |-- jpa-datasource.properties
        |           |   |-- persistence.xml
        |           |   |-- db
        |           |   |   |-- DDL
        |           |   |       |-- users_table_ddl.sql
        |           |   |-- services
        |           |       |-- java.sql.Driver
        |           |       |-- org.eclipse.microprofile.config.spi.ConfigSource
        |           |       |-- org.geektimes.boot.ApplicationBootstrapInitializer
        |           |       |-- org.geektimes.web.mvc.controller.Controller
        |           |-- static
        |           |   |-- css
        |           |   |   |-- bootstrap-4.6.0.min.css
        |           |   |-- js
        |           |       |-- bootstrap-4.6.0.min.js
        |           |       |-- jquery-3.5.1.slim.min.js
        |           |       |-- popper-1.16.1.min.js
        |           |-- WEB-INF
        |               |-- web.xml
        |               |-- jsp
        |                   |-- coda
        |                   |   |-- footer.jspf
        |                   |-- prelude
        |                       |-- header.jspf
        |                       |-- include-css.jspf
        |                       |-- include-head-meta.jspf
        |                       |-- include-js.jspf
        |                       |-- include-taglibs.jspf
        |                       |-- variables.jspf
```

### 快速启动

1. `git clone https://github.com/thread-zhou/geekbang-lessons.git`

2. `cd geekbang-lessons/projects/stage-0/user-platform` (进入 user-platform 目录)

3. `mvn clean package`

4. `java -jar user-web\target\user-web-v1-SNAPSHOT.jar`

5. 通过浏览器访问 `http://localhost:9090/`



