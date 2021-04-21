package com.github.router;

import com.github.router.annotate.Destination;
import com.github.router.annotate.DestinationMethod;
import com.google.auto.service.AutoService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;


@AutoService({Processor.class})
public class DestinationProcessor extends AbstractProcessor {

    private Elements elements;
    private Types typeUtils;
    private Filer filer;
    private Messager messager;
    private ProcessingEnvironment mProcessingEnvironment;

    private String rootProjectDir;
    private String moduleName;


    private Element activityElement;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        this.mProcessingEnvironment = processingEnvironment;
        messager = processingEnv.getMessager();
        typeUtils = processingEnv.getTypeUtils();
        elements = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        rootProjectDir = processingEnvironment.getOptions().get(Constants.ROOT_PROJECT_DIR);
        moduleName = processingEnvironment.getOptions().get(Constants.MODULE_NAME);
        Helper.init(typeUtils);
        //通过类名获取Activity Element类型
        activityElement = elements.getTypeElement(Constants.ACTIVITY);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 告诉编译器，当前注解处理器支持的注解类型
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.add(Destination.class.getCanonicalName());
        linkedHashSet.add(DestinationMethod.class.getCanonicalName());
        return linkedHashSet;
    }


    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // 避免多次调用 process 方法
        if (roundEnvironment.processingOver()) {
            return false;
        }


        Set<? extends Element> allDestinations =
                roundEnvironment.getElementsAnnotatedWith(Destination.class);

        if (allDestinations.isEmpty()) return false;

        // 生成Router表类名  模块名+_RouterMapping
        String routerMappingClassName = moduleName + Constants.ROUTER_MAPPING_SUFFIX;

        StringBuilder codeBuffer = new StringBuilder();
        codeBuffer.append("package com.github.gradle.mapping;\n\n");
        codeBuffer.append("import java.util.HashMap;\n");
        codeBuffer.append("import java.util.Map;\n\n");

        codeBuffer.append("public class ").append(routerMappingClassName).append(" {\n\n");
        codeBuffer.append("    public static Map<String, Class<?>> get() {\n\n");
        codeBuffer.append("        Map<String, Class<?>> mapping = new HashMap<>();\n");


        JsonArray destinationJsonArray = new JsonArray();

        for (Element element : allDestinations) {
            final TypeElement typeElement = (TypeElement) element;
            final Destination destination = typeElement.getAnnotation(Destination.class);
            if (destination == null) {
                continue;
            }

            if (!Helper.isSubtype(element, activityElement)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "The " + typeElement.getQualifiedName() + " not inheriting Activity");
            }

            String path = destination.url();
            String description = destination.description();
            String realPath = typeElement.getQualifiedName().toString() + ".class";

            JsonObject itemJson = new JsonObject();
            itemJson.addProperty(Constants.KEY_PATH, path);
            itemJson.addProperty(Constants.KEY_DESCRIPTION, description);
            itemJson.addProperty(Constants.KEY_REAL_PATH, realPath);
            destinationJsonArray.add(itemJson);

            codeBuffer
                    .append("        mapping.put(")
                    .append("\"").append(path).append("\"")
                    .append(",").append(realPath)
                    .append(");\n");
        }

        codeBuffer.append("        return mapping;\n");
        codeBuffer.append("    }\n");
        codeBuffer.append("}");


        try {
            String sourceCodeClassName =
                    "com.github.gradle.mapping." + routerMappingClassName;
            Writer sourceFile =
                    filer.createSourceFile(sourceCodeClassName).openWriter();
            sourceFile.write(codeBuffer.toString());
            sourceFile.flush();
            sourceFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        // 写入Json 文件 搜集文档
        File rootDirFile = new File(rootProjectDir);
        if (!rootDirFile.exists()) {
            throw new RuntimeException("root_project_dir not exists ");
        }

        File routerMappingDocDir = new File(rootDirFile, Constants.ROUTER_MAPPING_DOC_DIR);
        if (!routerMappingDocDir.exists() &&
                !routerMappingDocDir.mkdir()) {
            return true;
        }

        File mappingDocFile =
                new File(routerMappingDocDir, moduleName + Constants.ROUTER_MAPPING_DOC_NAME_SUFFIX);

        try {
            BufferedWriter out =
                    new BufferedWriter(new FileWriter(mappingDocFile));
            String jsonStr =
                    destinationJsonArray.toString();
            out.write(jsonStr);
            out.flush();
            out.close();
        } catch (Throwable throwable) {
            throw new RuntimeException("Error while write Json", throwable);
        }

        return true;
    }
}
