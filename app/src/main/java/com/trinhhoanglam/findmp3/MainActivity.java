package com.trinhhoanglam.findmp3;


import android.app.Activity;
import android.app.Dialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private int STORE_PERMISSION_CODE = 1;
    TextView txtView, txtFolder;
    ListView listViewSongs;
    String[] songTitles;
    Button btnChoose, btnUp, btnConfirm;
    String dirPath;
    static final int CUSTOM_DIALOG_ID = 0;
    ListView lwItems;

    File root;
    File curFolder;

    private List<String> fileList = new ArrayList<String>();
    private List<String> fileNameList = new ArrayList<String>();

    Dialog dialog;

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

        root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        curFolder = root;

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

    public void setListViewSongs() {
        final ArrayList<File> mySongs = findSongs(curFolder);

        songTitles = new String[mySongs.size()];
        for (int i = 0; i < mySongs.size(); i++) {
            songTitles[i] = mySongs.get(i).getName();
        }

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songTitles);
        listViewSongs.setAdapter(adp);


        listViewSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getApplicationContext(), PlayActivity.class)
                        .putExtra("pos",position)
                        .putExtra("songList", mySongs));
            }
        });
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

    @Override
    protected Dialog onCreateDialog(int id) {
        dialog = null;
        switch (id) {
            case CUSTOM_DIALOG_ID:
                dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.dirrctory_choose_dialog);
                dialog.setTitle("Choose Directory");
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);

                txtFolder = (TextView) dialog.findViewById(R.id.txtFolder);
                btnUp = (Button) dialog.findViewById(R.id.btnUp);
                btnUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ListDir(curFolder.getParentFile());
                    }
                });

                lwItems = (ListView) dialog.findViewById(R.id.lwItems);
                lwItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        File selected = new File(fileList.get(position));
                        if (selected.isDirectory())
                            ListDir(selected);
                        else {
                            Toast.makeText(MainActivity.this, "You cannot choose a file!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                btnConfirm = (Button) dialog.findViewById(R.id.btnConfirm);
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        txtView.setText(curFolder.getPath());
                        dialog.dismiss();
                        setListViewSongs();
                    }
                });

                ListDir(curFolder);

                break;
        }
        return  dialog;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        switch (id) {
            case CUSTOM_DIALOG_ID:

        }
    }

    void ListDir(File f) {
        if (f.equals(root)) {
            btnUp.setEnabled(false);
        } else {
            btnUp.setEnabled(true);
        }

        curFolder = f;
        txtFolder.setText(f.getPath());

        File[] files = f.listFiles();
        fileList.clear();
        fileNameList.clear();

        for (File file : files) {
            fileList.add(file.getPath());
            fileNameList.add(file.getName());
        }

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileNameList);
        lwItems.setAdapter(adp);
    }

    public void performFileSearch() {
        showDialog(CUSTOM_DIALOG_ID);
    }
}
