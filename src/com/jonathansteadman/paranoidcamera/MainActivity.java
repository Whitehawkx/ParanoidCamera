
package com.jonathansteadman.paranoidcamera;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainActivity extends Activity implements OnCheckedChangeListener {

    static final String TAG = "DevicePolicy";

    static final int ACTIVATION_REQUEST = 47; // identifies request id

    DevicePolicyManager mDPM;

    ComponentName mDeviceAdmin;

    CheckBox adminCheckBox;

    CheckBox cameraCheckBox;
    
    public static final int REQUEST_PHTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up admin checkbox
        adminCheckBox = (CheckBox)super.findViewById(R.id.enable_admin);
        adminCheckBox.setOnCheckedChangeListener(this);

        // set up camera check box
        cameraCheckBox = (CheckBox)super.findViewById(R.id.enable_photos);
        cameraCheckBox.setOnCheckedChangeListener(this);

        // Initialize Device Policy Manager service and our receiver class
        mDPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdmin = new ComponentName(this, DeviceAdmin.class);
        
        //passFail();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == adminCheckBox) {
            if (isChecked) {
                // Activate device administration
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "This is required!");
                startActivityForResult(intent, ACTIVATION_REQUEST);
            } else {
                mDPM.removeActiveAdmin(mDeviceAdmin);
                isChecked = false;
            }
            Log.d(TAG, "onCheckedChanged to: " + isChecked);
        }
        // on check functionality for camera check box
        if (buttonView == cameraCheckBox) {
            Intent intent = new Intent(this, CameraActivity.class);
            startActivity(intent);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVATION_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Log.i(TAG, "Administration enabled!");
                    adminCheckBox.setChecked(true);
                } else {
                    Log.i(TAG, "Administration enable FAILED!");
                    adminCheckBox.setChecked(false);
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // method to start camera activity, which immediately takes a picture
    public void passFail() {
        System.out.println("booya");
        Intent intent = new Intent(this, CameraFragment.class);
        startActivity(intent);
    }
}
