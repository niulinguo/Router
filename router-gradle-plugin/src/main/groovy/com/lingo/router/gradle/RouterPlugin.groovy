package com.lingo.router.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class RouterPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println("I am from RouterPlugin, apply from ${project.name}")

        project.getExtensions().create("router", RouterExtension)

        project.afterEvaluate {

            RouterExtension extension = project["router"]

            println(extension.wikiDir)
        }
    }
}