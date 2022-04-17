#include <SoftwareSerial.h>
#include <FastLED.h>

SoftwareSerial BTSerial(10, 11);// RX | TX

#define PIN_LED_OUT 13
#define LEDSTRIP_PIN  3
#define NUM_LEDS 50

//#define PIN_GATE_IN 2
#define SOUND_SENSOR_PIN A1
//#define IRQ_GATE_IN  0
#define MIN_DELAY 25

//int count;
//int state;
//int num_rec_data;
//int check_num_rec_data;
int data[100];
int led_color;
CRGB leds[NUM_LEDS];
CRGB saved_leds[NUM_LEDS];


//mode
int mode;
int led_index;
int led_delay;
int led_set_direction;
int led_curr_direction_or_option;
int max_intensity;
int min_intensity;
int fading_interval;

CRGB run_color_1[NUM_LEDS];
CRGB run_color_2[NUM_LEDS];



void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  BTSerial.begin(38400);
  //  Configure LED pin as output
  pinMode(PIN_LED_OUT, OUTPUT);

  // configure sound sensor
//  pinMode(PIN_GATE_IN, INPUT);
//  attachInterrupt(IRQ_GATE_IN, soundISR, CHANGE);

  // configure led strip
  FastLED.addLeds<WS2812, LEDSTRIP_PIN, GRB>(leds, NUM_LEDS);
  off_led(leds, NUM_LEDS);
  FastLED.show();
  led_color=0;

  //mode
  mode=-1;
  led_index=0;
  led_delay=0;
  led_set_direction=0;
  led_curr_direction_or_option=0;
  max_intensity=0;
  min_intensity=0;
  fading_interval=0;
 
  
  // Display status
  Serial.println("Initialized");
  

}
//void soundISR()
//{
//  int pin_val;
//  int sound_sensor_value;
//  
//  pin_val = digitalRead(PIN_GATE_IN);
//  digitalWrite(PIN_LED_OUT, pin_val); 
////   Serial.println(pin_val);
//  // Convert envelope value into a message
//  sound_sensor_value = analogRead(PIN_ANALOG_IN);
//  Serial.println(sound_sensor_value);
//}

