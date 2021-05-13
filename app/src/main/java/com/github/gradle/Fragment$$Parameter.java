package com.github.gradle;

import android.os.Bundle;

import com.github.router.runtime.ParameterInject;

public class Fragment$$Parameter implements ParameterInject {

    @Override
    public void inject(Object target) {
        TestFragment injectObject = (TestFragment) target;
        Bundle bundle = injectObject.getArguments();

        Object param = bundle.get("param");
        if (param != null) {
            injectObject.param = (String) param;
        }


        injectObject.param2 = (int) bundle.get("param2");
    }
}
