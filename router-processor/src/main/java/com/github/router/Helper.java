package com.github.router;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Types;

public class Helper {
    private static Types typeUtils;

    public static void init(Types types) {
        Helper.typeUtils = types;
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
}
