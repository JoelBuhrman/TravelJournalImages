package com.example.joelbuhrman.traveljournalimages;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.ArrayList;

/**
 * Created by JoelBuhrman on 16-04-05.
 */
public class MainActivity extends Activity implements View.OnClickListener {
    private static int mode;
    private static final int FILTER_MODE = 0;
    private static final int REGULAR_MODE = 1;


    CustomDialogCommandsClass cdcc;


    private ImageView image;
    private String dateValue, cityNameValue, directionValue;
    private TextView date, cityName, hide, direction;
    File imgFile;
    private String path;


    private ImageView filter;
    int[] filters = {R.drawable.filter1, R.drawable.filter2, R.drawable.filter3,R.drawable.filter4, R.drawable.filter5,R.drawable.filter8, R.drawable.filter9, R.drawable.filter11, R.drawable.filter12};


    private FloatingActionsMenu menuMultipleActions;
    private FloatingActionButton penAction, filterAction, saveAction, deleteAction;


    private ImageButton mic, info;
    private EditText editText;

    private boolean expanded;
    private RelativeLayout descriptionLayout, filterLayout, topRelLayout;
    ResizeAnimation expandAnimation, collapsAnimation;

    protected static final int RESULT_SPEECH = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        init();
        activateClickListeners();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            /*
            FloatingactionButtons
             */
            case R.id.action_pen:
                performExpandOrCollapseAction();
                break;
            case R.id.action_filter:
                performFilterAction();
                break;
            case R.id.action_save:
                saveImageAction();
                break;
            case R.id.action_delete:
                deleteImageAction();
                break;


            /*
            Descriptions komponenter
             */
            case R.id.mic:
                promptSpeechInput();
                break;
            case R.id.info:
                cdcc.show();
                break;
            case R.id.hide:
                performExpandOrCollapseAction();
                break;


            /*
            Close vid filter
             */

            case R.id.main_direction:
                if (mode == FILTER_MODE) {
                    performFilterAction();
                }
                break;


