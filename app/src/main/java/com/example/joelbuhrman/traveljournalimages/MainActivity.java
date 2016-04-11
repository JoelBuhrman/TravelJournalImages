package com.example.joelbuhrman.traveljournalimages;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends FragmentActivity {
    TextView date, tDescription, cityName;
    ImageView image, iDescription;
    RelativeLayout textLayout, parentLayout;
    EditText editText;
    String formattedDate;
    SimpleDateFormat df;
    private boolean expanded;
    ViewGroup.LayoutParams params;
    private ViewGroup.LayoutParams compressedParams, expandedParams;
    LayoutTransition transition;
    File imgFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        init();
        setOnClickListeners();



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

/*
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top);
        fragmentTransaction.replace(R.id.relLayout, t);
        fragmentTransaction.commit();*/


        //setColorFilter(image);

    }


    private void setOnClickListeners() {
        tDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                performExpandOrCollapse();

            }
        });

        iDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performExpandOrCollapse();
            }
        });

    }

    private void performExpandOrCollapse() {
        ViewGroup.LayoutParams params1 = textLayout.getLayoutParams();

        if (!expanded) {


                    /*iDescription.setImageResource(R.drawable.upsidedownarrow);
                    textLayout.setLayoutParams(expandedParams);*/

            //  textLayout.getLayoutParams().height=600;
            params1.height = 600;
            iDescription.setImageResource(R.drawable.vector_drawable_ic_keyboard_arrow_down_white___px);
            textLayout.setLayoutParams(params1);


            expanded = true;

        } else {

                    /*iDescription.setImageResource(R.drawable.test);
                    textLayout.setLayoutParams(compressedParams);*/
            params1.height = 0;
            iDescription.setImageResource(R.drawable.vector_drawable_ic_keyboard_arrow_up_white___px);
            textLayout.setLayoutParams(params1);

            expanded = false;
        }
    }

    public void deleteClicked(View view) {
        if (imgFile != null) {
            imgFile.delete();
        }

        startActivity(new Intent(this, CameraActivity2.class));

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void init() {
        date = (TextView) findViewById(R.id.chosen_image_date);
        Calendar c = Calendar.getInstance();
        df = new SimpleDateFormat("dd-MMM-yyyy");
        formattedDate = df.format(c.getTime());
        date.setText(handleDate(formattedDate)); // Ändra till datum då bilden togs


        cityName = (TextView) findViewById(R.id.cityName);


        image = (ImageView) findViewById(R.id.image);


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


        tDescription = (TextView) findViewById(R.id.description);
        iDescription = (ImageView) findViewById(R.id.arrow);

        textLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        parentLayout = (RelativeLayout) findViewById(R.id.relLayout);

        editText = (EditText) findViewById(R.id.editText);

        expanded = false;

        compressedParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        expandedParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 600);

        //textLayout.setY(500);
        //textLayout.setLayoutParams(compressedParams);

        /*transition= textLayout.getLayoutTransition();


        if (Build.VERSION.SDK_INT >= 15) {
            transition.enableTransitionType(LayoutTransition.CHANGING);



        }

       */
        //textLayout.getLayoutParams().height = 0;


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



    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
