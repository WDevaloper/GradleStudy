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
    List<User>[] list_param;


    @Parameter
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