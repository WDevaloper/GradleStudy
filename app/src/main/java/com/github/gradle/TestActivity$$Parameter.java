package com.github.gradle;

import com.github.router.ParameterInject;

public class TestActivity$$Parameter implements ParameterInject {

    @Override
    public void inject(Object target) {
        // 1、拿到被@Parameter注解标注的类型
        AptActivity injectObject = (AptActivity) target;
        // 2、拿到被@Parameter注解标注的Filed名
        injectObject.param = injectObject.getIntent().getStringExtra("param");

        injectObject.param2 = injectObject.getIntent().getIntExtra("param", 0);

        injectObject.getIntent().getSerializableExtra("");
    }
}
