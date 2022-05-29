# Support

> 基础组件支持项目

### consts

> 可对外暴露的常量类和枚举类

- Constants类
- Enum类

### tools

> 工具类、函数及可通用的类

- Utils类
- Function接口

### types （domain模块引用，依赖support-consts、support-tools）

> 可公用的DP

> 供domain层使用的基础接口、基类、注解、异常等

- marker接口
- 数据模型的基类
- Exception类

### models （application模块引用，依赖support-consts）

> 对外暴露的数据模型

- Command、Query、Event类
- DTO类
- VO类

### dependencies （infrastructure模块引用，依赖support-types）

> 供基础设施层使用的Bean