package com.jonathansteadman.paranoidcamera;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

public class CameraFragment extends Fragment {
    
    public String PHOTO_FILENAME = "";

    private Camera camera;

    private SurfaceView surfaceView;

    private View progressContainer;

    private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {

        @Override
        public void onShutter() {
            progressContainer.setVisibility(View.INVISIBLE);

        }
    };

    private Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            FileOutputStream os = null;
            boolean success = true;
            // save picture as a random #.jpg
            Random rand = new Random();
            int num = rand.nextInt();
            PHOTO_FILENAME = num + ".jpg";

            try {
                os = getActivity().openFileOutput(PHOTO_FILENAME, Context.MODE_PRIVATE);
                os.write(data);
            } catch (Exception e) {
                success = false;
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (Exception e) {
                    success = false;
                }
            }

            if (success) {
                Intent intent = new Intent();
                getActivity().setResult(Activity.RESULT_OK, intent);
            } else {
                getActivity().setResult(Activity.RESULT_CANCELED);
            }
            getActivity().finish();
            
            
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            camera = Camera.open(0);
        } else {
            camera = Camera.open();
        }
        Camera.Parameters params = camera.getParameters();
        List<String> supportedFocusModes = params.getSupportedFocusModes();
        // Only set continuous focus if we're on ice cream sandwich or later AND
        // our phone supports it
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
                && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                params.setRotation(90);
                camera.setDisplayOrientation(90);
            }
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                params.setRotation(0);
                camera.setDisplayOrientation(0);
            }

            camera.setParameters(params);

        } else {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                params.set("orientation", "portrait");
                params.set("rotation", 90);
            }
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                params.set("orientation", "landscape");
                params.set("rotation", 0);
            }
        }
    }

    @Override
    public void onPause() {

        super.onPause();
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        progressContainer = view.findViewById(R.id.progress_container);
        progressContainer.setVisibility(view.INVISIBLE);

        /*
        Button takePictureButton = (Button)view.findViewById(R.id.camera_button);
        takePictureButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                camera.takePicture(shutterCallback, null, jpegCallback);
            }
        });*/

        initSurfaceView(view);
        return view;
    }

    private void initSurfaceView(View view) {
        surfaceView = (SurfaceView)view.findViewById(R.id.camera_surface_view);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (camera != null) {
                        camera.setPreviewDisplay(holder);
                    }
                } catch (IOException ioe) {
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (camera != null) {
                    camera.stopPreview();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                if (camera == null)
                    return;

                Parameters parameters = camera.getParameters();
                //Size size = getBestSupportedSize(parameters.getSupportedPreviewSizes(), width,
                        //height);
                //parameters.setPreviewSize(size.width, size.height);
                parameters.setPreviewSize(1, 1);

                Size size = getBestSupportedSize(parameters.getSupportedPictureSizes(), width, height);
                parameters.setPictureSize(size.width, size.height);

                try {
                    camera.startPreview();
                    camera.takePicture(shutterCallback, null, jpegCallback);
                    
                } catch (Exception e) {
                    camera.release();
                    camera = null;
                }
            }
        });
    }

    private Size getBestSupportedSize(List<Size> sizes, int width, int height) {
        Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;

        for (Size size : sizes) {
            int area = size.width * size.height;

            if (area > largestArea) {
                bestSize = size;
                largestArea = area;
            }
        }
        return bestSize;
    }

}
