import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("com.vanniktech.maven.publish") version "0.30.0"
}

mavenPublishing {

    coordinates(
        groupId = "io.github.suwasto",
        artifactId = "zkpschnorrproofs",
        version = "0.1.0"
    )

    pom {
        name.set("KMPZKP")
        description.set("Kotlin Multiplatform library for Zero Knowledge Proofs using Schnorr proofs")
        url.set("https://github.com/suwasto/kmpzkp")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set("suwasto")
                name.set("Anang Suwasto")
                email.set("suwasto.anang@gmail.com")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/suwasto/kmpzkp.git")
            developerConnection.set("scm:git:ssh://github.com:suwasto/kmpzkp.git")
            url.set("https://github.com/suwasto/kmpzkp")
        }
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
        publishLibraryVariants("release")
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
