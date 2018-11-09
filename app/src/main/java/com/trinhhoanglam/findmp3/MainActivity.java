package com.trinhhoanglam.findmp3;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private int STORE_PERMISSION_CODE = 1;
    TextView txtView;
    ListView listViewSongs;
    String[] songTitles;
    Button btnChoose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtView = (TextView) findViewById(R.id.txtView);
        listViewSongs = (ListView) findViewById(R.id.listViewSongs);
        btnChoose = (Button) findViewById(R.id.btnChoose);


        if (ContextCompat.checkSelfPermission(MainActivity.this,  android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You have already granted permission", Toast.LENGTH_SHORT).show();
        }
        else {
            requestStoragePermission();
        }

        String txt = "";
        txt += Environment.getExternalStorageDirectory().getPath() + "\n";
        txtView.setText(txt);

        ArrayList<File> mySongs = findSongs(Environment.getExternalStorageDirectory());

        songTitles = new String[mySongs.size()];
        for (int i = 0; i < mySongs.size(); i++) {
            songTitles[i] = mySongs.get(i).getName();
        }

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songTitles);
        listViewSongs.setAdapter(adp);

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();
            }
        });
    }

    public ArrayList<File> findSongs(File root) {
        ArrayList<File> allSongs = new ArrayList<File>();

        File[] files = root.listFiles();

        for (File singleFile : files) {
            if (singleFile.isDirectory() && !singleFile.isHidden()) {
                allSongs.addAll(findSongs(singleFile));
            }
            else {
                if (singleFile.getName().endsWith(".mp3") && !singleFile.isHidden()) {
                    allSongs.add(singleFile);
                }
            }
        }

        return allSongs;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORE_PERMISSION_CODE);
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this).setTitle("Permission needed").setMessage("This permission is needed because of sth").setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORE_PERMISSION_CODE);
                }
            }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        }
        else {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORE_PERMISSION_CODE);
        }
    }

    public void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, STORE_PERMISSION_CODE);
    }
}
