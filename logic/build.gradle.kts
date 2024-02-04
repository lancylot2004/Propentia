plugins {
    kotlin("multiplatform") version "1.9.22"
}

group = ""
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

// tasks.test {
//    useJUnitPlatform()
// }

// kotlin {
//    jvmToolchain(21)
// }

kotlin {
    js(IR) {
        browser()
        binaries.library()
        binaries.executable()
        generateTypeScriptDefinitions()
    }

    // No @ExperimentalJsExport dawdle.
    sourceSets {
        val jsMain by getting
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        all {
            languageSettings.apply {
                optIn("kotlin.js.ExperimentalJsExport")
            }
        }
    }
}