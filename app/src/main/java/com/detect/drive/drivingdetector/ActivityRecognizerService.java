package com.detect.drive.drivingdetector;

import android.app.IntentService;
import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

/**
 * Created by hp on 03/03/2018.
 */

public class ActivityRecognizerService extends IntentService {

    public ActivityRecognizerService(){
        super("ActivityRecognizerService");
    }
    public ActivityRecognizerService(String name){
        super(name);
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)){
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivity(result.getProbableActivities());
        }
    }

    private void handleDetectedActivity(List<DetectedActivity> probableActivities){
        Intent intent = new Intent("activity-event");

        for(DetectedActivity activity:probableActivities){
            switch(activity.getType()){
                case DetectedActivity.IN_VEHICLE:{
                    Log.d("test","handledDetectedActivity: IN_VEHICLE "+activity.getConfidence());
                    intent.putExtra("msg","IN_VEHICLE : "+activity.getConfidence());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    break;
                }
                case DetectedActivity.ON_BICYCLE:{
                    Log.d("test","handledDetectedActivity: ON_BICYCLE "+activity.getConfidence());
                    intent.putExtra("msg","ON_BICYCLE : "+activity.getConfidence());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    break;
                }
                case DetectedActivity.ON_FOOT:{
                    Log.d("test","handledDetectedActivity: ON_FOOT "+activity.getConfidence());
                    intent.putExtra("msg","ON_FOOT : "+activity.getConfidence());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    break;
                }
                case DetectedActivity.RUNNING:{
                    Log.d("test","handledDetectedActivity: RUNNING "+activity.getConfidence());
                    intent.putExtra("msg","RUNNING : "+activity.getConfidence());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    break;
                }
                case DetectedActivity.STILL:{
                    Log.d("test","handledDetectedActivity: STILL "+activity.getConfidence());
                    intent.putExtra("msg","STILL : "+activity.getConfidence());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    break;
                }
                case DetectedActivity.WALKING:{
                    Log.d("test","handledDetectedActivity: WALKING "+activity.getConfidence());
                    intent.putExtra("msg","WALKING : "+activity.getConfidence());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    break;
                }
                case DetectedActivity.TILTING:{
                    Log.d("test","handledDetectedActivity: TILTING "+activity.getConfidence());
                    intent.putExtra("msg","TILTING : "+activity.getConfidence());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    break;
                }
                case DetectedActivity.UNKNOWN:{
                    Log.d("test","handledDetectedActivity: UNKNOWN "+activity.getConfidence());
                    intent.putExtra("msg","UNKNOWN : "+activity.getConfidence());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    break;
                }
                default:{
                    Log.d("test","handledDetectedActivity: DEFAULT "+activity.getConfidence());
                    intent.putExtra("msg","DEFAULT : "+activity.getConfidence());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                    break;
                }
            }
        }
    }
}
