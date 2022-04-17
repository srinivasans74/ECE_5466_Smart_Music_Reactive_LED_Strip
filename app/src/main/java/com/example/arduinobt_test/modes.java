package com.example.arduinobt_test;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

public class modes extends AppCompatActivity {
    //Bluetooth Connection
    private Bluetooth_Connection bluetooth;

    //Shared Items
    private Button SubmitButton;
    private TextView ActivateLabel, DeactivateLabel;
    private Switch[] ActivateSwitches;

    //Music Reactive Mode Items
    private Switch MusicReactiveActivateSwitch;

    //Running Modes Items
    private SeekBar Red1, Red2, Green1, Green2, Blue1, Blue2, Intensity1, Intensity2, RunningSpeed;
    private TextView ColorPreview1, ColorPreview2, Color1Label, Color2Label, Red1Label, Red2Label, Green1Label, Green2Label, Blue1Label, Blue2Label, Intensity1Label, Intensity2Label, RunningSpeedLabel, UnidirectionalLabel, BidirectionLabel;
    private Switch DirectionalSwitch, RunningActivateSwitch;
    private SeekBar[] RunningSeekbars;
    private TextView[] RunningTextviews;

    //Fading Mode Items
    private SeekBar FadingSpeedSeekBar, FadingMinimumSeekBar, FadingMaximumSeekBar;
    private TextView FadingSpeedLabel, FadingMinLabel, FadingMaxLabel;
    private Switch FadingActivateSwitch;
    private TextView[] FadingTextviews;
    private SeekBar[] FadingSeekBars;

