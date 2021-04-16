# Support

> 基础组件支持项目

### models （application模块引用，依赖support-dependencies）

> 可公用的模型

- DTO类
- VO类

### dependencies （infrastructure模块引用，依赖support-types）

> 供domain层使用的基础接口、基类、Beans

- config类

> 公共工具类

- tools类

### types （domain模块引用）

> 可公用（对外暴露）的DP
> 供domain层使用的基础接口、基类、Exceptions

- Marker接口
- 追踪变更支持
- 业务异常类

