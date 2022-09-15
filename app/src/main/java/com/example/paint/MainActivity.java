package com.example.paint;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.graphics.Color;

import android.net.Uri;

import android.os.Bundle;

import android.os.Environment;

import android.provider.MediaStore;

import android.view.View;

import android.view.ViewTreeObserver;

import android.widget.ImageButton;
import android.widget.Toast;


import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.google.android.material.slider.RangeSlider;


import java.io.File;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    // creating the object of type DrawView
    // in order to get the reference of the View
    private DrawView paint;
    private int STORAGE_PERMISSION_CODE = 1;

    // creating objects of type button
    private ImageButton pencil, color, shape, undo, save ;

    // creating a RangeSlider object, which will
    // help in selecting the width of the Stroke
    private RangeSlider rangeSlider;

    private AlertDialog alert, shapeAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // getting the reference of the views from their ids

        paint = (DrawView) findViewById(R.id.draw_view);

        rangeSlider = (RangeSlider) findViewById(R.id.rangebar);

        undo = (ImageButton) findViewById(R.id.btn_undo);
        pencil = (ImageButton) findViewById(R.id.btn_pencil);

        color = (ImageButton) findViewById(R.id.btn_color);

        shape = (ImageButton) findViewById(R.id.btn_shape);

        save = (ImageButton) findViewById(R.id.btn_save);

        // create alert dialog for color list
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Choisir couleur");
        String[] items = { "Bleue","Jaune","Noire","Rouge","Verte" };
        int checkedItem = 4;
        alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        paint.setColor(Color.BLUE);
                        break;
                    case 1:
                        paint.setColor(Color.YELLOW);
                        break;
                    case 2:
                        paint.setColor(Color.BLACK);
                        break;
                    case 3:
                        paint.setColor(Color.RED);
                        break;
                    case 4:
                        paint.setColor(Color.GREEN);
                        break;
                }
                dialog.dismiss();
            }
        });
        alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);

        // shape list

        // create alert dialog for color list
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(MainActivity.this);
        alertDialog2.setTitle("Choisir une forme");
        String[] shapeItems = { "Rectangle", "Circle" };
        int checkedShapeItem = 0;
        alertDialog2.setSingleChoiceItems(shapeItems, checkedShapeItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        paint.setAction("rect");
                        break;
                    case 1:
                        paint.setAction("circle");
                        break;
                }
                dialog.dismiss();
            }
        });
        shapeAlert = alertDialog2.create();
        shapeAlert.setCanceledOnTouchOutside(false);

        // creating a OnClickListener for each button,
        // to perform certain actions
        // the undo button will remove the most
        // recent stroke from the canvas
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paint.undo();
            }
        });

        // the save button will save the current
        // canvas which is actually a bitmap
        // in form of PNG, in the storage
        pencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paint.setAction("pencil");
            }
        });

        // show alertdialog for color
        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (alert.isShowing()) {
                    alert.hide();
                } else {
                    alert.show();
                }
            }
        });

        // the color button will allow the user
        // to select the color of his brush
        // the button will toggle the visibility of the RangeBar/RangeSlider
        shape.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (shapeAlert.isShowing())
                    shapeAlert.hide();
                else shapeAlert.show();
            }

        });

        // set the range of the RangeSlider
        rangeSlider.setValueFrom(0.0f);
        rangeSlider.setValueTo(100.0f);



        // adding a OnChangeListener which will

        // change the stroke width

        // as soon as the user slides the slider

        rangeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {

            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {

                paint.setStrokeWidth((int) value);

            }

        });


        // pass the height and width of the custom view
        // to the init method of the DrawView object
        ViewTreeObserver vto = paint.getViewTreeObserver();

        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override

            public void onGlobalLayout() {

                paint.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int width = paint.getMeasuredWidth();

                int height = paint.getMeasuredHeight();

                paint.init(height, width);

            }

        });

        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    try {
                        // getting the bitmap from DrawView class
                        Bitmap bmp = paint.save();

                        // opening a OutputStream to write into the file
                        OutputStream imageOutStream = null;

                        ContentValues cv = new ContentValues();

                        // property of the file
                        cv.put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, "Painting drawn");
                        cv.put(android.provider.MediaStore.Images.Media.BUCKET_ID,"drawn" );
                        cv.put(android.provider.MediaStore.Images.Media.BUCKET_DISPLAY_NAME,"Paint" );
                        cv.put(android.provider.MediaStore.Images.Media.BUCKET_DISPLAY_NAME,"Paint" );
                        cv.put(android.provider.MediaStore.Images.Media.DESCRIPTION,"Image here" );
                        cv.put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, "drawing");

                        // type of the file
                        cv.put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/png");

                        Uri uri = getContentResolver().insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
                        try {

                            // open the output stream with the above uri
                            imageOutStream = getContentResolver().openOutputStream(uri);


                            // this method writes the files in storage
                            bmp.compress(Bitmap.CompressFormat.PNG, 100, imageOutStream);

                            // close the output stream after use
                            imageOutStream.close();
                            Toast.makeText(MainActivity.this, "Image Saved", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    requestStoragePermission();
                }

            }
        });

    }
    private void requestStoragePermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to acces in your media")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();
        } else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"PERMISSION GRANTED",Toast.LENGTH_SHORT).show();
                try {
                    // getting the bitmap from DrawView class
                    Bitmap bmp = paint.save();

                    // opening a OutputStream to write into the file
                    OutputStream imageOutStream = null;

                    ContentValues cv = new ContentValues();

                    // name of the file
                    cv.put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, "Painting drawn");
                    cv.put(android.provider.MediaStore.Images.Media.BUCKET_ID,"drawn" );
                    cv.put(android.provider.MediaStore.Images.Media.BUCKET_DISPLAY_NAME,"Paint" );
                    cv.put(android.provider.MediaStore.Images.Media.BUCKET_DISPLAY_NAME,"Paint" );
                    cv.put(android.provider.MediaStore.Images.Media.DESCRIPTION,"Image here" );
                    cv.put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, "drawing");

                    // type of the file
                    cv.put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/png");

                    Uri uri = getContentResolver().insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
                    try {

                        // open the output stream with the above uri
                        imageOutStream = getContentResolver().openOutputStream(uri);


                        // this method writes the files in storage
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, imageOutStream);

                        // close the output stream after use
                        imageOutStream.close();
                        Toast.makeText(MainActivity.this, "Image Saved", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this,"PERMISSION DENIED",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
