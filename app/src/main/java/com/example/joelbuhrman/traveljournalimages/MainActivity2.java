package com.example.joelbuhrman.traveljournalimages;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.speech.RecognizerIntent;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by JoelBuhrman on 16-04-05.
 */
public class MainActivity2 extends Activity {
    private ImageView image;
    private ImageButton mic, info;
    private FloatingActionButton actionA, actionB, actionC, actionD;
    private EditText editText;
    private FloatingActionsMenu menuMultipleActions;
    private boolean expanded;
    SimpleDateFormat df;
    String formattedDate;
    private RelativeLayout relativeLayout, mainLayout;
    private TextView tDescription, date, cityName, hide;
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
        setOnSwipeListeners();


    }

    private void setOnSwipeListeners() {
        image.setOnTouchListener(new OnSwipeTouchListener(MainActivity2.this) {
            public void onSwipeTop() {
                performExpandOrCollapse();
                Toast.makeText(getApplicationContext(), "image up", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeBottom() {
                performExpandOrCollapse();
                Toast.makeText(getApplicationContext(), "image down", Toast.LENGTH_SHORT).show();
            }


        });


    }

    private void setOnClickListeners() {
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performExpandOrCollapse();
                // menuMultipleActions.toggle();


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
            }
        });

        actionD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imgFile != null) {
                    imgFile.delete();
                }

                startActivity(new Intent(getApplicationContext(), CameraActivity2.class));
                Toast.makeText(getApplicationContext(), "Deleted!", Toast.LENGTH_SHORT).show();
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

                CustomDialogClass cdd=new CustomDialogClass(MainActivity2.this);
                cdd.show();
            }
        });

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


                        editText.setText(editText.getText().toString() + handleSpeech(text.get(0))+". ");
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
                sb.replace(i, i, String.valueOf(t));
            }
        }


        return sb.toString();
    }

    private void performExpandOrCollapse() {

        if (expanded) {
            relativeLayout.startAnimation(collapsAnimation);
            expanded = false;
        } else {
            relativeLayout.startAnimation(expandAnimation);
            expanded = true;

        }
    }

    private void init() {
        editText = (EditText) findViewById(R.id.editText);
        date = (TextView) findViewById(R.id.chosen_image_date);
        Calendar c = Calendar.getInstance();
        df = new SimpleDateFormat("dd-MMM-yyyy");
        formattedDate = df.format(c.getTime());
        date.setText(handleDate(formattedDate)); // Ändra till datum då bilden togs
        info= (ImageButton)findViewById(R.id.info);

        cityName = (TextView) findViewById(R.id.cityName);


        image = (ImageView) findViewById(R.id.image);
        mic = (ImageButton) findViewById(R.id.mic);


        if (getIntent().getStringExtra("file_name") != null) {
            imgFile = new File(getIntent().getStringExtra("file_name").toString());
            if (imgFile.exists()) {

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());


                image.setImageBitmap(RotateBitmap(myBitmap, 90));

            } else {
                Toast.makeText(this, "Couldn't find image", Toast.LENGTH_SHORT).show();
            }
        } else {
            // image.setImageResource(R.drawable.adventure);
        }


        expanded = false;
        image = (ImageView) findViewById(R.id.image);
        //tDescription= (TextView)findViewById(R.id.description);

        actionA = (FloatingActionButton) findViewById(R.id.action_a);
        //actionB = (FloatingActionButton) findViewById(R.id.action_b);
        actionC = (FloatingActionButton) findViewById(R.id.action_c);
        actionD = (FloatingActionButton) findViewById(R.id.action_d);
        menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);

        expandAnimation = new ResizeAnimation(
                relativeLayout,
                800,
                0
        );
        expandAnimation.setDuration(500);
        collapsAnimation = new ResizeAnimation(
                relativeLayout,
                0,
                800
        );
        collapsAnimation.setDuration(500);
        hide = (TextView) findViewById(R.id.hide);

    }


    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


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

    public void showOtherOption(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void startNext(View view) {
        startActivity(new Intent(this, CameraActivity2.class));
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
}
