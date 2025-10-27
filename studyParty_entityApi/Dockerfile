# 使用官方 OpenJDK 镜像作为基础镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# entityApi 是一个库项目，通常不需要独立部署
# 但为了完整性，我们仍然创建一个 Dockerfile
# 实际上这个服务会被其他服务依赖

# 将构建好的 jar 文件复制到容器中
COPY target/studyParty_entityApi-1.0-SNAPSHOT.jar studyParty_entityApi.jar

# 这个服务通常不需要运行，因为它是一个依赖库
# 如果需要运行，可以取消下面的注释
# EXPOSE 8080
# ENTRYPOINT ["java", "-jar", "studyParty_entityApi.jar"]