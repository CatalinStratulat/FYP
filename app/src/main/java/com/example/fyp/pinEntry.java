package com.example.fyp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class pinEntry extends AppCompatActivity implements View.OnTouchListener {
    private ArrayList<String> pinList = new ArrayList<>();
    private int pinNr = 1;
    String path;
    String pathToSave;
    Long startTime;
    ArrayList<String> TouchDetected;

    private static final int RECORDER_SAMPLERATE = 48000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    private int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    private int BytesPerElement = 2; // 2 bytes in 16bit format

    final int REQUEST_PERMISSION_CODE = 1000;

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
        setButtonHandlers();

        //the whole screen becomes sensitive to touch
        RelativeLayout mLinearLayoutMain = findViewById(R.id.relativeLayout);
        mLinearLayoutMain.setOnTouchListener(this);

        if (!CheckPermissionFromDevice()) {
            requestPermission();
        }
        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID().toString();
        pathToSave = path + "_audio_record.3gp";

        findViewById(R.id.btnStart).performClick();
    }

    private void setButtonHandlers() {
        (findViewById(R.id.btnStart)).setOnClickListener(btnClick);
        (findViewById(R.id.btnStop)).setOnClickListener(btnClick);
    }

    private void enableButton(int id, boolean isEnable) {
        (findViewById(id)).setEnabled(isEnable);
    }

    private void enableButtons(boolean isRecording) {
        enableButton(R.id.btnStart, !isRecording);
        enableButton(R.id.btnStop, isRecording);
    }

    private void startRecording() {

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);

        recorder.startRecording();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
        startTime = System.currentTimeMillis();
        System.out.println(startTime);
        TouchDetected = new ArrayList<>();
    }

    //convert short to byte
    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }

    private void writeAudioDataToFile() {
        // Write the output audio in byte
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + UUID.randomUUID().toString();
        String pathToSave = path + "_audio_record.pcm";
//        String filePath = "/sdcard/voice8K16bitmono.pcm";
        short sData[] = new short[BufferElements2Rec];

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(pathToSave);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (isRecording) {
            // gets the voice output from microphone to byte format

            recorder.read(sData, 0, BufferElements2Rec);
            System.out.println("Short wirting to file" + sData.toString());
            try {
                // // writes the data to file from buffer
                // // stores the voice buffer
                byte bData[] = short2byte(sData);
                os.write(bData, 0, BufferElements2Rec * BytesPerElement);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        // stops the recording activity
        if (null != recorder) {
            isRecording = false;
            recorder.stop();
            recorder.release();
            recorder = null;
            recordingThread = null;
            System.exit(0);
        }
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnStart: {
                    enableButtons(true);
                    startRecording();
                    break;
                }
                case R.id.btnStop: {
                    enableButtons(false);
                    stopRecording();
                    break;
                }
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]
                {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                }, REQUEST_PERMISSION_CODE);
    }

    private boolean CheckPermissionFromDevice() {
        int storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }

    public void pinButtonDelete(View view) {
        TextView textview = findViewById(R.id.textView3);
        CharSequence text = textview.getText();
        if (text.length() > 0) {
            CharSequence subtext = text.subSequence(0, text.length() - 1);
            textview.setText(subtext);
            long currentTimestamp = System.currentTimeMillis();
            String touch = "Valid{Delete} "+((currentTimestamp-startTime)*1000);
            TouchDetected.add(touch);
        }
    }

    public void pinButtonEnter(View view) {
        TextView textview3 = findViewById(R.id.textView3);
        TextView textview1 = findViewById(R.id.textView1);
        long currentTimestamp = System.currentTimeMillis();
        String touch = "Valid{Enter} "+((currentTimestamp-startTime)*1000);
        TouchDetected.add(touch);

        if (textview3.length() == 4) {
            if (pinNr < 2) {
//                Toast.makeText(pinEntry.this, "" + textview3.getText() + " Has Been Entered", Toast.LENGTH_SHORT).show();
                String figure = "";
                textview3.setText(figure);

                String title = "Enter PIN [" + (pinList.get(pinNr)) + "]";
                textview1.setText(title);
                pinNr = pinNr + 1;
            } else if (pinNr == 2) {
                ArrayList<String> properList = ((Data) this.getApplication()).getList();
                properList.add("audio_file_path" + "=" + pathToSave);
//                findViewById(R.id.btnStop).performClick();

                System.out.println(TouchDetected.toString());
                processList(properList,TouchDetected);
//                  generateNoteOnSD(output);
//                Toast.makeText(pinEntry.this, "Thank you for completing this sample run", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(pinEntry.this, "PIN Must be correct Length", Toast.LENGTH_SHORT).show();

        }
    }

    public void pinButton3(View view) {
        TextView textview = findViewById(R.id.textView3);
        if (textview.length() < 4) {
            String str = "3";
            textview.append(str);
            long currentTimestamp = System.currentTimeMillis();
            String touch = "Valid{3} "+((currentTimestamp-startTime)*1000);
            TouchDetected.add(touch);
        }
    }

    public void pinButton6(View view) {
        TextView textview = findViewById(R.id.textView3);
        if (textview.length() < 4) {
            String str = "6";
            textview.append(str);
            long currentTimestamp = System.currentTimeMillis();
            String touch = "Valid{6} "+((currentTimestamp-startTime)*1000);
            TouchDetected.add(touch);
        }
    }

    public void pinButton9(View view) {
        TextView textview = findViewById(R.id.textView3);
        if (textview.length() < 4) {
            String str = "9";
            textview.append(str);
            long currentTimestamp = System.currentTimeMillis();
            String touch = "Valid{9} "+((currentTimestamp-startTime)*1000);
            TouchDetected.add(touch);
        }
    }

    public void pinButton0(View view) {
        TextView textview = findViewById(R.id.textView3);
        if (textview.length() < 4) {
            String str = "0";
            textview.append(str);
            long currentTimestamp = System.currentTimeMillis();
            String touch = "Valid{0} "+((currentTimestamp-startTime)*1000);
            TouchDetected.add(touch);
        }
    }

    public void pinButton2(View view) {
        TextView textview = findViewById(R.id.textView3);
        if (textview.length() < 4) {
            String str = "2";
            textview.append(str);
            long currentTimestamp = System.currentTimeMillis();
            String touch = "Valid{2} "+((currentTimestamp-startTime)*1000);
            TouchDetected.add(touch);
        }
    }

    public void pinButton5(View view) {
        TextView textview = findViewById(R.id.textView3);
        if (textview.length() < 4) {
            String str = "5";
            textview.append(str);
            long currentTimestamp = System.currentTimeMillis();
            String touch = "Valid{5} "+((currentTimestamp-startTime)*1000);
            TouchDetected.add(touch);
        }
    }

    public void pinButton8(View view) {
        TextView textview = findViewById(R.id.textView3);
        if (textview.length() < 4) {
            String str = "8";
            textview.append(str);
            long currentTimestamp = System.currentTimeMillis();
            String touch = "Valid{8} "+((currentTimestamp-startTime)*1000);
            TouchDetected.add(touch);
        }
    }

    public void pinButton1(View view) {
        TextView textview = findViewById(R.id.textView3);
        view.playSoundEffect(android.view.SoundEffectConstants.CLICK);
        if (textview.length() < 4) {
            String str = "1";
            textview.append(str);
            long currentTimestamp = System.currentTimeMillis();
            String touch = "Valid{1} "+((currentTimestamp-startTime)*1000);
            TouchDetected.add(touch);
        }
    }

    public void pinButton4(View view) {
        TextView textview = findViewById(R.id.textView3);
        if (textview.length() < 4) {
            String str = "4";
            textview.append(str);
            long currentTimestamp = System.currentTimeMillis();
            String touch = "Valid{4} "+((currentTimestamp-startTime)*1000);
            TouchDetected.add(touch);
        }
    }

    public void pinButton7(View view) {
        TextView textview = findViewById(R.id.textView3);
        if (textview.length() < 4) {
            String str = "7";
            textview.append(str);
            long currentTimestamp = System.currentTimeMillis();
            String touch = "Valid{7} "+((currentTimestamp-startTime)*1000);
            TouchDetected.add(touch);
        }
    }


    public void processList(ArrayList<String> properList,ArrayList<String> TouchList)
    {
        Workbook workbook = new XSSFWorkbook();
        String fullpath = path + "_FileName.xlsx";

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
            int currentVar =0;
            FileOutputStream fileOut = new FileOutputStream(excelFile); //Opening the file
            Sheet sheet = workbook.createSheet("Sheet 1");
            Row variables = sheet.createRow(0);
            Row values = sheet.createRow(1);

            for(String item : properList)
            {
                String seperator = "=";
                int location = item.indexOf(seperator);
                if (location != -1)
                {
                    String key = item.substring(0, location);
                    String value = item.substring(location+seperator.length());
                    variables.createCell(properList.indexOf(item)).setCellValue(key);
                    values.createCell(properList.indexOf(item)).setCellValue(value);
                    currentVar = properList.indexOf(item);
                }
            }
            System.out.println(currentVar);
            variables.createCell(currentVar+1).setCellValue("Touch (ms)");

            for(String item : TouchList)
            {
                if(TouchList.indexOf(item) == 0)
                {
                    values.createCell(currentVar+1).setCellValue(item);
                }
                else
                {
                    Row touches = sheet.createRow(TouchList.indexOf(item)+1);
                    touches.createCell(currentVar+1).setCellValue(item);
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
        long currentTimestamp = System.currentTimeMillis();
        String touch = "Invalid "+((currentTimestamp-startTime)*1000);
        TouchDetected.add(touch);
//        Toast.makeText(pinEntry.this, "Invalid Touch Detected at ="+currentTimestamp+" time started ="+startTime+" with difference ="+(currentTimestamp-startTime), Toast.LENGTH_SHORT).show();
        return false;
    }
}

