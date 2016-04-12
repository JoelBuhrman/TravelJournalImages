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
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by JoelBuhrman on 16-04-03.
 */
public class CameraActivity2 extends AppCompatActivity implements SurfaceHolder.Callback {
    private android.hardware.Camera camera;
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
    private ImageView flip, flash, flashShadow;
    // Flash modes supported by this camera
    private List<String> mSupportedFlashModes;
    private boolean frontCamera, autoFlashActivated;
    FileOutputStream outputStream;
    File file_image;
    Intent intent, intent2;




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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    /*
    Detektera klick på flash och flip
     */
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

        flash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFlashMode();
            }
        });


    }


    /*
    Tilldela alla komponenter
     */
    private void init() {
        simpleDateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        date = simpleDateFormat.format(new Date());
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        flip = (ImageView) findViewById(R.id.flip_icon);
        frontCamera = false;
        flash = (ImageView) findViewById(R.id.flash_icon);
        flashShadow = (ImageView) findViewById(R.id.flash_shadow);


        outputStream = null;
        intent = new Intent(getApplicationContext(), MainActivity2.class);
        intent2 = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

    }


    /*
    Spara till fotogalleriet
     */
    private void refreshGallery(File file) {

        intent2.setData(Uri.fromFile(file));
        sendBroadcast(intent2);


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


    /*
    Hämta filen som skapats
     */
    private File getDirc() {
        File dics = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        return new File(dics, "Travel Journey");
    }

    /*
    Helt enkelt ta bild
     */
    public void cameraImage() {
        camera.takePicture(null, null, jpegCallback);

    }

    /*
    Sätt inställningar till vår surfaceview (previewskärmen)
     */
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        try {
            //camera = android.hardware.Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            camera = android.hardware.Camera.open();
            mSupportedFlashModes = camera.getParameters().getSupportedFlashModes();




            // Set the camera to Auto Flash mode.
            if (mSupportedFlashModes != null && mSupportedFlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                camera.setParameters(parameters);
                autoFlashActivated = true;
            }
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

    /*
    När något ändras, tex om man kunde flipat skärmen.
     */
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        refreshCamera();
    }


    /*
    Om kameran lämnas
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //camera.stopPreview();
        camera.release();
        //camera = null;
    }


    /*
    skapa filen osv
     */
    public Camera.PictureCallback getJpegCallback() {
        return new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {

                file_image = getDirc();
                if (!file_image.exists() && !file_image.mkdirs()) {
                    Toast.makeText(getApplicationContext(), "Can't create directory to save Image", Toast.LENGTH_SHORT).show();
                    return;
                }

                photoFile = "Travel Journey" + date + ".jpg";
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
                intent.putExtra("file_name", takenImagePath);
                Toast.makeText(getApplicationContext(), takenImagePath, Toast.LENGTH_LONG).show();
                startActivity(intent);
                refreshGallery(picfile);


            }
        };
    }


    /*
    flipa kameran vid klick på flip
     */
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

    /*
    Sätt flash till off eller auto
     */
    private void setFlashMode() {
        if (autoFlashActivated) {
            if (mSupportedFlashModes != null && mSupportedFlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(parameters);
                autoFlashActivated = false;

                flash.setImageResource(R.drawable.vector_drawable_ic_flash_off_white___px);
                flashShadow.setImageResource(R.drawable.vector_drawable_ic_flash_off_black___px);
            }

        } else {

            if (mSupportedFlashModes != null && mSupportedFlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                Camera.Parameters parameters = camera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                camera.setParameters(parameters);
                autoFlashActivated = true;
                flash.setImageResource(R.drawable.vector_drawable_ic_flash_auto_white___px);
                flashShadow.setImageResource(R.drawable.vector_drawable_ic_flash_auto_black___px);
            }

        }
    }


}