            default:
                break;

        }


    }

    private void activateClickListeners() {

        /*
        FloatingActionButtons
         */
        penAction.setOnClickListener(this);
        filterAction.setOnClickListener(this);
        saveAction.setOnClickListener(this);
        deleteAction.setOnClickListener(this);


        /*
        Description komponenter
         */
        mic.setOnClickListener(this);
        info.setOnClickListener(this);
        hide.setOnClickListener(this);


        /*
        Close för filter
         */
        direction.setOnClickListener(this);

    }

    private void init() {
/*
ställer in vilket mode vi befinner oss i
 */
        mode = REGULAR_MODE;

         /*
        MainBilden med dess komponenter
         */
        date = (TextView) findViewById(R.id.chosen_image_date);

        //date.setText(handleDate(formattedDate)); // Ändra till datum då bilden togs
        cityName = (TextView) findViewById(R.id.cityName);
        image = (ImageView) findViewById(R.id.image);
        direction = (TextView) findViewById(R.id.main_direction);
        filter = (ImageView) findViewById(R.id.filter);
        filterLayout = (RelativeLayout) findViewById(R.id.filterLayout);
        topRelLayout= (RelativeLayout)findViewById(R.id.topRelLayout);



        /*
        Tempvärden för textViews
         */
        cityNameValue = "Temp";
        dateValue = "Temp";
        directionValue = "Temp";


        /*
        Check om vi kommer från en nytagen bild
         */
        if (getIntent().getStringExtra("file_name") != null) {
            imgFile = new File(getIntent().getStringExtra("file_name").toString());
            if (imgFile.exists()) {
                path = imgFile.getAbsolutePath();
                Bitmap myBitmap = BitmapFactory.decodeFile(path);

                if (getIntent().getStringExtra("camera_type").toString().equals("front")) {


                    image.setImageBitmap(RotateBitmap(myBitmap, 270)); //Blir spegelvänd..
                    image.setScaleX(-1);//Fulfix, rättvänd i appen atm


                } else {
                    image.setImageBitmap(RotateBitmap(myBitmap, 90));
                }
                String[] info = path.split("%");
                date.setText(handleDate(info[1]));
                dateValue = handleDate(info[1]);
                cityName.setText(info[2]);
                cityNameValue = info[2];
                direction.setText(info[3]);
                directionValue = info[3];

            } else {
                Toast.makeText(this, "Couldn't find image", Toast.LENGTH_SHORT).show();
            }
        }


        /*
        Custom dialogerna
         */
        cdcc = new CustomDialogCommandsClass(MainActivity.this);

        /*
        Komponenterna i Descriptiondelen
         */
        editText = (EditText) findViewById(R.id.editText);
        info = (ImageButton) findViewById(R.id.info);
        mic = (ImageButton) findViewById(R.id.mic);
        descriptionLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        /*
        Det som sköter expandering och kollaps
         */
        expanded = false;
        expandAnimation = new ResizeAnimation(descriptionLayout, 800, 0);
        expandAnimation.setDuration(500);
        collapsAnimation = new ResizeAnimation(descriptionLayout, 0, 800);
        collapsAnimation.setDuration(500);
        hide = (TextView) findViewById(R.id.hide);








        /*
        Vår floatingbutton med den knappar
         */
        penAction = (FloatingActionButton) findViewById(R.id.action_pen);
        filterAction = (FloatingActionButton) findViewById(R.id.action_filter);
        saveAction = (FloatingActionButton) findViewById(R.id.action_save);
        deleteAction = (FloatingActionButton) findViewById(R.id.action_delete);

        menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);


    }




    private void deleteImageAction() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (!isFinishing()) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Delete Image?")
                            .setMessage("Are you sure you want to delete this image?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    File f = new File(String.valueOf(imgFile));
                                    Toast.makeText(getApplicationContext(), "Deleted!", Toast.LENGTH_SHORT).show();
                                    getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                            MediaStore.Images.Media.DATA + "=?", new String[]{f.toString()});
                                    startCamera();
                                    finish();

                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).create().show();
                }
            }
        });
    }

    private void saveImageAction() {
        startActivity(new Intent(getApplicationContext(), CameraActivity.class));
        Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
    }

    private void performFilterAction() {
        RelativeLayout.LayoutParams rel_btn = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0);
        RelativeLayout.LayoutParams top_rel_btn = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0);
        if (expanded) {
            descriptionLayout.startAnimation(collapsAnimation);
            expanded = false;
        }

        switch (mode) {
            case FILTER_MODE:
                rel_btn = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 0);
                topRelLayout.setLayoutParams(top_rel_btn);
                menuMultipleActions.setVisibility(View.VISIBLE);
                mode = REGULAR_MODE;
                direction.setText(directionValue);
                cityName.setText(cityNameValue);
                date.setText(dateValue);
                rel_btn = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 0);

                break;
            case REGULAR_MODE:
                rel_btn = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 200);
                top_rel_btn = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 150);
                top_rel_btn.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                topRelLayout.setLayoutParams(top_rel_btn);
                menuMultipleActions.setVisibility(View.INVISIBLE);
                mode = FILTER_MODE;
                direction.setText("Done");
                cityName.setText("");
                date.setText("");

                break;
            default:
                break;

        }

        rel_btn.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        filterLayout.setLayoutParams(rel_btn);



    }




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




    private void performExpandOrCollapseAction() {

        if (expanded) {
            descriptionLayout.startAnimation(collapsAnimation);
            expanded = false;
        } else {
            descriptionLayout.startAnimation(expandAnimation);
            expanded = true;

        }
    }

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




    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public void setFilter(View view) {
        switch (view.getId()) {
            case R.id.filter1:
                filter.setImageResource(filters[0]);
                break;
            case R.id.filter2:
                filter.setImageResource(filters[1]);
                break;
            case R.id.filter3:
                filter.setImageResource(filters[2]);
                break;
            case R.id.filter4:
                filter.setImageResource(filters[3]);
                break;
            case R.id.filter5:
                filter.setImageResource(filters[4]);
                break;
            case R.id.filter6:
                filter.setImageResource(filters[5]);
                break;
            case R.id.filter7:
                filter.setImageResource(filters[6]);
                break;
            case R.id.filter8:
                filter.setImageResource(filters[7]);
                break;




            case R.id.none:
                filter.setImageResource(android.R.color.transparent);
                break;
            default:
                Toast.makeText(getApplicationContext(), "Filter error", Toast.LENGTH_SHORT).show();
                break;

        }

    }



    private String handleDate(String formattedDate) {
        String returnValue = formattedDate;

        if (returnValue.charAt(0) == '0') {
            returnValue = returnValue.substring(1, returnValue.length());
        }

       /* for (int i = 0; i < returnValue.length(); i++) {
            if (returnValue.charAt(i) == '-') {
                StringBuilder sb = new StringBuilder();
                sb.append(returnValue.substring(0, i));
                sb.append(' ');
                sb.append(returnValue.substring(i + 1, returnValue.length()));
                returnValue = sb.toString();
            }
        }*/
        String[] temp = returnValue.split("-");
        StringBuilder sb = new StringBuilder();
        sb.append(temp[0] + " ");
        String month = temp[1];
        char t = Character.toUpperCase(month.charAt(0));
        month.replace(month.charAt(0), t);
        sb.append(t);
        sb.append(month.substring(1, month.length()) + " ");
        sb.append(temp[2]);
        return sb.toString();
    }



    public void startCamera() {
        startActivity(new Intent(this, CameraActivity.class));
    }






    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }


}
