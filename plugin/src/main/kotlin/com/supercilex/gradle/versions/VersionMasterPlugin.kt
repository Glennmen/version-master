package com.supercilex.gradle.versions

import com.android.build.api.variant.impl.VariantOutputImpl
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.supercilex.gradle.versions.internal.VERSION_MASTER_PATH
import com.supercilex.gradle.versions.internal.VersionMasterRootPlugin
import com.supercilex.gradle.versions.tasks.ComputeVersionCode
import com.supercilex.gradle.versions.tasks.ComputeVersionName
import com.supercilex.gradle.versions.tasks.RetrieveGitCommitCount
import com.supercilex.gradle.versions.tasks.RetrieveGitDescription
import com.supercilex.gradle.versions.tasks.RetrieveGitTagList
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginConvention
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.findPlugin
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.getPlugin
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType

@Suppress("unused") // Used by Gradle
internal class VersionMasterPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.rootProject.plugins.apply(VersionMasterRootPlugin::class)

        project.extensions.create<VersionMasterExtension>(VERSION_MASTER_PATH).apply {
            configureDebugBuilds.convention(false)
            configureVersionCode.convention(true)
            configureVersionName.convention(true)
            versionCodeOffset.convention(0)
        }

        project.plugins.withType<AppPlugin> {
            applyInternal(project)
        }
        project.afterEvaluate {
            if (project.plugins.findPlugin(AppPlugin::class) == null) {
                throw IllegalStateException(
                        "The Android Gradle Plugin was not applied. Version Master " +
                                "cannot be configured.")
            }
        }
    }

    private fun applyInternal(project: Project) {
        val extension = project.extensions.getByType<VersionMasterExtension>()
        val android = project.the<BaseAppModuleExtension>()
        val workingDir = project.layout.buildDirectory.dir("version-master")

        val computeVersionCode =
                project.tasks.register<ComputeVersionCode>("computeAppVersionCode")
        val computeVersionName =
                project.tasks.register<ComputeVersionName>("computeAppVersionName")

        computeVersionCode {
            versionCodeOffset.set(extension.versionCodeOffset)

            commitCountFile.set(project.rootProject.tasks.named(
                    "retrieveGitCommitCount",
                    RetrieveGitCommitCount::class
            ).flatMap { it.commitCountFile })
            tagListFile.set(project.rootProject.tasks.named(
                    "retrieveGitTagList",
                    RetrieveGitTagList::class
            ).flatMap { it.tagListFile })
            gitDescribeFile.set(project.rootProject.tasks.named(
                    "retrieveGitDescription",
                    RetrieveGitDescription::class
            ).flatMap { it.gitDescribeFile })

            versionCodeFile.set(workingDir.map { it.file("version-code.txt") })
        }

        computeVersionName {
            gitDescribeFile.set(project.rootProject.tasks.named(
                    "retrieveGitDescription",
                    RetrieveGitDescription::class
            ).flatMap { it.gitDescribeFile })

            versionNameFile.set(workingDir.map { it.file("version-name.txt") })
        }

        val basePlugin = project.convention.getPlugin<BasePluginConvention>()
        android.onVariants v@{
            val shouldConfigureDebugBuilds =
                    extension.configureDebugBuilds.forUseAtConfigurationTime().get()
            if (!enabled || debuggable && !shouldConfigureDebugBuilds) {
                return@v
            }

            onProperties {
                for (output in outputs) {
                    if (extension.configureVersionCode.forUseAtConfigurationTime().get()) {
                        output.versionCode.set(computeVersionCode.map {
                            it.versionCodeFile.get().asFile.readText().toInt()
                        })
                    }

                    if (extension.configureVersionName.forUseAtConfigurationTime().get()) {
                        output.versionName.set(computeVersionName.map {
                            it.versionNameFile.get().asFile.readText()
                        })
                        (output as VariantOutputImpl).outputFileName.set(computeVersionName.map {
                            "${basePlugin.archivesBaseName}-${applicationId.get()}-" +
                                    "${output.baseName}-${output.versionName.get()}.apk"
                        })
                    }
                }
            }
        }
    }
}
