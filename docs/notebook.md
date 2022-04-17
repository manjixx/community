# 3. 需求分析
* 对标https://elasticsearch.cn

# 4.初识BootStrap
+ [bootstarp](https://v3.bootcss.com/)
+ [bootstrap栅格系统：](https://v3.bootcss.com/css/#grid)
+ [github OAuth]()

# 5.申请github APP 从github登录

* 注册github APP 文档说明
    + github页面底层API —— Github Docs Developers - APPS -Building OAuth APPS

* 注册github APP
    + Settings-develop Settings - OAuth Apps

* 图解Github登录流程
  + 点击登录按钮，调用github的authorize接口，此时github会自动跳转到callback地址，携带code回来
  + 我们获取到code之后，调用github的access_token接口获取到access_token
  + 获取的到access_token后继续调用github的user接口，即可返回user信息
  ![github登录流程图](./picture/github登录流程图.png)
  
* Github登录调用authorize
  * index.html <li><a href="https://github.com/login/oauth/authorize?client_id=5be78fe1bbbc06cf95de&redirect_uri=http://localhost:8080/callback&scope=user">登录</a></li>

* Github获取code
  * AuthorizeController
  * okHttp
  
* Github登录之获取用户信息
  * provider-GithubProvider.class 
  IOC:有```@Componet```注解，对象会自动实力化放入容器中
  * dto:data to object
    * AccessTokenDTO.class
  
> 此处结束后运行起来发现无法获取用户名和ID，按照如下帖子更正 
> https://www.mawen.co/question/723
> ![访问github API错误](./picture/访问githubAPI错误.png)

* 配置application.properties
springboot启动时，会以map的形式将文件中的值放入容器，通过```@Value(${github.client.id})```字段名获取相应的值
  
* Session 和cookie原理
  + 登录成功后显示登录态
  + session 相当于银行账户
    + 通过 ```HttpServletRequest```获取到session
    + 当我们把````HttpServletRequest```写入到方法时，SpringBoot会自动将上下文中的request放到中间供我们使用
    + 获取到session，之后需要在index.html中去判断是否拿到session-百度 如何使用thymeleaf 取session 
  + cookie 相当于银行银行卡
  
+ 图解MySQL并学习其基本用法
  + UML详解-麻匠社区微信公众号
    + 泛化关系
    + 实现关系
    + 聚合关系
    + 组合关系
    + 关联关系
    + 依赖关系
    + 实箭泛化虚实现，虚线依赖实关联，空菱聚合实组合，项目沟通图常见
  + MySQL
    + 自然型解释语言
    + database table record
    + CRUD
  + H2
    + 特点：
      + 非常快，开源的JDBC API
      + 可直接内置到server里边去，内存数据库
      + 可通过浏览器直接去访问
      + small footprin：about 2MB jar file，为什么选它的原因，通过jar依赖可以直接放入文件
      + 无法创建表：[https://blog.csdn.net/nruuu/article/details/123909184]
      + 
        ```SQL
            CREATE TABLE `user`(
            `id` INT AUTO_INCREMENT PRIMARY KEY,
            `account_id` VARCHAR(100),
            `name` VARCHAR(50),
            `token` CHAR(36),
            `gmt_create` BIGINT,
            `gmt_modified` BIGINT);
        ```
        
# P17.集成 Mybaits 并实现插入数据
  + http://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/
  + Spring 官方文档[https://docs.spring.io/spring-boot/docs/2.0.0.RC1/reference/htmlsingle/#boot-features-embedded-database-support]
  + 定义数据库连接池：
  ```
    spring.datasource.url=jdbc:mysql://localhost/test
    spring.datasource.username=dbuser
    spring.datasource.password=dbpass
    spring.datasource.driver-class-name=com.mysql.jdbc.Driver
  ```
  + Mybatis
    + Mybatis会自动解析state中的内容填入#{state}中
    ```java
        @Mapper
        public interface UserMapper {
        @Insert("insert into user(name,account_id,token,gmt_create,gmt_modified) values(#{name},#{account_id},#{token},#{gmt_create},#{gmt_modified})")
        void insert(User user);
        }
    ```
  + 网络与网络之间传输数据用dto，数据传递数据用model
  + SpringBoot启动项目时：Cannot load driver class: org.h2.Driver,缺乏依赖，添加Maven: com.h2database:h2:1.4.200
  
***
# P18 实现持久化登录状态获取
  + P16
  + 手动模拟cookie和session的交互方式，以实现在服务器宕机或重启时，用户都可以重新登录，并保持登录态
  + 问题：登录成功之后，如何利用java代码，往前端写一个cookie
  + 流程：
    + 登录成功后，获取到用户信息并存储之后，从数据库获得token，以token为依据来绑定前端与后端登录状态
    + 获得用户token代替原来的session，即我们主动往cookie中写一个session，并且在登录验证的时候，能将其识别处理，如果数据库中存在该session，如果存在则登录成功，不存在则未登录成功
    + 使用 HttpServletResponse response 将token写入cookie
      ```
      HttpServletResponse response
      // 此处将token存入数据库的过程已经完成写session的过程，因此仅需要将token写入cookie 即可
            response.addCookie(new Cookie("token",token));
      ```
    + 然后在indexController.java中获取到cookie中的token，与数据库中token对比
      + 注入UserMapper
      + UserMapper中构造findByToken，在数据库中查看是否存在该token，如果存在则得到user，然后利用
      ```
        request.getSession().setAttribute("githubUser",githubUser);
      ``` 
      将user放到前端里边，来确定展示登录或者是我
  
***
# P19 集成Flyway migration
  + Flyway:java数据库移植框架
  +  [Flyway官网](https://flywaydb.org/)

  + 项目流程：
    + pom.xml文件中添加flyway插件
    + 创建文件夹 ```src/main/resource/db/migration/V1__Create_user_table.sql```并填写创表语句
    + 删除原有数据库```rm ~/community·*```
    + 运行```mvn flyway migrate```创建新表
    + 创建文件```src/main/resource/db/migration/V2__Add_bio_col_to_user_table.sql```
  + 注意
    + 使用过程中，不能更改sql脚本文件，否则会抛出异常
    + 注意文件名称一定是V__XXX
    
# P20 使用BootStrap编写文章发布页

+ publish.html
+ bootstrap 栅栏系统,更改publish.html
+ 创建publishController
```java
    @Controller
    public class publishController {
    @GetMapping("/publish")
    public String publish(){
    return "publish";
    }
    }
```
+ 设计页面
  + 详见程序publish.html，在浏览器控制终端设计页面
  + resource-static-css-community.css,设计页边距与背景色

# P21 完成文章发布功能
+ 利用flyway创建数据库
+ 创建Mapper-QuestionMapper
+ 创建Question类
+ 在publishController创建doPublish方法：
    + 当为POST请求时，执行该方法，通过request获取cookie然后获取用户信息，将发布文章的相关信息存入数据库
+ 前端增加错误信息显示

+ 总结流程：
  + form表单中添加一个action，即我们请求的地址```/publish```即post方式的路由
  + 当该表单完成后，点击submit会寻找地址为```/publish```且方法为@PostMapping()的接口
  + 并且路由到该方法中，通过RequestParam将获取到的信息放入Model中回显到前端
    
# P22添加lombok支持
* 本节主要内容：做首页
+ [lombok官网](https://projectlombok.org/)
+ 本节主要功能，用lombok，@Data
  > All together now: A shortcut for @ToString, @EqualsAndHashCode, @Getter on all fields, 
  > and @Setter on all non-final fields, and @RequiredArgsConstructor!

# P23 完成首页问题列表功能
+ indexController 在页面跳转之前添加读取Question表的List，并将读取结果放入Model传输到前端
+ 问题：头像信息存放在User表中，Question表中只有creator信息与User表中id相关联，而此时Qeustion类中表示的是数据库模型，无法增加User对象
+ 解决方案：在传输层dto，增加QuestionDTO，相比与Question增加User对象
+ 问题：增加QuestionDTO后，获取到的List中存储的是QuestionDTO对象，无法通过QuestionMapper获得
+ 解决方案：引入Service层，创建QustionService，从现在需求来看该服务可以调用QuestionMapper和UserMapper，同时调用两张表，然后将两张表的内容组合起来
+ 带有驼峰的变量Mybatis无法直接赋值即如 AVATAR_URL无法转换为avatarUrl，使用````mybatis.configuration.map-underscore-to-camle-case = true```

# P24 答疑
* textarea 使用th:value不能回显，将value改为text
* fastjson可以自动将下划线标示映射到驼峰的属性
* h2数据库到底是什么 和链接异常处理
* 列表日期格式化问题
