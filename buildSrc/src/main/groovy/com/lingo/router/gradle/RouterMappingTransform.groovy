package com.lingo.router.gradle

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils

import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

class RouterMappingTransform extends Transform {

    @Override
    String getName() {
        return RouterMappingTransform.simpleName
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)

        final RouterMappingCollector collector = new RouterMappingCollector()

        transformInvocation.inputs.each {
            it.directoryInputs.each { directoryInput ->
                final File destDir = transformInvocation.outputProvider.getContentLocation(
                        directoryInput.name,
                        directoryInput.contentTypes,
                        directoryInput.scopes,
                        Format.DIRECTORY
                )
                collector.collect(directoryInput.file)
                FileUtils.copyDirectory(directoryInput.file, destDir)
            }

            it.jarInputs.each { jarInput ->
                final File dest = transformInvocation.outputProvider.getContentLocation(
                        jarInput.name,
                        jarInput.contentTypes,
                        jarInput.scopes,
                        Format.JAR
                )
                collector.collectFormJarFile(jarInput.file)
                FileUtils.copyFile(jarInput.file, dest)
            }
        }

        printLog(collector.getMappingClassNames().toString())

        final File mappingJarFile = transformInvocation.outputProvider.getContentLocation(
                "router_mapping",
                getOutputTypes(),
                getScopes(),
                Format.JAR
        )

        printLog(mappingJarFile.toString())

        if (!mappingJarFile.getParentFile().exists()) {
            mappingJarFile.getParentFile().mkdirs()
        }

        if (mappingJarFile.exists()) {
            mappingJarFile.delete()
        }

        final FileOutputStream fileOutputStream = new FileOutputStream(mappingJarFile)
        final JarOutputStream jarOutputStream = new JarOutputStream(fileOutputStream)
        final ZipEntry zipEntry = new ZipEntry(RouterMappingByteCodeBuilder.CLASS_NAME + ".class")
        jarOutputStream.putNextEntry(zipEntry)
        jarOutputStream.write(RouterMappingByteCodeBuilder.getByteCode(collector.getMappingClassNames()))
        jarOutputStream.closeEntry()
        jarOutputStream.close()
        fileOutputStream.close()
    }

    private void printLog(String msg) {
        println("${getName()} >>> $msg")
    }
}