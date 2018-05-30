package com.hasanalpzengin.wifidatacollector;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class BssidManager extends AppCompatActivity {

    private ArrayList<String> bssidList;
    private Spinner scanSpinner;
    private Button addBssidButton,scanButton;
    private ListView bssidListView;
    private ArrayAdapter<String> arrayAdapter;
    private BssidFileManager fileManager;
    private SpinnerAdapter spinnerAdapter;
    private final static int LOCATION_REQUEST = 5555;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bssid_manager);
        init();
    }

    private void init() {
        bssidListView = findViewById(R.id.bssid_list);
        scanSpinner = findViewById(R.id.spinner);
        addBssidButton = findViewById(R.id.addButton);
        scanButton = findViewById(R.id.scanButton);
        fileManager = new BssidFileManager(getApplicationContext());
        bssidList = new ArrayList<>();
        bssidList = fileManager.getList();

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearch();
            }
        });

        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1 ,bssidList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Get the Item from ListView
                View view = super.getView(position, convertView, parent);

                // Initialize a TextView for ListView each Item
                TextView tv = (TextView) view.findViewById(android.R.id.text1);

                // Set the text color of TextView (ListView Item)
                tv.setTextColor(Color.BLACK);

                // Generate ListView Item using TextView
                return view;
            }
        };
        bssidListView.setAdapter(arrayAdapter);

        bssidListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bssidList.remove(position);
                arrayAdapter.notifyDataSetChanged();
                fileManager.createList(bssidList);
                Toast.makeText(getApplicationContext(),getString(R.string.deletedBSSID), Toast.LENGTH_LONG).show();
            }
        });

        addBssidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selected = scanSpinner.getSelectedItem().toString();
                String bssid = selected.split(",")[1];
                String ssid = selected.split(",")[0];
                if (!bssidList.contains(selected)){
                    bssidList.add(selected);
                    arrayAdapter.notifyDataSetChanged();
                    fileManager.createList(bssidList);
                    Toast.makeText(getApplicationContext(),getString(R.string.addedBSSID), Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),getString(R.string.existBSSID), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void startSearch() {
        grantPermission();
        ArrayList<String> results = new ArrayList<>();
        //init wifi manager
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //scan all networks
        wifiManager.startScan();
        if (wifiManager.isWifiEnabled()) {
            List<ScanResult> wifiList = wifiManager.getScanResults();
            for (ScanResult result : wifiList){
                results.add(result.SSID+","+result.BSSID);
            }
            spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,results);
            scanSpinner.setAdapter(spinnerAdapter);
        }else{
            Toast.makeText(getApplicationContext(), getString(R.string.enable_wifi), Toast.LENGTH_LONG).show();
        }
    }

    private void grantPermission(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST);
            }
        }
    }
}
