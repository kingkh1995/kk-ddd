## 项目组成

- 根项目：root
- 组件支持：support
- 服务网关：gateway
- 任务中心：job-center
- 用户服务：user
- 促销服务：sales
- 健康码服务：heath-code

## [版本信息](/version_info.md)

## 服务架构

### domain模块

核心业务逻辑的集中地，包含有状态的Entity、领域服务DomainService、以及Infrastructure模块的防腐层的接口。

> 纯POJO模块，不依赖项目内其他模块，仅引用types模块。

- Type: Domain Primitive，值对象，作为实体的域；
- Entity: 实体，领域模型；
- Aggregate: 聚合根，由主实体和子实体聚合而成，领域模型；
- DomainService: 领域模型，用于封装多领域行为；
- Repository: 持久层接口，属于防腐层；
- Facade: 外部服务防腐层接口；
- MessageProducer: 消息发送防腐层接口。

### application模块

包含ApplicationService，不涉及任何业务逻辑，职责仅仅是组件编排，入参为Query（查询操作），Command（写操作，返回执行结果），Event（已发生事件响应，通常是写操作，不返回结果）等，出参为DTO。

> 纯POJO模块，直接引用domain模块和models模块。

> 基于CQRS架构，QueryService也属于查询层，可直接引用基础设施层，ApplicationService通过发送领域事件到查询层，使查询层数据更新。**

### infrastructure模块

基础设施层，包括Persistence、Messaging、External等领域层接口的具体实现，是领域层与外部的媒介，属于变更频次最低的模块。

> 直接引用domain模块和dependencies模块以及其他外部框架。

### web（或interface）模块

对外接口层，也属于基础设施层的一部分，web项目，包含Controller、Provider、Consumer、Scheduler等，不涉及任何业务相关的代码，职责是服务提供、协议转化、统一鉴权、会话管理、服务限流、异常处理、日志记录等。

> 直接引用application模块作为服务提供，infrastructure模块通过依赖注入作为服务实现，对domain模块应该是不可知的。

## 图示

### 模块结构图

![](/files/module_struct.png)

### Repository体系类结构图

![](/files/repository_struct.png)