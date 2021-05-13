package com.github.gradle;

import com.github.router.runtime.ParameterInject;

public class TestActivity$$Parameter implements ParameterInject {

    @Override
    public void inject(Object target) {
        // 1、拿到被@Parameter注解标注的类型
        AptActivity injectObject = (AptActivity) target;
        // 2、拿到被@Parameter注解标注的Filed名
        Object param = injectObject.getIntent().getExtras().get("param");
        if (param != null) {
            injectObject.param = (String) param;
        }
        injectObject.param2 = (int) injectObject.getIntent().getExtras().get("param");
        injectObject.getIntent().getSerializableExtra("");


    }
}
