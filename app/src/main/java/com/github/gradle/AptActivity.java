package com.github.gradle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.github.router.annotate.Destination;
import com.github.router.annotate.DestinationMethod;
import com.github.router.annotate.Parameter;

@Destination(url = "/app/AptActivity", description = "路由测试")
public class AptActivity extends AppCompatActivity {


    @Parameter(name = "param")
    String param;

    @Parameter(name = "param")
    int param2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apt);
        Log.e("AptActivity", "onCreate change");

        new AptActivity$$Parameter().inject(this);
    }
}