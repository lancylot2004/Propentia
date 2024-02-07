plugins {
    kotlin("multiplatform") version "1.9.22"
}

group = "dev.lancy.propentia"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    js(IR) {
        useEsModules()
        nodejs()
        binaries.executable()
        generateTypeScriptDefinitions()
    }

    sourceSets {
        val jsMain by getting
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        // No @ExperimentalJsExport dawdle.
        all {
            languageSettings.apply {
                optIn("kotlin.js.ExperimentalJsExport")
            }
        }
    }
}
