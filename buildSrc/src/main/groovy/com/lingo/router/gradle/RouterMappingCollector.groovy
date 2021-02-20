package com.lingo.router.gradle

import java.util.jar.JarEntry
import java.util.jar.JarFile

class RouterMappingCollector {
    private static final String TAG = "RouterMappingCollector"
    private static final String PATH_NAME = "com/lingo/router/mapping/"
    private static final String CLASS_NAME_PREFIX = "RouterMapping_"
    private static final String CLASS_FILE_SUFFIX = ".class"

    private final Set<String> mappingClassNames = new HashSet<>()

    Set<String> getMappingClassNames() {
        return mappingClassNames
    }

    void collect(File classFile) {
        if (classFile == null || !classFile.exists()) {
            return
        }
        if (classFile.isFile()) {
            final String path = classFile.path
//            printLog("class file path is $path")
            if (checkIsMappingFile(path, false)) {
                addToMappingSet(path)
            }
        } else {
            classFile.listFiles().each {
                collect(it)
            }
        }
    }

    void collectFormJarFile(File jarFile) {
        final Enumeration enumeration = new JarFile(jarFile).entries()
        while (enumeration.hasMoreElements()) {
            final JarEntry jarEntry = enumeration.nextElement()
            final String entryName = jarEntry.name
//            printLog("jar entry name is $entryName")
            if (checkIsMappingFile(entryName, true)) {
                addToMappingSet(entryName)
            }
        }
    }

    private void addToMappingSet(String path) {
        final String className = path.substring(path.lastIndexOf("/") + 1)
                .replace(CLASS_FILE_SUFFIX, "")
        mappingClassNames.add(className)
    }

    private static boolean checkIsMappingFile(String path, boolean isJar) {
        if (path == null || path.isEmpty()) {
            return false
        }
        if (path.contains(PATH_NAME + CLASS_NAME_PREFIX)
                && path.endsWith(CLASS_FILE_SUFFIX)) {
            return true
        }
        if (path.contains(PATH_NAME + "generated/RouterMapping.class")) {
            printLog("isJar:$isJar, 工程中找到了RouterMapping.java类，需要删除")
        }
        return false
    }

    private static void printLog(String msg) {
        println("$TAG >>> $msg")
    }
}