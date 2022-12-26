import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.timtheemployee"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.jetbrains.compose.ui:ui-graphics-desktop:1.1.0")
                implementation("org.jetbrains.compose.ui:ui-geometry-desktop:1.1.0")
                implementation("org.jetbrains.compose.foundation:foundation-desktop:1.1.0")
                implementation("org.jetbrains.compose.material:material-icons-extended-desktop:1.1.0")
            }
        }
        val jvmTest by getting
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-opt-in=RequiresOptIn"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Exe)
            packageName = "pomodoro"
            packageVersion = "1.0.0"

            windows {
                iconFile.set(File("icon.ico"))
            }
        }
    }
}
