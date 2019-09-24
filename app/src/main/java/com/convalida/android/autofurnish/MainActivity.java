package com.convalida.android.autofurnish;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 0;
    private static final int MAIN_REQUEST_CODE = 1;
    WebView webview;
    ImageView splash;
    boolean firstLoad=false;
    private static final String TAG="MainActivity";
 //   String url="http://www.autofurnish.com";
    String url="https://autofurnish.com";
    String ua="Mozilla/5.0 (Linux; Android 4.1.1; HTC One X Build/JRO03C) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.58 Mobile Safari/537.31";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        webview =  findViewById(R.id.web);
        splash= findViewById(R.id.welcomeImg);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

       if (!isNetworkAvailable()) {
          AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
          dialogBuilder.setMessage("Internet connection required");
          dialogBuilder.setPositiveButton("Retry",null);

          final AlertDialog alertDialog=dialogBuilder.create();
          alertDialog.show();
          alertDialog.setCancelable(false);
           alertDialog.setOnKeyListener(new Dialog.OnKeyListener(){

               @Override
               public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                   if(keyCode==KeyEvent.KEYCODE_BACK){
                       finishAffinity();
                       alertDialog.dismiss();
                   }
                   return true;
               }
           });

           Button positiveButton=alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
           positiveButton.setTextColor(getResources().getColor(R.color.colorRed));
           positiveButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent = new Intent(MainActivity.this,MainActivity.class);
                   //Intent intent=getIntent();
                   startActivity(intent);
               }
           });


        } else {
           if (Build.VERSION.SDK_INT <Build.VERSION_CODES.LOLLIPOP) {
               webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
               webview.getSettings().setLoadWithOverviewMode(true);
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                   webview.setWebContentsDebuggingEnabled(true);
               }
           }
           webview.setWebChromeClient(new WebChromeClient(){
                  public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg){
                   WebView newWebView=new WebView(MainActivity.this);
                   newWebView.getSettings().setJavaScriptEnabled(true);
                   newWebView.getSettings().setSupportZoom(true);
                   newWebView.getSettings().setBuiltInZoomControls(true);
                   newWebView.getSettings().setSupportMultipleWindows(true);

                   view.addView(newWebView);
                   resultMsg.sendToTarget();
                       return true;
                    }
                    });
           webview.getSettings().setLoadsImagesAutomatically(true);
             webview.getSettings().setDomStorageEnabled(true);
           webview.getSettings().setJavaScriptEnabled(true);
           webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
               // webview.getSettings().setUserAgentString("");
           webview.getSettings().setUserAgentString("Chrome/56.0.0.0 Mobile");
           Log.e(TAG,"Url is "+url);

           webview.loadUrl(url);
           webview.setWebViewClient(new WebViewClient(){

                public void onReceivedError(WebView webView,int errorCode, String description,String failingUrl) {
                    Log.e(TAG, "Error code is " + errorCode);
                    Log.e(TAG, "Description is " + description);
                    Log.e(TAG, "Failing url is " + failingUrl);
                    if (failingUrl.startsWith("mailto:")) {
                        webView.stopLoading();
                        Intent email = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",failingUrl.substring(7),null));
                       ComponentName emailApp=email.resolveActivity(getPackageManager());
                       ComponentName unsupportedAction=ComponentName.unflattenFromString("com.android.fallback/.Fallback");
                        if(emailApp==null||emailApp.equals(unsupportedAction)){
                           Toast.makeText(getApplicationContext(),"No appropriate app found in device",Toast.LENGTH_SHORT).show();
                       }
                                          else {
                           startActivity(Intent.createChooser(email, "Choose an Email client:"));
                       }
                       }
                else {
                        try {
                            webView.stopLoading();

                        } catch (Exception e) {

                        }
                        Log.e(TAG, "Error code is " + errorCode);
                        Log.e(TAG, "Description is " + description);
                        Log.e(TAG, "Failing url is " + failingUrl);
                        if (webView.canGoBack()) {
                            webView.goBack();
                        }
                        if (!isNetworkAvailable()) {
                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                            dialogBuilder.setMessage("Internet connection required");
                            dialogBuilder.setPositiveButton("Retry", null);

                            final AlertDialog alertDialog = dialogBuilder.create();
                            alertDialog.show();
                            alertDialog.setCancelable(false);
                            alertDialog.setOnKeyListener(new Dialog.OnKeyListener() {

                                @Override
                                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                                        finishAffinity();
                                        alertDialog.dismiss();
                                    }
                                    return true;
                                }
                            });

                            Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            positiveButton.setTextColor(getResources().getColor(R.color.colorRed));
                            positiveButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(MainActivity.this, MainActivity.class);

                                    startActivity(intent);
                                }
                            });
                        }
                    }
                }



                public void onPageStarted(WebView view, String currentUrl, Bitmap favicon){
                    super.onPageStarted(view, url, favicon);

                     Log.e(TAG,"Current url: onPageStarted "+currentUrl);
                     if(!firstLoad) {
                         if (currentUrl.equals("https://autofurnish.com/")) {
                             webview.setVisibility(View.INVISIBLE);
                             splash.setVisibility(View.VISIBLE);
                             firstLoad=true;
                         }
                     }

                    if(currentUrl.equals("https://www.facebook.com/autofurnish/")|| currentUrl.contains("twitter")||currentUrl.equals("https://plus.google.com/+Autofurnish")||currentUrl.contains("youtube")||currentUrl.contains("instagram")
                            || currentUrl.contains("play.google.com")|| currentUrl.contains("itunes.apple.com")||currentUrl.contains("convalidatech")){
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(currentUrl));

                        startActivityForResult(i,REQUEST_CODE);

                        String urlParent;




                              }

                }
                public void onPageFinished(WebView view, String url){
                        Log.e(TAG, "On page finished called: " + url);
                        webview.setVisibility(View.VISIBLE);
                        splash.setVisibility(View.INVISIBLE);
                        Log.e(TAG,"Parent page: "+url);
                    SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("Parent url",MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("Url",url);
                    editor.apply();


                }


           });
           Log.e(TAG,"Url is "+url);
           webview.loadUrl(url);
           }

}
protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==REQUEST_CODE){

onBackPressed();
            }
            else{
            webview.loadUrl(url);

        }
}


public void onBackPressed(){
             webview.goBack();

}

    boolean doubleBackToExitPressedOnce=false;
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(event.getAction()==KeyEvent.ACTION_DOWN){
            switch (keyCode){
                case KeyEvent.KEYCODE_BACK:
                    if(doubleBackToExitPressedOnce){
                        if(webview.canGoBack()){
                            webview.goBack();
                        }
                        else {
                            finishAffinity();
                        }
                        return true;

                    }
                    this.doubleBackToExitPressedOnce=true;
                    Toast.makeText(getApplicationContext(),"Press back again to go back",Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce=false;

                        }
                    },2000);

                    return true;
            }
        }
        return super.onKeyDown(keyCode,event);
    }

    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager= (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo=connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo!=null;
    }
    }
