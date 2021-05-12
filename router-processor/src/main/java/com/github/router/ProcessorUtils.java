package com.github.router;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class ProcessorUtils {
    private static Types typeUtils;
    private static Elements elements;

    public static void init(Types types, Elements elements) {
        ProcessorUtils.typeUtils = types;
        ProcessorUtils.elements = elements;
    }

    /**
     * element1 是否是 element2 的子类型   或  element2 是否是 element1 的父类型
     *
     * @param element1
     * @param element2
     * @return
     */
    public static boolean isSubtype(Element element1, Element element2) {
        return typeUtils.isSubtype(element1.asType(), element2.asType());
    }

    /**
     * 判定此 element2参数是否是element1的超类或超接口 或 判定此 element1 参数是否是element2的子类
     * <p>
     * element1 是不是 element2的弱类型
     *
     * @param element1
     * @param element2
     * @return
     */
    public static boolean isAssignable(Element element1, Element element2) {
        return typeUtils.isAssignable(element1.asType(), element2.asType());
    }

    /**
     * 是否相同类型
     *
     * @param element1 1
     * @param element2 1
     * @return
     */
    public static boolean isSameType(Element element1, Element element2) {
        return typeUtils.isSameType(element1.asType(), element2.asType());
    }


    /**
     * @return 获取Activity的 TypeElement
     */
    public static TypeElement getTypeElement(String qualifiedName) {
        return elements.getTypeElement(qualifiedName);
    }


    /**
     * @param typeClass 如： String.class
     * @return TypeElement
     */
    public static TypeElement getTypeElement(Class<?> typeClass) {
        return getTypeElement(typeClass.getName());
    }

    /**
     * @return 获取Activity的 TypeElement
     */
    public static TypeElement getActivityTypeElement() {
        return getTypeElement(Constants.ACTIVITY);
    }

    /**
     * @return 获取AndroidxFragment的 TypeElement
     */
    public static TypeElement getAndroidxFragmentTypeElement() {
        return getTypeElement(Constants.ANDROIDX_FRAGMENT);
    }

    /**
     * @return 获取Parcelable的 TypeElement
     */
    public static TypeElement getParcelableTypeElement() {
        return getTypeElement(Constants.PARCELABLE);
    }

    /**
     * @return 获取Serializable的 TypeElement
     */
    public static TypeElement getSerializableTypeElement() {
        return getTypeElement(Constants.SERIALIZABLE);
    }

    /**
     * @return 获取AppFragment的 TypeElement
     */
    public static TypeElement getAppFragmentTypeElement() {
        return getTypeElement(Constants.APP_FRAGMENT);
    }

    /**
     * @return 获取String的 TypeElement
     */
    public static TypeElement getStringTypeElement() {
        return getTypeElement(String.class);
    }

    public static TypeElement getBundleTypeElement() {
        return getTypeElement(Constants.BUNDLE);
    }
}
