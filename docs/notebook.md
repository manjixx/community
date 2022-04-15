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
