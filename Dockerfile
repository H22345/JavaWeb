# ===== 构建阶段 =====
# 用 Maven 镜像打包 jar 包
FROM maven:3.9-eclipse-temurin-21 AS builder

# 设置工作目录
WORKDIR /app

# 先复制 pom.xml，利用 Docker 缓存（依赖不变时不会重新下载）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 再复制源码打包
COPY src ./src
RUN mvn clean package -DskipTests -B

# ===== 运行阶段 =====
# 用更小的运行时镜像（比 Maven 镜像小 800MB+）
FROM eclipse-temurin:21-jre-alpine

# 设置时区（Alpine 镜像默认时区不对）
RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone

WORKDIR /app

# 从构建阶段拷贝 jar 包（spring-boot-maven-plugin 打包后的 fat jar 叫这个）
COPY --from=builder /app/target/SHmarketplace-*.jar app.jar

# 暴露端口（和 application-prod.properties 里的 server.port 对应）
EXPOSE 8080

# 启动命令（默认激活 prod 环境）
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
