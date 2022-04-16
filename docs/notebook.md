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
        
+ P16 集成 Mybaits 并实现插入数据
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
  
+ P17 实现持久化登录状态获取
  + P16
  + 手动模拟cookie和session的交互方式，以实现在服务器宕机或重启时，用户都可以重新登录，并保持登录态