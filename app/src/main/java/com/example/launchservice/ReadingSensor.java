package com.example.launchservice;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */


public class ReadingSensor extends IntentService implements SensorEventListener {


    private static final int SERVERPORT = 60005;
    private static final String SERVER_IP = "10.0.2.2";
    private Socket socket;
    private SensorManager sensorManager;
    private Sensor mSensor;

    public boolean sensorRunning = true;
    @Override
    public final void onSensorChanged(SensorEvent event) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        if (sensorRunning) {
            JSONObject json = new JSONObject();
            try {

                json.put("accel_values", "[" + event.values[0] + ", " + event.values[1] + ", " + event.values[2] + "]");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Do something with this sensor value.
            new Thread(new NetworkThread(json.toString())).start();
        }else{
            stopSelf();
        }

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    public class LocalBinder extends Binder {
        ReadingSensor getService(){
            return ReadingSensor.this;
        }
    }
    private final LocalBinder binder =new LocalBinder();

    @Override
    public IBinder onBind(Intent arg0){return binder;}

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "com.example.launchservice.action.FOO";
    private static final String ACTION_BAZ = "com.example.launchservice.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.launchservice.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.launchservice.extra.PARAM2";

    public ReadingSensor() {
        super("ReadingSensor");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, ReadingSensor.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, ReadingSensor.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) { //meter mensaje de trazabilidad
        new Thread(new ClientThread()).start();
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            } else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = null;

       /* if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null){
            List<Sensor> gravSensors = sensorManager.getSensorList(Sensor.TYPE_GRAVITY);
            for(int i=0; i<gravSensors.size(); i++) {
                if ((gravSensors.get(i).getVendor().contains("Google LLC")) &&
                        (gravSensors.get(i).getVersion() == 3)){
                    // Use the version 3 gravity sensor.
                    mSensor = gravSensors.get(i);
                }
            }
        }*/
        //if (mSensor == null){
            // Use the accelerometer.
            if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
                mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            } else{
                // Sorry, there are no accelerometers on your device.
                // You can't play this game.
            }

     //   }

        sensorManager.registerListener(this, mSensor, sensorManager.SENSOR_DELAY_NORMAL);


    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
    class ClientThread implements Runnable {

        @Override
        public void run() {
            try {
                InetAddress serverAd = InetAddress.getByName(SERVER_IP);

                socket = new Socket(serverAd, SERVERPORT);

            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    class NetworkThread implements Runnable {

        private String message;

        public NetworkThread(String message) {
            this.message = message;
        }

        @Override
        public void run() {
            try {
                PrintWriter out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())),
                        true);
                out.println(message);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
