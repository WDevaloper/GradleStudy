package com.github.gradle;

import android.os.Bundle;

import com.github.router.ParameterInject;

public class Fragment$$Parameter implements ParameterInject {

    @Override
    public void inject(Object target) {
        TestFragment injectObject = (TestFragment) target;
        Bundle bundle = injectObject.getArguments();
        injectObject.param = (String) bundle.get("param");
        injectObject.param2 = (int) bundle.get("param2");
    }
}
