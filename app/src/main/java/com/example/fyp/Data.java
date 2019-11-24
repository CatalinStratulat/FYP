package com.example.fyp;

import android.app.Application;

import java.util.ArrayList;

public class Data extends Application
{
    ArrayList<String> properList = new ArrayList<String>();

    public ArrayList<String> getList()
    {
        return properList;
    }

    public void setList(ArrayList<String> thelist)
    {
        properList = thelist;
    }
}
