# 基于Spring Boot+Elasticsearch v7.3的订单中心项目

创建基于Spring Boot+Elasticsearch v7.3+Maven的订单中心微服务项目，对外提供针对订单索引的CRUD服务，并且订单索引会按照月份划分索引。

![](C:\Users\Rickie\AppData\Local\Microsoft\Windows\INetCache\IE\IA6Y27QD\elasticsearch-order[1].png)

订单中心演示项目，搭建订单中心Elasticsearch相关微服务基本架构，我们先完成如下几个步骤：

- 创建Spring Boot + Maven项目。

- 引用Elasticsearch 相关的Jar包。
- 新建application.properties 配置文件和ElasticsearchConfig配置类。
- 定义order template 索引模板（Json格式）。
- 定义读取order template索引模板的JsonUtil 工具类。
- 对Elasticsearch提供的Java High Level REST Client API 进行封装，封装类EsUtil.java。
- 编写单元测试，进行结果验证；

本专栏的演示项目，重点是实现基于Elasticsearch Java High Level REST Client 的订单服务接口。

更详细的文档，可以参考如下技术专栏：

https://learning.snssdk.com/feoffline/toutiao_wallet_bundles/toutiao_learning_wap/online/album_detail.html?content_id=6739301223356170507

![](C:\Users\Rickie\AppData\Local\Microsoft\Windows\INetCache\IE\HXILK73G\《Elasticsearch%207.x订单中心实战》专栏海报2[1].png)