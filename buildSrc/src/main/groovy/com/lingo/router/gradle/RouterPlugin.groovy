package com.lingo.router.gradle

import com.android.build.api.transform.Transform
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import groovy.json.JsonSlurper
import org.gradle.api.Plugin
import org.gradle.api.Project

class RouterPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        if (project.plugins.hasPlugin(AppPlugin)) {
            AppExtension appExtension = project.extensions.getByType(AppExtension)
            Transform transform = new RouterMappingTransform()
            appExtension.registerTransform(transform)
        }

        File rootProjectDir = project.rootProject.projectDir
        def kapt = project.extensions.findByName("kapt")

        if (kapt == null) {
            throw new RuntimeException("请先使用kapt插件 apply plugin: 'kotlin-kapt'")
        }

        kapt.arguments {
            arg("root_project_dir", rootProjectDir.absolutePath)
        }

        project.clean.doFirst {
            File routerMappingDir = new File(rootProjectDir, "router_mapping")
            if (routerMappingDir.exists()) {
                routerMappingDir.deleteDir()
            }
        }

        if (!project.plugins.hasPlugin(AppPlugin)) {
            return
        }

        project.getExtensions().create("router", RouterExtension)

        project.afterEvaluate {

            project.tasks.findAll { task ->
                // compileDebugJavaWithJavac
                task.name.startsWith("compile") && task.name.endsWith("JavaWithJavac")
            }.each { task ->
                task.doLast {

                    File routerMappingDir = new File(rootProjectDir, "router_mapping")
                    if (!routerMappingDir.exists()) {
                        return
                    }

                    File[] allChildFiles = routerMappingDir.listFiles()
                    if (allChildFiles.length <= 0) {
                        return
                    }

                    StringBuffer markdownBuilder = new StringBuffer()
                    markdownBuilder.append("# 页面文档\n\n")
                    allChildFiles.each { child ->
                        if (child.name.endsWith(".json")) {
                            JsonSlurper jsonSlurper = new JsonSlurper()
                            def content = jsonSlurper.parse(child)
                            content.each { innerContent ->
                                def url = innerContent['url']
                                def description = innerContent['description']
                                def realPath = innerContent['realPath']

                                markdownBuilder.append("## $description\n")
                                markdownBuilder.append("- url: $url\n")
                                markdownBuilder.append("- realPath: $realPath\n\n")
                            }
                        }
                    }

                    RouterExtension extension = project["router"]

                    if (extension.wikiDir == null) {
                        throw new RuntimeException("请在主工程配置 router wikiDir")
                    }

                    File wikiFileDir = new File(extension.wikiDir)
                    if (!wikiFileDir.exists()) {
                        wikiFileDir.mkdir()
                    }

                    File wikiFile = new File(wikiFileDir, "页面文档.md")
                    if (wikiFile.exists()) {
                        wikiFile.delete()
                    }

                    wikiFile.write(markdownBuilder.toString())
                }
            }
        }

    }
}