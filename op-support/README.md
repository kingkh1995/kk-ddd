# Support

> 基础组件支持项目

### tools

> 工具类包

- utils类

### types （domain模块引用）

> 可公用（可对外暴露）的DP

> 供domain层使用的基础接口、基类、注解、异常等

- Marker接口
- 数据模型的基类
- 追踪变更支持
- 业务异常类

### models （application模块引用，依赖support-tools）

> 对外暴露的数据模型

- Command、Query、Event类
- DTO类
- VO类

### dependencies （infrastructure模块引用，依赖support-types、support-tools）

> 基础设施层使用的各种Beans
