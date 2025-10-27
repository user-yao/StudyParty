# 使用官方 OpenJDK 镜像作为基础镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 将构建好的 jar 文件复制到容器中
COPY target/studyParty_gateway.jar studyParty_gateway.jar

# 暴露端口
EXPOSE 8080

# 设置环境变量
ENV SPRING_APPLICATION_NAME=studyParty-gateway \
    NACOS_SERVERS=nacos:8848

# 运行应用
ENTRYPOINT ["java", "-jar", "studyParty_gateway.jar"]