package com.example.arduinobt_test;

import android.speech.tts.TextToSpeech;

import java.util.Hashtable;
import java.util.Locale;

public class Voice_Commands {
    private String[] tokens;
    private TextToSpeech textToSpeech;
    private String invalidCommand = "Please enter a valid command";
    Voice_Commands(){
        textToSpeech = MainActivity.textToSpeech;
    }

    public byte[] useStringArray(String[] tokens){
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].toUpperCase(Locale.ROOT);
        }
        this.tokens = tokens;
        return access_1();
    }

    private byte brightness() {
        for (int i = 0; i < tokens.length; i++) {
            if(tokens[i].equals("BRIGHTNESS")) {
                return getBrightnessValue();
            }
        }
        return -1;
    }

    private byte getBrightnessValue() {
        String speak_3 = "Please mention how much do you want to set the brightness to";
        String speak_4 = "Setting the brightness to ";
        for (int i = tokens.length - 1; i >= 0; i--) {
            String stringValue = tokens[i].replaceAll("[^0-9]+", "");
            if(!stringValue.equals("")) {
                byte value = Byte.parseByte(stringValue);
                if (value <= 100 && value >= 10) {
                    MainActivity.outputSpeech(speak_4 + value + "%");
                    return value;
                } else {
                    MainActivity.outputSpeech("Value must be between 10 and 100 percent");
                    return -1;
                }
            }
        }
        MainActivity.outputSpeech(speak_3);
        return -1;
    }

    private byte getOnOrOff(int x) {//break 3
        String SS = (x==0?"Turing off ":"Turing on ");
        String speak_4 ="Light emitting diode";
        for (int i = 0; i < tokens.length; i++) {
            if(tokens[i].equals("LED")) {
                MainActivity.outputSpeech(SS + speak_4);
                return (byte)x;
            }
        }
        MainActivity.outputSpeech(invalidCommand);
        return (byte)-1;
    }

    private byte get_mode_code() {//break 5
        Hashtable<String, Integer> my_dict = new Hashtable<>();
        for (MainActivity.Modes m : MainActivity.Modes.values()){
            my_dict.put(m.toString(), m.ordinal());
        }
        String speak_3="Please go through the documentation and use an available mode";
        String speak_4="Changing to ";
        for (int i = tokens.length-1; i >= 0; i--) {
            // accessing each element of array
            if (my_dict.containsKey(tokens[i])) {
                MainActivity.outputSpeech(speak_4 + tokens[i] + " mode");
                return my_dict.get(tokens[i]).byteValue();
            }
        }
        MainActivity.outputSpeech(speak_3);
        return -1;
    }

    private byte get_color_code() {
        Hashtable<String, Integer> my_dict = new Hashtable<>();
        for (MainActivity.Colors c : MainActivity.Colors.values()){
            my_dict.put(c.toString(), c.ordinal());
        }
        String speak_3 = "Please go through the document and provide an available color";
        String speak_4 = "Changing color to ";
        for (int i = tokens.length - 1; i >= 0; i--) {
            // accessing each element of array
            if (my_dict.containsKey(tokens[i])) {
                MainActivity.outputSpeech(speak_4 + tokens[i].toLowerCase(Locale.ROOT));
                return my_dict.get(tokens[i]).byteValue();
            }
        }
        MainActivity.outputSpeech(speak_3);
        return -1;
    }

    private byte[] access_1() {
        Hashtable<String, Integer> my_dict = new Hashtable<>();
        // Using a few dictionary Class methods
        // using put method
        for (MainActivity.Voice_Functions v : MainActivity.Voice_Functions.values()){
            my_dict.put(v.toString(), v.ordinal());
        }
        for (int i = 0; i < tokens.length; i++) {
            // accessing each element of array
            if (my_dict.containsKey(tokens[i])) {
                switch (my_dict.get(tokens[i])) {
                    case 0:
                    case 1:
                        return new byte[]{getOnOrOff(my_dict.get(tokens[i]))};
                    case 2:
                        return new byte[]{my_dict.get(tokens[i]).byteValue(), brightness()};
                    case 3:
                        return new byte[]{my_dict.get(tokens[i]).byteValue(), get_mode_code()};
                    case 4:
                        return new byte[]{my_dict.get(tokens[i]).byteValue(), get_color_code()};
                }
            }
        }
        MainActivity.outputSpeech(invalidCommand);
        return new byte[]{-1};
    }
}
