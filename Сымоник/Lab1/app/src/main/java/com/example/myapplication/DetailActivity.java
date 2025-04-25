package com.example.myapplication;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.MotionEvent;


import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetailActivity extends AppCompatActivity {

    private GestureDetector gestureDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        gestureDetector = new GestureDetector(this, new GestureListener(this));


        TextView nameTextView = findViewById(R.id.nameTextView);
        TextView elementTextView = findViewById(R.id.elementTextView);
        TextView weaponTextView = findViewById(R.id.weaponTextView);
        TextView regionTextView = findViewById(R.id.regionTextView);
        ImageView elementImageView = findViewById(R.id.elementImageView);
        ImageView thumbImageView = findViewById(R.id.detailThumbnailImageView);
        ImageView rarityImageView = findViewById(R.id.rarityImageView);


        CharacterItem item = (CharacterItem) getIntent().getSerializableExtra("character");

        if (item != null) {
            nameTextView.setText(item.getName());
            elementTextView.setText("Element: ");
            weaponTextView.setText("Weapon: " + item.getWeapon());
            regionTextView.setText("Region: " + item.getRegion());



            ArrayList<Talent> tal = item.getTalents();

            TextView talent1 = findViewById(R.id.talentName1);
            talent1.setText(tal.get(0).getName());
            TextView talent2 = findViewById(R.id.talentName2);
            talent2.setText(tal.get(1).getName());
            TextView talent3 = findViewById(R.id.talentName3);
            talent3.setText(tal.get(2).getName());
            TextView talent4 = findViewById(R.id.talentName4);
            talent4.setText(tal.get(3).getName());
            TextView talent5 = findViewById(R.id.talentName5);
            talent5.setText(tal.get(4).getName());

            if(tal.size() == 6)
            {
                TextView talent6 = findViewById(R.id.talentName6);
                talent6.setText(tal.get(5).getName());
            }




            ArrayList<Constellation> con = item.getConstellations();
            if(con.size() == 6)
            {
                TextView constelation1 = findViewById(R.id.constellation1);
                constelation1.setText(con.get(0).getName());
                TextView constelation2 = findViewById(R.id.constellation2);
                constelation2.setText(con.get(1).getName());
                TextView constelation3 = findViewById(R.id.constellation3);
                constelation3.setText(con.get(2).getName());
                TextView constelation4 = findViewById(R.id.constellation4);
                constelation4.setText(con.get(3).getName());
                TextView constelation5 = findViewById(R.id.constellation5);
                constelation5.setText(con.get(4).getName());
                TextView constelation6 = findViewById(R.id.constellation6);
                constelation6.setText(con.get(5).getName());
            }


            Glide.with(this).load("file:///android_asset/img/Element_" + item.getElement() + ".png").into(elementImageView);
            Glide.with(this).load("file:///android_asset/img/" + item.getThumbnail()).into(thumbImageView);
            Glide.with(this).load("file:///android_asset/img/Icon_" + item.getRarity() + "_Stars.png").into(rarityImageView);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }



    }





