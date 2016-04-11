package com.example.joelbuhrman.traveljournalimages;

import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by JoelBuhrman on 16-04-03.
 */
public class CameraActivity2 extends AppCompatActivity implements SurfaceHolder.Callback {
    private android.hardware.Camera camera;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @InjectView(R.id.surfaceView)
    SurfaceView surfaceView;
    @InjectView(R.id.btn_take_photo)
    FloatingActionButton btn_take_photo;
    private SurfaceHolder surfaceHolder;
    private android.hardware.Camera.PictureCallback jpegCallback;
    private android.hardware.Camera.ShutterCallback shutterCallback;
    private String takenImagePath, date, photoFile, file_name;
    private SimpleDateFormat simpleDateFormat;
    private File picfile;
    private ImageView flip;
    private boolean frontCamera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_dema_alt2);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ButterKnife.inject(this);


        init();
        setOnClickListeners();


        jpegCallback = getJpegCallback();


    }

    private void setOnClickListeners() {
        btn_take_photo.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View view) {

                cameraImage();

            }
        });

        flip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                flipCamera();
            }
        });
    }

    private void init() {
        simpleDateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        date = simpleDateFormat.format(new Date());
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        flip = (ImageView) findViewById(R.id.flip_icon);
        frontCamera = false;
    }

    private void refreshGallery(File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);

    }

    public void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            return;
        }

        try {
            camera.stopPreview();
        } catch (Exception e) {

        }
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File getDirc() {
        File dics = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        return new File(dics, "Camera_Demo");
    }

    public void cameraImage() {
        camera.takePicture(null, null, jpegCallback);

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        try {
            //camera = android.hardware.Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            camera = android.hardware.Camera.open();
        } catch (RuntimeException e) {

        }
        Camera.Parameters parameters;
        parameters = camera.getParameters();
        parameters.setPreviewFrameRate(20);
        //parameters.setPreviewSize(352, 288);
        //camera.setParameters(parameters);
        camera.setDisplayOrientation(90);


        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        refreshCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //camera.stopPreview();
        camera.release();
        //camera = null;
    }

    public Camera.PictureCallback getJpegCallback() {
        return new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                FileOutputStream outputStream = null;
                File file_image = getDirc();
                if (!file_image.exists() && !file_image.mkdirs()) {
                    Toast.makeText(getApplicationContext(), "Can't create directory to save Image", Toast.LENGTH_SHORT).show();
                    return;
                }

                photoFile = "Cam_Demo" + date + ".jpg";
                file_name = file_image.getAbsolutePath() + "/" + photoFile;
                picfile = new File(file_name);
                try {
                    outputStream = new FileOutputStream(picfile);
                    outputStream.write(bytes);
                    outputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                }
                takenImagePath = file_name;
                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                intent.putExtra("file_name", takenImagePath);
                startActivity(intent);
                refreshGallery(picfile);
                finish();
/*
                Toast.makeText(getApplicationContext(), "Picture saved", Toast.LENGTH_SHORT).show();

                refreshCamera();
                refreshGallery(picfile);*/


            }
        };
    }

    public void flipCamera() {
        if (frontCamera) {
            camera.stopPreview();
            camera.release();
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            try {
                camera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.setDisplayOrientation(90);
            camera.startPreview();
            frontCamera = false;
        } else {
            camera.stopPreview();
            camera.release();
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            try {
                camera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.setDisplayOrientation(90);
            camera.startPreview();
            frontCamera = true;
        }
    }


}
