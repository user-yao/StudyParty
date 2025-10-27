# 使用基础镜像（OpenJDK 17）
FROM openjdk:17-jdk-slim

# 安装 curl 用于健康检查
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# 设置工作目录
WORKDIR /app

# 复制构建好的 JAR 文件
COPY target/studyParty_dubboApi-exec.jar app.jar

# 暴露服务端口（Dubbo 服务端口）
EXPOSE 8090
EXPOSE 20880

# 设置环境变量（关键：配置 Nacos 地址和应用名称）
# 注意：这里使用了 Nacos 服务的容器名（需要与 Nacos 容器在同一个 Docker 网络）
ENV SPRING_APPLICATION_NAME=studyParty-dubboApi \
    NACOS_SERVERS=nacos:8848
# 启动命令（包含关键参数：注册到 Nacos）
# 注意：-Ddubbo.protocol.port=20880 确保 Dubbo 服务使用 20880 端口
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", \
           "-Ddubbo.protocol.port=20880", \
           "-Dspring.cloud.nacos.server-addr=${NACOS_SERVERS}", \
           "-Ddubbo.registry.address=${DUBBO_REGISTRY_ADDRESS}", \
           "-Ddubbo.config-center.address=${DUBBO_CONFIG_CENTER_ADDRESS}", \
           "-Ddubbo.registry.host=nacos", \
           "-Ddubbo.config-center.host=nacos", \
           "-Ddubbo.registry.port=8848", \
           "-Ddubbo.config-center.port=8848", \
           "-jar", "app.jar"]