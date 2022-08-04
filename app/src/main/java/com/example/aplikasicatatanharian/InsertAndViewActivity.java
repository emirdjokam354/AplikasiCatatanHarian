package com.example.aplikasicatatanharian;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class InsertAndViewActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUST_CODE_STORAGE = 100;
    EditText editFileName, editContent;
    Button btnSimpan;
    boolean isEditable = false;
    String fileName = "";
    String tempCatatan = "";
    int eventId = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_and_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        editFileName = findViewById(R.id.editFileName);
        editContent = findViewById(R.id.editContent);
        btnSimpan = findViewById(R.id.btnSimpan);
        btnSimpan.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fileName = extras.getString("filename");
            editFileName.setText(fileName);

            getSupportActionBar().setTitle("Ubah Catatan");
        }else {
            getSupportActionBar().setTitle("Tambah Catatan");
        }

        eventId = 1;
        if(Build.VERSION.SDK_INT >= 23) {
            if (periksaIzinPenyimpanan()) {
                bacaFile();
            }
        }else {
            bacaFile();
        }

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btnSimpan:
                eventId = 2;
                if (!tempCatatan.equals(editContent.getText().toString())) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (periksaIzinPenyimpanan()) {
                            tampilkanDialogKonfirmasiPenyimpanan();
                        }else {
                            tampilkanDialogKonfirmasiPenyimpanan();
                        }
                    }
                }
                break;
        }
    }

    void tampilkanDialogKonfirmasiPenyimpanan() {
        new AlertDialog.Builder(this).setTitle("Simpan Catatan").setMessage("Apakah anda yakin ingin menyimpan catatan ini ?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        buatDanUbah();
                    }
                }).setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public void onBackPressed() {
        if (!tempCatatan.equals(editContent.getText().toString())) {
            tampilkanDialogKonfirmasiPenyimpanan();
        }
            super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    void buatDanUbah() {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            return;
        }

        String path = Environment.getExternalStoragePublicDirectory("DCIM")+"/catatan_harian";
//        String pathFile = getExternalFilesDir('/catatan_harian'), editFileName.getText().toString());
        File parent = new File(path);
        if (parent.exists()) {
            File file = new File(path, editFileName.getText().toString());
            FileOutputStream outputStream = null;
            try {
                file.createNewFile();
                outputStream = new FileOutputStream(file);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
//                outputStreamWriter.append(editFileName.getText());
                outputStreamWriter.append(editContent.getText());
                outputStreamWriter.flush();
                outputStreamWriter.close();
                outputStreamWriter.flush();
                outputStreamWriter.close();
            }catch (IOException e ) {
                e.printStackTrace();
            }
        }else {
            String catatan = "/catatan_harian";
            parent.mkdirs();
            File file = new File(path, editFileName.getText().toString());
            FileOutputStream outputStream = null;
            try {
                file.createNewFile();
                outputStream = new FileOutputStream(file, false);
                outputStream.write(editContent.getText().toString().getBytes());
                outputStream.flush();
                outputStream.close();
                Toast.makeText(getApplicationContext(), "Simpan Berhasil", Toast.LENGTH_SHORT).show();
            }catch (FileNotFoundException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }catch (IOException e ) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        onBackPressed();
    }

    public boolean periksaIzinPenyimpanan() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }else {
                ActivityCompat.requestPermissions(this, new String[] {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE
                }, REQUST_CODE_STORAGE);
                return false;
            }
        }else {
            return  true;
        }
    }

    void bacaFile() {
        String path = Environment.getExternalStoragePublicDirectory("DCIM")+"/catatan_harian";
        File file = new File(path, editFileName.getText().toString());
        if (file.exists()) {
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = br.readLine();

                while (line !=null) {
                    text.append(line);
                    line = br.readLine();
                }

                br.close();
            }catch (IOException e) {
                System.out.println("Error " + e.getMessage());
            }
            tempCatatan = text.toString();
            editContent.setText(text.toString());
        }
    }
}
