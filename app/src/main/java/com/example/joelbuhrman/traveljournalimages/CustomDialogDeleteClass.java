package com.example.joelbuhrman.traveljournalimages;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by JoelBuhrman on 16-04-09.
 */
public class CustomDialogDeleteClass extends Dialog implements
        View.OnClickListener {

    public Activity c;
    public Dialog d;
    public ImageButton close;
    public TextView yesDelete, noDelete;
    public ImageButton cancelDelete;
    private File imgFile;

    public CustomDialogDeleteClass(Activity a, File imgFile) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.imgFile = imgFile;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_delete_dialog);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yesButton:
                if (imgFile.exists()) {
                    imgFile.delete();
                }
                Toast.makeText(getContext(), "Deleted!", Toast.LENGTH_SHORT).show();
                dismiss();
                break;
            case R.id.noButton:
                dismiss();
                break;
            case R.id.cancelDelete:
                dismiss();
                break;


            default:
                break;
        }
        dismiss();
    }


}