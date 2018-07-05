package com.gglabs.mywebview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class GoogleActivity extends AppCompatActivity {

    private static final String TAG = "GoogleActivity";

    public static final int BYTES_IN_KB = 1000;
    public static final int BYTES_IN_MB = 1000000;


    private RelativeLayout layImage, layWebView;
    //private Button btnBack;
    private TextView tvLoading;
    private ImageView ivImage;
    private ProgressBar pbKn, pbUnk;
    private DownloadImageTask downloadTask;
    private WebView webView;
    private WVClient wvClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init();

        webView.loadUrl("https://images.google.com/?hl=en");
    }

    private void init() {
        layImage = (RelativeLayout) findViewById(R.id.lay_image);
        layWebView = (RelativeLayout) findViewById(R.id.lay_wbv_google);
        tvLoading = (TextView) findViewById(R.id.tv_loading);
        pbKn = (ProgressBar) findViewById(R.id.pb_google_kn);
        pbUnk = (ProgressBar) findViewById(R.id.pb_google_unk);
        webView = (WebView) findViewById(R.id.wbv_google);
        ivImage = (ImageView) findViewById(R.id.iv_image);

        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backClick();
            }
        });

        tvLoading.setVisibility(View.GONE);
        layImage.setVisibility(View.GONE);
        pbKn.setVisibility(View.GONE);

        wvClient = new WVClient(this);
        webView.setWebViewClient(wvClient);
        webView.getSettings().setJavaScriptEnabled(true);
    }

    @Override
    public void onBackPressed() {
        if (layImage.getVisibility() == View.VISIBLE) {
            backClick();
        } else if (webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }

    private void backClick() {
        if (downloadTask != null) {
            downloadTask.cancel(true);
            hideShowView(layImage, layWebView);
        }
    }

    private void hideShowView(View hide, View show) {
        hide.setVisibility(View.GONE);
        show.setVisibility(View.VISIBLE);
    }

    private class WVClient extends WebViewClient {

        private static final String TAG = "WVClient";

        private Context context;
        private String currentUrl;

        public WVClient(GoogleActivity activity) {
            super();
            this.context = activity;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //Log.d(TAG, "shouldOverrideUrlLoading(): URL(" + url.length() + "): " + url);

            String shortUrl = url;
            String currShortUrl = "";
            if (url.length() > 4) shortUrl = url.substring(url.length() - 4);
            if (currentUrl.length() > 25)
                currShortUrl = currentUrl.substring(currentUrl.length() - 25);

            if (!UrlUtils.isUrlContainsImage(shortUrl)) {
                Log.d(TAG, "Page DOESN'T contains image");

                if (currShortUrl.contains("imgrc=") && !UrlUtils.isUrlContainsImage(shortUrl)) {
                    Toast.makeText(context, "Can't show this image", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            } else {
                Log.d(TAG, "Page CONTAINS image !");

                downloadTask = new DownloadImageTask(GoogleActivity.this);
                downloadTask.execute(url, UrlUtils.getImageExtension(shortUrl));
                return true;
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d(TAG, "onPageFinished(): URL(" + url.length() + "): " + url);
            pbUnk.setVisibility(View.GONE);
            currentUrl = url;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Integer, String> {
        //-------------------------------------------<Params, Progress, Result>
        private static final String TAG = "DownloadImageTask";

        private int BUFFER_SIZE = 1024;

        private Context context;
        private String loadingMessage;
        private String imagePath;

        private Map<WeakReference, View> weakReferences;
        private final WeakReference wkLayWeb, wkLayImage, wkWebView, wkPbKn, wkTvLoading, wkIvImage;
        //private WeakReference<GoogleActivity> wkThisActivity;

        DownloadImageTask(GoogleActivity googleActivity) {
            this.context = googleActivity;
            loadingMessage = context.getResources().getString(R.string.downloading_image);

            weakReferences = new HashMap<>();
            weakReferences.put(wkLayImage = new WeakReference(layImage), layImage);
            weakReferences.put(wkLayWeb = new WeakReference(layWebView), layWebView);
            weakReferences.put(wkWebView = new WeakReference(webView), webView);
            weakReferences.put(wkPbKn = new WeakReference(pbKn), pbKn);
            weakReferences.put(wkTvLoading = new WeakReference(tvLoading), tvLoading);
            weakReferences.put(wkIvImage = new WeakReference(ivImage), ivImage);
            //wkThisActivity = new WeakReference<>(googleActivity);
        }

        @Override
        protected void onPreExecute() {
            ivImage.setImageBitmap(null);
            pbKn.setProgress(0);
            tvLoading.setText(loadingMessage);

            pbKn.setVisibility(View.VISIBLE);
            tvLoading.setVisibility(View.VISIBLE);
            layImage.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream input;
            FileOutputStream fileOut;
            try {
                imagePath = getFilesDir() + "image" + params[1];

                URL url = new URL(params[0]);
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();

                fileOut = new FileOutputStream(imagePath);
                input = urlConnection.getInputStream();
                int imgSize = urlConnection.getContentLength();

                byte[] buffer = new byte[BUFFER_SIZE];
                int readFromBuffer;
                int actuallyRead = 0;

                while ((readFromBuffer = input.read(buffer)) != -1) {
                    fileOut.write(buffer, 0, readFromBuffer);
                    actuallyRead += readFromBuffer;
                    publishProgress(actuallyRead, imgSize);
                }

                input.close();
                fileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return imagePath;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            for (Map.Entry<WeakReference, View> entry : weakReferences.entrySet()) {

                if (entry.getValue() != null)
                    weakReferences.put(entry.getKey(), (View) entry.getKey().get());
            }
/*            layImage = (RelativeLayout) weakReferences.get(layImage).get();
            layWebView = (RelativeLayout) weakReferences.get(layWebView).get();
            webView = (WebView) weakReferences.get(webView).get();
            pbKn = (ProgressBar) weakReferences.get(pbKn).get();
            tvLoading = (TextView) weakReferences.get(tvLoading).get();
            ivImage = (ImageView) weakReferences.get(ivImage).get();*/

            float current = values[0];
            float target = values[1];
            float progress = (current / target) * 100;

            ((ProgressBar) weakReferences.get(wkPbKn)).setProgress((int) progress);
            ((TextView) weakReferences.get(wkTvLoading)).setText(
                    loadingMessage + formatFileSize(current) + " / " + formatFileSize(target));

//            pbKn.setProgress((int) progress);
//            tvLoading.setText(loadingMessage + formatFileSize(current) + " / " + formatFileSize(target));
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            new File(imagePath).delete();
            backClick();
        }

        @Override
        protected void onPostExecute(String imgPath) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
            ivImage.setImageBitmap(bitmap);
            pbKn.setVisibility(View.GONE);
            tvLoading.setVisibility(View.GONE);

            layWebView.setVisibility(View.GONE);
        }
    }

    public static String formatFileSize(float size) {
        float convertedSize = size;
        String suffix = " Bytes";

        if (size > BYTES_IN_MB) {
            suffix = " Mb";
            convertedSize = size / BYTES_IN_MB;
        } else if (size > BYTES_IN_KB) {
            suffix = " Kb";
            convertedSize = size / BYTES_IN_KB;
        }

        return String.valueOf(String.format("%.2f", convertedSize)) + suffix;
    }

    public static int getPageSize(URL url) {
        int result = -1;
        try {
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            result = urlConnection.getContentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void log(String tag, String msg) {

        StackTraceElement[] stackTraceElement = Thread.currentThread()
                .getStackTrace();
        int currentIndex = -1;
        for (int i = 0; i < stackTraceElement.length; i++) {
            if (stackTraceElement[i].getMethodName().compareTo("log") == 0) {
                currentIndex = i + 1;
                break;
            }
        }

        String fullClassName = stackTraceElement[currentIndex].getClassName();
        String className = fullClassName.substring(fullClassName
                .lastIndexOf(".") + 1);
        String methodName = stackTraceElement[currentIndex].getMethodName();
        String lineNumber = String
                .valueOf(stackTraceElement[currentIndex].getLineNumber());

        Log.i(tag, msg);
        Log.i(tag + " position", "at " + fullClassName + "." + methodName + "("
                + className + ".java:" + lineNumber + ")");

    }

}

