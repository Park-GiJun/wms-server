import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.script

/*
 * wms-server CI/CD — 서비스별 독립 빌드 + 배포. (백엔드 전용 — web 없음.)
 *
 * 토폴로지: 같은 홈서버의 TeamCity 에이전트가 이미지를 빌드하고 deploy/docker-compose.yml 로
 *           해당 서비스만 무중단 교체한다(레지스트리 없음 — 빌드=배포 동일 호스트).
 *           에이전트에 docker / docker compose 가 있어야 하고 deploy/.env 가 서버에 존재해야 한다.
 *           공유 인프라(infra-postgres/infra-redis/kafka, 네트워크 infra-net)는 미리 떠 있어야 한다.
 *
 * 적용: TeamCity 에서 이 repo 를 Versioned Settings(Kotlin DSL)로 연결하면 아래 BuildType 들이 생성된다.
 *       (이 파일은 반드시 VCS 루트의 .teamcity/ 에 있어야 인식된다.)
 *       서버 버전에 맞춰 version 값을 조정한다(불일치 시 DSL 컴파일 경고).
 */
version = "2026.1"

project {
    val composeFile = "deploy/docker-compose.yml"

    // compose 서비스명 = backend 모듈명. 각 항목이 독립 배포 BuildType 1개가 된다.
    // 피처 서비스를 /new-service 로 추가하면 이 목록에도 한 줄 추가한다.
    val backendServices = listOf(
        "platform-server",
        "gateway",
        "master-service",
        "user-service",
        "notification-service",
    )

    backendServices.forEach { svc ->
        buildType {
            id("Deploy_${svc.replace("-", "_")}")
            name = "Deploy · $svc"
            description = "$svc 이미지 빌드 후 compose 로 해당 서비스만 교체"

            vcs { root(DslContext.settingsRoot) }

            steps {
                script {
                    name = "build & deploy"
                    scriptContent = """
                        set -e
                        docker compose -f $composeFile build $svc
                        docker compose -f $composeFile up -d --no-deps $svc
                    """.trimIndent()
                }
            }
            // 트리거 없음 — 수동 실행 전용(Run 버튼).
        }
    }
}
