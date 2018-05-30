package com.hasanalpzengin.wifidatacollector;

import android.Manifest;
import android.Manifest.permission;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shawnlin.numberpicker.NumberPicker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {

    private Button scanButton;
    private TextView resultText, lastRecord;
    private ProgressBar progressBar;
    private NumberPicker numberPickerX, numberPickerY, numberPickerZ;
    private ArrayList<Result> results;
    private final static int LOCATION_REQUEST = 5555;
    private final static int WRITE_REQUEST = 3333;
    private int scanCount = 0;
    private BssidFileManager fileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        scanButton = findViewById(R.id.scanButton);
        resultText = findViewById(R.id.scanText);
        progressBar = findViewById(R.id.progressBar);
        lastRecord = findViewById(R.id.lastRecord);
        numberPickerX = (NumberPicker) findViewById(R.id.numberPickerX);
        numberPickerY = (NumberPicker) findViewById(R.id.numberPickerY);
        numberPickerZ = (NumberPicker) findViewById(R.id.numberPickerZ);

        numberPickerX.setMinValue(0);
        numberPickerY.setMinValue(0);
        numberPickerZ.setMinValue(0);
        numberPickerX.setMaxValue(2000);
        numberPickerY.setMaxValue(2000);
        numberPickerZ.setMaxValue(3);

        numberPickerX.setOnValueChangedListener(this);
        numberPickerY.setOnValueChangedListener(this);
        numberPickerZ.setOnValueChangedListener(this);

        results = new ArrayList<>();

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearch();
                lastRecord.setText(numberPickerX.getValue()+","+numberPickerY.getValue()+","+numberPickerZ.getValue()+" times:" + (scanCount-1));
            }
        });

        fileManager = new BssidFileManager(getApplicationContext());

        resultText.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.createFile: {
                createFile();
                printResults();
                break;
            }
            case R.id.clearResults:{
                clearResults();
                printResults();
                break;
            }
            case R.id.deleteLastResult:{
                deleteLastResult();
                printResults();
                break;
            }
            case R.id.editAccessPoints:{
                Intent intent = new Intent(getApplicationContext(), BssidManager.class);
                startActivity(intent);
            }
        }
        return true;
    }

    private void deleteLastResult() {
        results.remove(results.size()-1);
    }

    private void clearResults() {
        results.clear();
    }

    private void createFile() {
        try {
            ArrayList<String> bssidList = fileManager.getBssidList();

            StringBuilder stringBuilder = new StringBuilder();
            for (String bssid : bssidList) {
                    stringBuilder.append(bssid+",");
            }
            stringBuilder.append("X,Y,Z\n");
            for (Result result :results){
                ArrayList<Wifi> wifiList = result.getWifiList();
                for (int i=0; i<bssidList.size(); i++){
                    boolean founded = false;
                    for (Wifi wifi : wifiList){
                        if (bssidList.get(i).contentEquals(wifi.getBssid())){
                            stringBuilder.append(wifi.getSignal()+",");
                            founded = true;
                        }
                    }
                    if (!founded){
                        stringBuilder.append("-100,");
                    }
                }

                stringBuilder.append(result.getCoordinate_x()+","+result.getCoordinate_y()+","+result.getCoordinate_z());
                stringBuilder.append("\n");

                Log.d("CSV Row: ",stringBuilder.toString());
            }
            stringBuilder.deleteCharAt(stringBuilder.length()-1);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{permission.WRITE_EXTERNAL_STORAGE, permission.READ_EXTERNAL_STORAGE}, WRITE_REQUEST);
                }
            }

            File newDirectory = new File(Environment.getExternalStorageDirectory(),"/Download/LearningData");
            if (!newDirectory.exists()){
                newDirectory.setWritable(true);
                newDirectory.mkdirs();
            }
            //file name
            Calendar currentTime = Calendar.getInstance();
            String filePath = newDirectory.getPath()+"/"+currentTime.get(Calendar.YEAR)+"_"
                    +currentTime.get(Calendar.MONTH)+"_"
                    +currentTime.get(Calendar.DAY_OF_MONTH)+"_"
                    +currentTime.get(Calendar.HOUR_OF_DAY)+"_"
                    +currentTime.get(Calendar.MINUTE)+"_"
                    +currentTime.get(Calendar.SECOND)
                    +".csv";

            Log.d("Path", filePath);

            File file = new File(filePath);
            file.createNewFile();

            Log.d("FileCreated","Created");

            FileOutputStream csvFile = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(csvFile);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.write(stringBuilder.toString());

            bufferedWriter.close();
            outputStreamWriter.close();
            csvFile.close();

            Log.d("FilePath", filePath);

            Intent intentShareFile = new Intent(Intent.ACTION_SEND);

            if (file.exists()){
                if (file.canRead()){
                    Log.d("File","Readable");
                }
                Log.d("Path",file.getPath());
                Log.d("File", "Exists");
            }


            intentShareFile.setType("text/*");
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            startActivity(Intent.createChooser(intentShareFile, "ShareFile"));

            FileInputStream fileInputStream = new FileInputStream(new File(filePath));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line = null;

            while((line = bufferedReader.readLine())!=null){
                Log.d("BufferedReader","Entered");
                System.out.println(line);
            }

            fileInputStream.close();
            inputStreamReader.close();
            bufferedReader.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void grantPermission(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST);
            }
        }
    }

    private void startSearch() {

        grantPermission();
        //init wifi manager
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //scan all networks
        wifiManager.startScan();
        if (wifiManager.isWifiEnabled()) {
            if (scanCount<5) {
                //create result object
                Result result = new Result(numberPickerX.getValue(), numberPickerY.getValue(), numberPickerZ.getValue());
                //print

                List<ScanResult> wifiList = wifiManager.getScanResults();
                for (ScanResult wifi : wifiList) {
                    String BSSID = wifi.BSSID;
                    int signalLevel = wifi.level;
                    String name = wifi.SSID;
                    //add wifi to list
                    result.addToList(new Wifi(BSSID, name, signalLevel));
                }
                scanCount++;
                scanButton.setText(getString(R.string.scanButton) + " " + scanCount);
                //add prepared result to arrayList
                results.add(result);
            }else{
                Snackbar.make(findViewById(R.id.mainLayout), getString(R.string.morethan5scan), Snackbar.LENGTH_LONG).show();
            }
        }else{
            Snackbar.make(findViewById(R.id.mainLayout), getString(R.string.enable_wifi), Snackbar.LENGTH_LONG).show();
        }

        printResults();
    }

    private void printResults(){
        resultText.setText("");
        ArrayList<String> bssidList = fileManager.getBssidList();
        HashMap<String, String> bssidResults = new HashMap<>();

        for (String string : bssidList){
            bssidResults.put(string, "");
            Log.d("BSSID Element",string);
        }
        for (Result result: results){
            for (String bssid : bssidList) {
                for (Wifi wifi : result.getWifiList()) {
                    if (wifi.getBssid().contentEquals(bssid)) {
                        String newValue = bssidResults.get(bssid).concat(wifi.getSignal()+",");
                        Log.d("NewValue",newValue);
                        bssidResults.put(bssid, newValue);
                    }
                }
            }
        }

        for(Map.Entry<String, String> entry : bssidResults.entrySet()) {
            String key = entry.getKey();
            String results = entry.getValue();
            resultText.append(key+" | "+results+"\n");
        }
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        scanCount = 0;
        scanButton.setText(getString(R.string.scanButton)+" "+scanCount);
    }
}
