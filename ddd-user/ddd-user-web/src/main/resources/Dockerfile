# 该镜像需要依赖的基础镜像
FROM openjdk
# 将当前目录下的jar包复制到docker容器的/目录下
ADD ddd-user-web-2022.0.0.jar /ddd-user-web-2022.0.0.jar
# 添加临时文件目录（用于tomcat）
VOLUME /tmp
# 运行过程中创建一个jar文件
RUN bash -c 'touch /ddd-user-web-2022.0.0.jar'
# 声明服务运行在8080端口（只是声明非暴露）
EXPOSE 8080
# 指定维护者的名字
MAINTAINER kaikoo
# 启动时jar包并设置启动参数
ENTRYPOINT ["java", "-jar", "/ddd-user-web-2022.0.0.jar", "--spring.profiles.active=test"]
