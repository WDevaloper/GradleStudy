package com.github.hotfit;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.router.annotate.Destination;

@Destination(url = "/hotfix/HotfixActivity", description = "热修复")
public class HotfixActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
