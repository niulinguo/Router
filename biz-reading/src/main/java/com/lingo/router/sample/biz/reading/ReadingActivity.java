package com.lingo.router.sample.biz.reading;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.lingo.router.annotations.Destination;

@Destination(url = "router://page/reading", description = "阅读月面")
public class ReadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);
    }
}