package com.example.fyp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.HapticFeedbackConstants;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;


public class passwordEntry extends AppCompatActivity implements View.OnTouchListener {

    private static final int RECORDER_SAMPLERATE = 48000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private ArrayList<String> passwordList = new ArrayList<>();
    private int pinNr = 1;
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath();
    private Long startTime;
    private ArrayList<String> TouchDetected;
    private String newpath = path + "/FYP" + "/Passwords" + "/" + UUID.randomUUID().toString() + "/";
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    private int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    private int BytesPerElement = 2; // 2 bytes in 16bit format
    private final int REQUEST_PERMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");
        // 9 pins plus one starter in the strings.xml
        passwordList.add("Hello123");
        passwordList.add("byebye22");
//        passwordList.add("0987");
//        passwordList.add("3967");
//        passwordList.add("0359");
//        passwordList.add("1063");
//        passwordList.add("8888");
//        passwordList.add("0258");
//        passwordList.add("9406");
//        passwordList.add("9942");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_entry);
        setButtonHandlers();
        createFileStructure();

        //the whole screen becomes sensitive to touch
        RelativeLayout mLinearLayoutMain = findViewById(R.id.relativeLayout2);
        mLinearLayoutMain.setOnTouchListener(this);

        if (!CheckPermissionFromDevice()) {
            requestPermission();
        }

        findViewById(R.id.btnStart2).performClick();
    }

    private void setButtonHandlers() {
        (findViewById(R.id.btnStart2)).setOnClickListener(btnClick);
        (findViewById(R.id.btnStop2)).setOnClickListener(btnClick);
    }

    private void createFileStructure() {
        File pins = new File(newpath);
        if(!pins.isDirectory())
        {
            pins.mkdirs();
        }
    }

    private void enableButton(int id, boolean isEnable) {
        (findViewById(id)).setEnabled(isEnable);
    }

    private void enableButtons(boolean isRecording) {
        enableButton(R.id.btnStart2, !isRecording);
        enableButton(R.id.btnStop2, isRecording);
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

        String pathToSave = newpath + "audio_record.pcm";
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
        }
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnStart2: {
                    enableButtons(true);
                    startRecording();
                    break;
                }
                case R.id.btnStop2: {
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
