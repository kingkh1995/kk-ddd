# 项目组成
- 根项目：root
- 组件支持：support
- 用户服务：user
- 促销服务：sales
## 服务架构
#### Domain模块 
- 核心业务逻辑的集中地，包含有状态的Entity、领域服务 Domain Service、以及Infrastructure模块的接口类。
- 纯POJO模块，不依赖项目内其他模块，仅依赖Types模块。
#### Application模块
- 包含Application Service，不涉及任何业务逻辑，职责仅仅是组件编排。
- 纯POJO模块，依赖Domain模块。
#### Infrastructure模块
- 基础设施层，包括Persistence、Messaging、External等等，领域层与外部的媒介，是变更频次最低的模块。
- 依赖Domain模块。
#### Web模块
- web项目，包含controller等。
- 依赖Application模块，同时依赖Infrastructure模块实现web功能，也依赖Models模块。


