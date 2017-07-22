package com.herokuapp.nmsutton.mickaleoapp2;

//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

/***************/

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.io.ObjectInputStream;
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
import android.widget.ImageView;
import android.widget.Toast;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.os.AsyncTask;

public class MickaleoApp extends Activity implements SensorEventListener {

    private static final String TAG = "MainActivity";

    static {
      if(!OpenCVLoader.initDebug()) {
          Log.d(TAG, "OpenCV not loaded");
      } else {
          Log.d(TAG, "OpenCV loaded");
      }
    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mickaleo_app);
    }*/

    //EditText textOut;
    TextView textIn;
    TextView socket_data;
    ImageView imageView1;
    float[] accelerometer_values = new float[3];
    float[] magnetic_field_values = new float[3];
    private SensorManager mSensorManager;
    private Sensor magneticField;
    private int rate;
    private String transmission_values_text;
    String data_received = "";

    // Used to load the 'native-lib' library on application startup.
    /*static {
        System.loadLibrary("native-lib");
    }*/

    private SensorManager sensorManager;
    private boolean color = false;
    private View view;
    private long lastUpdate;
    BufferedReader is2 = null;
    String data_transmitted = "";
    Mat image_transmitted = Mat.zeros(100,400, CvType.CV_8UC3);
    //IplImage image_transmitted2 = IplImage.create(w, h, 8, 4);
    //ImageIO.read();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mickaleo_app);
        /* added to avoid crash
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);*/


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        rate = SensorManager.SENSOR_DELAY_GAME;

        /*view = findViewById(R.id.textView);
        view.setBackgroundColor(Color.GREEN);*/

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lastUpdate = System.currentTimeMillis();

        //String textOut2 = "test1";
        //textOut = (EditText)findViewById(R.id.textout);
        textIn = (TextView)findViewById(R.id.textin);
        socket_data = (TextView)findViewById(R.id.socket_data);
        Button button_enable = (Button)findViewById(R.id.button_enable);
        button_enable.setOnClickListener(buttonEnableOnClickListener);
        Button button_disable = (Button)findViewById(R.id.button_disable);
        button_disable.setOnClickListener(buttonDisableOnClickListener);

        imageView1 = (ImageView) findViewById(R.id.imageView1);

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
        return values;
    }

    private float[] getMagneticField(SensorEvent event) {
        float[] values = event.values;
        return values;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /*
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
    */

    void enable_movement_tracking() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    void disable_movement_tracking() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    Button.OnClickListener buttonEnableOnClickListener
            = new Button.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            //float[] empty;
            //new MyTask().execute();
            enable_movement_tracking();
        }};

    Button.OnClickListener buttonDisableOnClickListener
            = new Button.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            //float[] empty;
            //new MyTask().execute();
            disable_movement_tracking();
        }};


    private class MyTask extends AsyncTask<Void, Void, Void>{

        String textResult;

        @Override
        protected Void doInBackground(Void... params) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    socket_data.setText(data_transmitted);

                    // make a mat and draw something
                    //Mat m = Mat.zeros(100,400, CvType.CV_8UC3);
                    Mat m = image_transmitted;
                    //Core.putText(m, "hi there ;)", new Point(30,80), Core.FONT_HERSHEY_SCRIPT_SIMPLEX, 2.2, new Scalar(200,200,0),2);
                    //m = imread("/sdcard/inna7_edit.jpg", CV_LOAD_IMAGE_COLOR);

                    // convert to bitmap:
                    Bitmap bm = Bitmap.createBitmap(m.cols(), m.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(m, bm);

                    // find the imageview and draw it!
                    ImageView iv = (ImageView) findViewById(R.id.imageView1);
                    iv.setImageBitmap(bm);
                }

                //public void helloworld() {

                //}
            });

            new Thread() {
                public void run() {

                    Socket socket = null;
                    //Socket socket_in = null;
                    DataOutputStream dataOutputStream = null;
                    //DataInputStream dataInputStream = null;

                    try

                    {
                        //data_transmitted = "loading data";
                        socket = new Socket("10.0.0.150", 8888);
                        //socket_in = new Socket("10.0.0.150", 8887);

                        dataOutputStream = new DataOutputStream(socket.getOutputStream());
                        transmission_values_text = String.valueOf(accelerometer_values[0]) + "\n"
                                + String.valueOf(accelerometer_values[1]) + "\n"
                                + String.valueOf(accelerometer_values[2]) + "\n"
                                + String.valueOf(magnetic_field_values[0]) + "\n"
                                + String.valueOf(magnetic_field_values[1]) + "\n"
                                + String.valueOf(magnetic_field_values[2]);
                        dataOutputStream.writeUTF(transmission_values_text);

                        //data_transmitted = "loading data2";
                        //is2 = new BufferedReader(new InputStreamReader(dataInputStream));
                        //data_transmitted = "data transmitted: " + is.readLine();
                        //InputStreamReader is3 = new InputStreamReader(dataInputStream);
                        //data_received = String.valueOf(is3.read());

                        /*dataInputStream = new DataInputStream(socket_in.getInputStream());
                        data_transmitted = "data transmitted: ";
                        data_received = dataInputStream.readUTF();
                        data_transmitted = data_transmitted + data_received;*/

                        //doInBackground();
                        //runOnUiThread();

                        //dataOutputStream.writeUTF(String.valueOf(accelerometer_values[0])+"\t"+String.valueOf(accelerometer_values[1])+"\t"+String.valueOf(accelerometer_values[2])+"\t"+String.valueOf(magnetic_field_values[0])+"\t"+String.valueOf(magnetic_field_values[1])+"\t"+String.valueOf(magnetic_field_values[2])+"\ty-position:\t"+(1920*(accelerometer_values[2]+9.81)/19.62)+"\tx-position:\t"+(1080*(1-((magnetic_field_values[1]+45)/90))));
                        //dataOutputStream.writeUTF(String.valueOf("\n "));
                    } catch (
                            UnknownHostException e
                            )

                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (
                            IOException e
                            )

                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } finally

                    {
                        if (socket != null) {
                            try {
                                socket.close();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                        /*if (socket_in != null) {
                            try {
                                socket_in.close();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }*/

                        if (dataOutputStream != null) {
                            try {
                                dataOutputStream.close();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                        /*if (dataInputStream != null) {
                            try {
                                dataInputStream.close();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }*/
                    }
                }
            }.start();

            new Thread() {
                public void run() {
                    Socket socket_in = null;
                    DataInputStream dataInputStream = null;

                    try
                    {
                        socket_in = new Socket("10.0.0.150", 8887);

                        dataInputStream = new DataInputStream(socket_in.getInputStream());
                        data_transmitted = "data transmitted: ";
                        data_received = dataInputStream.readUTF();
                        data_transmitted = data_transmitted + data_received;
                        /*ObjectInputStream in = new ObjectInputStream(socket_in.getInputStream());
                        image_transmitted = (Mat) in.readObject();*/

                    } catch (
                            UnknownHostException e
                            )

                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (
                            IOException e
                            )

                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } /*catch (
                            ClassNotFoundException e
                            )
                    {
                        e.printStackTrace();
                    }*/
                    finally

                    {

                        if (socket_in != null) {
                            try {
                                socket_in.close();
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
                }
            }.start();

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
