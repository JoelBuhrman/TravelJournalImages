package com.example.joelbuhrman.traveljournalimages;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by JoelBuhrman on 16-04-05.
 */
public class MainActivity2 extends Activity {
    private static int NEW_IMAGE = 0;
    private static int OLD_IMAGE = 1;
    CustomDialogCommandsClass cdcc;
    CustomDialogDeleteClass cddc;
    private int state;
    private ImageView image;
    private ImageButton mic, info, cancelDelete;
    private FloatingActionButton actionA, actionB, actionC, actionD;
    private EditText editText;
    private FloatingActionsMenu menuMultipleActions;
    private boolean expanded;
    SimpleDateFormat df;
    String formattedDate;
    private RelativeLayout relativeLayout, mainLayout;
    private TextView tDescription, date, cityName, hide, yesDelete, noDelete;
    File imgFile;
    ResizeAnimation expandAnimation, collapsAnimation;
    protected static final int RESULT_SPEECH = 1;
    private String descriptionText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_alt2);

        init();
        setOnClickListeners();


    }


    private void setOnClickListeners() {
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performExpandOrCollapse();


            }
        });

        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });

        actionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CameraActivity2.class));
                Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        actionD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (!isFinishing()) {
                            new AlertDialog.Builder(MainActivity2.this)
                                    .setTitle("Delete Image?")
                                    .setMessage("Are you sure you want to delete this image?")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            deleteImage(imgFile);
                                            startCamera();
                                            finish();

                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).create().show();
                        }
                    }
                });

                //cddc.show();

            }
        });


        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performExpandOrCollapse();
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                cdcc.show();
            }
        });


    }

    /*
    Delete en fil
     */
    private void deleteImage(File file) {
        File f = new File(String.valueOf(file));
        Toast.makeText(getApplicationContext(), "Deleted!", Toast.LENGTH_SHORT).show();
        getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Images.Media.DATA + "=?", new String[]{f.toString()});

    }


    /*
    Metod som tar emot speechInput
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");


        try {
            startActivityForResult(intent, RESULT_SPEECH);

        } catch (ActivityNotFoundException a) {
            Toast t = Toast.makeText(getApplicationContext(),
                    "Opps! Your device doesn't support Speech to Text",
                    Toast.LENGTH_SHORT);
            t.show();
        }
    }

    /*
    Metoden som tar emot resultatet från Speech
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    String s = handleSpeech(text.get(0));
                    if (s.equals("delete")) {
                        editText.setText("");

                    } else {


                        editText.setText(editText.getText().toString() + handleSpeech(text.get(0)));
                    }
                }
                break;
            }

        }
    }

    /*
    Metod för att hantera så att tal-till-text ska skriva ut punkter osv
     */
    private String handleSpeech(String descriptionText) {

        /**
         * Tar bort null från början och sätter första till stor bokstav
         */

        if (descriptionText.contains("Radera allt") || descriptionText.contains("radera allt")) {
            return "delete";
        } else {
            String temp = descriptionText;
            //temp= handleFirstLetter(temp);

            temp = temp.replaceAll(" frågetecken", "?");
            temp = temp.replaceAll(" punkt", ".");
            temp = temp.replaceAll(" utropstecken", "!");
            temp = temp.replaceAll(" kommatecken", ",");

            return handleCapitalLetters(temp);
        }

    }

    /*
    Metod som sätter stora bokstäver vid ny mening
     */
    private String handleCapitalLetters(String s) {
        String temp = s;
        char first = s.charAt(0);
        first = Character.toUpperCase(first);
        temp = first + temp.substring(1, temp.length());

        StringBuilder sb = new StringBuilder();
        sb.append(temp);

        for (int i = 2; i < sb.length() - 1; i++) {
            char c = temp.charAt(i - 2);
            char t = temp.charAt(i);

            if (c == '.' || c == '!' || c == '?') {
                t = Character.toUpperCase(t);

                sb.replace(i, i + 1, String.valueOf(t));
            }
        }


        return sb.toString();
    }

    /*
    Metoden som antingen collapsar eller expandar
     */
    private void performExpandOrCollapse() {

        if (expanded) {
            relativeLayout.startAnimation(collapsAnimation);
            expanded = false;
        } else {
            relativeLayout.startAnimation(expandAnimation);
            expanded = true;

        }
    }

    /*
    tilldelar alla komponenter värden
     */
    private void init() {

        /*
        Om det ska finnas ett state
         */
        state = NEW_IMAGE;


         /*
        MainBilden med dess komponenter
         */
        date = (TextView) findViewById(R.id.chosen_image_date);
        Calendar c = Calendar.getInstance();
        df = new SimpleDateFormat("dd-MMM-yyyy");
        formattedDate = df.format(c.getTime());
        date.setText(handleDate(formattedDate)); // Ändra till datum då bilden togs
        cityName = (TextView) findViewById(R.id.cityName);
        image = (ImageView) findViewById(R.id.image);
        String city = getCityName();
        if (city != null) {
            cityName.setText(city);
        } else {
            cityName.setText("");
        }


        /*
        Check om vi kommer från en nytagen bild
         */
        if (getIntent().getStringExtra("file_name") != null) {
            imgFile = new File(getIntent().getStringExtra("file_name").toString());
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                if (getIntent().getStringExtra("camera_type").toString().equals("front")) {
                    image.setImageBitmap(RotateBitmap(myBitmap, 270)); //Blir spegelvänd..
                    image.setScaleX(-1);//Fulfix, rättvänd i appen atm

                } else {
                    image.setImageBitmap(RotateBitmap(myBitmap, 90));
                }

            } else {
                Toast.makeText(this, "Couldn't find image", Toast.LENGTH_SHORT).show();
            }
        }


        /*
        Custom dialogerna
         */
        cdcc = new CustomDialogCommandsClass(MainActivity2.this);
        cddc = new CustomDialogDeleteClass(MainActivity2.this, imgFile);



        /*
        Komponenterna i Descriptiondelen
         */
        editText = (EditText) findViewById(R.id.editText);
        info = (ImageButton) findViewById(R.id.info);
        mic = (ImageButton) findViewById(R.id.mic);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        /*
        Det som sköter expandering och kollaps
         */
        expanded = false;
        expandAnimation = new ResizeAnimation(relativeLayout, 800, 0);
        expandAnimation.setDuration(500);
        collapsAnimation = new ResizeAnimation(relativeLayout, 0, 800);
        collapsAnimation.setDuration(500);
        hide = (TextView) findViewById(R.id.hide);








        /*
        Vår floatingbutton med den knappar
         */
        actionA = (FloatingActionButton) findViewById(R.id.action_a);
        actionC = (FloatingActionButton) findViewById(R.id.action_c);
        actionD = (FloatingActionButton) findViewById(R.id.action_d);
        menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);



    }


    /*
    Metod för att bilden ska vara rättvänd
     */
    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    /*
    Metod för att skriva ut datumet på rätt sätt
     */
    private String handleDate(String formattedDate) {
        String returnValue = formattedDate;

        if (returnValue.charAt(0) == '0') {
            returnValue = returnValue.substring(1, returnValue.length());
        }

        for (int i = 0; i < returnValue.length(); i++) {
            if (returnValue.charAt(i) == '-') {
                StringBuilder sb = new StringBuilder();
                sb.append(returnValue.substring(0, i));
                sb.append(' ');
                sb.append(returnValue.substring(i + 1, returnValue.length()));
                returnValue = sb.toString();
            }
        }
        return returnValue;
    }

    /*
    Startar kameran
     */
    public void startCamera() {
        startActivity(new Intent(this, CameraActivity2.class));
    }


    /*
    Hämtar namnet på staden man befinner sig
     */
    public String getCityName() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            return addresses.get(0).getLocality();
        }
        return null;
    }


    /*
    Aminationen för expand/collapse
     */
    private class ResizeAnimation extends Animation {
        final int targetHeight;
        View view;
        int startHeight;

        public ResizeAnimation(View view, int targetHeight, int startHeight) {
            this.view = view;
            this.targetHeight = targetHeight;
            this.startHeight = startHeight;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int newHeight = (int) (startHeight + (targetHeight - startHeight) * interpolatedTime);
            view.getLayoutParams().height = newHeight;
            view.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
