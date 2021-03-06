package com.example.fyp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class pinEntry extends AppCompatActivity implements View.OnTouchListener {

    private ArrayList<String> pinList = new ArrayList<>();
    private int pinNr = 1;
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath();
    private Long startTime;
    private long delay = 85000000;
    private String newpath = path + "/FYP" + "/Pins" + "/" + UUID.randomUUID().toString() + "/";
    private MediaRecorder recorder = null;

    private ArrayList<String> AttributeList;
    private ArrayList<String> ExampleList;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");
        // 9 pins plus one starter in the strings.xml
        pinList.add("1234");
        pinList.add("6789");
        pinList.add("0987");
        pinList.add("3967");
        pinList.add("0359");
        pinList.add("1063");
        pinList.add("8888");
        pinList.add("0258");
        pinList.add("9406");
        pinList.add("9942");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_entry);


        createFileStructure();

        //the whole screen becomes sensitive to touch
        RelativeLayout mLinearLayoutMain = findViewById(R.id.relativeLayout);
        mLinearLayoutMain.setOnTouchListener(this);


        setupMediaRecorders();
        startRecording();
    }

    private void setupMediaRecorders()
    {
        recorder = new MediaRecorder();
        recorder.setAudioChannels(2);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setAudioSamplingRate(48000);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(newpath+"audio_stereo.mp3");
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
    }

    private void startRecording()
    {
        System.out.println(startTime);
        AttributeList = new ArrayList<>();
        ExampleList = new ArrayList<>();
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
        startTime = System.nanoTime();
    }

    private void stopRecording()
    {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    private void createFileStructure() {
        File pins = new File(newpath);
        if(!pins.isDirectory())
        {
            pins.mkdirs();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }



    public void pinButtonDelete(View view) {
        long currentTimestamp = System.nanoTime();
        String touch = ""+((currentTimestamp-startTime-delay));
        String digit = "Delete";
        AttributeList.add(digit);
        ExampleList.add(touch);
//        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        TextView textview = findViewById(R.id.textView3);
        CharSequence text = textview.getText();
        if (text.length() > 0) {
            CharSequence subtext = text.subSequence(0, text.length() - 1);
            textview.setText(subtext);

        }
    }

    public void pinButtonEnter(View view) {
        long currentTimestamp = System.nanoTime();
        String touch = ""+((currentTimestamp-startTime-delay));
        String digit = "Enter";
        AttributeList.add(digit);
        ExampleList.add(touch);
//        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        TextView textview3 = findViewById(R.id.textView3);
        TextView textview1 = findViewById(R.id.textView1);

        if (textview3.length() == 4) {
            if (pinNr < 1) {
//                Toast.makeText(pinEntry.this, "" + textview3.getText() + " Has Been Entered", Toast.LENGTH_SHORT).show();
                String figure = "";
                textview3.setText(figure);

                String title = "Enter PIN [" + (pinList.get(pinNr)) + "]";
                textview1.setText(title);
                pinNr = pinNr + 1;
            } else if (pinNr == 1) {
                ArrayList<String> properList = ((Data) this.getApplication()).getList();
                stopRecording();
                System.out.println(ExampleList.toString());
                processList(properList,ExampleList,AttributeList);
                finish();
            }

        } else {
            Toast.makeText(pinEntry.this, "PIN Must be correct Length", Toast.LENGTH_SHORT).show();

        }
    }

    public void pinButton3(View view)
    {
        long currentTimestamp = System.nanoTime();
        String touch = ""+((currentTimestamp-startTime-delay));
        String digit = "3";
        AttributeList.add(digit);
        ExampleList.add(touch);
////        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        TextView textview = findViewById(R.id.textView3);
        if (textview.length() < 4) {
            String str = "3";
            textview.append(str);
        }
    }

    public void pinButton6(View view) {
        long currentTimestamp = System.nanoTime();
        String touch = ""+((currentTimestamp-startTime-delay));
        String digit = "6";
        AttributeList.add(digit);
        ExampleList.add(touch);
        TextView textview = findViewById(R.id.textView3);
////        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        if (textview.length() < 4) {
            String str = "6";
            textview.append(str);
        }
    }

    public void pinButton9(View view) {
        long currentTimestamp = System.nanoTime();
        String touch = ""+((currentTimestamp-startTime-delay));
        String digit = "9";
        AttributeList.add(digit);
        ExampleList.add(touch);
////        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        TextView textview = findViewById(R.id.textView3);
        if (textview.length() < 4) {
            String str = "9";
            textview.append(str);

        }
    }

    public void pinButton0(View view) {
        long currentTimestamp = System.nanoTime();
        String touch = ""+((currentTimestamp-startTime-delay));
        String digit = "0";
        AttributeList.add(digit);
        ExampleList.add(touch);
//        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);

        TextView textview = findViewById(R.id.textView3);
        if (textview.length() < 4) {
            String str = "0";
            textview.append(str);

        }
    }

    public void pinButton2(View view) {
        long currentTimestamp = System.nanoTime();
        String touch = ""+((currentTimestamp-startTime-delay));
        String digit = "2";
        AttributeList.add(digit);
        ExampleList.add(touch);
//        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        TextView textview = findViewById(R.id.textView3);
        if (textview.length() < 4) {
            String str = "2";
            textview.append(str);

        }
    }

    public void pinButton5(View view) {
        long currentTimestamp = System.nanoTime();
        String touch = ""+((currentTimestamp-startTime-delay));
        String digit = "5";
        AttributeList.add(digit);
        ExampleList.add(touch);
//        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        TextView textview = findViewById(R.id.textView3);
        if (textview.length() < 4) {
            String str = "5";
            textview.append(str);

        }
    }

    public void pinButton8(View view) {
        long currentTimestamp = System.nanoTime();
        String touch = ""+((currentTimestamp-startTime-delay));
        String digit = "8";
        AttributeList.add(digit);
        ExampleList.add(touch);
//        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        TextView textview = findViewById(R.id.textView3);
        if (textview.length() < 4) {
            String str = "8";
            textview.append(str);

        }
    }

    public void pinButton1(View view) {
        long currentTimestamp = System.nanoTime();
        String touch = ""+((currentTimestamp-startTime-delay));
        String digit = "1";
        AttributeList.add(digit);
        ExampleList.add(touch);
//        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        TextView textview = findViewById(R.id.textView3);
        view.playSoundEffect(android.view.SoundEffectConstants.CLICK);
        if (textview.length() < 4) {
            String str = "1";
            textview.append(str);

        }
    }

    public void pinButton4(View view) {
        long currentTimestamp = System.nanoTime();
        String touch = ""+((currentTimestamp-startTime-delay));
        String digit = "4";
        AttributeList.add(digit);
        ExampleList.add(touch);
//        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        TextView textview = findViewById(R.id.textView3);
        if (textview.length() < 4) {
            String str = "4";
            textview.append(str);

        }
    }

    public void pinButton7(View view) {
        long currentTimestamp = System.nanoTime();
        String touch = ""+((currentTimestamp-startTime-delay));
        String digit = "7";
        AttributeList.add(digit);
        ExampleList.add(touch);
        TextView textview = findViewById(R.id.textView3);
//        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        if (textview.length() < 4) {
            String str = "7";
            textview.append(str);
        }
    }


    public void processList(ArrayList<String> properList,ArrayList<String> TouchList,ArrayList<String> dataList)
    {
        Workbook workbook = new XSSFWorkbook();
        String fullpath = newpath + "data.xlsx";

        File excelFile = new File(fullpath);
        if(!excelFile.exists())
        {
            try
            {
                excelFile.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            int currentRow =0;
            FileOutputStream fileOut = new FileOutputStream(excelFile); //Opening the file
            Sheet sheet = workbook.createSheet("Sheet 1");
            ArrayList<String> userVarListValues = new ArrayList<>();
            ArrayList<String> userVarListKeys = new ArrayList<>();
            ArrayList<String> userTouchDataList = new ArrayList<>();

            for(String item : properList)
            {
                String seperator = "=";
                int location = item.indexOf(seperator);
                if (location != -1)
                {
                    String key = item.substring(0, location);
                    userVarListKeys.add(key);
                    String value = item.substring(location+seperator.length());
                    userVarListValues.add(value);
                }
            }
            System.out.println(userVarListKeys);

            userVarListKeys.add("Character");
            userVarListKeys.add("TouchNs");

            int i=0;
            Row variableKeyRow = sheet.createRow(i);
            while( i < userVarListKeys.size())
            {
                variableKeyRow.createCell(i).setCellValue(userVarListKeys.get(i));
                i++;
            }
            int j=0;
            while( j < TouchList.size())
            {
                j++;
                Row nextRow = sheet.createRow(j);
                for(int cellColumn=0;cellColumn<userVarListValues.size();cellColumn++)
                {
                    nextRow.createCell(cellColumn).setCellValue(userVarListValues.get(cellColumn));
                }
                for(int cellColumn=(userVarListValues.size());cellColumn < (userVarListKeys.size());cellColumn++)
                {
                    System.out.println(userVarListKeys.get(cellColumn));

                    if(userVarListKeys.get(cellColumn).equals("Character"))
                    {
                        nextRow.createCell(cellColumn).setCellValue(dataList.get(j-1));
                    }
                    else if(userVarListKeys.get(cellColumn).equals("TouchNs"))
                    {
                        nextRow.createCell(cellColumn).setCellValue(TouchList.get(j-1));
                    }
                }
            }
            workbook.write(fileOut); //Writing all your row column inside the file
            fileOut.flush();
            fileOut.close(); //closing the file and done
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        long currentTimestamp = System.nanoTime();
//        String touch = ""+((currentTimestamp-startTime-delay));
//        ExampleList.add(touch);
        return false;
    }
}

