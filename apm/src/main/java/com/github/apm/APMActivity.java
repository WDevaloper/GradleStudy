package com.github.apm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Printer;
import android.util.SparseArray;
import android.util.SparseLongArray;
import android.view.View;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class APMActivity extends AppCompatActivity {

    private static final String TAG = APMActivity.class.getSimpleName();
    private WeakHashMap<Long, String> stackTraces = new WeakHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_p_m);


        Looper looper = Looper.myLooper();
        looper.setMessageLogging(new Printer() {
            @Override
            public void println(String x) {
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                if (stackTrace.length <= 0) return;
                stackTraces.put(System.currentTimeMillis(), getStackTrace(stackTrace));
            }
            
        });
    }

    private String getStackTrace(StackTraceElement[] stackTrace) {
        StringBuilder sbf = new StringBuilder();

        for (StackTraceElement element : stackTrace) {
            if (sbf.length() > 0) {
                sbf.append(" <- ");
                sbf.append(System.getProperty("line.separator"));
            }
            sbf.append(
                    MessageFormat.format("{0}.{1}() {2}"
                            , element.getClassName()
                            , element.getMethodName()
                            , element.getLineNumber()));
        }
        return sbf.toString();
    }

    public void logger(View view) {
        for (Map.Entry<Long, String> longStringEntry : stackTraces.entrySet()) {
            String value = longStringEntry.getValue();
            Long key = longStringEntry.getKey();
            Log.e(TAG, "logger: key = "+key);
            Log.e(TAG, value);
        }
    }
}