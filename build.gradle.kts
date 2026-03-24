// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.kotlinx.kover) apply true
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

tasks.register<Exec>("fullCoverageReport") {
    commandLine("./gradlew", "app:koverHtmlReportDebug", "components:koverHtmlReportDebug", "model:koverHtmlReport")
}

dependencies {
    kover(project(":app"))
    kover(project(":components"))
    kover(project(":model"))
}

kover {
    currentProject {
        createVariant("custom") {
        }
    }

    reports {
        // filters for all report types of all build variants
        filters {
            excludes {
                androidGeneratedClasses()
            }
        }

        variant("custom") {
            // verification only for 'custom' report variant
            verify {
                rule {
                    minBound(50)
                }
            }

            // filters for all report types only of 'custom' build type
            filters {
                excludes {
                    androidGeneratedClasses()
                    classes(
                        // excludes debug classes
                        "*.DebugUtil"
                    )
                }
            }
        }
    }
}