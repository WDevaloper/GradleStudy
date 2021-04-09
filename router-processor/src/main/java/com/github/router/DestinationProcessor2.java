package com.github.router;

import com.github.router.annotate.Destination;
import com.github.router.annotate.DestinationMethod;
import com.google.auto.service.AutoService;

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


@AutoService({Processor.class})
public class DestinationProcessor2 extends AbstractProcessor {

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
        Set<? extends Element> allDestinationMethods =
                roundEnvironment.getElementsAnnotatedWith(DestinationMethod.class);

        if (allDestinations.isEmpty()
                && allDestinationMethods.isEmpty()) {
            return false;
        }

        RouterElementVisitor8 routerElementVisitor8 = new RouterElementVisitor8(filer);

        for (Element allDestination : allDestinations) {
            //allDestination.accept(routerElementVisitor8, null);
        }

        for (Element allDestination : allDestinationMethods) {
            //allDestination.accept(routerElementVisitor8, null);
        }

        return true;
    }
}
