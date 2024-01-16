package com.example.myapplication.FaceMesh;

import android.content.Context;
import android.content.Intent;

public class ActivityUtils {

    public static void restartActivity(Context context) {
        Intent intent = new Intent(context, FaceMeshActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        // Assuming this method is called from an activity
        // If not, you might need to pass the reference to the activity and call finish() on it
        // ((Activity) context).finish();
    }
}
