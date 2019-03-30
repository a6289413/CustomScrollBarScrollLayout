package com.example.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;

import com.example.myapplication.scrolllayout.CustomScrollBarScrollLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CustomScrollBarScrollLayout csbsl_scroll_layout = findViewById(R.id.csbsl_scroll_layout);
        csbsl_scroll_layout.addScrollViewContent(LayoutInflater.from(this).inflate(R.layout.scroll_view_content, null));
    }
}
