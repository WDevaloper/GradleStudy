package com.github.router;

import com.github.router.annotate.Parameter;
import com.google.auto.service.AutoService;

import java.io.Writer;
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
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;


@AutoService({Processor.class})
public class ParameterProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;

    //临时map存储，用来存放@Parameter注解的属性集合，生成类文件时遍历
    //key : 类节点  value：被@Parameter注解的属性集合
    private Map<TypeElement, List<Element>> tempParameterMap = new ConcurrentHashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        messager = processingEnv.getMessager();
        ProcessorUtils.init(processingEnvironment.getTypeUtils(), processingEnvironment.getElementUtils());
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

        messager.printMessage(Diagnostic.Kind.NOTE,"ParameterProcessor");

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
            TypeElement superTypeElement = (TypeElement) entry.getKey();
            String typeName = superTypeElement.getQualifiedName().toString();
            String simpleTypeName = superTypeElement.getSimpleName().toString();

            PackageElement packageElement = (PackageElement) superTypeElement.getEnclosingElement();
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

            if (ProcessorUtils.isSubtype(superTypeElement,
                    ProcessorUtils.getAppFragmentTypeElement()) ||
                    ProcessorUtils.isSubtype(superTypeElement,
                            ProcessorUtils.getAndroidxFragmentTypeElement())) {
                codeBuffer.append("        ")
                        .append(ProcessorUtils.getBundleTypeElement().getQualifiedName())
                        .append(" bundle = injectObject.getArguments();\n");
            }


            for (Element paramElement : entry.getValue()) {
                createInject(superTypeElement, codeBuffer, paramElement);
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

    private void createInject(TypeElement superTypeElement, StringBuilder codeBuffer, Element paramElement) {
        //被@Parameter注解属性信息
        TypeMirror typeMirror = paramElement.asType();
        // 获取字段的类型
        int fileType = typeMirror.getKind().ordinal();

        // 被@Parameter注解的属性名
        String filedName = paramElement.getSimpleName().toString();

        Parameter parameter = paramElement.getAnnotation(Parameter.class);
        String name = parameter.name();
        String desc = parameter.desc();

        if (!EmptyUtils.isEmpty(name)) filedName = name;

        if (ProcessorUtils.isSubtype(superTypeElement,
                ProcessorUtils.getActivityTypeElement())) {
            processorActivity2(codeBuffer, paramElement, filedName);
        } else if (ProcessorUtils.isSubtype(superTypeElement,
                ProcessorUtils.getAppFragmentTypeElement()) ||
                ProcessorUtils.isSubtype(superTypeElement,
                        ProcessorUtils.getAndroidxFragmentTypeElement())) {
            processorFragment(codeBuffer, paramElement, filedName, desc);
        }
    }


    private void processorFragment(StringBuilder codeBuffer, Element paramElement, String filedName, String desc) {
        codeBuffer.append("        Object ").append(filedName).append(" = bundle.get(\"")
                .append(filedName)
                .append("\");\n")
                .append("        if (").append(filedName).append(" != null) {\n")
                .append("            injectObject.").append(filedName)
                .append(" = (")
                .append(paramElement.asType().toString())
                .append(") ")
                .append(filedName)
                .append(";\n")
                .append("        }\n\n");
    }

    private void processorActivity2(StringBuilder codeBuffer, Element paramElement, String filedName) {
        codeBuffer.append("        Object ")
                .append(filedName)
                .append(" = injectObject.getIntent().getExtras().get(\"")
                .append(filedName)
                .append("\");\n").append("        if (").append(filedName)
                .append(" != null) {\n").append("            injectObject.")
                .append(filedName).append(" = (")
                .append(paramElement.asType().toString()).append(") ").append(filedName).append(";\n")
                .append("        }\n\n");
    }

    private void processorActivity(StringBuilder codeBuffer, Element paramElement, TypeMirror typeMirror, int fileType, String filedName) {
        if (typeMirror.toString().equals(
                ProcessorUtils.getStringTypeElement().getQualifiedName().toString())) {
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
        } else if (fileType == TypeKind.BOOLEAN.ordinal()) {
            codeBuffer.append("        injectObject.")
                    .append(filedName)
                    .append(" = injectObject.getIntent().getBooleanExtra(\"")
                    .append(filedName)
                    .append("\", false);\n");
        } else if (fileType == TypeKind.DOUBLE.ordinal()) {
            codeBuffer.append("        injectObject.")
                    .append(filedName)
                    .append(" = injectObject.getIntent().getDoubleExtra(\"")
                    .append(filedName)
                    .append("\", 0.0);\n");
        } else if (fileType == TypeKind.FLOAT.ordinal()) {
            codeBuffer.append("        injectObject.")
                    .append(filedName)
                    .append(" = injectObject.getIntent().getFloatExtra(\"")
                    .append(filedName)
                    .append("\", 0);\n");
        } else if (fileType == TypeKind.LONG.ordinal()) {
            codeBuffer.append("        injectObject.")
                    .append(filedName)
                    .append(" = injectObject.getIntent().getLongExtra(\"")
                    .append(filedName)
                    .append("\", 0);\n");
        } else if (ProcessorUtils.isSubtype(paramElement,
                ProcessorUtils.getParcelableTypeElement())) {//Parcelable的子类 如Bundle
            codeBuffer.append("        injectObject.")
                    .append(filedName)
                    .append(" = injectObject.getIntent().getParcelableExtra(\"")
                    .append(filedName)
                    .append("\");\n");
        } else if (ProcessorUtils.isSubtype(paramElement,
                ProcessorUtils.getSerializableTypeElement())) {
            //Serializable的实现类，declaredType继承至ReferenceType即应用类型,注解处理代码也不多
            // 需要注意的是在生成类型时int[]、 String[]、ArrayList和Map实现了Serializable
            codeBuffer.append("        injectObject.")
                    .append(filedName).append(" =(")
                    .append(paramElement.asType().toString())
                    .append(") injectObject.getIntent().getSerializableExtra(\"")
                    .append(filedName)
                    .append("\");\n");
        }
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
