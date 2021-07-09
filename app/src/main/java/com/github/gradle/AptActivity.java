package com.github.gradle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.gradle.jvm.ReflectTest;
import com.github.router.annotate.Destination;
import com.github.router.annotate.DestinationMethod;
import com.github.router.annotate.Parameter;
import com.github.router.runtime.Router;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Destination(url = "/app/AptActivity", description = "路由测试")
public class
AptActivity extends AppCompatActivity {
    private static final String TAG = AptActivity.class.getSimpleName();

    @Parameter
    String param;


    @Parameter
    int param2;

    @Parameter
    long long_param;

    @Parameter(desc = "啦啦啦")
    Long long_param2;


    @Parameter
    int[] int_param;


    @Parameter
    String[] string_param;


    @Parameter
    List<User> list_param;


    @Parameter
    User user;

    @Parameter(name = "person")
    Person person;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apt);

        ViewGroup contentView = (FrameLayout) getWindow().getDecorView().findViewById(android.R.id.content);


        {
            TextView textView = new TextView(this);
            textView.setText("Hello World");
            textView.setTextSize(60f);
            textView.setTextColor(Color.parseColor("#000000"));
            contentView.addView(textView, new FrameLayout.LayoutParams(-1, -1));
        }


        Router.inject(this);


        Log.e(TAG, "param >>> " + param);
        Log.e(TAG, "param2 >>> " + param2);
        Log.e(TAG, "long_param >>> " + long_param);
        Log.e(TAG, "long_param2 >>> " + long_param2);
        Log.e(TAG, "int_param >>> " + Arrays.toString(int_param));
        Log.e(TAG, "string_param >>> " + Arrays.toString(string_param));
        Log.e(TAG, "list_param >>> " + list_param);
        Log.e(TAG, "user >>> " + user);
        Log.e(TAG, "person >>> " + person);


        try {
            Class<?> aClass = Class.forName("com.github.gradle.jvm.ReflectTest");
            Constructor<?> aClassConstructor = aClass.getConstructor();
            Object object = aClassConstructor.newInstance();
            Method aClassDeclaredMethod = aClass.getDeclaredMethod("getAgeTest");
            Object result = aClassDeclaredMethod.invoke(object);
            Log.e(TAG, "onCreate: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }


        //LiveData 是如何跟 LifecycleOwner 进行绑定，做到感知生命周期的？
        //
        //LiveData 只在 LifecycleOwner active 状态发送通知，是怎么处理的？
        //
        //LiveData 会自动在 DESTROY 的状态下取消订阅，是怎么处理的？
        //
        //通过 setValue()/postValue() 更新数据的处理流程是如何？
        //
        //生命周期变化后数据处理流程是怎么样的？


        MutableLiveData<String> liveString = new MutableLiveData<>();
        liveString.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String s) {
                Log.d(TAG, "onChanged() called with: s = [" + s + "]");
            }
        });

        liveString.postValue("程序亦非猿");
    }


    @Override
    protected void onResume() {
        super.onResume();
        getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                Log.e(TAG, "onStateChanged: " + event.name());
            }
        });
    }
}