void loop() {
  // put your main code here, to run repeatedly:
  
   //---------------bluetooth reading-----------------------//
   if(BTSerial.available() > 0)
   { 
      //-------data read--------//
      data[0] = BTSerial.read(); // Reads the data from the serial port
      Serial.println(data[0]);

      //--------LED control------//
      switch(data[0]){
        case 0:
              for (int i = 0; i < NUM_LEDS; i++) {
//                saved_leds[i]= CRGB(0,0,0);
                leds[i] = CRGB(0,0,0);
              }
              FastLED.show();
              mode = -1;
              break;
        case 1:
              if(led_color==1){
                for (int i = 0; i < NUM_LEDS; i++) {
                    leds[i] = saved_leds[i];
                  }
              }else{
                 for (int i = 0; i < NUM_LEDS; i++) {
                    saved_leds[i]= CRGB(0,0,255);
//                    saved_leds[i].fadeLightBy(0);
                    leds[i] = CRGB(0,0,255);
//                    leds[i].fadeLightBy(0);
                  }
                  led_color=1;
              }
              FastLED.show();
            break;
        case 2:
              {
                Serial.println("case 2:");
                while(BTSerial.available()<=0) {};
                data[1] = BTSerial.read();
                while(BTSerial.available()<=0) {};
                data[2] = BTSerial.read();
                while(BTSerial.available()<=0) {};
                data[3] = BTSerial.read();
                while(BTSerial.available()<=0) {};
                data[4] = BTSerial.read();
                while(BTSerial.available()<=0) {};
                data[5] = BTSerial.read();
                Serial.println("received data:");
                Serial.println(data[1]);
                Serial.println(data[2]);
                Serial.println(data[3]);
                Serial.println(data[4]);
                Serial.println(data[5]);
                int red = ((data[1]&240)>>4)*17;
                int blue = ((data[1]&15))*17;
                int green = ((data[2]&240)>>4)*17;
                int intensity = ((data[2]&15))*17;
                int initialized_LEDs = data[3];
                int start_index = data[4];
                int end_index = data[5];
                Serial.println("led setup:");
                Serial.println(red);
                Serial.println(blue);
                Serial.println(green);
                Serial.println(intensity);
                Serial.println(initialized_LEDs);
                Serial.println(start_index);
                Serial.println(end_index);
  
                if(led_color==0){
                  for (int i = 0; i < start_index; i++) {  
                    saved_leds[i] = CRGB(0,0,0);
                  }
                  for (int i = start_index; i < end_index; i++) {  
                    saved_leds[i] = CRGB(red,blue,green);
                    saved_leds[i].maximizeBrightness(intensity);
                  }
                  for (int i = end_index; i < NUM_LEDS; i++) {  
                    saved_leds[i] = CRGB(0,0,0);
                  }
                  led_color=1;
                }else{
                  for (int i = start_index; i < end_index; i++) {  
                    saved_leds[i] = CRGB(red,blue,green);
                    saved_leds[i].maximizeBrightness(intensity);
                  }
                  for (int i = initialized_LEDs; i < NUM_LEDS; i++) {  
                    saved_leds[i] = CRGB(0,0,0);
                  }
                }
                for(int i=0;i<NUM_LEDS;i++){
                  leds[i]=saved_leds[i];
                }
                FastLED.show();
                mode=0;
                break;
              }
        case 3://music reactive
             {
                if(led_color==1){
                  for (int i = 0; i < NUM_LEDS; i++) {
                      leds[i] = saved_leds[i];
                    }
                }else{
                   for (int i = 0; i < NUM_LEDS; i++) {
                      saved_leds[i]= CRGB(0,0,255);
                      leds[i] = CRGB(0,0,255);
                    }
                    led_color=1;
                }
                  while(BTSerial.available()<=0) {};
                  data[1] = BTSerial.read();
                 int mode_one=data[1]&1;
                 if(mode_one==1){
                    mode=1;
                    
                 }else{
                   mode =0;
                 }
                 break;
             }
        case 4://running
             {
                 Serial.println("case 4:");
                 while(BTSerial.available()<=0) {};
                  data[1] = BTSerial.read();
                  while(BTSerial.available()<=0) {};
                  data[2] = BTSerial.read();
                  while(BTSerial.available()<=0) {};
                  data[3] = BTSerial.read();
                  while(BTSerial.available()<=0) {};
                  data[4] = BTSerial.read();
                  while(BTSerial.available()<=0) {};
                  data[5] = BTSerial.read();
                  while(BTSerial.available()<=0) {};
                  data[6] = BTSerial.read();
                  Serial.println("received data:");
                  Serial.println(data[1]);
                  Serial.println(data[2]);
                  Serial.println(data[3]);
                  Serial.println(data[4]);
                  Serial.println(data[5]);
                  Serial.println(data[6]);
                 if(led_color==1){
                    for (int i = 0; i < NUM_LEDS; i++) {
                        leds[i] = saved_leds[i];
                      }
                  }else{
                     for (int i = 0; i < NUM_LEDS; i++) {
                        saved_leds[i]= CRGB(0,0,255);
                        leds[i] = CRGB(0,0,255);
                      }
                      led_color=1;
                  }
                 int mode_on = data[1]&1;
                 if(mode_on==1){
                   mode=2;
                   led_set_direction = (data[1]&2)>>1;
                   led_curr_direction_or_option=1;//right
                   led_delay=100-(data[6]&100);
                   led_index=0;
                    for (int i = 0; i < NUM_LEDS; i++) {
                       run_color_1[i] = CRGB(((data[2]&240)>>4)*17,((data[2]&15))*17,((data[3]&240)>>4)*17);
                       run_color_1[i].maximizeBrightness(((data[3]&15))*17);
                    }
                    for (int i = 0; i < NUM_LEDS; i++) {
                       run_color_2[i] = CRGB(((data[4]&240)>>4)*17,((data[4]&15))*17,((data[5]&240)>>4)*17);
                       run_color_2[i].maximizeBrightness(((data[5]&15))*17);
                    }
                 }else{
                    mode = 0;
                 }
                 break;
             }
        case 5://fading
             {
                Serial.println("case 5:");
                while(BTSerial.available()<=0) {};
                data[1] = BTSerial.read();
                while(BTSerial.available()<=0) {};
                data[2] = BTSerial.read();
                while(BTSerial.available()<=0) {};
                data[3] = BTSerial.read();
                Serial.println("received data:");
                Serial.println(data[1]);
                Serial.println(data[2]);
                Serial.println(data[3]);
                int mode_on = data[1]&1;
                if(mode_on==1){
                  mode = 3;
                  led_delay=100-(data[1]&254)>>1;
                  max_intensity= data[2];
                   Serial.println("MAX:");
                   Serial.println(max_intensity);
                  min_intensity=data[3];
                   Serial.println("MIN:");
                   Serial.println(min_intensity);
                  fading_interval=0;
                  led_curr_direction_or_option=1;
                  
                  
                }else{
                  mode = 0;  
                }
                  
             break; 
             }
        case 6://blinking
             {
                Serial.println("case 6:");
                while(BTSerial.available()<=0) {};
                data[1] = BTSerial.read();
                while(BTSerial.available()<=0) {};
                data[2] = BTSerial.read();
                while(BTSerial.available()<=0) {};
                data[3] = BTSerial.read();
                Serial.println("received data:");
                Serial.println(data[1]);
                Serial.println(data[2]);
                Serial.println(data[3]);
                int mode_on = data[1]&1;
                if(mode_on==1){
                  mode = 4;
                  led_delay=100-(data[1]&254)>>1;
                  max_intensity= data[2];
                  min_intensity=data[3];
                  
                  
                }else{
                  mode = 0;  
                }
                  
             break; 
             }
        case 7:
              {
                //voice command----color control
                Serial.println("case 7:");
                while(BTSerial.available()<=0) {};
                data[1] = BTSerial.read();
                while(BTSerial.available()<=0) {};
                data[2] = BTSerial.read();
                while(BTSerial.available()<=0) {};
                data[3] = BTSerial.read();
                
                int red = data[1];
                int green = data[2];
                int blue = data[3]; 
//                Serial.println("rgb:");
//                Serial.println(red);
//                Serial.println(green);
//                Serial.println(blue);
                for (int i = 0; i < NUM_LEDS; i++) {  
                  saved_leds[i] = CRGB(red,green,blue);
                  leds[i] = CRGB(red,green,blue);
                }
                led_color=1;
                FastLED.show();
                break;
              }
        case 8:
              {
                //voice command----brightness control
                Serial.println("case 8:");
                while(BTSerial.available()<=0) {};
                data[1] = BTSerial.read();
                Serial.println(data[1]);
                int intensity=data[1];
                for (int i = 0; i < NUM_LEDS; i++) {  
                  saved_leds[i].maximizeBrightness(intensity);
                  leds[i].maximizeBrightness(intensity);
                }
                FastLED.show();
                break;
              }
        default:
              {
                Serial.println("command not recognized.");
//                off_led(leds, NUM_LEDS);
//                FastLED.show();    
              }     
      }  
   }
       if(mode>-1){
           switch(mode){
              case 0:
                {
                      Serial.println("off mode");
                      //off mode
                      if(led_color==1){
                        Serial.println("has history");
                        for (int i = 0; i < NUM_LEDS; i++) {
                          leds[i] = saved_leds[i];
                        }
                      }else{
                        Serial.println("no history");
                         for (int i = 0; i < NUM_LEDS; i++) {
                          leds[i] = CRGB(0,0,255);//blue
                          saved_leds[i]= CRGB(0,0,255);//blue
            //              leds[i].fadeLightBy(0);
                          led_color=1;
                        }     
                      }    
                      FastLED.show();
                      mode = -1;
                    }
                    break;
              case 1:
                {
                    //music reactive
                    if(led_color==1){
//                      Serial.println("has history");
                      for (int i = 0; i < NUM_LEDS; i++) {
                        leds[i] = saved_leds[i];
                      }
                    }else{
//                      Serial.println("no history");
                       for (int i = 0; i < NUM_LEDS; i++) {
                        leds[i] = CRGB(0,0,255);//blue
                        saved_leds[i]= CRGB(0,0,255);//blue
                        led_color=1;
                      }     
                    }
                     int sound_sensor_value;
                     sound_sensor_value = analogRead(SOUND_SENSOR_PIN);
                     Serial.println(sound_sensor_value);
                     if(sound_sensor_value>50 && sound_sensor_value<=80){
                        for (int i = 0; i < NUM_LEDS; i++) {
                          if(leds[i].r==0 && leds[i].g==0 && leds[i].b == 0){
                    
                          }else{
                              leds[i].r = (leds[i].r+50)%256;
                              leds[i].g = leds[i].g;
                              leds[i].b = leds[i].b;
                          }
                        }
                      }else if(sound_sensor_value>80 && sound_sensor_value<=110){
                        for (int i = 0; i < NUM_LEDS; i++) {
                          if(leds[i].r==0 && leds[i].g==0 && leds[i].b == 0){
                    
                          }else{
                              leds[i].r = leds[i].r;
                              leds[i].g = (leds[i].g+50)%256;
                              leds[i].b = leds[i].b;
                          }
                        }
                      }else if(sound_sensor_value>110 && sound_sensor_value<=140){
                        for (int i = 0; i < NUM_LEDS; i++) {
                          if(leds[i].r==0 && leds[i].g==0 && leds[i].b == 0){
                    
                          }else{
                              leds[i].r = leds[i].r;
                              leds[i].g = leds[i].g;
                              leds[i].b = (leds[i].b+50)%250;
                          }
                        }
                      }else if(sound_sensor_value>140 && sound_sensor_value<=170){
                        for (int i = 0; i < NUM_LEDS; i++) {
                          if(leds[i].r==0 && leds[i].g==0 && leds[i].b == 0){
                    
                          }else{
                              leds[i].r = (leds[i].r+100)%256;
                              leds[i].g = (leds[i].g+100)%256;
                              leds[i].b = leds[i].b;
                          }
                        }
                        
                      }
                      else if(sound_sensor_value>170 && sound_sensor_value<=200){
                    //    Serial.println("here1");
                        for (int i = 0; i < NUM_LEDS; i++) {
                          if(leds[i].r==0 && leds[i].g==0 && leds[i].b == 0){
                    
                          }else{
                              leds[i].r = (leds[i].r+100)%256;
                              leds[i].g = leds[i].g;
                              leds[i].b = (leds[i].b+100)%256;
                          }
                        }
                      }else if(sound_sensor_value>200 && sound_sensor_value<=230){
                        for (int i = 0; i < NUM_LEDS; i++) {
                          if(leds[i].r==0 && leds[i].g==0 && leds[i].b == 0){
                    
                          }else{
                              leds[i].r = leds[i].r;
                              leds[i].g = (leds[i].g+100)%256;
                              leds[i].b = (leds[i].b+100)%256;
                          }
                        }
                      }else if(sound_sensor_value>230 && sound_sensor_value<=260){
                        for (int i = 0; i < NUM_LEDS; i++) {
                          if(leds[i].r==0 && leds[i].g==0 && leds[i].b == 0){
                    
                          }else{
                              leds[i].r = leds[i].r;
                              leds[i].g = (leds[i].g+100)%256;
                              leds[i].b = (leds[i].b+100)%256;
                          }
                        }
                      }else if(sound_sensor_value>260){
                        for (int i = 0; i < NUM_LEDS; i++) {
                          if(leds[i].r==0 && leds[i].g==0 && leds[i].b == 0){
                    
                          }else{
                              leds[i].r = (leds[i].r+200)%256;
                              leds[i].g = (leds[i].g+200)%256;
                              leds[i].b = (leds[i].b+200)%256;
                          }
                        }
                      }
                        FastLED.show();
                        delay(100);
                        break;
                  }
              case 2:
                 {
                   Serial.println("running mode");
                   //running mode
                   if(led_set_direction==1){
                      //direction: ->
                      if(led_curr_direction_or_option==1){
                        //right
                        leds[led_index]=run_color_1[led_index];
                        FastLED.show();
                        led_index++;
                        delay(led_delay+MIN_DELAY);
                        if(led_index==NUM_LEDS){
                          led_curr_direction_or_option=0;
                        }
                      }else{
                        //direction: <-
                        leds[led_index]=run_color_2[led_index];
                        FastLED.show();
                        delay(led_delay+MIN_DELAY);
                        if(led_index==0){
                          led_curr_direction_or_option=1;
                        } else {
                          led_index--;
                        }         
                      }
                      
                   }else{
                     //unidirectional
                     if(led_curr_direction_or_option==1){
                        //first color
                        leds[led_index]=run_color_1[led_index];
                        FastLED.show();
                        led_index++;
                        delay(led_delay+MIN_DELAY);
                        if(led_index==NUM_LEDS){
                          led_index=0;
                          led_curr_direction_or_option=0;
                        }
                      }else{
                        //second color
                        leds[led_index]=run_color_2[led_index];
                        FastLED.show();
                        led_index++;
                        delay(led_delay+MIN_DELAY);
                        if(led_index==NUM_LEDS){
                          led_index=0;
                          led_curr_direction_or_option=1;
                        }         
                      }
                   }
                  break;
                 }
              case 3:
                {
                  //fading mode
                  if(led_color==1){
//                    Serial.println("has history");
                    for (int i = 0; i < NUM_LEDS; i++) {
                      leds[i] = saved_leds[i];
                    }
                  }else{
//                    Serial.println("no history");
                     for (int i = 0; i < NUM_LEDS; i++) {
                      leds[i] = CRGB(0,0,255);//blue
                      saved_leds[i]= CRGB(0,0,255);//blue
        //              leds[i].fadeLightBy(0);
                      led_color=1;
                    }     
                  }
                  if(led_curr_direction_or_option==1){
                    //direction: down to up
                    for (int i = 0; i < NUM_LEDS; i++) {
                       leds[i].maximizeBrightness(min_intensity+fading_interval);
                    }
                    FastLED.show();
                    delay(led_delay+MIN_DELAY);
                    fading_interval+= (max_intensity-min_intensity)/10;
//                    Serial.print("up_value:");
//                    Serial.println(min_intensity+fading_interval);
                    if(min_intensity+fading_interval>max_intensity){
                      fading_interval=0;
                      led_curr_direction_or_option=0;
                    }
                  }else{
                    //direction: up to down
                    for (int i = 0; i < NUM_LEDS; i++) {
                       leds[i].maximizeBrightness(max_intensity-fading_interval);
                    }
                    FastLED.show();
                    delay(led_delay+MIN_DELAY);
                    fading_interval+= (max_intensity-min_intensity)/10;
//                    Serial.print("down_value:");
//                    Serial.println(max_intensity-fading_interval);
                    if(max_intensity-fading_interval<min_intensity){
                      fading_interval=0;
                      led_curr_direction_or_option=1;
                    }         
                  }     
//                  delay(1000);              
                  break;
                }
              case 4:
                {
                  //blinking mode 
                  if(led_color==1){
//                    Serial.println("has history");
                    for (int i = 0; i < NUM_LEDS; i++) {
                      leds[i] = saved_leds[i];
                    }
                  }else{
//                    Serial.println("no history");
                     for (int i = 0; i < NUM_LEDS; i++) {
                      leds[i] = CRGB(0,0,255);//blue
                      saved_leds[i]= CRGB(0,0,255);//blue
        //              leds[i].fadeLightBy(0);
                      led_color=1;
                    }     
                  }
                  
                  if(led_curr_direction_or_option==1){
                    //direction: down to up
                    for (int i = 0; i < NUM_LEDS; i++) {
                       leds[i].maximizeBrightness(max_intensity);
                    }
                    FastLED.show();
                    delay(led_delay+5*MIN_DELAY);
                    led_curr_direction_or_option=0;
                  }else{
                    //direction: up to down
                    for (int i = 0; i < NUM_LEDS; i++) {
                       leds[i].fadeLightBy(255-min_intensity);
                    }
                    FastLED.show();
                    delay(led_delay+5*MIN_DELAY);
                    led_curr_direction_or_option=1;      
                  }
                  break;
                }

              
           }
       }
   
//   //---------------sound sensor & LED strip-----------------------//
//  // Check the envelope input
//   int right_color[3], left_color[3],ledstrip_delay;
//   int rgb_color[3];
//    int sound_sensor_value;
//    sound_sensor_value = analogRead(PIN_ANALOG_IN);
////    Serial.print("Sensor value: ");
////    Serial.println(sound_sensor_value);
//    
//



  
//  FastLED.show();
//  delay(100);

  
}
void off_led(CRGB *leds, int num_leds){
  for (int i = 0; i < NUM_LEDS; i++) {
    leds[i] = CRGB(0, 0, 0);
  }
  FastLED.show();
}
