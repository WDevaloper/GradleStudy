package com.github.gradle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.github.router.annotate.Destination;
import com.github.router.annotate.DestinationMethod;
import com.github.router.annotate.Parameter;

import java.util.List;

@Destination(url = "/app/AptActivity", description = "路由测试")
public class AptActivity extends AppCompatActivity {


    @Parameter(name = "param")
    String param;

    @Parameter(name = "param2")
    int param2;

    @Parameter(name = "long_param")
    long long_param;

    @Parameter(name = "long_param2")
    Long long_param2;


    @Parameter(name = "int_param")
    int[] int_param;


    @Parameter(name = "string_param")
    String[] string_param;


    @Parameter(name = "list_param")
    List<User>[] list_param;


    @Parameter(name = "user")
    User user;

    @Parameter(name = "person")
    Person person;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apt);
        Log.e("AptActivity", "onCreate change");

        new AptActivity$$Parameter().inject(this);
    }
}