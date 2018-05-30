package com.hasanalpzengin.wifidatacollector;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.hasanalpzengin.wifidatacollector.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by hasalp on 11.04.2018.
 */

public class BssidFileManager {
    private Context context;
    private static final String fileName = "bssid_list.data";

    public BssidFileManager(Context context) {
        this.context = context;
    }

    public ArrayList<String> getList() {
        ArrayList<String> temp_list = new ArrayList<>();
        String line;
        try {
            Log.d("FileManager","Entered");
            FileInputStream fileInput = context.openFileInput(fileName);
            InputStreamReader inputReader = new InputStreamReader(fileInput);
            BufferedReader bufferedReader = new BufferedReader(inputReader);

            while ((line = bufferedReader.readLine()) != null) {
                temp_list.add(line);
            }

            fileInput.close();
            inputReader.close();
            bufferedReader.close();
        }catch (IOException e){

        }catch (NullPointerException e){
            Toast.makeText(context, context.getString(R.string.init_bssid), Toast.LENGTH_LONG).show();
        }
        return temp_list;
    }

    public void createList(ArrayList<String> bssidList) {
        try{
            FileOutputStream fileOutput = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            for (String bssid : bssidList) {
                Log.d("WriteLoop",bssid);
                if (!bssidList.get(bssidList.size()-1).contentEquals(bssid)) {
                    fileOutput.write((bssid + "\n").getBytes());
                }else{
                    fileOutput.write((bssid).getBytes());
                }
            }
            fileOutput.close();
        }catch (IOException e){

        }
    }

    public ArrayList<String> getBssidList() {
        ArrayList<String> temp_list = new ArrayList<>();
        String line;
        try {
            Log.d("FileManager","Entered");
            FileInputStream fileInput = context.openFileInput(fileName);
            InputStreamReader inputReader = new InputStreamReader(fileInput);
            BufferedReader bufferedReader = new BufferedReader(inputReader);

            while ((line = bufferedReader.readLine()) != null) {
                String bssid = line.split(",")[1];
                temp_list.add(bssid);
            }

            fileInput.close();
            inputReader.close();
            bufferedReader.close();
        }catch (IOException e){

        }catch (NullPointerException e){
            Toast.makeText(context, context.getString(R.string.init_bssid), Toast.LENGTH_LONG).show();
        }
        return temp_list;
    }
}
