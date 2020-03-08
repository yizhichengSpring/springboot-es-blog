

## 前言

elastichsearch作为一款全文检索的分布式数据库，在很多公司已经落地，用来做搜索像垂直搜索引擎都在使用。本篇博客主要介绍了，es的简单使用，以及用Springboot集成es，完成一个简单的博客检索系统。


## 安装elasticsearch 、kibana


### 安装es

安装es的过程比较简单 [https://www.elastic.co/cn/downloads/past-releases#elasticsearch](https://www.elastic.co/cn/downloads/past-releases#elasticsearch) 从官网选择对应版本即可，下载完毕之后，解压对应的压缩包，得到一个文件夹，进入bin目录执行./elasticsearch即可。（我这里选择了版本7.5.0）

得到以下日志，表明启动成功。打开浏览器尝试去访问es.

![](https://tva1.sinaimg.cn/large/00831rSTgy1gcmi76jkl1j31dw0u0nex.jpg)


![](https://tva1.sinaimg.cn/large/00831rSTgy1gcmi8a6amhj31f00s0jz3.jpg)

返回对应json串，说明es启动成功

- number 指es对应版本这里为7.5.0
- cluster_name 指es对应节点名称，这里为默认名称elasticsearch

### 安装kibana

Kibana 是为 Elasticsearch设计的开源分析和可视化平台。你可以使用 Kibana 来搜索,查看存储在 Elasticsearch 索引中的数据并与之交互，你可以把它理解为类似连接MySQL的工具navicat。但是需要注意,kibana的版本要和es的版本一致。


[https://www.elastic.co/cn/downloads/past-releases#kibana](https://www.elastic.co/cn/downloads/past-releases#kibana)

安装、启动方式几乎和es一致，进入bin目录，执行./kibana即可。

![](https://tva1.sinaimg.cn/large/00831rSTgy1gcmifdwm6rj30l101maac.jpg)

看到红色框内部分出现，表名启动成功，我们去访问一下

![](https://tva1.sinaimg.cn/large/00831rSTgy1gcmig3hq7qj31i50k1qcy.jpg)


出现此画面，表示启动成功。

![](https://tva1.sinaimg.cn/large/00831rSTgy1gcmih27sh6j31gb0u0k4f.jpg)

![](https://tva1.sinaimg.cn/large/00831rSTgy1gcmiiqseckj31go0u0131.jpg)

点击红色框部分进入交互界面。

## 使用postman和kibana去交互es

### postman

试着翻译一下就是，建立一个名称student的索引。body里的json存入对应索引中。

![](https://tva1.sinaimg.cn/large/00831rSTgy1gcmimh1jeoj312i0t841h.jpg)

![](https://tva1.sinaimg.cn/large/00831rSTgy1gcmiqx8jubj31ci0j6767.jpg)

- http请求类型为**PUT**
- student为索引名称
- _doc为类型


---

这个就很好理解了，去查询刚才id为1，索引为student的数据。

![](https://tva1.sinaimg.cn/large/00831rSTgy1gcmirq4w20j31b20u0dj8.jpg)


### kibana


![](https://tva1.sinaimg.cn/large/00831rSTgy1gcmiuqyusoj31zw0icn0q.jpg)

![](https://tva1.sinaimg.cn/large/00831rSTgy1gcmiw45cozj31fn0u0doy.jpg)

这个为复合查询，查找last_name为yizhicheng 或者age 为30的，两项条件满足一个即可，可以理解类似为MySQL中的**or**



## MySQL和elasticsearch


MySQL | ES
---|---
Database | Index
Table | Type
Row | Document
Column | Field
Schema | Mapping


### MySQL ES数据同步

如果想把MySQL的数据，同步至es中，应该如何去做，这其中是两个问题

1. 使用什么工具同步？
2. 如何同步？全量or增量


#### 使用什么工具同步?

logstach，logstach为es家族的一部分，专门用来做数据同步，并且安装使用方便，也是官方推荐的一种方案。

我们先看下mysql要同步到es中的数据

![](https://tva1.sinaimg.cn/large/00831rSTgy1gcmjpapyrwj31cu0jmafd.jpg)

我把建表语句放在这里，方便大家使用。


```
/*
 Navicat Premium Data Transfer

 Source Server         : my-prod
 Source Server Type    : MySQL
 Source Server Version : 80013
 Source Host           : 47.95.3.32:3306
 Source Schema         : my_blog

 Target Server Type    : MySQL
 Target Server Version : 80013
 File Encoding         : 65001

 Date: 08/03/2020 15:21:12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_blog
-- ----------------------------
DROP TABLE IF EXISTS `tb_blog`;
CREATE TABLE `tb_blog` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `title` varchar(60) DEFAULT NULL COMMENT '博客标题',
  `author` varchar(60) DEFAULT NULL COMMENT '博客作者',
  `content` mediumtext COMMENT '博客内容',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of tb_blog
-- ----------------------------
BEGIN;
INSERT INTO `tb_blog` VALUES (9, 'Spring boot快速入门', '岸远水声微', '1.Spring Boot是什么\nSpring Boot 是由 Pivotal 团队提供的全新框架，其设计目的是用来简化新 Spring 应用的初始搭建以及开发过程。该框架采用“习惯优于配置”的方式开发，可以快速构建Spring应用。\n\n特性\n\n能够创建独立的Spring应用\n本身嵌入了Tomcat、Jetty容器\n提供可选的starter依赖库简化应用构建配置\n自动配置Spring以及第三方依赖\n提供生产级的特性，如度量、检查和外部化配置\n无代码生成并且不需要XML配置\r\n————————————————\r\n版权声明：本文为CSDN博主「岸远水声微」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。\r\n原文链接：https://blog.csdn.net/hzygcs/article/details/85230526', '2020-03-07 14:23:25', '2020-03-07 14:23:27');
INSERT INTO `tb_blog` VALUES (10, 'springboot——简单项目实战', 'qq_38793958邓鑫涛', '一，概述\n\nspringboot 的本质还是spring，他们之间的区别就在与简化了一些配置文件的操作和依赖的管理；\n\n举个例子：\n\n比如你从商家买了一个结构复杂的桌子，按照spring的模式，它会把零件发给你，组装是你自己的事情。而这张桌子是在你组装好后才能使用它多种多样的功能。\n\n而按照springboot的模式，他相当于直接把这个桌子发给你，你只需要知道你想要桌子的型号和功能，买回来就能很快速的使用。\n\n二，重点介绍;\n\n（1）起步依赖；\n\n（2）自动配置；\n\n  问题来了，什么是起步依赖呢？依赖我们知道，他在我们利用Maven开发时具有很重要的作用，从概念中我们或许可以知道他的意义：告诉Spring Boot需要什么功能，它就能引入需要的库；我的理解是，在头疼的众多依赖中，springboot设计了一种方法，他可以有效的提供给你想要的依赖而不会出现版本冲突，或依赖缺失等令我们抓耳挠腮的问题。\n\n自动配置设计是springboot的高度自动化的特点，至于有多高，下面的学习中我们将慢慢领悟。\n\n三，开始第一个程序\n\n（1）开发环境：安装有sts插件的eclipse\n\n（2）Thymeleaf来定义Web视图；\n\n（3）数据持久化：Spring Data JPA；\n\n（4）数据库：嵌入式的H2数据库；\n\n（5）功能：构建以个简单的阅读列表应用程序。\n\n四，具体步骤（很具体，具体到令人发指）\n\n1，打开eclipse，安装sts插件：\n\n（1）Help--->Eclipse MarkePlace--->Populer\n\n  找到Spring Tool Suite (STS) for Eclipse点击安装\n\n（呀！竟然找不到，没关系，没有什么是一个链接解决不了的：https://blog.csdn.net/zhen_6137/article/details/79383941）\n\n（好吧！插件安装失败，咱们改用IDEA吧！）\n\n打开IDEA：new -->project--->Spring Initializer \n\nProject SDK ：Java版本\n\nService URL选择默认。\r\n————————————————\r\n版权声明：本文为CSDN博主「qq_38793958邓鑫涛」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。\r\n原文链接：https://blog.csdn.net/qq_38793958/article/details/82898091', '2020-03-07 14:23:30', '2020-03-07 14:23:33');
INSERT INTO `tb_blog` VALUES (11, '初识SpringBoot', '一倾而尽', '什么是SpringBoot\n初闻SpringBoot的开发者，必然会想到Spring这一时下最流行的开发框架，SpringBoot确实和Spring有着千丝万缕的关系。要想将SpringBoot学习透彻，对于Spring的学习也是必不可少的（不对Spring作过多介绍）。\n随着动态语言的流行（Ruby、Groovy、Scala、Node.js），Java的开发显得也就显得格外的笨重，即使是使用各种流行框架（Spring等），依然会有各种繁重的配置，导致了低效率的开发、复杂的部署流程以及第三方技术集成难度大。为了提升开发效率，节约开发成本，SpringBoot也就应用而生。它使用习惯优于配置的理念，让开发者无需繁重、复杂的文件配置就可以快速地将项目运行起来。使用SpringBoot很容易创建一个独立运行（运行jar，内嵌Servlet）、准生产级别的基于Spring框架的项目，它可以不用或者说只需要使用很少的Spring配置。\n使用SpringBoot能为我们带来什么\n简单的概括起来就是简单、快速、配置少\n比起传统的Spring Web项目，它不需要下列如此多的步骤：\n1. 配置web.xml，springmvc.xml和spring.xml\n2. 配置数据库连接池，配置数据库事务等\n3. 配置记录系统工作的日志\n4. 配置加载系统运行时系统配置文件的读取\n…\n5. 代码编写完成后，需要部署到tomcat等运行环境上调试\n6. 不支持持续集成、持续部署等\n等等一系列的因素都为SpringBoot的向荣产生了良好的开端。\n快速入门\n1.maven构建项目\n若是熟悉IDE，直接使用IDE完成即可，这里从最原始的方法开始。\n1.1 访问http://start.spring.io/\n1.2 选择构建工具Maven Project、Spring Boot版本2.1.4以及一些工程基本信息，点击“Generate Project”，即可下载项目压缩包：\r\n————————————————\r\n版权声明：本文为CSDN博主「一倾而尽」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。\r\n原文链接：https://blog.csdn.net/weixin_38339025/article/details/89037359', '2020-03-07 14:24:08', '2020-03-07 14:24:12');
INSERT INTO `tb_blog` VALUES (12, 'SpringBoot启动过程', 'chengbinbbs', 'SpringApplicationRunListeners内部持有SpringApplicationRunListener集合和1个Log日志类。用于SpringApplicationRunListener监听器的批量执行。\n\nSpringApplicationRunListener用于监听SpringApplication的run方法的执行，它定义了5个步骤：\n\nstarting：run方法执行的时候立马执行，对应的事件类型是ApplicationStartedEvent\nenvironmentPrepared：ApplicationContext创建之前并且环境信息准备好的时候调用，对应的事件类型是ApplicationEnvironmentPreparedEvent\ncontextPrepared：ApplicationContext创建好并且在source加载之前调用一次，没有具体的对应事件\ncontextLoaded：ApplicationContext创建并加载之后并在refresh之前调用，对应的事件类型是ApplicationPreparedEvent\nfinished：run方法结束之前调用，对应事件的类型是ApplicationReadyEvent或ApplicationFailedEvent\nSpringApplicationRunListener目前只有一个实现类EventPublishingRunListener，详见获取SpringApplicationRunListeners。它把监听的过程封装成了SpringApplicationEvent事件并让内部属性ApplicationEventMulticaster接口的实现类SimpleApplicationEventMulticaster广播出去，广播出去的事件对象会被SpringApplication中的listeners属性进行处理。\n\n所以说SpringApplicationRunListener和ApplicationListener之间的关系是通过ApplicationEventMulticaster广播出去的SpringApplicationEvent所联系起来的\n\n2.启动事件监听器\n通过listeners.starting()可以启动事件监听器SpringApplicationRunListener ，SpringApplicationRunListener 是一个启动事件监听器接口：\r\n————————————————\r\n版权声明：本文为CSDN博主「chengbinbbs」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。\r\n原文链接：https://blog.csdn.net/chengbinbbs/article/details/88557162', '2020-03-07 14:24:47', '2020-03-07 14:24:52');
INSERT INTO `tb_blog` VALUES (13, 'springboot知识点整理', 'Janson_Lin', '那 Spring Boot 有何魔法？自动配置、起步依赖、Actuator、命令行界面(CLI) 是 Spring Boot 最重要的 4 大核心特性，其中 CLI 是 Spring Boot 的可选特性，虽然它功能强大，但也引入了一套不太常规的开发模型，因而这个系列的文章仅关注其它 3 种特性。\n\n如文章标题，本文是这个系列的第一部分，将为你打开 Spring Boot 的大门，重点为你剖析其启动流程以及自动配置实现原理。要掌握这部分核心内容，理解一些 Spring 框架的基础知识，将会让你事半功倍。\n\n一、抛砖引玉：探索Spring IoC容器\n如果有看过 SpringApplication.run()方法的源码，Spring Boot 冗长无比的启动流程一定会让你抓狂，透过现象看本质，SpringApplication 只是将一个典型的 Spring 应用的启动流程进行了扩展，因此，透彻理解 Spring 容器是打开 Spring Boot 大门的一把钥匙。\n\n1.1、Spring IoC容器\n可以把 Spring IoC 容器比作一间餐馆，当你来到餐馆，通常会直接招呼服务员：点菜！至于菜的原料是什么？如何用原料把菜做出来？可能你根本就不关心。IoC 容器也是一样，你只需要告诉它需要某个bean，它就把对应的实例（instance）扔给你，至于这个bean是否依赖其他组件，怎样完成它的初始化，根本就不需要你关心。\n\n作为餐馆，想要做出菜肴，得知道菜的原料和菜谱，同样地，IoC 容器想要管理各个业务对象以及它们之间的依赖关系，需要通过某种途径来记录和管理这些信息。 BeanDefinition对象就承担了这个责任：容器中的每一个 bean 都会有一个对应的 BeanDefinition 实例，该实例负责保存bean对象的所有必要信息，包括 bean 对象的 class 类型、是否是抽象类、构造方法和参数、其它属性等等。当客户端向容器请求相应对象时，容器就会通过这些信息为客户端返回一个完整可用的 bean 实例。\n\n原材料已经准备好（把 BeanDefinition 看着原料），开始做菜吧，等等，你还需要一份菜谱， BeanDefinitionRegistry和 BeanFactory就是这份菜谱，BeanDefinitionRegistry 抽象出 bean 的注册逻辑，而 BeanFactory 则抽象出了 bean 的管理逻辑，而各个 BeanFactory 的实现类就具体承担了 bean 的注册以及管理工作。', '2020-03-07 14:25:30', '2020-03-07 14:25:34');
INSERT INTO `tb_blog` VALUES (14, 'Elasticsearch学习，请先看这一篇！', 'achuo', '题记：\nElasticsearch研究有一段时间了，现特将Elasticsearch相关核心知识、原理从初学者认知、学习的角度，从以下9个方面进行详细梳理。欢迎讨论……\n\n0. 带着问题上路——ES是如何产生的？\n（1）思考：大规模数据如何检索？\n如：当系统数据量上了10亿、100亿条的时候，我们在做系统架构的时候通常会从以下角度去考虑问题： \n1）用什么数据库好？(mysql、sybase、oracle、达梦、神通、mongodb、hbase…) \n2）如何解决单点故障；(lvs、F5、A10、Zookeep、MQ) \n3）如何保证数据安全性；(热备、冷备、异地多活) \n4）如何解决检索难题；(数据库代理中间件：mysql-proxy、Cobar、MaxScale等;) \n5）如何解决统计分析问题；(离线、近实时)\n\n（2）传统数据库的应对解决方案\n对于关系型数据，我们通常采用以下或类似架构去解决查询瓶颈和写入瓶颈： \n解决要点： \n1）通过主从备份解决数据安全性问题； \n2）通过数据库代理中间件心跳监测，解决单点故障问题； \n3）通过代理中间件将查询语句分发到各个slave节点进行查询，并汇总结果 ', '2020-03-07 14:26:12', '2020-03-07 14:26:16');
INSERT INTO `tb_blog` VALUES (15, 'Elasticsearch简介与实战', '山影少年', 'Elasticsearch是一个开源的分布式、RESTful 风格的搜索和数据分析引擎，它的底层是开源库Apache Lucene。\n  Lucene 可以说是当下最先进、高性能、全功能的搜索引擎库——无论是开源还是私有，但它也仅仅只是一个库。为了充分发挥其功能，你需要使用 Java 并将 Lucene 直接集成到应用程序中。 更糟糕的是，您可能需要获得信息检索学位才能了解其工作原理，因为Lucene 非常复杂。\n  为了解决Lucene使用时的繁复性，于是Elasticsearch便应运而生。它使用 Java 编写，内部采用 Lucene 做索引与搜索，但是它的目标是使全文检索变得更简单，简单来说，就是对Lucene 做了一层封装，它提供了一套简单一致的 RESTful API 来帮助我们实现存储和检索。\n  当然，Elasticsearch 不仅仅是 Lucene，并且也不仅仅只是一个全文搜索引擎。 它可以被下面这样准确地形容：\n\n一个分布式的实时文档存储，每个字段可以被索引与搜索；\n一个分布式实时分析搜索引擎；\n能胜任上百个服务节点的扩展，并支持 PB 级别的结构化或者非结构化数据。\n由于Elasticsearch的功能强大和使用简单，维基百科、卫报、Stack Overflow、GitHub等都纷纷采用它来做搜索。现在，Elasticsearch已成为全文搜索领域的主流软件之一。\n  下面将介绍Elasticsearch的安装与简单使用。\n\n安装并运行Elasticsearch\n  安装 Elasticsearch 之前，你需要先安装一个较新版本的 Java，最好的选择是，你可以从\n\n作者：山阴少年\n链接：https://www.jianshu.com/p/d48c32423789\n来源：简书\n著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。', '2020-03-07 14:27:01', '2020-03-07 14:27:06');
INSERT INTO `tb_blog` VALUES (16, '终于有人把Elasticsearch原理讲透了！', 'channingbreeze', '1.场景—:使用Elasticsearch作为主要的后端 传统项目中,搜索引擎是部署在成熟的数据存储的顶部,以提供快速且相关的搜索能力。这是因为早期的搜索引擎不能提供耐用的​​存储或其他经常需要的功能,如统计。 ...\n2.场景二:在现有系统中增加elasticsearch 由于ES不能提供存储的所有功能,一些场景下需要在现有系统数据存储的基础上新增...\n3.场景三:使用elasticsearch和现有的工具 在一些使用情况下,您不必写一行代码...', '2020-03-07 14:28:01', '2020-03-07 14:28:04');
INSERT INTO `tb_blog` VALUES (17, '小记：介绍vue.js', '来自东北的大黑猫', '  vue.js是目前比较热门的前端框架之一。它具有易用，灵活，高效等特点。它也提供一种帮助我们快速构建饼开发前端项目的模式。本次分享主要就是介绍vuejs，了解vuejs的基本知识，以及开发流程，进一步了解如何通过vuejs构建一个前端项目。\n\n    主要通过四个部分讲解，介绍vue,vue的实例，以及vue的技术栈，最后是vue插件的使用。\n\n    什么是vue?vue就是一个渐进式的JS框架。主要作用就是动态构建用户界面。\n\n    渐进式是什么意思呢？\n\n    vue的核心功能就是一个视图模板引擎，包含声明式渲染以及组件系统。在核心部件的基础上添加客户端路由、大规模状态管理来构建一个完整的框架，下图就是vue包含的所有部件。这些功能是相互独立的，可以在核心部件基础上选择任意你所需要的部件。这也就是‘渐进式’的概念。\r\n————————————————\r\n版权声明：本文为CSDN博主「来自东北的大黑猫」的原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接及本声明。\r\n原文链接：https://blog.csdn.net/qq_16858683/article/details/81021315', '2020-03-07 14:28:48', '2020-03-07 14:28:52');
INSERT INTO `tb_blog` VALUES (18, 'MQ详解及四大MQ比较', '迭名', '息服务器，作为server提供消息核心服务\n\n      2.2 Producer\n\n消息生产者，业务的发起方，负责生产消息传输给broker，\n\n      2.3 Consumer\n\n消息消费者，业务的处理方，负责从broker获取消息并进行业务逻辑处理\n\n      2.4 Topic\n\n主题，发布订阅模式下的消息统一汇集地，不同生产者向topic发送消息，由MQ服务器分发到不同的订阅者，实现消息的       广播\n\n      2.5 Queue\n\n队列，PTP模式下，特定生产者向特定queue发送消息，消费者订阅特定的queue完成指定消息的接收\n\n      2.6 Message\n\n消息体，根据不同通信协议定义的固定格式进行编码的数据包，来封装业务数据，实现消息的传输\n\n \n\n3 消息中间件模式分类\n      3.1 点对点\n\nPTP点对点:使用queue作为通信载体 \n', '2020-03-07 14:30:08', '2020-03-07 14:30:12');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;

```


[https://www.elastic.co/cn/downloads/past-releases#logstash](https://www.elastic.co/cn/downloads/past-releases#logstash)

注:**版本同样要和es一致，选择7.5.0**

下载完毕之后，查看文件夹。

![](https://tva1.sinaimg.cn/large/00831rSTgy1gcmj70uyboj30780aiaaj.jpg)

mysql 的jar包为我后期放入，需要和你的MySQL版本一致。我们需要**在config目录下创建一个文件为mysql.conf** 。


```
   input {
     jdbc {
        # jar包存放目录
      jdbc_driver_library => "/Users/yi/Downloads/logstash-7.5.0/mysql-connector-java-8.0.13.jar"
       #  MySQL 驱动
       jdbc_driver_class => "com.mysql.cj.jdbc.Driver"
       # 8.0以上版本：一定要把serverTimezone=UTC天加上
       jdbc_connection_string => "jdbc:mysql://127.0.0.1:3306/my_blog?characterEncoding=utf8&useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true"
       jdbc_user => "root"
       jdbc_password => "root"
       schedule => "* * * * *"
      clean_run => true
        # 需要导入进es的数据，这里选择全部导入，根据时间倒序
      statement => "SELECT * FROM tb_blog  ORDER BY update_time DESC"
    }
  }
  output {
      elasticsearch {
          # ES的IP地址及端口
          hosts => ["127.0.0.1:9200"]
          # 索引名称 会在es中建一个blog的索引
          index => "blog"
          # 需要关联的数据库中有有一个id字段，对应类型中的id
          document_id => "%{id}"
          #document_type => "user"
      }

  }
```

所有注释我基本已经加上去了，其实配置还是很简单的。

##### 启动

进入bin目录下


```
./logstach -f ../config/mysql.conf
```

启动也是白启动，肯定会报错，说到这，你估计要气了，有坑早不说，现在才说，这个问题，网上出现的几率并不高，可能是我使用的7.5.0这个版本在2020-03月还算比较新？

解决方案也很简单。

[https://stackoverflow.com/questions/53237741/logstash-com-mysql-jdbc-driver-not-loaded](https://stackoverflow.com/questions/53237741/logstash-com-mysql-jdbc-driver-not-loaded)

![](https://tva1.sinaimg.cn/large/00831rSTgy1gcmjjruyzcj318g0fcju0.jpg)

将mysql的jar包放入 对应logstach-core/lib/jars/目录下即可。

![](https://tva1.sinaimg.cn/large/00831rSTgy1gcmjlrlm0rj31ru0amdjf.jpg)

放入之后，再回bin目录执行命令即可。

```
./logstach -f ../config/mysql.conf
```

去kibana中查看


```
GET /blog/_search
```

![](https://tva1.sinaimg.cn/large/00831rSTgy1gcmjupjmwaj30u00wigxi.jpg)

10条数据已经全部导入。

#### 如何同步？全量or增量

建议第一次全量同步，并记录一个标记位，下次再导入的时候，可以选择增量同步，从标记位开始。


## ik 分词

es最大的优势就是搜索，但是系统自带的中文分词并不好用。

 输入:
```
POST _analyze
{
  "analyzer": "standard",
  "text": "你好，我是中国人"
}
```

输出:

```
{
  "tokens" : [
    {
      "token" : "你",
      "start_offset" : 0,
      "end_offset" : 1,
      "type" : "<IDEOGRAPHIC>",
      "position" : 0
    },
    {
      "token" : "好",
      "start_offset" : 1,
      "end_offset" : 2,
      "type" : "<IDEOGRAPHIC>",
      "position" : 1
    },
    {
      "token" : "我",
      "start_offset" : 3,
      "end_offset" : 4,
      "type" : "<IDEOGRAPHIC>",
      "position" : 2
    },
    {
      "token" : "是",
      "start_offset" : 4,
      "end_offset" : 5,
      "type" : "<IDEOGRAPHIC>",
      "position" : 3
    },
    {
      "token" : "中",
      "start_offset" : 5,
      "end_offset" : 6,
      "type" : "<IDEOGRAPHIC>",
      "position" : 4
    },
    {
      "token" : "国",
      "start_offset" : 6,
      "end_offset" : 7,
      "type" : "<IDEOGRAPHIC>",
      "position" : 5
    },
    {
      "token" : "人",
      "start_offset" : 7,
      "end_offset" : 8,
      "type" : "<IDEOGRAPHIC>",
      "position" : 6
    }
  ]
}

```
- 把每一个字按照字符隔开了
- 按照我的想法，应该分隔成这样的才对，你好、我、是、中国人


[https://github.com/medcl/elasticsearch-analysis-ik/releases](https://github.com/medcl/elasticsearch-analysis-ik/releases)

ik分词的地址，注:**ik下载的版本同样要和es版本一致，我这里还是选择了7.5.0**

![](https://tva1.sinaimg.cn/large/00831rSTgy1gcmk36i82xj31m60nejtx.jpg)

### 如何让es使用ik分词呢

将ik分词压缩包解压，复制里面的全部文件，到es文件夹plugin目录下，创建个文件夹，我这里叫ik，然后把所有文件粘贴进ik文件夹中。

![](https://tva1.sinaimg.cn/large/00831rSTgy1gcmk5kfxluj31a303d758.jpg)

重启即可。

### 测试ik分词


```

POST _analyze
{
  "analyzer": "ik_smart",
  "text": "你好，我是中国人"
}
```


```
{
  "tokens" : [
    {
      "token" : "你好",
      "start_offset" : 0,
      "end_offset" : 2,
      "type" : "CN_WORD",
      "position" : 0
    },
    {
      "token" : "我",
      "start_offset" : 3,
      "end_offset" : 4,
      "type" : "CN_CHAR",
      "position" : 1
    },
    {
      "token" : "是",
      "start_offset" : 4,
      "end_offset" : 5,
      "type" : "CN_CHAR",
      "position" : 2
    },
    {
      "token" : "中国人",
      "start_offset" : 5,
      "end_offset" : 8,
      "type" : "CN_WORD",
      "position" : 3
    }
  ]
}

```

## Springboot 整合es

### pom.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.2.1.RELEASE</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.study</groupId>
	<artifactId>springboot-es</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>springboot-es</name>
	<description>Demo project for Spring Boot</description>

	<properties>
		<java.version>1.8</java.version>
	</properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>

```

### application.properties

```
#通用数据源配置
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/my_blog?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=你的密码
#Hikari数据源专用配置
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
#JPA相关配置
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
#es
spring.data.elasticsearch.cluster-nodes=127.0.0.1:9300
spring.data.elasticsearch.cluster-name=elasticsearch

#mVc
spring.mvc.static-path-pattern=/**
```

### 实体类


```
package com.study.springboot.es.entity.es;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.util.Date;

/**
 * @author yi
 * @ClassName EsBlog
 * @Description TODO
 * @Date
 **/
@Data
@Document(indexName = "blog",type = "_doc",useServerConfiguration = true,createIndex = false)
public class EsBlog {
    @Id
    private Integer id;
    @Field (type = FieldType.Text, analyzer = "ik_max_word")
    private String title;
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String author;
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String content;
    @Field(type = FieldType.Date,format = DateFormat.custom,pattern = "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis")
    private Date createTime;
    @Field(type = FieldType.Date,format = DateFormat.custom,pattern = "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis")
    private Date updateTime;
}

```

### repository


```
package com.study.springboot.es.repository.es;

import com.study.springboot.es.entity.es.EsBlog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author yi
 * @ClassName EsBlogRepository
 * @Description TODO
 * @Date
 **/


public interface EsBlogRepository extends ElasticsearchRepository<EsBlog,Integer> {
}

```


### 测试用例

```
package com.study.springboot.es;

import com.study.springboot.es.entity.es.EsBlog;
import com.study.springboot.es.repository.es.EsBlogRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@Slf4j
@RunWith(SpringRunner.class)
class SpringbootEsApplicationTests {

    @Autowired
    private EsBlogRepository esBlogRepository;

    @Test
    void contextLoads() {
    }

    @Test
    public void testEs(){
        Iterable<EsBlog> all = esBlogRepository.findAll();
        for (EsBlog esBlog : all) {
            log.info(">>> {} <<<",esBlog.getTitle());
        }

    }

}
```

即表明启动成功

![](https://tva1.sinaimg.cn/large/00831rSTgy1gcml06bfb5j31wo0ngjzb.jpg)

给一个完整的例子

[https://github.com/yizhichengSpring/springboot-es-blog](https://github.com/yizhichengSpring/springboot-es-blog)

以上步骤全部完毕后，启动项目，默认访问 [http://localhost:8080/index.html](http://localhost:8080/index.html)

![](https://tva1.sinaimg.cn/large/00831rSTgy1gcmlnujm3jj312w0u0qv5.jpg)
https://tva1.sinaimg.cn/large/00831rSTgy1gcmlnujm3jj312w0u0qv5.jpg




