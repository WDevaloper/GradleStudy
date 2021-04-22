package com.github.gradle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.github.router.annotate.Destination;
import com.github.router.annotate.DestinationMethod;

@Destination(url = "/app/AptActivity", description = "路由测试")
public class AptActivity extends AppCompatActivity {

    @Override
    @DestinationMethod(url = "/app/AptActivity")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apt);
        Log.e("AptActivity", "onCreate change");
    }
}