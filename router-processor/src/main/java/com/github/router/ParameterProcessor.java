package com.github.router;

import com.github.router.annotate.Parameter;
import com.google.auto.service.AutoService;

import java.io.Writer;
import java.lang.annotation.ElementType;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;


@AutoService({Processor.class})
public class ParameterProcessor extends AbstractProcessor {

    private Filer filer;
    private Elements elements;
    private Messager messager;


    //临时map存储，用来存放@Parameter注解的属性集合，生成类文件时遍历
    //key : 类节点  value：被@Parameter注解的属性集合
    private Map<TypeElement, List<Element>> tempParameterMap = new ConcurrentHashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        messager = processingEnv.getMessager();
        elements = processingEnvironment.getElementUtils();
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
        linkedHashSet.add(Parameter.class.getCanonicalName());
        return linkedHashSet;
    }


    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // 避免多次调用 process 方法
        if (roundEnvironment.processingOver()) {
            return false;
        }


        Set<? extends Element> allParams =
                roundEnvironment.getElementsAnnotatedWith(Parameter.class);

        if (allParams.isEmpty()) return false;


        collectValueParam(allParams);

        //Element element = allParams.iterator().next();
        // 返回父元素,这里就是类(AptActivity)
        //TypeElement type = (TypeElement) element.getEnclosingElement();
        // com.github.gradle.AptActivity
        // String typeName = type.getQualifiedName().toString();
        //String simpleTypeName = type.getSimpleName().toString();

        //PackageElement packageElement = (PackageElement) type.getEnclosingElement();
        //com.github.gradle
        //String packageName = packageElement.getQualifiedName().toString();

        for (Map.Entry<TypeElement, List<Element>> entry : tempParameterMap.entrySet()) {
            TypeElement type = (TypeElement) entry.getKey();
            String typeName = type.getQualifiedName().toString();
            String simpleTypeName = type.getSimpleName().toString();

            PackageElement packageElement = (PackageElement) type.getEnclosingElement();
            String packageName = packageElement.getQualifiedName().toString();

            //生成的类名
            String generatorClassName = simpleTypeName + "$$Parameter";

            StringBuilder codeBuffer = new StringBuilder();
            codeBuffer.append("package ").append(packageName).append(";\n\n");
            codeBuffer.append("public class ").append(generatorClassName)
                    .append(" implements ").append("com.github.router.runtime.ParameterInject")
                    .append(" {\n\n");

            codeBuffer.append("    @Override\n")
                    .append("    public void inject(Object target) {\n").append("        ")
                    .append(typeName).append(" injectObject = (").append(typeName).append(") target;\n");

            for (Element paramElement : entry.getValue()) {
                //被@Parameter注解属性信息
                TypeMirror typeMirror = paramElement.asType();
                int fileType = typeMirror.getKind().ordinal();
                // 被@Parameter注解的属性名
                String filedName = paramElement.getSimpleName().toString();
                if (String.class.getName().equals(typeMirror.toString())) {
                    codeBuffer.append("        injectObject.")
                            .append(filedName)
                            .append(" = injectObject.getIntent().getStringExtra(\"")
                            .append(filedName)
                            .append("\");\n");
                } else if (fileType == TypeKind.INT.ordinal()) {
                    codeBuffer.append("        injectObject.")
                            .append(filedName)
                            .append(" = injectObject.getIntent().getIntExtra(\"")
                            .append(filedName)
                            .append("\", 0);\n");
                }
            }

            codeBuffer.append("    }\n");
            codeBuffer.append("}");


            try {
                String sourceCodeClassName =
                        packageName + "." + generatorClassName;
                Writer sourceFile =
                        filer.createSourceFile(sourceCodeClassName).openWriter();
                sourceFile.write(codeBuffer.toString());
                sourceFile.flush();
                sourceFile.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    private void collectValueParam(Set<? extends Element> allParams) {
        for (Element element : allParams) {
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            if (tempParameterMap.containsKey(typeElement)) {
                tempParameterMap.get(typeElement).add(element);
            } else {
                CopyOnWriteArrayList<Element> files = new CopyOnWriteArrayList<>();
                files.add(element);
                tempParameterMap.put(typeElement, files);
            }
        }
    }
}
