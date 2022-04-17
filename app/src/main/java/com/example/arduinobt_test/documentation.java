package com.example.arduinobt_test;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Locale;

public class documentation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documentation);
        TextView color = findViewById(R.id.colors_text);
        color.setText("Available Colors:");
        color.setTextColor(Color.WHITE);
        for (MainActivity.Colors c : MainActivity.Colors.values()) {
            String currentColor = c.toString().toLowerCase(Locale.ROOT);
            currentColor = currentColor.substring(0,1).toUpperCase(Locale.ROOT) + currentColor.substring(1);
            color.setText(color.getText().toString() + "\n\t\t" + currentColor);
        }
    }
}