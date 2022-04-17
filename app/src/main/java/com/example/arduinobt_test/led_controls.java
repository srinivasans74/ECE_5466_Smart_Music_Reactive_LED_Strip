package com.example.arduinobt_test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class led_controls extends AppCompatActivity implements callbackInterface {
    Bluetooth_Connection bluetooth;
    Button onOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_controls);
        bluetooth = MainActivity.bluetooth;
        bluetooth.registerCallbackInterface(this);
        onOff = findViewById(R.id.on_off_button);
    }

    public void color_picker (View view) {
        Intent bluetooth = new Intent(this, com.example.arduinobt_test.color_picker.class);
        startActivity(bluetooth);
    }

    public void on_off (View view) {
        if (MainActivity.onOffState == 1) {
            bluetooth.write(new byte[]{0});
            onOff.setText("Turn On");
            MainActivity.onOffState = 0;
        } else {
            System.out.println("ON");
            bluetooth.write(new byte[]{1});
            onOff.setText("Turn Off");
            MainActivity.onOffState = 1;
        }
    }

    public void mode_setup (View view) {
        Intent intent = new Intent(this, com.example.arduinobt_test.modes.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        bluetooth.removeRegisteredCallback(this);
    }

    @Override
    public void callback(Object obj) {
        if(obj.getClass() == Bluetooth_Connection.class){
            EditText status = findViewById(R.id.bluetooth_status);
            status.setText("Bluetooth: " + bluetooth.getStatus());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(MainActivity.onOffState==1) {
            onOff.setText("Turn Off");
        } else {
            onOff.setText("Turn On");
        }
    }
}