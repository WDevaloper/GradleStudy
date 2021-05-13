package com.github.gradle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.github.router.annotate.Destination;
import com.github.router.annotate.DestinationMethod;
import com.github.router.annotate.Parameter;
import com.github.router.runtime.Router;

import java.util.Arrays;
import java.util.List;

@Destination(url = "/app/AptActivity", description = "路由测试")
public class AptActivity extends AppCompatActivity {
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
    }
}