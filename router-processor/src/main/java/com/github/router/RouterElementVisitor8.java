package com.github.router;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.SimpleElementVisitor8;

public class RouterElementVisitor8 extends SimpleElementVisitor8<Void, Void> {

    private final Filer FILER;

    public RouterElementVisitor8(Filer filer) {
        FILER = filer;
    }

    @Override
    public Void visitTypeParameter(TypeParameterElement typeParameterElement, Void aVoid) {
        System.out.println("visitTypeParameter");
        return super.visitTypeParameter(typeParameterElement, aVoid);
    }

    @Override
    public Void visitUnknown(Element element, Void aVoid) {
        System.out.println("visitUnknown");
        return super.visitUnknown(element, aVoid);
    }

    @Override
    public Void visitExecutable(ExecutableElement executableElement, Void aVoid) {
        System.out.println("visitExecutable");
        System.out.println(executableElement.getSimpleName());
        return super.visitExecutable(executableElement, aVoid);
    }

    @Override
    public Void visitType(TypeElement typeElement, Void aVoid) {
        System.out.println("visitType");
        System.out.println(typeElement.getSimpleName());
        return super.visitType(typeElement, aVoid);
    }

    @Override
    public Void visitVariable(VariableElement variableElement, Void aVoid) {
        System.out.println("visitVariable");
        return super.visitVariable(variableElement, aVoid);
    }

    @Override
    public Void visitPackage(PackageElement packageElement, Void aVoid) {
        System.out.println("visitPackage");
        return super.visitPackage(packageElement, aVoid);
    }
}
