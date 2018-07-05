package com.gglabs.mywebview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private WebView wbvMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init(){
        wbvMain = (WebView) findViewById(R.id.wbv_main);

        wbvMain.setWebViewClient(new WebViewClient());
        wbvMain.getSettings().setJavaScriptEnabled(true);
    }

    public String getStudentList(){
        String[] lostStudents = {
          "Shoshan", "Igal", "Itzik", "Itamar", "Nisim", "Tzahi"
        };
        StringBuilder sb = new StringBuilder("<html><head></head><body><ul>");
        for (String s : lostStudents) sb.append("<li>").append(s).append("</li>");

        return sb.append("</ul></body></html>").toString();
    }

    public void inFileSys(View view){
        wbvMain.loadUrl("file:////android_asset/index.html");
    }

    public void inner(View view) {
        String source = getStudentList();
        wbvMain.loadData(source, "text/html", "UTF-8");
    }

    public void localNet(View view) {
        wbvMain.loadUrl("http://10.0.0.20:8080/Materna");
    }

    public void internet(View view) {
        wbvMain.loadUrl("https://www.youtube.com/embed/VvCnhyAP1m8");
    }

    public void downloadClick(View view) {
        Intent i = new Intent(this, DownloadActivity.class);
        startActivity(i);
    }

    public void hwClick(View view) {
        Intent i = new Intent(this, GoogleActivity.class);
        startActivity(i);
    }

}
