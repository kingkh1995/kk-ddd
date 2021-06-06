# 项目组成

- 根项目：root
- 组件支持：support
- 用户服务：user
- 促销服务：sales

## 服务架构

### Domain模块

- 核心业务逻辑的集中地，包含有状态的Entity、领域服务Domain Service、以及Infrastructure模块的接口类。
- 纯POJO模块，不依赖项目内其他模块，仅依赖Types模块。

### Application模块

- 包含Application Service，不涉及任何业务逻辑，职责仅仅是组件编排，入参为Query（查询操作），Command（写操作，返回执行结果），Event（已发生事件响应，通常是写操作，不返回结果）等Entity，出参为DTO。
- 纯POJO模块，依赖Domain模块和Models模块。

### Infrastructure模块

- 基础设施层，包括Persistence、Messaging、External等领域层接口的具体实现，是领域层与外部的媒介，属于变更频次最低的模块。
- 依赖Domain模块和Dependencies模块以及其他外部框架。

### Web模块

- 对外接口层，web项目，包含Controller、Provider、Scheduler、MessageListener等，不涉及任何业务相关的代码，职责是服务提供、协议转化、统一鉴权、会话管理、服务限流、异常处理、日志记录等。
- 集成Application模块和Infrastructure模块，分别作为服务提供和服务实现，对domain模块应该是不可知的。
