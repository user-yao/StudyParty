# 使用官方 OpenJDK 镜像作为基础镜像
FROM openjdk:17-jdk-slim

# 安装 curl 用于健康检查
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# 设置工作目录
WORKDIR /app

# 将构建好的 jar 文件复制到容器中
COPY target/studyParty_enterprise.jar studyParty_enterprise.jar

# 暴露端口
EXPOSE 8080

# 设置环境变量
ENV SPRING_APPLICATION_NAME=studyParty-enterprise \
    NACOS_SERVERS=nacos:8848

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 运行应用
ENTRYPOINT ["java", "-jar", "studyParty_enterprise.jar"]