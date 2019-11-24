package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class Confirmation extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmation);
    }


    public void confirmed(View view)
    {
        Intent intent = new Intent (this, pinEntry.class);
        startActivity(intent);
    }
}
