package com.detect.drive.drivingdetector;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SensorEventListener{

    public GoogleApiClient apiClient;
    private String xMessage = "";

    private static final int ADMIN_INTENT = 15;
    private static final String description = "Allow Driver Detector to use phone locking feature.";
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mComponentName;
    private SensorManager sensorManager;
    private Sensor magsensor;
    private double magnitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiClient = new GoogleApiClient.Builder(MainActivity.this).
                addApi(ActivityRecognition.API).
                addConnectionCallbacks(MainActivity.this).
                addConnectionCallbacks(MainActivity.this).
                build();
        apiClient.connect();

        sensorManager=(SensorManager)getSystemService(SENSOR_SERVICE);
        magsensor=sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(nMessageReceiver, new IntentFilter("activity-event"));


        mDevicePolicyManager = (DevicePolicyManager)getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        mComponentName = new ComponentName(this, AdminReceiver.class);
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,description);
        startActivityForResult(intent, ADMIN_INTENT);
    }

    private BroadcastReceiver nMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("msg");
            String[] strArr = message.split(" : ");
            if(!xMessage.equals(message)) {
                xMessage = message;
                message = Calendar.getInstance().getTime() +" : "+ message;
                TextView txt1 = (TextView) findViewById(R.id.txt1);
                txt1.setMovementMethod(new ScrollingMovementMethod());
                txt1.setText(txt1.getText() + message + "\n");
            }
            if(message.contains("VEHICLE")){
                TextView txt1 = (TextView) findViewById(R.id.txt1);
                txt1.setMovementMethod(new ScrollingMovementMethod());
                txt1.setText(txt1.getText() +"<"+strArr[0] +"><"+strArr[1]+"><"+magnitude+">\n");
            }
            if(strArr[0].equals("IN_VEHICLE") && (Integer.parseInt(strArr[1]) > 40) && magnitude > 70){
                lockNow();
                displayNotification();
            }
        }
    };

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(nMessageReceiver);
        super.onDestroy();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Intent intent = new Intent(MainActivity.this, ActivityRecognizerService.class);
        PendingIntent pendingIntent = PendingIntent.getService(MainActivity.this,0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(apiClient,2000,pendingIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x=event.values[0];
        float y=event.values[1];
        float z=event.values[2];
        magnitude = Math.sqrt((x * x) + (y * y) + (z * z));

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADMIN_INTENT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Registered As Admin", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), "Failed to register as Admin", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void lockNow(){
        boolean isAdmin = mDevicePolicyManager.isAdminActive(mComponentName);
        if (isAdmin) {
            mDevicePolicyManager.lockNow();
        }else{
            Toast.makeText(getApplicationContext(), "Not Registered as admin", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayNotification(){
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(MainActivity.this).
                setSmallIcon(android.R.drawable.stat_notify_error).
                setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher)).
                setContentTitle("Phone Locked").
                setContentText("Your phone has been locked because you we're driving");
        notificationBuilder.setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS|Notification.DEFAULT_VIBRATE);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
        notificationManagerCompat.notify(1,notificationBuilder.build());

    }

    @Override
    protected void onResume() {
        sensorManager.registerListener(this,magsensor,SensorManager.SENSOR_DELAY_NORMAL);

        super.onResume();
    }


    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }
}
