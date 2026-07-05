# wms 백엔드 공용 멀티스테이지 이미지. compose 가 ARG MODULE 로 어느 모듈을 담을지 지정한다.
#   docker compose -f deploy/docker-compose.yml build <service>   (service 명 = 모듈명)
# 빌드 컨텍스트 = backend/ (deploy/docker-compose.yml 의 x-backend-build 참조).

# ── build stage: JDK 25 툴체인으로 해당 모듈 bootJar 생성 ──
FROM eclipse-temurin:25-jdk AS build
WORKDIR /src

# 의존성 캐시 레이어: 빌드 스크립트/래퍼 먼저 복사
COPY gradlew ./
COPY gradle ./gradle
COPY settings.gradle.kts build.gradle.kts gradle.properties ./
COPY shared/build.gradle.kts ./shared/
COPY platform-server/build.gradle.kts ./platform-server/
COPY gateway/build.gradle.kts ./gateway/
COPY master-service/build.gradle.kts ./master-service/
COPY user-service/build.gradle.kts ./user-service/
COPY notification-service/build.gradle.kts ./notification-service/
RUN chmod +x gradlew

# 소스 복사 후 지정 모듈만 bootJar
COPY . .
ARG MODULE
RUN ./gradlew :${MODULE}:bootJar --no-daemon

# ── run stage: JRE 만 담은 경량 런타임 ──
FROM eclipse-temurin:25-jre AS run
WORKDIR /app
ARG MODULE
COPY --from=build /src/${MODULE}/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
