package com.example.nmsutton.position_tracker;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

//import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.os.Bundle;
//import android.widget.TextView;
import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
//import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.os.AsyncTask;

public class position_tracker extends Activity implements SensorEventListener {

    EditText textOut;
    TextView textIn;
    float[] accelerometer_values = new float[3];
    float[] magnetic_field_values = new float[3];
    private SensorManager mSensorManager;
    private Sensor magneticField;
    private int rate;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private SensorManager sensorManager;
    private boolean color = false;
    private View view;
    private long lastUpdate;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position_tracker);
        /* added to avoid crash
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);*/


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        rate = SensorManager.SENSOR_DELAY_GAME;

        view = findViewById(R.id.textView);
        view.setBackgroundColor(Color.GREEN);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();

        //String textOut2 = "test1";
        textOut = (EditText)findViewById(R.id.textout);
        Button buttonSend = (Button)findViewById(R.id.send);
        textIn = (TextView)findViewById(R.id.textin);
        buttonSend.setOnClickListener(buttonSendOnClickListener);

        //new MyTask().execute();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometer_values = getAccelerometer(event);
            //magnetic_field_values = getMagneticField(event);
            new MyTask().execute();
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magnetic_field_values = getMagneticField(event);
            new MyTask().execute();
        }
    }

    private float[] getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        /*float x = values[0];
        float y = values[1];
        float z = values[2];*/

        /*float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;
        if (accelationSquareRoot >= 2) //
        {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;
            Toast.makeText(this, "Device was shuffed", Toast.LENGTH_SHORT)
                    .show();
            if (color) {
                view.setBackgroundColor(Color.GREEN);
            } else {
                view.setBackgroundColor(Color.RED);
            }
            color = !color;
        }*/
        return values;
    }

    private float[] getMagneticField(SensorEvent event) {
        float[] values = event.values;
        // Movement
        /*float x = values[0];
        float y = values[1];
        float z = values[2];*/
        return values;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);
        //mSensorManager.registerListener(sensorListener, magneticField, rate);
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
        //mSensorManager.registerListener(sensorListener, magneticField, rate);
    }

    Button.OnClickListener buttonSendOnClickListener
            = new Button.OnClickListener(){

    @Override
    public void onClick(View arg0) {
        float[] empty;
        new MyTask().execute();
    }};

    private class MyTask extends AsyncTask<Void, Void, Void>{

        String textResult;

        @Override
        protected Void doInBackground(Void... params) {

            /*URL textUrl;

            try {
                textUrl = new URL("http://sites.google.com/site/androidersite/text.txt");

                BufferedReader bufferReader
                        = new BufferedReader(new InputStreamReader(textUrl.openStream()));

                String StringBuffer;
                String stringText = "";
                while ((StringBuffer = bufferReader.readLine()) != null) {
                    stringText += StringBuffer;
                }
                bufferReader.close();

                textResult = stringText;
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                textResult = e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                textResult = e.toString();
            }*/

            // TODO Auto-generated method stub
            Socket socket = null;
            DataOutputStream dataOutputStream = null;
            DataInputStream dataInputStream = null;

            try {
                socket = new Socket("10.0.0.150", 8888);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream.writeUTF(String.valueOf(accelerometer_values[0])+"\t"+String.valueOf(accelerometer_values[1])+"\t"+String.valueOf(accelerometer_values[2])+"\t"+String.valueOf(magnetic_field_values[0])+"\t"+String.valueOf(magnetic_field_values[1])+"\t"+String.valueOf(magnetic_field_values[2]));
                //textIn.setText(dataInputStream.readUTF());
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataOutputStream != null) {
                    try {
                        dataOutputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (dataInputStream != null) {
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void result) {

            //textMsg.setText(textResult);
            //textPrompt.setText("Finished!");

            super.onPostExecute(result);
        }

    }
}


