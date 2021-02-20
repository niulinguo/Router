package com.lingo.router.processor;

import com.google.auto.service.AutoService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lingo.router.annotations.Destination;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("com.lingo.router.annotations.Destination")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class DestinationProcessor extends AbstractProcessor {

    private static final String TAG = "DestinationProcessor";

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        if (roundEnvironment.processingOver()) {
            return false;
        }

        printLog("process start ...");

        final Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(Destination.class);

        printLog("destination elements count = " + elements.size());
        if (elements.isEmpty()) {
            return false;
        }

        final List<DestinationInfo> infoList = new ArrayList<>();

        for (Element element : elements) {
            final TypeElement typeElement = (TypeElement) element;
            final Destination annotation = typeElement.getAnnotation(Destination.class);
            if (annotation == null) {
                continue;
            }
            final String url = annotation.url();
            final String description = annotation.description();
            final String realPath = typeElement.getQualifiedName().toString();

            printLog("url:" + url);
            printLog("description:" + description);
            printLog("realPath:" + realPath);

            final DestinationInfo info = new DestinationInfo(realPath, url, description);
            infoList.add(info);
        }

        geneClass(infoList);
        geneJson(infoList);

        printLog("process finish ...");

        return false;
    }

    private static void printLog(String msg) {
        System.out.println(TAG + " >>> " + msg);
    }

    private void geneJson(final List<DestinationInfo> dataList) {
        final JsonArray array = new JsonArray();

        for (DestinationInfo info : dataList) {
            final JsonObject object = new JsonObject();
            object.addProperty("url", info.getUrl());
            object.addProperty("description", info.getDescription());
            object.addProperty("realPath", info.getClassPath());

            array.add(object);
        }

        final String rootProjectDir = processingEnv.getOptions().get("root_project_dir");
        final File rootDirFile = new File(rootProjectDir);
        if (!rootDirFile.exists()) {
            throw new RuntimeException("root_project_dir not exist!");
        }

        final File routerFileDir = new File(rootDirFile, "router_mapping");
        if (!routerFileDir.exists()) {
            final boolean success = routerFileDir.mkdir();
            if (!success) {
                throw new RuntimeException("创建router_mapping目录失败");
            }
        }

        final File mappingFile = new File(routerFileDir, "mapping_" + System.currentTimeMillis() + ".json");
        try {
            final Writer writer = new BufferedWriter(new FileWriter(mappingFile));
            writer.write(array.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Error writing file", e);
        }
    }

    private void geneClass(final List<DestinationInfo> dataList) {
        final String packageName = "com.lingo.router.mapping";
        final String className = "RouterMapping_" + System.currentTimeMillis();
        final String fullClassName = packageName + "." + className;

        final StringBuilder builder = new StringBuilder();
        builder.append("package " + packageName + ";\n\n");
        builder.append("import java.util.HashMap;\n");
        builder.append("import java.util.Map;\n\n");
        builder.append("public final class ").append(className).append(" {\n\n");
        builder.append("\tpublic static Map<String, String> get() {\n");
        builder.append("\t\tfinal HashMap<String, String> map = new HashMap<>();\n\n");
        for (DestinationInfo info : dataList) {
            builder.append("\t\tmap.put(\"")
                    .append(info.getUrl()).append("\", \"")
                    .append(info.getClassPath())
                    .append("\");\n");
        }
        builder.append("\n");
        builder.append("\t\treturn map;\n");
        builder.append("\t}\n");
        builder.append("}\n");

        try {
            final JavaFileObject source = processingEnv.getFiler()
                    .createSourceFile(fullClassName);
            final Writer writer = source.openWriter();
            writer.write(builder.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Error writing file", e);
        }
    }
}
