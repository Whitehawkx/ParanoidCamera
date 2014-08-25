
package com.jonathansteadman.paranoidcamera;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;


public class DeviceAdmin extends DeviceAdminReceiver {

    public MainActivity main;
    
    DevicePolicyManager mDPM;

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
    }

    @SuppressWarnings("null")
    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        super.onPasswordFailed(context, intent);
        // Get the number of unlock attempts. WHen it is greater than or equal to ONE, 
        // call the method in MainActivity to start the camera
        mDPM = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (mDPM.getCurrentFailedPasswordAttempts() >= 1) {
            System.out.println("Eureka!");
            main.passFail();
        }
        
        
    }
    
}
