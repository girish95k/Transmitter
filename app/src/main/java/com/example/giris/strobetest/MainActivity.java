package com.example.giris.strobetest;
import java.math.BigInteger;
import java.util.Arrays;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.security.Policy;

public class MainActivity extends AppCompatActivity {

    Button sendButton;
    EditText editText;

    long onDurations[];
    long offDurations[];
    int ons=0;
    int offs=0;
    long averageOnDuration;
    long averageOffDuration;

    Button button;
    private Camera camera;
    private boolean isFlashOn;
    private boolean hasFlash;
    Parameters params;

    long totalTime = 0;
    //String inputBits = "11111111111111111111111111111111111111111111111111111111111111111110111101011010100111111111111111111111111111101001001001001111100110100111111101010011110101001010101001010100101010101010101010100111010101010011";
    String input = "Hello world!";
    String inputBits="";
    int current = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Transmitter");

        editText = (EditText)findViewById(R.id.inputString);

        sendButton = (Button)findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            Log.e("input", editText.getText().toString());
            String inputChars[] = editText.getText().toString().split("(?!^)");

            onDurations = new long[1000];
            offDurations = new long[1000];

            //String inputBits = "101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010101010";
            //String inputBits = "00101010000000000000111011000000000000100101000000000000111100000000000000110101000000000000001101000000000000111111000000000000";
            //String inputBits = "000000000000101010000000111100000000110101000000010001000000111110000000";
            //String inputBits = "10101110001010100000100111101011101010001001010111010100000011111101010101011111010010101111010111010100110100110010111111";
            // = new BigInteger(input.getBytes()).toString(2);

            String inputBits = "00";

            for(int i = 0; i<inputChars.length; i++) {

                inputBits += new BigInteger(inputChars[i].getBytes()).toString(2);
                inputBits += "000000000000";
                //inputBits += new BigInteger("e".getBytes()).toString(2);
            }
            /*
            for(int i=0; i<inputBitsTemp.length(); i++)
            {
                if(inputBitsTemp.charAt(i)=='1')
                    inputBits+="1010";
                else
                    inputBits+="1001";
            }*/


            Log.e("input", inputBits);
            getCamera();

            long[] samples = new long[100_000];
            int pauseInMicros = 1000000;

            for (int i = 0; i < samples.length; i++) {

                Log.d("-----", "start");
                long totalTimeInLoop = 0;


                long firstTime = System.nanoTime();
                //busyWaitMicros(pauseInMicros);
                long timeForNano = System.nanoTime() - firstTime;
                samples[i] = timeForNano;
                //Log.e("elapsed time", timeForNano + "");
                Log.d("flash status", isFlashOn+"");
                totalTime+=timeForNano;

                boolean flashOperation = false;
                long flashStart = System.nanoTime();
                if(inputBits.charAt(current)=='1')
                {
                    if (isFlashOn) {
                        //turnOffFlash();
                        //button.setText("ON");
                        busyWaitMicros(pauseInMicros);
                        totalTimeInLoop+=pauseInMicros;
                    } else {
                        busyWaitMicros(pauseInMicros-25000);
                        turnOnFlash();
                        //button.setText("OFF");
                        flashOperation = true;
                    }
                }
                if(inputBits.charAt(current)=='0')
                {
                    if (isFlashOn) {
                        busyWaitMicros(pauseInMicros-17000);
                        turnOffFlash();
                        //button.setText("ON");
                        flashOperation = true;
                    } else {
                        //turnOnFlash();
                        //button.setText("OFF");
                        busyWaitMicros(pauseInMicros);
                        totalTimeInLoop+=pauseInMicros;
                    }
                }

                long flashEnd = System.nanoTime();
                //Log.e("flash operation", (flashEnd - flashStart)+"");
                Log.e("flash operation", (long)((flashEnd-flashStart)*(0.001))+"");
                /*
                if(flashOperation)
                {
                    busyWaitMicros(40000 - (long)((flashEnd-flashStart)*(0.001)));
                    totalTimeInLoop+=40000 - (long)((flashEnd-flashStart)*(0.001));
                    flashOperation = false;
                    Log.e("in if", 40000 - (long)((flashEnd-flashStart)*(0.001))+"");
                }
                */
                current++;
                if(current==inputBits.length())
                {
                    turnOffFlash();
                    finish();
                    break;
                    //current=0;
                    //Log.d("time taken for cycle", totalTime+"");
                    //Log.d("length", inputBits.length()+"");
                    //totalTime = 0;
                }
                /*
                if (isFlashOn) {
                    turnOffFlash();
                    //button.setText("ON");
                } else {
                    turnOnFlash();
                    //button.setText("OFF");
                }
                */
                Log.e("in loop", i+"   :   "+samples[i]);
                Log.e("total time", totalTimeInLoop+"");
                //Log.e("time", samples[i]+"");
            }

            //System.out.printf("Time for micro busyWait %.0f\n", Arrays.stream(samples).average().getAsDouble());
            /*
            button = (Button) findViewById(R.id.button);

            hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

            if(!hasFlash) {

                AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
                alert.setTitle("Error");
                alert.setMessage("Sorry, your device doesn't support flash light!");
                alert.setButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                alert.show();
                return;
            }

            getCamera();

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isFlashOn) {
                        turnOffFlash();
                        button.setText("ON");
                    } else {
                        turnOnFlash();
                        button.setText("OFF");
                    }

                }
            });
            */

            }
        });
    }

    private void getCamera() {

        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            }catch (Exception e) {

            }
        }

    }

    private void turnOnFlash() {

        long startTime = System.nanoTime();
        if(!isFlashOn) {
            if(camera == null || params == null) {
                return;
            }

            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isFlashOn = true;
        }
        long turnOnTime = System.nanoTime()-startTime;
        onDurations[ons++] = turnOnTime;
        long avg = 0;
        int t = 1;
        for (int i = 0; i<ons; i++) {
            avg += (onDurations[i] - avg) / t;
            ++t;
        }
        averageOnDuration = avg;
        Log.e("turn on average", avg+"");
        Log.e("turn on time", turnOnTime+"");
    }

    private void turnOffFlash() {

        long startTime = System.nanoTime();
        if (isFlashOn) {
            if (camera == null || params == null) {
                return;
            }

            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;
        }
        long turnOffTime = System.nanoTime()-startTime;
        offDurations[offs++] = turnOffTime;
        long avg = 0;
        int t = 1;
        for (int i = 0; i<offs; i++) {
            avg += (offDurations[i] - avg) / t;
            ++t;
        }
        averageOffDuration = avg;
        Log.e("turn off average", avg+"");
        Log.e("turn off time", (System.nanoTime()-startTime)+"");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // on pause turn off the flash
        turnOffFlash();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // on resume turn on the flash
        if(hasFlash)
            turnOnFlash();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // on starting the app get the camera params
        getCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // on stop release the camera
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }



    public static void busyWaitMicros(long micros){
        long waitUntil = System.nanoTime() + (micros * 1_000);
        while(waitUntil > System.nanoTime()){
            ;
        }
    }

}