    //Blinking Modes Items
    private SeekBar BlinkingSpeedSeekBar, BlinkingMinimumSeekBar, BlinkingMaximumSeekBar;
    private TextView BlinkingSpeedLabel, BlinkingMinLabel, BlinkingMaxLabel;
    private Switch BlinkingActivateSwitch;
    private TextView[] BlinkingTextviews;
    private SeekBar[] BlinkingSeekBars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modes);
        bluetooth = MainActivity.bluetooth;
        //get the spinner from the xml.
        Spinner dropdown = findViewById(R.id.mode_select);
        //create a list of items for the spinner.
        String[] items = new String[]{"None", "Music Reactive", "Running", "Fading", "Blinking"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View view, int i, long l) {
                ((TextView) parentView.getChildAt(0)).setTextColor(Color.WHITE);
                switch (i) {
                    case 0:
                        hideRunning();
                        hideBlinking();
                        hideFading();
                        hideMusicReactive();
                        SubmitButton.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        hideRunning();
                        hideBlinking();
                        hideFading();
                        showMusicReactive();
                        SubmitButton.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        hideBlinking();
                        hideFading();
                        hideMusicReactive();
                        showRunning();
                        SubmitButton.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        hideBlinking();
                        hideRunning();
                        hideMusicReactive();
                        showFading();
                        SubmitButton.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        hideFading();
                        hideRunning();
                        hideMusicReactive();
                        showBlinking();
                        SubmitButton.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //Shared Items
        SubmitButton = findViewById(R.id.submit_button);
        ActivateLabel = findViewById(R.id.activate_label);
        DeactivateLabel = findViewById(R.id.deactivate_label);

        //Music Reactive Switch
        MusicReactiveActivateSwitch = findViewById(R.id.music_reactive_activate_switch);

        //Music Reactive default state
        hideMusicReactive();

        //Running directional Switch
        DirectionalSwitch = findViewById(R.id.directional_switch);
        RunningActivateSwitch = findViewById(R.id.running_activate_switch);

        //Running Labels
        ColorPreview1 = findViewById(R.id.color_preview_1);
        ColorPreview2 = findViewById(R.id.color_preview_2);
        Color1Label = findViewById(R.id.color_1_label);
        Color2Label = findViewById(R.id.color_2_label);
        Red1Label = findViewById(R.id.color_1_red_label);
        Red2Label = findViewById(R.id.color_2_red_label);
        Green1Label = findViewById(R.id.color_1_green_label);
        Green2Label = findViewById(R.id.color_2_green_label);
        Blue1Label = findViewById(R.id.color_1_blue_label);
        Blue2Label = findViewById(R.id.color_2_blue_label);
        Intensity1Label = findViewById(R.id.intensity_1_label);
        Intensity2Label = findViewById(R.id.intensity_2_label);
        RunningSpeedLabel = findViewById(R.id.running_speed_label);
        UnidirectionalLabel = findViewById(R.id.unidirectional_label);
        BidirectionLabel = findViewById(R.id.bidirectional_label);

        //Running Seekbars
        RunningSpeed = findViewById(R.id.running_speed_seek_bar);
        Red1 = findViewById(R.id.red_seek_bar_color_1);
        Red2 = findViewById(R.id.red_seek_bar_color_2);
        Green1 = findViewById(R.id.green_seek_bar_color_1);
        Green2 = findViewById(R.id.green_seek_bar_color_2);
        Blue1 = findViewById(R.id.blue_seek_bar_color_1);
        Blue2 = findViewById(R.id.blue_seek_bar_color_2);
        Intensity1 = findViewById(R.id.intensity_seek_bar_color_1);
        Intensity2 = findViewById(R.id.intensity_seek_bar_color_2);
        Intensity1.setOnSeekBarChangeListener(changedSeekBarColor1());
        Red1.setOnSeekBarChangeListener(changedSeekBarColor1());
        Green1.setOnSeekBarChangeListener(changedSeekBarColor1());
        Blue1.setOnSeekBarChangeListener(changedSeekBarColor1());
        Intensity2.setOnSeekBarChangeListener(changedSeekBarColor2());
        Red2.setOnSeekBarChangeListener(changedSeekBarColor2());
        Green2.setOnSeekBarChangeListener(changedSeekBarColor2());
        Blue2.setOnSeekBarChangeListener(changedSeekBarColor2());

        //Running Arrays
        RunningSeekbars = new SeekBar[]{Red1, Red2, Green1, Green2, Blue1, Blue2, Intensity1, Intensity2, RunningSpeed};
        RunningTextviews = new TextView[]{ColorPreview1, ColorPreview2, Color1Label, Color2Label, Red1Label, Red2Label, Green1Label, Green2Label, Blue1Label, Blue2Label, Intensity1Label, Intensity2Label, RunningSpeedLabel, UnidirectionalLabel, BidirectionLabel, ActivateLabel, DeactivateLabel};

        //Running items default state
        hideRunning();

        //Fading Seekbar
        FadingSpeedSeekBar = findViewById(R.id.fading_speed_seek_bar);
        FadingMinimumSeekBar = findViewById(R.id.fading_minimum_seek_bar);
        FadingMaximumSeekBar = findViewById(R.id.fading_maximum_seek_bar);
        FadingMinimumSeekBar.setOnSeekBarChangeListener(minFadingBrightnessChanged());
        FadingMaximumSeekBar.setOnSeekBarChangeListener(maxFadingBrightnessChanged());

        //Fading TextViews
        FadingSpeedLabel = findViewById(R.id.fading_speed_label);
        FadingMinLabel = findViewById(R.id.fading_minimum_label);
        FadingMaxLabel = findViewById(R.id.fading_maximum_label);

        //Fading Switch
        FadingActivateSwitch = findViewById(R.id.fading_activate_switch);

        //Fading Array
        FadingTextviews = new TextView[]{FadingSpeedLabel, FadingMinLabel, FadingMaxLabel, ActivateLabel, DeactivateLabel};
        FadingSeekBars = new SeekBar[]{FadingSpeedSeekBar, FadingMaximumSeekBar, FadingMinimumSeekBar};

        //Fading items default state
        hideFading();

        //Blinking Seekbar
        BlinkingSpeedSeekBar = findViewById(R.id.blinking_speed_seek_bar);
        BlinkingMinimumSeekBar = findViewById(R.id.blinking_minimum_seek_bar);
        BlinkingMaximumSeekBar = findViewById(R.id.blinking_maximum_seek_bar);
        BlinkingMinimumSeekBar.setOnSeekBarChangeListener(minBlinkingBrightnessChanged());
        BlinkingMaximumSeekBar.setOnSeekBarChangeListener(maxBlinkingBrightnessChanged());

        //Blinking TextViews
        BlinkingSpeedLabel = findViewById(R.id.blinking_speed_label);
        BlinkingMinLabel = findViewById(R.id.blinking_minimum_label);
        BlinkingMaxLabel = findViewById(R.id.blinking_maximum_label);

        //Blinking Switch
        BlinkingActivateSwitch = findViewById(R.id.blinking_activate_switch);

        //Blinking Array
        BlinkingTextviews = new TextView[]{BlinkingSpeedLabel, BlinkingMinLabel, BlinkingMaxLabel, ActivateLabel, DeactivateLabel};
        BlinkingSeekBars = new SeekBar[]{BlinkingSpeedSeekBar, BlinkingMaximumSeekBar, BlinkingMinimumSeekBar};

        //Blinking items default state
        hideBlinking();

        //Array of Switches
        ActivateSwitches = new Switch[]{MusicReactiveActivateSwitch, RunningActivateSwitch, FadingActivateSwitch, BlinkingActivateSwitch};
        for (Switch s: ActivateSwitches) {
            s.setOnCheckedChangeListener(activateSwitchChangeListener());
        }
    }

    private void showMusicReactive () {
        ActivateLabel.setVisibility(View.VISIBLE);
        DeactivateLabel.setVisibility(View.VISIBLE);
        MusicReactiveActivateSwitch.setVisibility(View.VISIBLE);
    }

    private void hideMusicReactive () {
        ActivateLabel.setVisibility(View.INVISIBLE);
        DeactivateLabel.setVisibility(View.INVISIBLE);
        MusicReactiveActivateSwitch.setVisibility(View.INVISIBLE);
    }

    private void hideRunning () {
        for (TextView text: RunningTextviews) {
            text.setVisibility(View.INVISIBLE);
        }
        for (SeekBar seek: RunningSeekbars) {
            seek.setVisibility(View.INVISIBLE);
        }
        DirectionalSwitch.setVisibility(View.INVISIBLE);
        RunningActivateSwitch.setVisibility(View.INVISIBLE);
        Button runningSubmit = findViewById(R.id.submit_button);
        runningSubmit.setVisibility(View.INVISIBLE);
    }

    private void showRunning () {
        for (TextView text: RunningTextviews) {
            text.setVisibility(View.VISIBLE);
        }
        for (SeekBar seek: RunningSeekbars) {
            seek.setVisibility(View.VISIBLE);
        }
        DirectionalSwitch.setVisibility(View.VISIBLE);
        RunningActivateSwitch.setVisibility(View.VISIBLE);
        Button runningSubmit = findViewById(R.id.submit_button);
        runningSubmit.setVisibility(View.VISIBLE);
    }

    private void hideBlinking () {
        for (TextView text: BlinkingTextviews) {
            text.setVisibility(View.INVISIBLE);
        }
        for (SeekBar seek: BlinkingSeekBars) {
            seek.setVisibility(View.INVISIBLE);
        }
        BlinkingActivateSwitch.setVisibility(View.INVISIBLE);
        Button blinkingSubmit = findViewById(R.id.submit_button);
        blinkingSubmit.setVisibility(View.INVISIBLE);
    }

    private void showBlinking () {
        for (TextView text: BlinkingTextviews) {
            text.setVisibility(View.VISIBLE);
        }
        for (SeekBar seek: BlinkingSeekBars) {
            seek.setVisibility(View.VISIBLE);
        }
        BlinkingActivateSwitch.setVisibility(View.VISIBLE);
        Button blinkingSubmit = findViewById(R.id.submit_button);
        blinkingSubmit.setVisibility(View.VISIBLE);
    }

    private void hideFading () {
        for (TextView text: FadingTextviews) {
            text.setVisibility(View.INVISIBLE);
        }
        for (SeekBar seek: FadingSeekBars) {
            seek.setVisibility(View.INVISIBLE);
        }
        FadingActivateSwitch.setVisibility(View.INVISIBLE);
        Button fadingSubmit = findViewById(R.id.submit_button);
        fadingSubmit.setVisibility(View.INVISIBLE);
    }

    private void showFading () {
        for (TextView text: FadingTextviews) {
            text.setVisibility(View.VISIBLE);
        }
        for (SeekBar seek: FadingSeekBars) {
            seek.setVisibility(View.VISIBLE);
        }
        FadingActivateSwitch.setVisibility(View.VISIBLE);
        Button fadingSubmit = findViewById(R.id.submit_button);
        fadingSubmit.setVisibility(View.VISIBLE);
    }

    public void submit (View view) {
        Spinner dropdown = findViewById(R.id.mode_select);
        switch(dropdown.getSelectedItemPosition()) {
            case 1:
                bluetooth.write(new byte[] {3, (byte)(MusicReactiveActivateSwitch.isChecked()?1:0)});
                break;
            case 2:
                byte RedGreen1 = (byte)(Red1.getProgress()<<4|Green1.getProgress());
                byte BlueIntensity1 = (byte)(Blue1.getProgress()<<4|Intensity1.getProgress());
                byte RedGreen2 = (byte)(Red2.getProgress()<<4|Green2.getProgress());
                byte BlueIntensity2 = (byte)(Blue2.getProgress()<<4|Intensity2.getProgress());
                byte FirstByte = (RunningActivateSwitch.isChecked()?(byte)1:(byte)0);
                if(DirectionalSwitch.isChecked()){
                    FirstByte |= 0x02;
                } else {
                    FirstByte &= 0xFD;
                }
                byte Speed = (byte)(RunningSpeed.getProgress()+1);
                bluetooth.write(new byte[]{(byte) 4, FirstByte, RedGreen1, BlueIntensity1, RedGreen2, BlueIntensity2, Speed});
                break;
            case 3: //fading
                byte fadingSpeedAndState = (FadingActivateSwitch.isChecked()?(byte)1:(byte)0);
                int fadingSpeed = FadingSpeedSeekBar.getProgress()<<1;
                fadingSpeedAndState |= fadingSpeed;
                byte fadingMaxIntensity = (byte)(FadingMaximumSeekBar.getProgress()+1);
                byte fadingMinIntensity = (byte)FadingMinimumSeekBar.getProgress();
                bluetooth.write(new byte[]{(byte)5, fadingSpeedAndState, fadingMaxIntensity, fadingMinIntensity});
                break;
            case 4:
                byte blinkingSpeedAndState = (BlinkingActivateSwitch.isChecked()?(byte)1:(byte)0);
                int speed = BlinkingSpeedSeekBar.getProgress()<<1;
                blinkingSpeedAndState |= speed;
                byte maxIntensity = (byte)(BlinkingMaximumSeekBar.getProgress()+1);
                byte minIntensity = (byte)BlinkingMinimumSeekBar.getProgress();
                bluetooth.write(new byte[]{(byte)6, blinkingSpeedAndState, maxIntensity, minIntensity});
        }
        MainActivity.onOffState = 1;
    }

    private SeekBar.OnSeekBarChangeListener changedSeekBarColor1 () {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int intensity = (Intensity1.getProgress() * (Intensity1.getMax() + 1)) << 24;
                int red = (Red1.getProgress() * (Red1.getMax() + 1)) << 16;
                int green = (Green1.getProgress() * (Green1.getMax() + 1)) << 8;
                int blue = Blue1.getProgress() * (Blue1.getMax() + 1);
                ColorPreview1.setBackgroundColor(intensity + red + blue + green);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }
    private SeekBar.OnSeekBarChangeListener changedSeekBarColor2 () {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int intensity = (Intensity2.getProgress() * (Intensity2.getMax() + 1)) << 24;
                int red = (Red2.getProgress() * (Red2.getMax() + 1)) << 16;
                int green = (Green2.getProgress() * (Green2.getMax() + 1)) << 8;
                int blue = Blue2.getProgress() * (Blue2.getMax() + 1);
                ColorPreview2.setBackgroundColor(intensity + red + blue + green);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }

    private SeekBar.OnSeekBarChangeListener minBlinkingBrightnessChanged () {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (BlinkingMinimumSeekBar.getProgress() > BlinkingMaximumSeekBar.getProgress()){
                    BlinkingMaximumSeekBar.setProgress(BlinkingMinimumSeekBar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }

    private SeekBar.OnSeekBarChangeListener maxBlinkingBrightnessChanged () {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (BlinkingMinimumSeekBar.getProgress() > BlinkingMaximumSeekBar.getProgress()){
                    BlinkingMinimumSeekBar.setProgress(BlinkingMaximumSeekBar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }

    private SeekBar.OnSeekBarChangeListener minFadingBrightnessChanged () {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (FadingMinimumSeekBar.getProgress() > FadingMaximumSeekBar.getProgress()){
                    FadingMaximumSeekBar.setProgress(FadingMinimumSeekBar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }

    private SeekBar.OnSeekBarChangeListener maxFadingBrightnessChanged () {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (FadingMinimumSeekBar.getProgress() > FadingMaximumSeekBar.getProgress()){
                    FadingMinimumSeekBar.setProgress(FadingMaximumSeekBar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }

    private CompoundButton.OnCheckedChangeListener activateSwitchChangeListener () {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                for (CompoundButton s:ActivateSwitches) {
                    if (b && s != compoundButton) {
                        s.setChecked(false);
                    }
                }
            }
        };
    }
}