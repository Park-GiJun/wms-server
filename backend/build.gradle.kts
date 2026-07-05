import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    // 루트에선 적용만 선언하고, 각 모듈이 필요한 것을 켠다.
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.spring) apply false
    alias(libs.plugins.kotlin.jpa) apply false
    alias(libs.plugins.springBoot) apply false
    alias(libs.plugins.springDependencyManagement) apply false
}

allprojects {
    group = "com.gijun.wms"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    // 모든 모듈은 Kotlin + JDK 25 툴체인을 공유한다.
    apply(plugin = "org.jetbrains.kotlin.jvm")

    configure<JavaPluginExtension> {
        // 빌드/실행 툴체인은 JDK 25.
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }

    // Kotlin 2.3.0 부터 JVM_25 바이트코드 타깃 지원 → Java/Kotlin 모두 25 로 통일.
    tasks.withType<JavaCompile>().configureEach {
        options.release.set(25)
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_25)
            freeCompilerArgs.add("-Xjsr305=strict") // Spring nullability 메타데이터 엄격 적용
        }
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    // bootRun 실행 디렉터리를 repo 루트로 고정 → springboot4-dotenv 가 거기의 .env 를 읽는다.
    // (backend/ 의 부모 = 저장소 루트. boot 플러그인이 없는 모듈에는 BootRun 태스크가 없어 무영향.)
    tasks.withType<org.springframework.boot.gradle.tasks.run.BootRun>().configureEach {
        workingDir = rootProject.projectDir.parentFile
    }
}
