## 项目背景

小说网站业务难度适中，没有商城系统那种复杂的业务。但是作为互联网项目，一样需要面对大规模用户和海量数据的处理，所以高并发、高可用、高性能、高容错、可扩展性、可维护性也是小说网站设计需要考虑的问题，商城系统中所用到的技术同样适用于小说网站。

综上所述，使用微服务架构来构建一个小说门户平台是非常有必要的，利用微服务构建的小说门户平台来学习现下流行技术相较于业务比较复杂的商城系统来说也是比较容易的，非常适合于没有实际微服务项目经验的同学用来学习和入门微服务技术栈。

## 项目简介

novel 是一套基于时下最新 Java 技术栈 `Spring Boot 3` + `Vue 3` 开发的前后端分离学习型小说项目， 由小说门户系统、作家后台管理系统、平台后台管理系统等多个子系统构成。包括小说推荐、作品检索、小说排行榜、小说阅读、小说评论、会员中心、作家专区、充值订阅、新闻发布等功能。

novel-cloud，基于 `Spring Cloud 2022` & `Spring Cloud Alibaba 2022` 构建。

## 开发环境

- MySQL 8.0
- Redis 7.0
- Elasticsearch 8.6.2
- RabbitMQ 3.x
- XXL-JOB 2.3.1
- Nacos 3.0.0
- JDK 17
- Maven 3.8

## 后端技术选型

| 技术                   |       版本       | 说明                                | 官网                               |                                                              学习                                                               |
|----------------------|:--------------:|-----------------------------------| -------------------------------------- |:-----------------------------------------------------------------------------------------------------------------------------:|
| Spring Cloud         |    2022.0.1    | 微服务开发的一站式解决方案                     | [进入](https://spring.io/projects/spring-cloud) |                              [进入](https://docs.spring.io/spring-cloud/docs/current/reference/html/)                               |
| Spring Cloud Alibaba | 2022.0.0.0-RC1 | 阿里巴巴贡献的 Spring Cloud 微服务开发一站式解决方案 | [进入](https://github.com/alibaba/spring-cloud-alibaba) |                              [进入](https://spring-cloud-alibaba-group.github.io/github-pages/2021/zh-cn/2021.0.5.0/index.html)                               |
| Nacos                |     2.2.1      | 服务发现和配置管理                         | [进入](https://nacos.io)  |                              [进入](https://nacos.io/zh-cn/docs/what-is-nacos.html)                               |
| Spring Boot Admin    |     3.0.2      | 微服务管理和监控                          | [进入](https://github.com/codecentric/spring-boot-admin)                |                                [进入](https://codecentric.github.io/spring-boot-admin/3.0.0-M1)                                 |
| Spring Boot          |     3.0.5      | 容器 + MVC 框架                       | [进入](https://spring.io/projects/spring-boot) |                              [进入](https://docs.spring.io/spring-boot/docs/3.0.0/reference/html)                               |
| MyBatis              |     3.5.9      | ORM 框架                            | [进入](http://www.mybatis.org)                |                                       [进入](https://mybatis.org/mybatis-3/zh/index.html)                                       |
| MyBatis-Plus         |     3.5.3      | MyBatis 增强工具                      | [进入](https://baomidou.com/)                 |                                           [进入](https://baomidou.com/pages/24112f/)                                            |
| JJWT                 |     0.11.5     | JWT 登录支持                          | [进入](https://github.com/jwtk/jjwt)          |                                                               -                                                               |
| Lombok               |    1.18.24     | 简化对象封装工具                          | [进入](https://github.com/projectlombok/lombok) |                                         [进入](https://projectlombok.org/features/all)                                          |
| Caffeine             |     3.1.0      | 本地缓存支持                            | [进入](https://github.com/ben-manes/caffeine)              |                                  [进入](https://github.com/ben-manes/caffeine/wiki/Home-zh-CN)                                  |
| Redis                |      7.0       | 分布式缓存支持                           | [进入](https://redis.io)                 |                                                  [进入](https://redis.io/docs)                                                  |
| Redisson             |     3.17.4     | 分布式锁实现                            | [进入](https://github.com/redisson/redisson)                 |                              [进入](https://github.com/redisson/redisson/wiki/%E7%9B%AE%E5%BD%95)                               |
| RabbitMQ             |      3.x       | 开源消息中间件                           | [进入](https://www.rabbitmq.com)                 |                                [进入](https://www.rabbitmq.com/tutorials/tutorial-one-java.html)                                |
| MySQL                |      8.0       | 数据库服务                             | [进入](https://www.mysql.com)                 |                       [进入](https://docs.oracle.com/en-us/iaas/mysql-database/doc/getting-started.html)                        |
| ShardingSphere-JDBC  |     5.1.1      | 数据库分库分表支持                         | [进入](https://shardingsphere.apache.org)                 |                              [进入](https://shardingsphere.apache.org/document/5.1.1/cn/overview)                               |
| Elasticsearch        |     8.6.2      | 搜索引擎服务                            | [进入](https://www.elastic.co)                 |                       [进入](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)                        |
| XXL-JOB              |     2.3.1      | 分布式任务调度平台                         | [进入](https://www.xuxueli.com/xxl-job)                 |                                             [进入](https://www.xuxueli.com/xxl-job)                                             |
| Springdoc-openapi    |     2.0.0      | Swagger 3 接口文档自动生成                | [进入](https://github.com/springdoc/springdoc-openapi)                |                                                 [进入](https://springdoc.org/)                                                  |

## 项目结构

```
novel-cloud
├── novel-core -- 项目核心模块，供其它各个业务微服务依赖
├── novel-gateway -- 基于 Spring Cloud Gateway 构建的微服务网关
├── novel-monitor -- 基于 Spring Boot Admin 构建的微服务监控中心
├── novel-search -- 基于 Elasticsearch 构建的搜索服务
├── novel-home -- 首页微服务
├── novel-news -- 新闻微服务
├── novel-book -- 小说微服务
├── novel-user -- 会员微服务
├── novel-author -- 作家微服务  
└── novel-resource -- 资源微服务 
```

