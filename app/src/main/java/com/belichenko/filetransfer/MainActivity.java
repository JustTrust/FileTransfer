package com.belichenko.filetransfer;

import android.content.Context;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;

public class MainActivity extends ActionBarActivity {

    ArrayAdapter<File> adapterDisks;
    ArrayAdapter<File> adapterFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapterDisks = new ArrayAdapter<File>(this, android.R.layout.simple_spinner_dropdown_item);
        Spinner disksSpinner = (Spinner) findViewById(R.id.diskList);
        disksSpinner.setAdapter(adapterDisks);

        adapterFiles = new ArrayAdapter<File>(this, android.R.layout.simple_list_item_1);
        ListView fileList = (ListView) findViewById(R.id.listOfFiles);
        fileList.setAdapter(adapterFiles);

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapterDisks.add((File) msg.obj);
                        break;
                    case 2:
                        adapterFiles.add((File) msg.obj);
                        break;
                    case 3:
                        TextView statusText = (TextView) findViewById(R.id.status_msg);
                        statusText.setText(msg.obj.toString());
                        break;
                }
            }
        };

        disksSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                File selectedDisk = (File) parentView.getItemAtPosition(position);
                TextView discSpace = (TextView) findViewById(R.id.diskSpace);
                //discSpace.setText(selectedDisk.getAbsolutePath());
                OurOutputStream.setSettingPath(selectedDisk);
                Log.d("Exchange_log", "Celected disk "+selectedDisk.getAbsolutePath());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Log.d("Exchange_log", "On nothing selected ");
            }

        });
        new ConnectToServer(handler).start();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Searching servers on local subnet
     */
    public void onFindServerBtClick(View view) {

    }
    private static String getReadableSize(long size) {
        if (size <= 0)
            return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size
                / Math.pow(1024, digitGroups))
                + " " + units[digitGroups];
    }



}

