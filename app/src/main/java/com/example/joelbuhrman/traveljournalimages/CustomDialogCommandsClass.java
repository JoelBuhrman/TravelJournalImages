package com.example.joelbuhrman.traveljournalimages;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

/**
 * Created by JoelBuhrman on 16-04-09.
 */
public class CustomDialogCommandsClass extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public ImageButton close;

    public CustomDialogCommandsClass(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
           

            default:
                break;
        }
        dismiss();
    }
}