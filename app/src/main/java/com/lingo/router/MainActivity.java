package com.lingo.router;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.lingo.grade.R;
import com.lingo.router.annotations.Destination;

@Destination(url = "router://page/home2", description = "Java首页")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }
}