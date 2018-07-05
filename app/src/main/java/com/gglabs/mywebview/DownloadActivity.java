package com.gglabs.mywebview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class DownloadActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DownloadActivity";

    private Button btnSave;
    private EditText etUrl;
    private ImageView ivImage;
    private ProgressBar pbDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        btnSave = (Button) findViewById(R.id.btnSave);
        etUrl = (EditText) findViewById(R.id.etUrl);
        ivImage = (ImageView) findViewById(R.id.iv_image);
        pbDownload = (ProgressBar) findViewById(R.id.pb_download);

        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnSave) {
            SaveImage saveImageAsync = new SaveImage();
            saveImageAsync.execute();
        }
    }

    private class SaveImage extends AsyncTask<Void, Void, Void> {

        final String urlFromUser = etUrl.getText().toString().trim();

        @Override
        protected void onPreExecute() {
            ivImage.setVisibility(View.GONE);
            pbDownload.setVisibility(View.VISIBLE);
            btnSave.setEnabled(false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            InputStream input;
            FileOutputStream fos;
            try {
                input = new URL(urlFromUser).openStream();
                fos = new FileOutputStream(getFilesDir() + "/image.jpg");

                int b;
                while ((b = input.read()) != -1){
                    fos.write(b);
                    Log.d(TAG, "input.available(): " + input.available());
                }
                input.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Bitmap bitmap = BitmapFactory.decodeFile(getFilesDir() + "/image.jpg");
            ivImage.setImageBitmap(bitmap);
            //ivImage.setImageBitmap(null);
            pbDownload.setVisibility(View.GONE);
            ivImage.setVisibility(View.VISIBLE);
            btnSave.setEnabled(true);
        }
    }

}
