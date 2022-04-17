package com.example.arduinobt_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import android.content.ActivityNotFoundException;
import android.speech.tts.TextToSpeech;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements callbackInterface {
    private static final int VOICE_COMMANDS_CODE = 100;
    public static Bluetooth_Connection bluetooth = null;
    private Voice_Commands speechRecognition;
    private static byte onOffStatus = 2;
    private static final String[] ANDROID_12_BLE_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    public static int onOffState = 0;
    public enum Voice_Functions {OFF, ON, SET, MODE, COLOR};
    public enum Modes {MUSIC} //RUNNING, BLINKING, FADING};
    public enum Colors {VIOLET, INDIGO, BLUE, GREEN, YELLOW, ORANGE, RED, PINK, MAGENTA, PURPLE, WHITE}
    //Holds RGB values for the colors    Red         Green       Blue
    private byte[][] Color_Codes =  {{(byte)0x8F, (byte)0x00, (byte)0xFF},//Violet
                                     {(byte)0x4B, (byte)0x00, (byte)0x82},//Indigo
                                     {(byte)0x00, (byte)0x00, (byte)0xFF},//Blue
                                     {(byte)0x00, (byte)0xFF, (byte)0x00},//Green
                                     {(byte)0xFF, (byte)0xFF, (byte)0x00},//Yellow
                                     {(byte)0xFF, (byte)0xA5, (byte)0x00},//Orange
                                     {(byte)0xFF, (byte)0x00, (byte)0x00},//Red
                                     {(byte)0xFF, (byte)0xC0, (byte)0xCB},//Pink
                                     {(byte)0xFF, (byte)0x00, (byte)0xFF},//Magenta
                                     {(byte)0x80, (byte)0x00, (byte)0x80},//Purple
                                     {(byte)0xFF, (byte)0xFF, (byte)0xFF}};//White
    public static TextToSpeech textToSpeech;
    private String pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        speechRecognition = new Voice_Commands();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            bluetooth = new Bluetooth_Connection();
            bluetooth.registerCallbackInterface(this);
        } else {
            ActivityCompat.requestPermissions(this, ANDROID_12_BLE_PERMISSIONS, 9999);
        }
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                // if No error is found then only it will run
                if (i != TextToSpeech.ERROR) {
                    // To Choose language of speech
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 9999:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                break;
        }
        bluetooth = new Bluetooth_Connection();
        bluetooth.registerCallbackInterface(this);
        while (!bluetooth.available()) {}
        onOffStatus = bluetooth.read();
    }

    public void led_controls_button(View view) {
        if (bluetooth != null) {
            Intent led_controls = new Intent(this, com.example.arduinobt_test.led_controls.class);
            startActivity(led_controls);
        } else {
            Toast.makeText(this, "Bluetooth not Connected", Toast.LENGTH_SHORT);
        }
    }

    public void documentation_button (View view) {
        Intent documentation = new Intent(this, com.example.arduinobt_test.documentation.class);
        startActivity(documentation);
    }

    /*
    public void Record(View View) {
        if (bluetooth != null) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, 10);
            } else {
                Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Bluetooth not Connected", Toast.LENGTH_SHORT);
        }
    }
    */

    @Override
    public void callback(Object obj) {
        if(obj.getClass() == Bluetooth_Connection.class){
            EditText status = findViewById(R.id.bluetooth_status);
            status.setText("Bluetooth status: " + bluetooth.getStatus());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bluetooth.close();
        bluetooth = null;
    }

    public void record_button(View v) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        try {
            startActivityForResult(intent, VOICE_COMMANDS_CODE);
        } catch (ActivityNotFoundException a) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case VOICE_COMMANDS_CODE: {
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    pb=result.get(0);
                    byte[] output = speechRecognition.useStringArray(pb.split(" "));
                    switch (output[0]) {
                        case 0:
                            if (output.length == 1) {
                                onOffState = 0;
                                break;
                            }
                        case 1:
                            if (output.length == 1) {
                                onOffState = 1;
                                break;
                            }
                            return;
                        case 2://brightness
                            if (output.length == 2 && output[1] != (byte)-1) {
                                int brightnessValue = output[1] * 255 / 100;
                                output[1] = (byte)brightnessValue;
                                output[0] = (byte)8;
                                onOffState = 1;
                                break;
                            }
                            return;
                        case 3://mode (function code for music reactive is already 3)
                            if (output.length == 2 && output[1] != (byte)-1) {
                                output[1] = 1; //(turn on)
                                onOffState = 1;
                                break;
                            }
                            return;
                        case 4://color
                            if(output.length == 2 && output[1] != (byte)-1) {
                                byte color = output[1];
                                output = new byte[4];
                                output[0] = (byte)7;
                                for (int i = 0; i < 3; i++) {
                                    output[i+1] = Color_Codes[color][i];
                                }
                                onOffState = 1;
                                break;
                            }
                            return;
                        default:
                            return;
                    }
                    bluetooth.write(output);
                }
                break;
            }

        }
    }

    public static void outputSpeech(String output){
        textToSpeech.speak(output, TextToSpeech.QUEUE_FLUSH, null);
    }
}