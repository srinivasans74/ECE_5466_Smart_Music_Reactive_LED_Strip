package com.example.arduinobt_test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class color_picker extends AppCompatActivity implements callbackInterface{
    SeekBar Intensity, Red, Green, Blue;
    TextView ColorPreview;
    Bluetooth_Connection bluetooth;
    EditText SelectedStart;
    EditText SelectedEnd;
    EditText InitializedLEDs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_picker);
        Red = (SeekBar) findViewById(R.id.red_seek_bar);
        Blue = (SeekBar) findViewById(R.id.blue_seek_bar);
        Green = (SeekBar) findViewById(R.id.green_seek_bar);
        Intensity = (SeekBar) findViewById(R.id.intensity_seek_bar);
        ColorPreview = (TextView) findViewById(R.id.color_preview);
        Intensity.setOnSeekBarChangeListener(changedSeekBar());
        Red.setOnSeekBarChangeListener(changedSeekBar());
        Green.setOnSeekBarChangeListener(changedSeekBar());
        Blue.setOnSeekBarChangeListener(changedSeekBar());
        bluetooth = MainActivity.bluetooth;
        bluetooth.registerCallbackInterface(this);
        SelectedStart = findViewById(R.id.select_start_led);
        SelectedEnd = findViewById(R.id.select_stop_led);
        InitializedLEDs = findViewById(R.id.initialize_led);
    }
    public void cancelButton (){
        finish();
    }
    private SeekBar.OnSeekBarChangeListener changedSeekBar () {
        return new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int intensity = ((Intensity.getProgress()+1) * (Intensity.getMax()+2)) << 24;
                int red = (Red.getProgress() * (Red.getMax() + 1)) << 16;
                int green = (Green.getProgress() * (Green.getMax() + 1)) << 8;
                int blue = Blue.getProgress() * (Blue.getMax() + 1);
                ColorPreview.setBackgroundColor(intensity + red + blue + green);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }

    public void led_send (View view) {
        byte RedGreen = (byte)(Red.getProgress()<<4|Green.getProgress());
        byte BlueIntensity = (byte)((Blue.getProgress()<<4)|(Intensity.getProgress()+1));
        int start = 0;
        int stop = 0;
        int initialized = 0;
        byte startByte = 0;
        byte stopByte = 0;
        byte initializedByte = 0;
        boolean sendError = false;
        try {
            initialized = Integer.parseInt(InitializedLEDs.getText().toString());
            start = Integer.parseInt(SelectedStart.getText().toString());
            stop = Integer.parseInt(SelectedEnd.getText().toString());
        } catch (Exception e) {
            sendError = true;
        }
        if(initialized <= 150 && initialized > 0 && start<=initialized && start > 0 && stop <= initialized && stop > 0) {
            initializedByte = (byte)(initialized & 0xFF);
            startByte = (byte)(--start & 0xFF);
            stopByte = (byte)(stop & 0xFF);
        } else {
            sendError = true;
        }
        if(sendError){
            Toast.makeText(getApplicationContext(), "Invalid LED Input.", Toast.LENGTH_SHORT);
        } else {
            bluetooth.write(new byte[]{(byte) 2, RedGreen, BlueIntensity, initializedByte, startByte, stopByte});
            MainActivity.onOffState = 1;
        }
    }

    @Override
    public void callback(Object obj) {

    }
}