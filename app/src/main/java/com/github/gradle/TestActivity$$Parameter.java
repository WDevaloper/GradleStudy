package com.github.gradle;

import com.github.router.ParameterInject;

public class TestActivity$$Parameter implements ParameterInject {

    @Override
    public void inject(Object target) {
        // 1、拿到被@Parameter注解标注的类型
        AptActivity injectObject = (AptActivity) target;
        // 2、拿到被@Parameter注解标注的Filed名
        injectObject.param = (String) injectObject.getIntent().getExtras().get("param");
        injectObject.param2 = (int) injectObject.getIntent().getExtras().get("param");
        injectObject.getIntent().getSerializableExtra("");


    }
}
