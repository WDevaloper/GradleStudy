package com.github.router;

import com.github.router.annotate.Destination;
import com.github.router.annotate.DestinationMethod;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.SimpleElementVisitor8;


@AutoService({Processor.class})
public class DestinationProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnv.getMessager();
        filer = processingEnvironment.getFiler();
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


        String className = "RouterMapping_" + System.currentTimeMillis();

        StringBuilder codeBuffer = new StringBuilder();


        codeBuffer.append("package com.github.gradle.mapping;\n\n");
        codeBuffer.append("import java.util.HashMap;\n");
        codeBuffer.append("import java.util.Map;\n\n");

        codeBuffer.append("public class ").append(className).append(" {\n\n");
        codeBuffer.append("    public static Map<String, String> get() {\n\n");
        codeBuffer.append("        Map<String, String> mapping = new HashMap<>();\n");


        for (Element element : allDestinations) {
            final TypeElement typeElement = (TypeElement) element;
            final Destination destination = typeElement.getAnnotation(Destination.class);
            if (destination == null) {
                continue;
            }

            String url = destination.url();
            String description = destination.description();
            String qualifiedName = typeElement.getQualifiedName().toString();

            codeBuffer.append("        mapping.put(")
                    .append("\"").append(url).append("\"")
                    .append(",")
                    .append("\"").append(qualifiedName).append("\"")
                    .append(");\n");

            System.out.println("url " + url + " description " + description + " className " + className);
        }

        codeBuffer.append("        return mapping;\n");
        codeBuffer.append("    }\n");
        codeBuffer.append("}");


        try {
            String sourceClassName =
                    "com.github.gradle.mapping." + className;
            Writer sourceFile =
                    filer.createSourceFile(sourceClassName).openWriter();
            sourceFile.write(codeBuffer.toString());
            sourceFile.flush();
            sourceFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return true;
    }
}
