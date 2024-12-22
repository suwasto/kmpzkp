import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("maven-publish")
}

group = "io.github.suwasto"
version = "0.1.0"

//publishing {
//
//}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        withSourcesJar(publish = false)
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
    }

    val xcf = XCFramework()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "zkpschnorrproofs"
            xcf.add(this)
            isStatic = true
        }
        it.compilations.getByName("main") {
            val CommonCrypto by cinterops.creating {
                // Path to the .def file
                definitionFile.set(project.file("src/nativeInterop/cinterop/CommonCrypto.def"))

                includeDirs("/usr/include", "/usr/include/CommonCrypto")
            }
        }
        it.binaries.all {
            linkerOpts("-L${projectDir}/build/fat-framework", "-framework", "zkpschnoorproofs")
        }
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            //put your multiplatform dependencies here
            implementation(libs.bignum)
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "io.github.suwasto.zkpschnorrproofs"
    compileSdk = 34
    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
